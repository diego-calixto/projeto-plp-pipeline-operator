package lfp.plp.functional2.expression;

import lfp.plp.expressions1.util.Tipo;
import lfp.plp.expressions2.expression.Expressao;
import lfp.plp.expressions2.expression.Valor;
import lfp.plp.expressions2.memory.AmbienteCompilacao;
import lfp.plp.expressions2.memory.AmbienteExecucao;
import lfp.plp.expressions2.memory.VariavelJaDeclaradaException;
import lfp.plp.expressions2.memory.VariavelNaoDeclaradaException;

public class ExpComposicao implements Expressao {

	private Expressao esquerda;
	private Expressao direita;

	public ExpComposicao(Expressao esquerda, Expressao direita) {
		this.esquerda = esquerda;
		this.direita = direita;
	}

	public Expressao getEsquerda() {
		return esquerda;
	}

	public Expressao getDireita() {
		return direita;
	}

	@Override
	public Valor avaliar(AmbienteExecucao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		throw new UnsupportedOperationException(
				"Function composition evaluation is not implemented.");
	}

	@Override
	public boolean checaTipo(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		throw new UnsupportedOperationException(
				"Function composition type checking is not implemented.");
	}

	@Override
	public Tipo getTipo(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		throw new UnsupportedOperationException(
				"Function composition type inference is not implemented.");
	}

	@Override
	public Expressao reduzir(AmbienteExecucao ambiente) {
		esquerda = esquerda.reduzir(ambiente);
		direita = direita.reduzir(ambiente);
		return this;
	}

	@Override
	public ExpComposicao clone() {
		return new ExpComposicao(esquerda.clone(), direita.clone());
	}
}
