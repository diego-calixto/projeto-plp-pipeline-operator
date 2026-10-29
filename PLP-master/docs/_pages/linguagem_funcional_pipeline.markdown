---
layout: linguagem
permalink: /linguagens/funcional-pipeline
linguagem: Funcional Pipeline
short: FuncPipe
order: 5
---

- Estende a Linguagem Funcional 2 (LF2) com os operadores de *forward pipeline* (`|>`) e composição de funções (`>>`)
- O operador `|>` estabelece um fluxo de processamento explícito da esquerda para a direita (*left-to-right data flow*), reduzindo o aninhamento excessivo de parênteses em chamadas encadeadas
- Permite passar um ou múltiplos argumentos posicionais separados por vírgula para a função à direita do pipeline (`x |> f` ou `x, y |> f`)
- O operador `>>` cria a composição matemática de duas funções (`(f >> g)(x) = g(f(x))`), aplicando primeiro `f` e depois `g`
- A composição funcional (`>>`) possui precedência gramatical maior que o pipeline (`|>`), de modo que `x |> f >> g` é avaliado como `x |> (f >> g)`
- Ambos os operadores possuem associatividade à esquerda
- Inclui checagem estática de tipos com tratamento e disparo da exceção `TiposIncompativeisException`
- Preserva a semântica operacional e ambientes de execução (*closures*) da LF2


## Fontes

{% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/Programa.java" %} ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %}

Expressao ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Valor.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpUnaria.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpBinaria.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/expression/ExpDeclaracao.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Id.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/expression/Aplicacao.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional1/expression/IfThenElse.java" %}\
{% include code-indent.html %}| "(" {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %} ")"

Valor ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorConcreto.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/expression/ValorAbstrato.java" %}

ValorAbstrato ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/expression/ValorFuncao.java" %}

ValorConcreto ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorInteiro.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorBooleano.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorString.java" %}

ValorFuncao ::= "fn" ListId "." {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %}

ExpUnaria ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpMenos.java" content='"-" Expressao' %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpNot.java" content='"not" Expressao' %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpLength.java" content='"length" Expressao' %}

ExpBinaria ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpSoma.java" content='Expressao "+" Expressao' %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpSub.java" content='Expressao "-" Expressao' %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpAnd.java" content='Expressao "and" Expressao' %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpOr.java" content='Expressao "or" Expressao' %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpEquals.java" content='Expressao "==" Expressao' %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpConcat.java" content='Expressao "++" Expressao' %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/expression/ExpPipeline.java" content='Expressao "|>" Expressao' %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/expression/ExpPipeline.java" content='ListExp "|>" Expressao' %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/expression/ExpComposicao.java" content='Expressao ">>" Expressao' %}

ExpDeclaracao ::= "let" {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional1/declaration/DeclaracaoFuncional.java" %} "in" {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %}

DeclaracaoFuncional ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional1/declaration/DecVariavel.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/declaration/DecFuncao.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional1/declaration/DecComposta.java" %}

DecVariavel ::= "var" {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Id.java" %} "=" {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %}

DecFuncao ::= "fun" ListId "=" {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %}

DecComposta ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional1/declaration/DeclaracaoFuncional.java" %} "," {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional1/declaration/DeclaracaoFuncional.java" %}

ListId ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Id.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Id.java" %} ListId

Aplicacao ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %} "(" ListExp ")"

ListExp ::= {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %}\
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %} "," ListExp

IfThenElse ::= "if" {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %} "then" {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %} "else" {% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java" %}


## Classes auxiliares

{% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/expression/TiposIncompativeisException.java" %}\
{% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/util/PartialInstantiatorVisitor.java" %}\
{% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/memory/Ambiente.java" %}\
{% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/memory/AmbienteCompilacao.java" %}\
{% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/memory/AmbienteExecucao.java" %}\
{% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/memory/Contexto.java" %}\
{% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/memory/ContextoCompilacao.java" %}\
{% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/memory/ContextoExecucao.java" %}\
{% include code-link.html path="FuncionalPipeline/src/lfp/plp/expressions2/memory/StackHandler.java" %}


## Exemplos de Programas e Testes

{% include code-link.html path="Testes/TesteFuncionalPipeline.txt" %}\
{% include code-link.html path="FuncionalPipeline/test/lfp/plp/functional2/PipelineComposicaoTest.java" %}


## Parser

{% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional2/parser/Funcional2.jj" %}


## Exemplos de Sintaxe e Semântica

### Pipeline com um argumento
O valor à esquerda é passado como argumento para a função à direita:

```text
let fun incrementa x = x + 1 in 5 |> incrementa
```

Isso equivale à aplicação direta `incrementa(5)` e resulta em `6`.

### Pipeline com múltiplos argumentos
Os argumentos são escritos em sequência, separados por vírgulas e sem parênteses:

```text
let fun soma x y = x + y in 2, 3 |> soma
```

Isso equivale à aplicação direta `soma(2, 3)` e resulta em `5`. O pipeline aplica a função aos argumentos na ordem em que aparecem; a quantidade e os tipos precisam corresponder à assinatura da função.

### Pipeline encadeado
Cada resultado é passado sequencialmente à próxima função:

```text
let fun incrementa x = x + 1, fun dobro x = x + x
in 5 |> incrementa |> dobro
```

O resultado é `12`, equivalente a `dobro(incrementa(5))`.

### Composição funcional
`f >> g` cria uma função que primeiro aplica `f` e depois `g`. A composição implementada aceita funções unárias:

```text
let fun incrementa x = x + 1, fun dobro x = x + x
in 5 |> incrementa >> dobro
```

Como a composição tem precedência maior que o pipeline, essa expressão aplica a função composta a `5` e resulta em `12` (`dobro(incrementa(5))`).

### Formas inválidas
- `(2, 3) |> soma` é sintaticamente inválida: a lista de argumentos do pipeline não deve ser envolvida por parênteses.
- `2, 3 |> incrementa` é sintaticamente válida, mas falha na checagem de tipos porque `incrementa` recebe apenas um argumento.
- `5 |> 10` também é sintaticamente válida, mas falha na checagem de tipos porque o lado direito do pipeline precisa ser uma função.


## Precedência e Associatividade da Implementação

1. **Associatividade à Esquerda:** Expressões como `x |> f |> g` e `f >> g >> h` são agrupadas como `((x |> f) |> g)` e `((f >> g) >> h)`, respectivamente.
2. **Precedência Relativa:** A composição `f >> g` tem precedência maior que o pipeline. Assim, `x |> f >> g` é interpretado como `x |> (f >> g)`.
