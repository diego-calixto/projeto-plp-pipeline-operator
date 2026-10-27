# Pipeline e Composição na Linguagem Funcional 2

## Disciplina

Paradigmas de Linguagens de Programação (PLP) — CIn/UFPE.

## Professor

Prof. Dr. Augusto Cezar Alves Sampaio.

## Equipe

- Diego Henrique Vilaça Calixto
- Júlia Arnaud de Melo Fragoso

## Contextualização

A Linguagem Funcional 2 (LF2) permite definir funções e utilizá-las como valores, mas chamadas encadeadas podem exigir vários níveis de parênteses, como `g(f(h(x)))`. Este projeto estende a LF2 com os operadores de pipeline (`|>`) e composição de funções (`>>`) para expressar transformações de dados da esquerda para a direita.

O projeto parte da implementação de LF2 em Java disponível em [`PLP-master/FuncionalPipeline`](PLP-master/FuncionalPipeline) e mantém as construções existentes da linguagem, acrescentando parsing, representação na AST, checagem de tipos e avaliação para os novos operadores.

## Funcionalidades

- **Pipeline (`|>`)**: aplica uma função aos argumentos escritos à esquerda. Aceita um ou vários argumentos, sem exigir parênteses na lista do pipeline.
- **Composição (`>>`)**: compõe duas funções da esquerda para a direita; `f >> g` representa uma função que aplica primeiro `f` e depois `g`.
- **Encadeamento**: ambos os operadores são associativos à esquerda.
- **Precedência**: a aplicação e as operações aritméticas, lógicas e de comparação têm precedência maior que a composição; a composição tem precedência maior que o pipeline.
- **Checagem estática**: valida se o lado direito do pipeline é uma função e se seus parâmetros correspondem em quantidade e tipo aos argumentos fornecidos. A composição também verifica a compatibilidade das funções.
- **Compatibilidade com LF2**: preserva construções como declarações `let`, funções nomeadas, funções anônimas, chamadas tradicionais, condicionais, expressões aritméticas e booleanas e operações com strings.

## Sintaxe dos operadores

```text
Expressao       ::= ExpPipeline
ExpPipeline     ::= ExpPipeline "|>" ExpComposicao
                  | ArgumentosPipeline "|>" ExpComposicao
                  | ExpComposicao
ArgumentosPipeline ::= ExpComposicao "," ExpComposicao
                     | ArgumentosPipeline "," ExpComposicao
ExpComposicao   ::= ExpComposicao ">>" ExpBinaria
                  | ExpBinaria
```

As regras completas da linguagem estão em [`BNF.md`](BNF.md), [`BNF_simplificada.md`](BNF_simplificada.md) e [`PLP-master/FuncionalPipeline/src/lfp/plp/functional2/parser/Funcional2.jj`](PLP-master/FuncionalPipeline/src/lfp/plp/functional2/parser/Funcional2.jj).

### Pipeline com um argumento

```text
let fun incrementa x = x + 1 in
5 |> incrementa
```

O resultado é `6`, equivalente a `incrementa(5)`.

### Pipeline com vários argumentos

```text
let fun soma x y = x + y in
2, 3 |> soma
```

O resultado é `5`, equivalente à chamada tradicional `soma(2, 3)`. A lista de argumentos do pipeline é separada por vírgulas, sem envolver a lista em parênteses: `(2, 3) |> soma` não é a sintaxe aceita.

### Encadeamento e composição

```text
let fun incrementa x = x + 1,
    fun dobro x = x + x
in 5 |> incrementa >> dobro
```

Como `>>` tem precedência maior que `|>`, a expressão aplica a composição `incrementa >> dobro` ao valor `5`: primeiro incrementa, depois dobra. O resultado é `12`.

O encadeamento `5 |> incrementa |> dobro` também é válido e, por associatividade à esquerda, equivale a `(5 |> incrementa) |> dobro`.

## Execução

### Requisitos

- Java 8 ou superior.
- Apache Maven.

### Executar os testes

Na raiz do repositório:

```bash
mvn -f PLP-master/FuncionalPipeline/pom.xml test
```

### Executar o interpretador

Entre no diretório do módulo e execute o programa de exemplo padrão (`input`):

```bash
cd PLP-master/FuncionalPipeline
mvn clean generate-sources compile exec:java
```

O parser lê o arquivo `input`, verifica os tipos e avalia a expressão, imprimindo o resultado no terminal. Para usar outro arquivo de entrada, informe seu caminho ao executar a classe principal `lfp.plp.functional2.parser.Func2Parser`.

Por exemplo, ainda dentro do diretório do módulo, para executar `input2`:

```bash
mvn exec:java -Dexec.args="input2"
```

## Estrutura do módulo

| Caminho | Responsabilidade |
|---|---|
| `PLP-master/FuncionalPipeline/src/lfp/plp/functional2/parser/Funcional2.jj` | Gramática JavaCC e construção da AST durante o parsing |
| `PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpPipeline.java` | AST, checagem de tipos e avaliação do pipeline |
| `PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/ExpComposicao.java` | AST, checagem de tipos e avaliação da composição |
| `PLP-master/FuncionalPipeline/src/lfp/plp/functional2/expression/Aplicacao.java` | Aplicação de funções e validação de aridade |
| `PLP-master/FuncionalPipeline/test/lfp/plp/functional2/` | Testes de integração dos operadores e da linguagem |
| `PLP-master/FuncionalPipeline/input` | Programa de exemplo usado por padrão pelo interpretador |

## Limitações

- O pipeline aceita múltiplos argumentos posicionais; não há suporte a placeholders nem aplicação parcial.
- A composição `>>` é implementada para funções unárias.
- Não são introduzidos novos tipos de dados nem avaliação preguiçosa.
