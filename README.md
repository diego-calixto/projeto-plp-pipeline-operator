# Proposta de Projeto: Extensão da Linguagem Funcional 2 (LF2) com Operadores de Pipeline e Composição de Funções

## 1. Contexto

A Linguagem Funcional 2 (LF2), utilizada como linguagem de estudo na disciplina de Paradigmas de Linguagens de Programação (PLP), disponibiliza suporte a funções como valores de primeira classe e funções de alta ordem. Contudo, a avaliação de chamadas de funções encadeadas na LF2 exige o aninhamento sucessivo de parênteses (por exemplo, `g(f(h(x)))`), o que prejudica a legibilidade e contrapõe o fluxo natural de transformação de dados.

Para resolver essa limitação, este projeto propõe a extensão da LF2 por meio da incorporação nativa de dois operadores funcionais: o operador de *forward pipeline* (`|>`) e o operador de composição de funções (`>>`). A inclusão dessas primitivas reduz o aninhamento sintático e alinha a LF2 às convenções de linguagens funcionais modernas (como Elixir, F# e OCaml), estabelecendo um fluxo de processamento explícito da esquerda para a direita (*left-to-right data flow*).

## 2. Objetivos

### Objetivo Geral

O objetivo geral deste projeto é estender o compilador/interpretador da Linguagem Funcional 2 (LF2) — contemplando sua gramática, árvore de sintaxe abstrata (AST), sistema de verificação estática de tipos e semântica operacional —, de modo a prover suporte aos operadores de *pipeline* (`|>`) e composição funcional (`>>`).

### Objetivos Específicos

- Atualizar a especificação sintática formal em BNF da LF2, definindo a precedência gramatical e a associatividade à esquerda para os operadores `|>` e `>>`.
- Expandir a AST e a etapa de *parsing* da linguagem para representar adequadamente as novas construções sintáticas.
- Implementar regras formais de inferência e checagem estática de tipos para ambos os operadores no módulo de verificação de tipos (`checaTipo` / `getTipo`), garantindo o tratamento de exceções de incompatibilidade de tipos.
- Implementar a execução dos operadores no interpretador (`avaliar`), reescrevendo internamente `x |> f` como a chamada tradicional `f(x)` e `f >> g` como a criação de uma nova função `fn x => g(f(x))` (desaçucaramento sintático), reaproveitando a estrutura de execução já existente na LF2.
- Construir uma suíte de testes unitários e de integração cobrindo casos válidos de encadeamento com tipos primitivos e funções de alta ordem, além de cenários de erro de tipagem.

## 3. Escopo

### Incluído

- **Modificações Gramaticais e Parsing:** Suporte aos símbolos `|>` e `>>` na análise léxica e sintática.
- **Associatividade e Precedência:** Definição de associatividade à esquerda para ambos os operadores. A hierarquia de precedência garantirá que a composição de funções (`>>`) tenha maior precedência que o *pipeline* (`|>`), permitindo a interpretação direta da expressão `x |> f >> g` como `x |> (f >> g)`.
- **Verificação Estática de Tipos:**
    - Para `e1 |> e2`: Validação de que `e1` possui tipo $T_1$ e `e2` possui tipo $T_1 \rightarrow T_2$, resultando no tipo $T_2$.
    - Para `e1 >> e2`: Validação de que `e1` possui tipo $T_1 \rightarrow T_2$ e `e2` possui tipo $T_2 \rightarrow T_3$, resultando no tipo $T_1 \rightarrow T_3$.
    - Lançamento de exceção de tipagem (`TiposIncompativeisException`) ao detectar inconsistência entre os tipos de entrada e saída.
- **Semântica Operacional de Avaliação:**
    - Avaliação do operador `|>` via aplicação do valor resultante de `e1` à função `e2`.
    - Avaliação do operador `>>` gerando uma nova estrutura de `ValorFuncao` que representa $\lambda x . e_2(e_1(x))$, mantendo o ambiente de execução (*closure*) preservado.
- **Infraestrutura de Testes:** Bateria de testes automatizados para validação do pipeline de execução.

### Não Incluído

- Suporte a *placeholders* para aplicação parcial em funções com múltiplos argumentos (e.g., `x |> f(a, _, b)`).
- Avaliação preguiçosa (*lazy evaluation*).
- Adição de novos tipos de dados estruturados não previstos na LF2 original (como tuplas, registros ou monadas).
- Operadores de direção oposta, como *backward pipeline* (`<|`) ou composição reversa (`<<`).

## 4. Especificação Sintática (BNF)

A gramática formal da LF2 é estendida com os novos níveis de precedência. A estrutura abaixo reflete a hierarquia onde a aplicação tradicional de função e os operadores aritméticos/lógicos mantêm alta precedência, seguidos pelo operador de composição (`>>`) e, por fim, pelo operador de *pipeline* (`|>`), ambos associativos à esquerda.

# BNF

```
(* Regra raiz da linguagem *)
<Expressao>         ::= <ExpPipeline>

(* Operador de Pipeline (|>) - Menor precedência, associativo à esquerda *)
<ExpPipeline>       ::= <ExpPipeline> "|>" <ExpComposicao>
                      | <ExpComposicao>

(* Operador de Composição (>>) - Precedência intermediária, associativo à esquerda *)
<ExpComposicao>     ::= <ExpComposicao> ">>" <ExpLogicaDisjuncao>
                      | <ExpLogicaDisjuncao>

(* Expressões Lógicas, Relacionais e Aritméticas (LF2 Padrão) *)
<ExpLogicaDisjuncao>::= <ExpLogicaDisjuncao> "or" <ExpLogicaConjuncao>
                      | <ExpLogicaConjuncao>

<ExpLogicaConjuncao>::= <ExpLogicaConjuncao> "and" <ExpRelacional>
                      | <ExpRelacional>

<ExpRelacional>     ::= <ExpAditiva> "==" <ExpAditiva>
                      | <ExpAditiva> "<=" <ExpAditiva>
                      | <ExpAditiva>

<ExpAditiva>        ::= <ExpAditiva> "+" <ExpMultiplicativa>
                      | <ExpAditiva> "-" <ExpMultiplicativa>
                      | <ExpMultiplicativa>

<ExpMultiplicativa> ::= <ExpMultiplicativa> "*" <ExpAplicacao>
                      | <ExpMultiplicativa> "/" <ExpAplicacao>
                      | <ExpAplicacao>

(* Aplicação de Função e Termos Primários - Maior precedência *)
<ExpAplicacao>      ::= <ExpPrimaria> "(" <Expressao> ")"
                      | <ExpPrimaria>

<ExpPrimaria>       ::= <Id>
                      | <ValorPrimitivo>
                      | <ExpFuncao>
                      | "(" <Expressao> ")"

(* Definição de Abstração Funcional (LF2 Padrão) *)
<ExpFuncao>         ::= "fn" <Id> "=>" <Expressao>
```

### Propriedades da Gramática:

1. **Associatividade à Esquerda:** A recursão à esquerda nas regras `<ExpPipeline>` e `<ExpComposicao>` garante que expressões como `x |> f |> g` e `f >> g >> h` sejam agrupadas naturalmente como `((x |> f) |> g)` e `((f >> g) >> h)`, respectivamente.
2. **Precedência Relativa:** Como `<ExpPipeline>` deriva `<ExpComposicao>`, a composição `f >> g` é avaliada antes da aplicação via pipeline. Assim, `x |> f >> g` é interpretado sintaticamente como `x |> (f >> g)`.