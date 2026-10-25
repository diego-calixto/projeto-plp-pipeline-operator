package lfp.plp.functional2.parser;

import java.io.StringReader;
import java.util.List;

import lfp.plp.expressions2.expression.Expressao;
import lfp.plp.expressions2.expression.Id;
import lfp.plp.expressions2.expression.ExpSoma;
import lfp.plp.functional2.Programa;
import lfp.plp.functional2.expression.ExpComposicao;
import lfp.plp.functional2.expression.ExpPipeline;

public final class Funcional2SyntaxTest {

	private static final Func2Parser PARSER =
			new Func2Parser(new StringReader(""));

	public static void main(String[] args) throws Exception {
		assertPipeline("a |> f", "a", "f");
		ExpPipeline chained = (ExpPipeline) parse("a |> f |> g");
		assertEquals("g", ((Id) chained.getDir()).getIdName());
		assertPipeline(chained.getEsq(), "a", "f");

		ExpPipeline composedChain =
				(ExpPipeline) parse("a |> f >> g >> h");
		ExpComposicao outerComposition =
				(ExpComposicao) composedChain.getDir();
		assertComposition(outerComposition.getEsq(), "f", "g");
		assertEquals("h", ((Id) outerComposition.getDir()).getIdName());

		ExpPipeline composedPipeline =
				(ExpPipeline) parse("a |> f >> g");
		assertEquals("a", ((Id) composedPipeline.getEsq()).getIdName());
		assertComposition(composedPipeline.getDir(), "f", "g");

		assertPipelineArguments("a, b |> f", "a", "b");
		assertPipelineArguments("a, b, c |> f", "a", "b", "c");
		ExpPipeline groupedArgument = (ExpPipeline) parse("(a) |> f");
		assertEquals("a", ((Id) groupedArgument.getEsq()).getIdName());
		ExpPipeline groupedExpression =
				(ExpPipeline) parse("(a + b) |> f");
		if (!(groupedExpression.getEsq() instanceof ExpSoma)
				|| groupedExpression.getArgumentosEsquerda() != null) {
			throw new AssertionError(
					"Grouped expression was mistaken for pipeline arguments.");
		}
		ExpPipeline argumentsChained =
				(ExpPipeline) parse("a, b |> f |> g");
		assertEquals("g", ((Id) argumentsChained.getDir()).getIdName());
		assertPipelineArguments(argumentsChained.getEsq(), "a", "b");

		ExpPipeline argumentsComposition =
				(ExpPipeline) parse("a, b |> f >> g");
		assertEquals(2, argumentsComposition.getArgumentosEsquerda().size());
		assertComposition(argumentsComposition.getDir(), "f", "g");

		parse("a + b");
		parse("f(a, b)");
		parse("fn x . x");
		parse("let var x = 1 in x");
		parse("if true then 1 else 2");
		assertRejected("(a, b) |> f");
		System.out.println("Funcional2 syntax tests passed.");
	}

	private static void assertPipeline(String source, String left, String right)
			throws Exception {
		assertPipeline(parse(source), left, right);
	}

	private static void assertPipeline(Expressao expression, String left,
			String right) {
		ExpPipeline pipeline = (ExpPipeline) expression;
		assertEquals(left, ((Id) pipeline.getEsq()).getIdName());
		assertEquals(right, ((Id) pipeline.getDir()).getIdName());
	}

	private static void assertPipelineArguments(String source, String... names)
			throws Exception {
		assertPipelineArguments(parse(source), names);
	}

	private static void assertPipelineArguments(Expressao expression,
			String... names) {
		ExpPipeline pipeline = (ExpPipeline) expression;
		List<Expressao> args = pipeline.getArgumentosEsquerda();
		assertEquals(names.length, args.size());
		for (int i = 0; i < names.length; i++) {
			assertEquals(names[i], ((Id) args.get(i)).getIdName());
		}
		assertEquals("f", ((Id) pipeline.getDir()).getIdName());
	}

	private static void assertComposition(Expressao expression, String left,
			String right) {
		ExpComposicao composition = (ExpComposicao) expression;
		assertEquals(left, ((Id) composition.getEsq()).getIdName());
		assertEquals(right, ((Id) composition.getDir()).getIdName());
	}

	private static Expressao parse(String source) throws Exception {
		PARSER.ReInit(new StringReader(source));
		Programa program = PARSER.Input();
		return program.getExpressao();
	}

	private static void assertRejected(String source) throws Exception {
		try {
			parse(source);
			throw new AssertionError("Expected syntax rejection: " + source);
		} catch (ParseException expected) {
			// Parenthesized comma expressions are not pipeline argument lists.
		}
	}

	private static void assertEquals(Object expected, Object actual) {
		if (!expected.equals(actual)) {
			throw new AssertionError(
					"Expected <" + expected + "> but was <" + actual + ">");
		}
	}
}
