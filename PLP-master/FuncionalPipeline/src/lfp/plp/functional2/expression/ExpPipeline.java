package lfp.plp.functional2.expression;

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
 * Representa uma expressao de Forward Pipeline (esq |> dir) compativel com a BNF.
 * Passa o resultado da expressao da esquerda como argumento para a funcao da direita.
 */
public class ExpPipeline extends ExpBinaria {

	public ExpPipeline(Expressao esq, Expressao dir) {
		super(esq, dir, "|>");
	}

	public Expressao getDado() {
		return getEsq();
	}

	public Expressao getFuncao() {
		return getDir();
	}

	private Tipo getFuncType(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Tipo tipo = null;
		if (getDir() instanceof Id) {
			tipo = amb.get((Id) getDir());
		} else if (getDir() != null) {
			tipo = getDir().getTipo(amb);
		}
		return tipo;
	}

	@Override
	public Valor avaliar(AmbienteExecucao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Valor valDir = getDir().avaliar(amb);
		if (!(valDir instanceof ValorFuncao)) {
			throw new TiposIncompativeisException("Expressao a direita do operador |> deve ser funcional, mas foi: " + valDir);
		}

		Valor valEsq = getEsq().avaliar(amb);
		List<Expressao> args = Collections.singletonList((Expressao) valEsq);

		Aplicacao app = new Aplicacao(getDir(), args);
		return app.avaliar(amb);
	}

	@Override
	protected boolean checaTipoElementoTerminal(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Tipo tipoDir = getFuncType(amb);
		if (tipoDir == null) {
			return false;
		}

		if (tipoDir instanceof TipoFuncao) {
			TipoFuncao tf = (TipoFuncao) tipoDir;
			return tf.checaTipo(amb, Collections.singletonList(getEsq()));
		}

		if (tipoDir instanceof TipoPolimorfico) {
			return true;
		}

		return false;
	}

	@Override
	public Tipo getTipo(AmbienteCompilacao amb)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		Tipo tipoDir = getFuncType(amb);

		if (tipoDir instanceof TipoFuncao) {
			TipoFuncao tf = (TipoFuncao) tipoDir;
			return tf.getTipo(amb, Collections.singletonList(getEsq()));
		}

		if (tipoDir instanceof TipoPolimorfico) {
			return new TipoPolimorfico();
		}

		return null;
	}

	@Override
	public ExpBinaria clone() {
		return new ExpPipeline(esq.clone(), dir.clone());
	}
}
