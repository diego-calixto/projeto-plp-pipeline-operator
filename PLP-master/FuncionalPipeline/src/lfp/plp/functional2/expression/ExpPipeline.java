package lfp.plp.functional2.expression;

import static lfp.plp.expressions1.util.ToStringProvider.listToString;

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
 * Representa uma expressao de Forward Pipeline (esq |> dir) compativel com a BNF.
 * Passa o resultado da expressao da esquerda como argumento para a funcao da direita.
 */
public class ExpPipeline extends ExpBinaria {

	private List<Expressao> argumentosEsquerda;

	public ExpPipeline(Expressao esq, Expressao dir) {
		super(esq, dir, "|>");
	}

	public ExpPipeline(List<? extends Expressao> argumentos, Expressao dir) {
		this(primeiroArgumento(argumentos), dir);
		this.argumentosEsquerda = new ArrayList<Expressao>(argumentos);
	}

	public Expressao getDado() {
		return getEsq();
	}

	public Expressao getFuncao() {
		return getDir();
	}

	public List<Expressao> getArgumentosEsquerda() {
		return argumentosEsquerda;
	}

	private static Expressao primeiroArgumento(
			List<? extends Expressao> argumentos) {
		if (argumentos == null || argumentos.isEmpty()) {
			throw new IllegalArgumentException(
					"Um pipeline deve receber pelo menos um argumento.");
		}
		return argumentos.get(0);
	}

	private List<? extends Expressao> getArgumentosAplicacao() {
		return argumentosEsquerda == null ? Collections.singletonList(getEsq()) : argumentosEsquerda;
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

		Aplicacao app = new Aplicacao(getDir(), getArgumentosAplicacao());
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
			return tf.checaTipo(amb, getArgumentosAplicacao());
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
			if (tf.getDominio().size() != getArgumentosAplicacao().size()) {
				return null;
			}
			return tf.getTipo(amb, getArgumentosAplicacao());
		}

		if (tipoDir instanceof TipoPolimorfico) {
			return new TipoPolimorfico();
		}

		return null;
	}

	@Override
	public ExpBinaria clone() {
		if (argumentosEsquerda == null) {
			return new ExpPipeline(esq.clone(), dir.clone());
		}

		List<Expressao> argumentos = new ArrayList<Expressao>(
				argumentosEsquerda.size());
		for (Expressao argumento : argumentosEsquerda) {
			argumentos.add(argumento.clone());
		}
		return new ExpPipeline(argumentos, dir.clone());
	}

	@Override
	public Expressao reduzir(AmbienteExecucao ambiente) {
		if (argumentosEsquerda == null) {
			this.esq = this.esq.reduzir(ambiente);
			this.dir = this.dir.reduzir(ambiente);
		} else {
			List<Expressao> argumentos = new ArrayList<Expressao>(
					argumentosEsquerda.size());
			for (Expressao argumento : argumentosEsquerda) {
				argumentos.add(argumento.reduzir(ambiente));
			}
			argumentosEsquerda = argumentos;
			this.esq = argumentos.get(0);
			this.dir = this.dir.reduzir(ambiente);
		}
		return this;
	}

	@Override
	public String toString() {
		String dados = argumentosEsquerda == null
				? String.valueOf(esq)
				: "(" + listToString(argumentosEsquerda, ",") + ")";
		return String.format("%s |> %s", dados, dir);
	}
}
