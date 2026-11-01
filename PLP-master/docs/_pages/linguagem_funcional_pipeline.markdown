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
- O operador `>>` compõe duas funções: aplica primeiro `f` e, em seguida, aplica `g` ao resultado
- A composição funcional (`>>`) possui precedência gramatical maior que o pipeline (`|>`); assim, em `x |> f >> g`, o pipeline aplica ao valor `x` a composição de `f` com `g`
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
{% include code-indent.html %}| {% include code-link.html path="FuncionalPipeline/src/lfp/plp/functional1/expression/IfThenElse.java" %}

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

### <a href="{{ site.baseurl }}/ide/?language=FuncPipe&amp;code={{ 'let fun incrementa x = x + 1 in 5 |> incrementa' | url_encode }}">Pipeline com um argumento</a>

O valor à esquerda é passado como argumento para a função à direita:

```text
let fun incrementa x = x + 1 in 5 |> incrementa
```

Isso equivale à aplicação direta `incrementa(5)` e resulta em `6`.

### <a href="{{ site.baseurl }}/ide/?language=FuncPipe&amp;code={{ 'let fun soma x y = x + y in 2, 3 |> soma' | url_encode }}">Pipeline com múltiplos argumentos</a>

Os argumentos são escritos em sequência, separados por vírgulas e sem parênteses:

```text
let fun soma x y = x + y in 2, 3 |> soma
```

Isso equivale à aplicação direta `soma(2, 3)` e resulta em `5`. O pipeline aplica a função aos argumentos na ordem em que aparecem; a quantidade e os tipos precisam corresponder à assinatura da função.

### <a href="{{ site.baseurl }}/ide/?language=FuncPipe&amp;code={{ 'let fun incrementa x = x + 1, fun dobro x = x + x in 5 |> incrementa |> dobro' | url_encode }}">Pipeline encadeado</a>

Cada resultado é passado sequencialmente à próxima função:

```text
let fun incrementa x = x + 1, fun dobro x = x + x
in 5 |> incrementa |> dobro
```

O resultado é `12`, equivalente a `dobro(incrementa(5))`.

### <a href="{{ site.baseurl }}/ide/?language=FuncPipe&amp;code={{ 'let fun incrementa x = x + 1, fun dobro x = x + x in 5 |> incrementa >> dobro' | url_encode }}">Composição funcional</a>

`f >> g` cria uma função que primeiro aplica `f` e depois `g`. A composição implementada aceita funções unárias:

```text
let fun incrementa x = x + 1, fun dobro x = x + x
in 5 |> incrementa >> dobro
```
Como a composição tem precedência maior que o pipeline, essa expressão aplica a função composta a `5` e resulta em `12` (`dobro(incrementa(5))`).

### <a href="{{ site.baseurl }}/ide/?language=FuncPipe&amp;code={{ 'let fun tamanho s = length s, fun dobro n = n + n in "abc" |> tamanho >> dobro' | url_encode }}">Composição entre funções com tipos diferentes</a>

A composição exige que o tipo retornado pela primeira função seja compatível com o argumento da segunda. `tamanho` transforma uma string em inteiro; `dobro` recebe um inteiro e retorna outro. Assim, `"abc" |> tamanho >> dobro` resulta em `6`.

```text
let fun tamanho s = length s, fun dobro n = n + n
in "abc" |> tamanho >> dobro
```

### <a href="{{ site.baseurl }}/ide/?language=FuncPipe&amp;code={{ '5 |> fn x . x + x' | url_encode }}">Pipeline com função anônima</a>

O lado direito do pipeline não precisa ser um identificador declarado: também pode ser uma função criada com `fn`. Aqui, a função recebe `5` e retorna `10`.

```text
5 |> fn x . x + x
```

### <a href="{{ site.baseurl }}/ide/?language=FuncPipe&amp;code={{ 'let fun dobro x = x + x in 2 + 3 |> dobro' | url_encode }}">Precedência entre aritmética e pipeline</a>

As operações aritméticas são agrupadas antes do pipeline. Portanto, a expressão abaixo é interpretada como `(2 + 3) |> dobro`, e não como `2 + (3 |> dobro)`. O resultado é `10`.

```text
let fun dobro x = x + x in 2 + 3 |> dobro
```

### <a href="{{ site.baseurl }}/ide/?language=FuncPipe&amp;code={{ 'let fun incrementa x = x + 1, fun decrementa x = x - 1 in 5 |> if false then incrementa else decrementa' | url_encode }}">Escolha condicional da função</a>

Uma expressão `if` pode escolher qual função receberá o valor. Como a condição é falsa, a função escolhida é `decrementa` e o resultado é `4`. Os dois ramos precisam ser compatíveis como funções.

```text
let fun incrementa x = x + 1, fun decrementa x = x - 1
in 5 |> if false then incrementa else decrementa
```

### Formas inválidas
- `(2, 3) |> soma` é sintaticamente inválida: a lista de argumentos do pipeline não deve ser envolvida por parênteses.
- `2, 3 |> incrementa` é sintaticamente válida, mas falha na checagem de tipos porque `incrementa` recebe apenas um argumento.
- `5 |> 10` também é sintaticamente válida, mas falha na checagem de tipos porque o lado direito do pipeline precisa ser uma função.
- `let fun ehZero x = x == 0, fun incrementa x = x + 1 in ehZero >> incrementa` falha na checagem de tipos: `ehZero` retorna booleano, mas `incrementa` espera um inteiro.
- `let fun soma x y = x + y, fun dobro x = x + x in soma >> dobro` falha na checagem de tipos: a implementação da composição aceita somente funções unárias.


## Precedência e Associatividade da Implementação

1. **Associatividade à Esquerda:** Expressões como `x |> f |> g` e `f >> g >> h` são agrupadas como `((x |> f) |> g)` e `((f >> g) >> h)`, respectivamente.
2. **Precedência Relativa:** A composição `f >> g` tem precedência maior que o pipeline. Assim, `x |> f >> g` é interpretado como `x |> (f >> g)`.
