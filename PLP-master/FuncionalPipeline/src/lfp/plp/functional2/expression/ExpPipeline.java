package lfp.plp.functional2.expression;

import java.util.ArrayList;
import java.util.List;

import lfp.plp.expressions1.util.Tipo;
import lfp.plp.expressions2.expression.Expressao;
import lfp.plp.expressions2.expression.Valor;
import lfp.plp.expressions2.memory.AmbienteCompilacao;
import lfp.plp.expressions2.memory.AmbienteExecucao;
import lfp.plp.expressions2.memory.VariavelJaDeclaradaException;
import lfp.plp.expressions2.memory.VariavelNaoDeclaradaException;

public class ExpPipeline implements Expressao {

	private Expressao esquerda;
	private List<Expressao> argumentosEsquerda;
	private Expressao direita;

	public ExpPipeline(Expressao esquerda, Expressao direita) {
		this.esquerda = esquerda;
		this.direita = direita;
	}

	public ExpPipeline(List<? extends Expressao> argumentosEsquerda,
			Expressao direita) {
		this.argumentosEsquerda = new ArrayList<Expressao>(argumentosEsquerda);
		this.direita = direita;
	}

	public Expressao getEsquerda() {
		return esquerda;
	}

	public List<Expressao> getArgumentosEsquerda() {
		return argumentosEsquerda;
	}

	public Expressao getDireita() {
		return direita;
	}

	@Override
	public Valor avaliar(AmbienteExecucao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		throw new UnsupportedOperationException(
				"Pipeline evaluation is not implemented.");
	}

	@Override
	public boolean checaTipo(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		throw new UnsupportedOperationException(
				"Pipeline type checking is not implemented.");
	}

	@Override
	public Tipo getTipo(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		throw new UnsupportedOperationException(
				"Pipeline type inference is not implemented.");
	}

	@Override
	public Expressao reduzir(AmbienteExecucao ambiente) {
		if (esquerda != null) {
			esquerda = esquerda.reduzir(ambiente);
		}
		if (argumentosEsquerda != null) {
			List<Expressao> reduzidos =
					new ArrayList<Expressao>(argumentosEsquerda.size());
			for (Expressao argumento : argumentosEsquerda) {
				reduzidos.add(argumento.reduzir(ambiente));
			}
			argumentosEsquerda = reduzidos;
		}
		direita = direita.reduzir(ambiente);
		return this;
	}

	@Override
	public ExpPipeline clone() {
		if (argumentosEsquerda != null) {
			List<Expressao> copiados =
					new ArrayList<Expressao>(argumentosEsquerda.size());
			for (Expressao argumento : argumentosEsquerda) {
				copiados.add(argumento.clone());
			}
			return new ExpPipeline(copiados, direita.clone());
		}
		return new ExpPipeline(esquerda.clone(), direita.clone());
	}
}
