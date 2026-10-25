package lfp.plp.functional2.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lfp.plp.expressions1.util.Tipo;
import lfp.plp.expressions2.expression.ExpBinaria;
import lfp.plp.expressions2.expression.Expressao;
import lfp.plp.expressions2.expression.Id;
import lfp.plp.expressions2.expression.Valor;
import lfp.plp.expressions2.memory.AmbienteCompilacao;
import lfp.plp.expressions2.memory.AmbienteExecucao;
import lfp.plp.expressions2.memory.VariavelJaDeclaradaException;
import lfp.plp.expressions2.memory.VariavelNaoDeclaradaException;
import lfp.plp.functional1.util.TipoFuncao;
import lfp.plp.functional1.util.TipoPolimorfico;

/**
 * Representa uma expressao de Composicao de Funcoes (esq >> dir) compativel com a BNF.
 * Compoe duas funcoes da esquerda para a direita, equivalente a (f >> g)(x) = g(f(x)).
 */
public class ExpComposicao extends ExpBinaria {

	public ExpComposicao(Expressao esq, Expressao dir) {
		super(esq, dir, ">>");
	}

	public Expressao getPrimeiraFuncao() {
		return getEsq();
	}

	public Expressao getSegundaFuncao() {
		return getDir();
	}

	private Tipo getFuncType(Expressao exp, AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Tipo tipo = null;
		if (exp instanceof Id) {
			tipo = amb.get((Id) exp);
		} else if (exp != null) {
			tipo = exp.getTipo(amb);
		}
		return tipo;
	}

	@Override
	public Valor avaliar(AmbienteExecucao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Valor funcF = getEsq().avaliar(amb);
		Valor funcG = getDir().avaliar(amb);

		if (!(funcF instanceof ValorFuncao) || !(funcG instanceof ValorFuncao)) {
			throw new TiposIncompativeisException("Operando do operador >> deve ser uma funcao.");
		}

		ValorFuncao vfF = (ValorFuncao) funcF;
		ValorFuncao vfG = (ValorFuncao) funcG;

		Id p = new Id("arg");
		List<Id> params = Collections.singletonList(p);

		Expressao chamadaF = new Aplicacao(vfF, Collections.singletonList((Expressao) p));
		Expressao corpo = new Aplicacao(vfG, Collections.singletonList(chamadaF));

		ValorFuncao funcComposta = new ValorFuncao(params, corpo);
		return funcComposta.avaliar(amb);
	}

	@Override
	protected boolean checaTipoElementoTerminal(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Tipo tEsq = getFuncType(getEsq(), amb);
		Tipo tDir = getFuncType(getDir(), amb);

		if (tEsq == null || tDir == null) {
			return false;
		}

		if (tEsq instanceof TipoFuncao && tDir instanceof TipoFuncao) {
			TipoFuncao tfEsq = (TipoFuncao) tEsq;
			TipoFuncao tfDir = (TipoFuncao) tDir;

			if (tfEsq.getDominio().size() != 1 || tfDir.getDominio().size() != 1) {
				return false;
			}

			Tipo retornoF = tfEsq.getImagem();
			Tipo paramG = tfDir.getDominio().get(0);

			return retornoF.eIgual(paramG);
		}

		if (tEsq instanceof TipoPolimorfico || tDir instanceof TipoPolimorfico) {
			return true;
		}

		return false;
	}

	@Override
	public Tipo getTipo(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Tipo tEsq = getFuncType(getEsq(), amb);
		Tipo tDir = getFuncType(getDir(), amb);

		if (tEsq instanceof TipoFuncao && tDir instanceof TipoFuncao) {
			TipoFuncao tfEsq = (TipoFuncao) tEsq;
			TipoFuncao tfDir = (TipoFuncao) tDir;
			return new TipoFuncao(new ArrayList<Tipo>(tfEsq.getDominio()), tfDir.getImagem());
		}
		if (tEsq instanceof TipoFuncao) {
			return new TipoFuncao(new ArrayList<Tipo>(((TipoFuncao) tEsq).getDominio()), new TipoPolimorfico());
		}
		if (tDir instanceof TipoFuncao) {
			return new TipoFuncao(Collections.singletonList((Tipo) new TipoPolimorfico()), ((TipoFuncao) tDir).getImagem());
		}
		return new TipoPolimorfico();
	}

	@Override
	public ExpBinaria clone() {
		return new ExpComposicao(esq.clone(), dir.clone());
	}
}
