# Proposta de Projeto: Extensão da Linguagem Funcional 2 (LF2) com Operadores de Pipeline e Composição de Funções

## Disciplina

Paradigmas de Linguagens de Programação (PLP) — CIn/UFPE.

## Professor

Prof. Dr. Augusto Cezar Alves Sampaio.

## Alunos:
* Diego Henrique Vilaça Calixto
* Júlia Arnaud de Melo Fragoso

## 1. Contexto

A Linguagem Funcional 2 (LF2), utilizada como linguagem de estudo na disciplina de Paradigmas de Linguagens de Programação (PLP), disponibiliza suporte a funções como valores de primeira classe e funções de alta ordem. Contudo, a avaliação de chamadas de funções encadeadas na LF2 exige o aninhamento sucessivo de parênteses (por exemplo, `g(f(h(x)))`), o que prejudica a legibilidade e contrapõe o fluxo natural de transformação de dados.

Para resolver essa limitação, este projeto propõe a extensão da LF2 por meio da incorporação nativa de dois operadores funcionais: o operador de *forward pipeline* (`|>`) e o operador de composição de funções (`>>`). A inclusão dessas primitivas reduz o aninhamento sintático e alinha a LF2 às convenções de linguagens funcionais modernas (como Elixir, F# e OCaml), estabelecendo um fluxo de processamento explícito da esquerda para a direita (*left-to-right data flow*).

O projeto parte da implementação de LF2 em Java disponível em [`PLP-master/FuncionalPipeline`](PLP-master/FuncionalPipeline) e mantém as construções existentes da linguagem, acrescentando parsing, representação na AST, checagem de tipos e avaliação para os novos operadores. A gramática também permite passar um ou vários argumentos posicionais à função no lado direito do pipeline.

## 2. Objetivos

### Objetivo Geral

O objetivo geral deste projeto é estender o compilador/interpretador da Linguagem Funcional 2 (LF2), contemplando sua gramática, árvore de sintaxe abstrata (AST), sistema de verificação estática de tipos e semântica operacional, de modo a prover suporte aos operadores de *pipeline* (`|>`) e composição funcional (`>>`).

### Objetivos Específicos

- Atualizar a especificação sintática formal em BNF da LF2, definindo a precedência gramatical e a associatividade à esquerda para os operadores `|>` e `>>`.
- Expandir a AST e a etapa de *parsing* da linguagem para representar adequadamente as novas construções sintáticas.
- Implementar regras formais de inferência e checagem estática de tipos para ambos os operadores no módulo de verificação de tipos (`checaTipo` / `getTipo`), garantindo o tratamento de exceções de incompatibilidade de tipos.
- Implementar a execução dos operadores no interpretador (`avaliar`), tratando `x |> f` como a aplicação de `f` a `x` e `x, y |> f` como a aplicação de `f` a `x` e `y`; `f >> g` cria uma função que aplica primeiro `f` e depois `g`, reaproveitando a estrutura de execução já existente na LF2.
- Construir uma suíte de testes unitários e de integração cobrindo casos válidos de encadeamento com tipos primitivos e funções de alta ordem, além de cenários de erro de tipagem.

## 3. Escopo

O escopo deste projeto abrange a inclusão dos operadores de pipeline (|>) e composição de funções (>>) na análise léxica, sintática e semântica da linguagem, garantindo associatividade à esquerda, suporte a múltiplos argumentos posicionais e maior precedência para a composição, o que permite a correta interpretação da expressão x |> f >> g como x |> (f >> g). O trabalho contempla a verificação estática de tipos com o disparo da exceção TiposIncompativeisException, a implementação da semântica operacional com preservação do ambiente de execução (closure), e a criação de uma bateria de testes automatizados; em contrapartida, ficam expressamente fora do escopo o suporte a placeholders, a aplicação parcial, a avaliação preguiçosa (lazy evaluation), novos tipos de dados estruturados e operadores em direção oposta (<| e <<).

## 4. Especificação Sintática (BNF)

A BNF abaixo é uma versão simplificada, apresentada para fins didáticos e de demonstração, com links para as classes Java correspondentes. Por ser um resumo, ela não demonstra detalhes importantes da implementação, como a precedência e a associatividade dos operadores nem a forma específica da lista de argumentos no pipeline. A especificação detalhada desses aspectos está em [`BNF.md`](BNF.md).

### BNF simplificada

<pre>
<a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/Programa.java">Programa</a> ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a> ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Valor.java">Valor</a>
            | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpUnaria.java">ExpUnaria</a>
            | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpBinaria.java">ExpBinaria</a>
            | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpDeclaracao.java">ExpDeclaracao</a>
            | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Id.java">Id</a>
            | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/Aplicacao.java">Aplicacao</a>
            | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional1/expression/IfThenElse.java">IfThenElse</a>
            | "(" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a> ")"

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Valor.java">Valor</a> ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorConcreto.java">ValorConcreto</a>
        | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ValorAbstrato.java">ValorAbstrato</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ValorAbstrato.java">ValorAbstrato</a> ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ValorFuncao.java">ValorFuncao</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorConcreto.java">ValorConcreto</a> ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorInteiro.java">ValorInteiro</a>
                | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorBooleano.java">ValorBooleano</a>
                | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorString.java">ValorString</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ValorFuncao.java">ValorFuncao</a> ::= "fn" ListId "." <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpUnaria.java">ExpUnaria</a> ::= "-" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a>
            | "not" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a>
            | "length" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpBinaria.java">ExpBinaria</a> ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpSoma.java">Expressao "+" Expressao</a>
             | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpSub.java">Expressao "-" Expressao</a>
             | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpAnd.java">Expressao "and" Expressao</a>
             | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpOr.java">Expressao "or" Expressao</a>
             | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpEquals.java">Expressao "==" Expressao</a>
             | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpConcat.java">Expressao "++" Expressao</a>
             | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpPipeline.java">Expressao "|>" Expressao</a>
             | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpPipeline.java">ListExp "|>" Expressao</a>
             | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpComposicao.java">Expressao ">>" Expressao</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpDeclaracao.java">ExpDeclaracao</a> ::= "let" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DeclaracaoFuncional.java">DeclaracaoFuncional</a> "in" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DeclaracaoFuncional.java">DeclaracaoFuncional</a> ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DecVariavel.java">DecVariavel</a>
                      | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/declaration/DecFuncao.java">DecFuncao</a>
                      | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DecComposta.java">DecComposta</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DecVariavel.java">DecVariavel</a> ::= "var" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Id.java">Id</a> "=" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/declaration/DecFuncao.java">DecFuncao</a> ::= "fun" ListId "=" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a>

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DecComposta.java">DecComposta</a> ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DeclaracaoFuncional.java">DeclaracaoFuncional</a> "," <a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DeclaracaoFuncional.java">DeclaracaoFuncional</a>

ListId ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Id.java">Id</a>
         | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Id.java">Id</a> ListId

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/Aplicacao.java">Aplicacao</a> ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a> "(" ListExp ")"

ListExp ::= <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a>
          | <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a> "," ListExp

<a href="PLP-master/FuncionalPipeline/src/lfp/plp/functional1/expression/IfThenElse.java">IfThenElse</a> ::= "if" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a> "then" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a> "else" <a href="PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java">Expressao</a>
</pre>

### Exemplos de sintaxe e semântica

**Pipeline com um argumento:** o valor à esquerda é passado como argumento para a função à direita.

```text
let fun incrementa x = x + 1 in 5 |> incrementa
```

Isso equivale à aplicação direta `incrementa(5)` e resulta em `6`.

**Pipeline com múltiplos argumentos:** os argumentos são escritos em sequência, separados por vírgulas e sem parênteses.

```text
let fun soma x y = x + y in 2, 3 |> soma
```

Isso equivale à aplicação direta `soma(2, 3)` e resulta em `5`. O pipeline aplica a função aos argumentos na ordem em que aparecem; a quantidade e os tipos precisam corresponder à assinatura da função.

**Pipeline encadeado:** cada resultado é passado à próxima função.

```text
let fun incrementa x = x + 1, fun dobro x = x + x
in 5 |> incrementa |> dobro
```

O resultado é `12`, equivalente a `dobro(incrementa(5))`.

**Composição funcional:** `f >> g` cria uma função que primeiro aplica `f` e depois `g`. A composição implementada aceita funções unárias.

```text
let fun incrementa x = x + 1, fun dobro x = x + x
in 5 |> incrementa >> dobro
```

Como a composição tem precedência maior que o pipeline, essa expressão aplica a função composta a `5` e resulta em `12` (`dobro(incrementa(5))`).

**Formas inválidas:**

- `(2, 3) |> soma` é sintaticamente inválida: a lista de argumentos do pipeline não deve ser envolvida por parênteses.
- `2, 3 |> incrementa` é sintaticamente válida, mas falha na checagem de tipos porque `incrementa` recebe apenas um argumento.
- `5 |> 10` também é sintaticamente válida, mas falha na checagem de tipos porque o lado direito do pipeline precisa ser uma função.

### Mapeamento dos Componentes Sintáticos para as Classes Java

| Construto / Regra BNF | Classe Java | Pacote | Descrição |
| :--- | :--- | :--- | :--- |
| `Programa` | [`Programa`](PLP-master/FuncionalPipeline/src/lfp/plp/functional2/Programa.java) | `lfp.plp.functional2` | Ponto de entrada do programa que encapsula a expressão raiz |
| `Expressao` | [`Expressao`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Expressao.java) | `lfp.plp.expressions2.expression` | Interface base para todas as expressões da linguagem |
| `Valor` | [`Valor`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Valor.java) | `lfp.plp.expressions2.expression` | Interface que agrupa valores concretos e abstratos |
| `ValorConcreto` | [`ValorConcreto`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorConcreto.java) | `lfp.plp.expressions2.expression` | Superclasse abstrata para valores primitivos |
| `ValorAbstrato` | [`ValorAbstrato`](PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ValorAbstrato.java) | `lfp.plp.functional2.expression` | Interface para valores de alta ordem |
| `ValorFuncao` | [`ValorFuncao`](PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ValorFuncao.java) | `lfp.plp.functional2.expression` | Representação de funções como valor (`fn ... . ...`) |
| `ValorInteiro` | [`ValorInteiro`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorInteiro.java) | `lfp.plp.expressions2.expression` | Encapsula literais inteiros |
| `ValorBooleano` | [`ValorBooleano`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorBooleano.java) | `lfp.plp.expressions2.expression` | Encapsula literais booleanos (`true`, `false`) |
| `ValorString` | [`ValorString`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ValorString.java) | `lfp.plp.expressions2.expression` | Encapsula literais string |
| `ExpUnaria` | [`ExpUnaria`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpUnaria.java) | `lfp.plp.expressions2.expression` | Superclasse para expressões unárias |
| `"-" Expressao` | [`ExpMenos`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpMenos.java) | `lfp.plp.expressions2.expression` | Expressão de menos unário |
| `"not" Expressao` | [`ExpNot`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpNot.java) | `lfp.plp.expressions2.expression` | Expressão de negação lógica |
| `"length" Expressao` | [`ExpLength`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpLength.java) | `lfp.plp.expressions2.expression` | Expressão de tamanho de string |
| `ExpBinaria` | [`ExpBinaria`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpBinaria.java) | `lfp.plp.expressions2.expression` | Superclasse para expressões binárias |
| `Expressao "+" Expressao` | [`ExpSoma`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpSoma.java) | `lfp.plp.expressions2.expression` | Operação de adição |
| `Expressao "-" Expressao` | [`ExpSub`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpSub.java) | `lfp.plp.expressions2.expression` | Operação de subtração |
| `Expressao "and" Expressao` | [`ExpAnd`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpAnd.java) | `lfp.plp.expressions2.expression` | Conjunção lógica |
| `Expressao "or" Expressao` | [`ExpOr`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpOr.java) | `lfp.plp.expressions2.expression` | Disjunção lógica |
| `Expressao "==" Expressao` | [`ExpEquals`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpEquals.java) | `lfp.plp.expressions2.expression` | Comparação de igualdade |
| `Expressao "++" Expressao` | [`ExpConcat`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/ExpConcat.java) | `lfp.plp.expressions2.expression` | Concatenação de strings |
| `Expressao "\|>" Expressao` | [`ExpPipeline`](PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpPipeline.java) | `lfp.plp.functional2.expression` | Forward pipeline com um argumento |
| `ListExp "\|>" Expressao` | [`ExpPipeline`](PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpPipeline.java) | `lfp.plp.functional2.expression` | Forward pipeline com múltiplos argumentos posicionais separados por vírgulas, sem parênteses |
| `Expressao ">>" Expressao` | [`ExpComposicao`](PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpComposicao.java) | `lfp.plp.functional2.expression` | Composição de funções unárias |
| `ExpDeclaracao` | [`ExpDeclaracao`](PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpDeclaracao.java) | `lfp.plp.functional2.expression` | Expressão de declaração local (`let ... in ...`) |
| `DeclaracaoFuncional` | [`DeclaracaoFuncional`](PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DeclaracaoFuncional.java) | `lfp.plp.functional1.declaration` | Interface para declarações funcionais |
| `DecVariavel` | [`DecVariavel`](PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DecVariavel.java) | `lfp.plp.functional1.declaration` | Declaração de variável (`var id = exp`) |
| `DecFuncao` | [`DecFuncao`](PLP-master/FuncionalPipeline/src/lfp/plp/functional2/declaration/DecFuncao.java) | `lfp.plp.functional2.declaration` | Declaração de função (`fun id params = exp`) |
| `DecComposta` | [`DecComposta`](PLP-master/FuncionalPipeline/src/lfp/plp/functional1/declaration/DecComposta.java) | `lfp.plp.functional1.declaration` | Declaração composta separada por vírgula |
| `Id` | [`Id`](PLP-master/FuncionalPipeline/src/lfp/plp/expressions2/expression/Id.java) | `lfp.plp.expressions2.expression` | Identificador na linguagem |
| `Aplicacao` | [`Aplicacao`](PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/Aplicacao.java) | `lfp.plp.functional2.expression` | Aplicação/chamada de função |
| `IfThenElse` | [`IfThenElse`](PLP-master/FuncionalPipeline/src/lfp/plp/functional1/expression/IfThenElse.java) | `lfp.plp.functional1.expression` | Expressão condicional if-then-else |

### Precedência e associatividade da implementação

1. **Associatividade à Esquerda:** Expressões como `x |> f |> g` e `f >> g >> h` são agrupadas como `((x |> f) |> g)` e `((f >> g) >> h)`, respectivamente.
2. **Precedência Relativa:** A composição `f >> g` tem precedência maior que o pipeline. Assim, `x |> f >> g` é interpretado como `x |> (f >> g)`. A especificação gramatical detalhada está em [`BNF.md`](BNF.md); a BNF simplificada acima não representa essas propriedades.