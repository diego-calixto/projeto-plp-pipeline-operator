package lfp.plp.functional2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import org.junit.Test;

import lfp.plp.expressions1.excecao.ErroTipoException;
import lfp.plp.expressions1.util.Tipo;
import lfp.plp.expressions2.expression.ExpEquals;
import lfp.plp.expressions2.expression.Expressao;
import lfp.plp.expressions2.expression.ExpSoma;
import lfp.plp.expressions2.expression.Id;
import lfp.plp.expressions2.expression.Valor;
import lfp.plp.expressions2.expression.ValorInteiro;
import lfp.plp.expressions2.expression.ValorString;
import lfp.plp.expressions2.memory.AmbienteCompilacao;
import lfp.plp.expressions2.memory.AmbienteExecucao;
import lfp.plp.expressions2.memory.ContextoCompilacao;
import lfp.plp.expressions2.memory.ContextoExecucao;
import lfp.plp.functional1.util.TipoFuncao;
import lfp.plp.functional2.expression.Aplicacao;
import lfp.plp.functional2.expression.ExpComposicao;
import lfp.plp.functional2.expression.ExpPipeline;
import lfp.plp.functional2.expression.ValorFuncao;
import lfp.plp.functional2.parser.Func2Parser;

public class PipelineComposicaoTest {

	private Programa parse(String input) throws Exception {
		Func2Parser parser = new Func2Parser(new ByteArrayInputStream(input.getBytes("UTF-8")));
		return parser.Input();
	}

	@Test
	public void testASTPipelineSimples() throws Exception {
		// x |> f
		// inc = fn x . x + 1
		Id x = new Id("x");
		ValorFuncao inc = new ValorFuncao(Collections.singletonList(x), new ExpSoma(x, new ValorInteiro(1)));
		ExpPipeline pipe = new ExpPipeline(new ValorInteiro(5), inc);

		AmbienteCompilacao ambComp = new ContextoCompilacao();
		assertTrue("checaTipo do pipeline simples deve ser valido", pipe.checaTipo(ambComp));
		Tipo tipo = pipe.getTipo(ambComp);
		assertTrue(tipo.eInteiro());

		AmbienteExecucao ambExec = new ContextoExecucao();
		Valor res = pipe.avaliar(ambExec);
		assertTrue(res instanceof ValorInteiro);
		assertEquals(6, ((ValorInteiro) res).valor().intValue());
	}

	@Test
	public void testASTPipelineEncadeado() throws Exception {
		// (10 |> inc) |> dobro
		Id x = new Id("x");
		ValorFuncao inc = new ValorFuncao(Collections.singletonList(x), new ExpSoma(x, new ValorInteiro(1)));
		ValorFuncao dobro = new ValorFuncao(Collections.singletonList(x), new ExpSoma(x, x));

		ExpPipeline pipe1 = new ExpPipeline(new ValorInteiro(10), inc);
		ExpPipeline pipe2 = new ExpPipeline(pipe1, dobro);

		AmbienteCompilacao ambComp = new ContextoCompilacao();
		assertTrue(pipe2.checaTipo(ambComp));

		AmbienteExecucao ambExec = new ContextoExecucao();
		Valor res = pipe2.avaliar(ambExec);
		assertEquals(22, ((ValorInteiro) res).valor().intValue());
	}

	@Test
	public void testASTComposicaoSimples() throws Exception {
		// inc >> dobro
		Id x = new Id("x");
		ValorFuncao inc = new ValorFuncao(Collections.singletonList(x), new ExpSoma(x, new ValorInteiro(1)));
		ValorFuncao dobro = new ValorFuncao(Collections.singletonList(x), new ExpSoma(x, x));

		ExpComposicao comp = new ExpComposicao(inc, dobro);

		AmbienteCompilacao ambComp = new ContextoCompilacao();
		assertTrue(comp.checaTipo(ambComp));
		Tipo tipo = comp.getTipo(ambComp);
		assertTrue(tipo instanceof TipoFuncao);
		TipoFuncao tf = (TipoFuncao) tipo;
		assertEquals(1, tf.getDominio().size());
		assertTrue(tf.getDominio().get(0).eInteiro());
		assertTrue(tf.getImagem().eInteiro());

		// Agora aplica a funcao composta a 4: (inc >> dobro)(4) = dobro(inc(4)) = 10
		ExpPipeline pipe = new ExpPipeline(new ValorInteiro(4), comp);
		assertTrue(pipe.checaTipo(ambComp));

		AmbienteExecucao ambExec = new ContextoExecucao();
		Valor res = pipe.avaliar(ambExec);
		assertEquals(10, ((ValorInteiro) res).valor().intValue());
	}

	@Test
	public void testPipelineRejeitaNaoFuncaoADireita() throws Exception {
		// 5 |> 10
		ExpPipeline pipe = new ExpPipeline(new ValorInteiro(5), new ValorInteiro(10));
		AmbienteCompilacao ambComp = new ContextoCompilacao();
		assertFalse("Pipeline com constante a direita deve falhar checaTipo", pipe.checaTipo(ambComp));

		try {
			AmbienteExecucao ambExec = new ContextoExecucao();
			pipe.avaliar(ambExec);
			fail("Deve lancar TiposIncompativeisException ou ErroTipoException");
		} catch (ErroTipoException e) {
			// Sucesso
		}
	}

	@Test
	public void testPipelineRejeitaTipoArgumentoIncompativel() throws Exception {
		// "texto" |> inc
		Id x = new Id("x");
		ValorFuncao inc = new ValorFuncao(Collections.singletonList(x), new ExpSoma(x, new ValorInteiro(1)));
		ExpPipeline pipe = new ExpPipeline(new ValorString("texto"), inc);

		AmbienteCompilacao ambComp = new ContextoCompilacao();
		assertFalse("Argumento string para funcao que espera inteiro deve falhar checaTipo", pipe.checaTipo(ambComp));
	}

	@Test
	public void testComposicaoRejeitaFuncoesIncompatíveis() throws Exception {
		// ePar : INTEIRO -> BOOLEANO
		// inc : INTEIRO -> INTEIRO
		// ePar >> inc deve falhar pois retorno de ePar (BOOLEANO) incompativel com entrada de inc (INTEIRO)
		Id x = new Id("x");
		ValorFuncao ePar = new ValorFuncao(Collections.singletonList(x), new ExpEquals(x, new ValorInteiro(0)));
		ValorFuncao inc = new ValorFuncao(Collections.singletonList(x), new ExpSoma(x, new ValorInteiro(1)));

		ExpComposicao comp = new ExpComposicao(ePar, inc);
		AmbienteCompilacao ambComp = new ContextoCompilacao();
		assertFalse("Composicao de funcoes com tipos retorno/parametro incompativeis deve falhar checaTipo", comp.checaTipo(ambComp));
	}

	@Test
	public void testComposicaoRejeitaNaoFuncao() throws Exception {
		// 5 >> inc
		Id x = new Id("x");
		ValorFuncao inc = new ValorFuncao(Collections.singletonList(x), new ExpSoma(x, new ValorInteiro(1)));
		ExpComposicao comp = new ExpComposicao(new ValorInteiro(5), inc);

		AmbienteCompilacao ambComp = new ContextoCompilacao();
		assertFalse(comp.checaTipo(ambComp));

		try {
			AmbienteExecucao ambExec = new ContextoExecucao();
			comp.avaliar(ambExec);
			fail("Deve lancar TiposIncompativeisException ou ErroTipoException");
		} catch (ErroTipoException e) {
			// Sucesso
		}
	}

	@Test
	public void testParsingPipelineSimples() throws Exception {
		String codigo = "let fun inc x = x + 1 in 5 |> inc";
		Programa prog = parse(codigo);
		assertTrue(prog.checaTipo());
		assertEquals(new ValorInteiro(6), prog.executar());
	}

	@Test
	public void testParsingPipelineEncadeadoAssociatividadeEsquerda() throws Exception {
		String codigo = "let fun inc x = x + 1, fun dobro x = x + x in 10 |> inc |> dobro";
		Programa prog = parse(codigo);
		assertTrue(prog.checaTipo());
		assertEquals(new ValorInteiro(22), prog.executar());
	}

	@Test
	public void testParsingPrecedenciaAritmeticaSobrePipeline() throws Exception {
		// 2 + 3 |> dobro => dobro(2 + 3) = 10
		String codigo = "let fun dobro x = x + x in 2 + 3 |> dobro";
		Programa prog = parse(codigo);
		assertTrue(prog.checaTipo());
		assertEquals(new ValorInteiro(10), prog.executar());
	}

	@Test
	public void testParsingComposicaoPrecedenciaSobrePipeline() throws Exception {
		// 5 |> inc >> dobro => 5 |> (inc >> dobro) = dobro(inc(5)) = 12
		String codigo = "let fun inc x = x + 1, fun dobro x = x + x in 5 |> inc >> dobro";
		Programa prog = parse(codigo);
		assertTrue(prog.checaTipo());
		assertEquals(new ValorInteiro(12), prog.executar());
	}

	@Test
	public void testParsingPipelineComLambdas() throws Exception {
		String codigo = "5 |> (fn x . x + x) |> (fn y . y + 10)";
		Programa prog = parse(codigo);
		assertTrue(prog.checaTipo());
		assertEquals(new ValorInteiro(20), prog.executar());
	}

	@Test
	public void testAplicacaoDiretaComMultiplosArgumentos() throws Exception {
		Programa prog = parse("let fun soma x y = x + y in soma(2, 3)");
		assertTrue(prog.checaTipo());
		assertEquals(new ValorInteiro(5), prog.executar());
	}

	@Test
	public void testPipelineComMultiplosArgumentos() throws Exception {
		Programa prog = parse("let fun soma x y = x + y in (2, 3) |> soma");
		assertTrue(prog.checaTipo());
		assertEquals(new ValorInteiro(5), prog.executar());
	}

	@Test
	public void testAplicacoesComMultiplosArgumentosRejeitamAridadeIncompativel()
			throws Exception {
		Programa chamadaDireta = parse(
				"let fun soma x y = x + y in soma(2)");
		assertFalse(chamadaDireta.checaTipo());

		Programa chamadaPipeline = parse(
				"let fun soma x y = x + y in (2, 3, 4) |> soma");
		assertFalse(chamadaPipeline.checaTipo());
	}

	@Test
	public void testAplicacoesComMultiplosArgumentosRejeitamTiposIncompativeis()
			throws Exception {
		Programa chamadaDireta = parse(
				"let fun soma x y = x + y in soma(2, \"texto\")");
		assertFalse(chamadaDireta.checaTipo());

		Programa chamadaPipeline = parse(
				"let fun soma x y = x + y in (2, \"texto\") |> soma");
		assertFalse(chamadaPipeline.checaTipo());
	}

	@Test
	public void testAplicacaoInvalidaEmExecucaoInformaAridade() throws Exception {
		Id x = new Id("x");
		ValorFuncao funcao = new ValorFuncao(Collections.singletonList(x), x);
		try {
			new Aplicacao(funcao, Collections.<Expressao>emptyList())
					.avaliar(new ContextoExecucao());
			fail("Aplicacao com aridade incorreta deve falhar claramente");
		} catch (ErroTipoException expected) {
			assertTrue(expected.getMessage().contains("Aridade"));
		}
	}

	@Test
	public void testParsingPipelineComStrings() throws Exception {
		String codigo = "\"hello\" |> (fn s . s ++ \" world\") |> (fn s . length s)";
		Programa prog = parse(codigo);
		assertTrue(prog.checaTipo());
		assertEquals(new ValorInteiro(11), prog.executar());
	}

	@Test
	public void testParsingErroTipoPipelineNaoFuncao() throws Exception {
		String codigo = "5 |> 10";
		Programa prog = parse(codigo);
		assertFalse("5 |> 10 nao deve passar na checagem de tipos", prog.checaTipo());
	}

	@Test
	public void testParsingErroTipoPipelineArgumentoIncompativel() throws Exception {
		String codigo = "let fun inc x = x + 1 in \"texto\" |> inc";
		Programa prog = parse(codigo);
		assertFalse("string em inc deve falhar na checagem de tipos", prog.checaTipo());
	}

	@Test
	public void testParsingErroTipoComposicaoIncompativel() throws Exception {
		String codigo = "let fun ePar x = x == 0, fun inc x = x + 1 in ePar >> inc";
		Programa prog = parse(codigo);
		assertFalse("ePar >> inc deve falhar na checagem de tipos", prog.checaTipo());
	}

	@Test
	public void testProgramaSemRegressaoDec() throws Exception {
		String codigo = "(let fun dec n = if (n==0) then 0 else n + dec(n-1) in dec)(5)";
		Programa prog = parse(codigo);
		assertTrue(prog.checaTipo());
		assertEquals(new ValorInteiro(15), prog.executar());
	}

	@Test
	public void testProgramaSemRegressaoFatorial() throws Exception {
		String codigo = "let fun fat n = let fun mult x y = if (x == 0) then (0) else (y + (mult((x - 1),y))) in if (n == 0) then (1) else (mult(n,(fat (n - 1)))) in fat(5)";
		Programa prog = parse(codigo);
		assertTrue(prog.checaTipo());
		assertEquals(new ValorInteiro(120), prog.executar());
	}
}
