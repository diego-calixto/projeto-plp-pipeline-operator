# Especificação Técnica (Spec): Extensão da Linguagem Funcional 2 (LF2) com Operadores de Pipeline (`|>`) e Composição (`>>`)

- **Projeto:** FuncionalPipeline (LF2 + Pipeline + Composição)
- **Disciplina:** Paradigmas de Linguagens de Programação (PLP) - CIn / UFPE
- **Módulo Alvo:** `PLP-master/FuncionalPipeline`
- **Referência Formal:** `BNF.md`

---

## 1. Visão Geral e Motivação

Na Linguagem Funcional 2 (LF2), funções são valores de primeira classe e funções de alta ordem são suportadas. Todavia, a composição e encadeamento de transformações exigem aninhamento excessivo de parênteses, por exemplo:

```text
h(g(f(x)))
```

Essa sintaxe compromete a legibilidade e inverte a ordem natural de leitura dos dados. Para solucionar isso, esta especificação detalha a introdução de dois novos operadores funcionais:

1. **Forward Pipeline (`|>`)**: canaliza o resultado da expressão à esquerda como argumento da função à direita (`x |> f` equivale a `f(x)`).
2. **Composição de Funções (`>>`)**: compõe duas funções da esquerda para a direita (`f >> g` equivale a $\lambda x . g(f(x))$).

Ambos os operadores possuem **associatividade à esquerda**, estabelecendo um fluxo de dados intuitivo (*left-to-right data flow*).

---

## 2. Especificação Gramatical Formal

### 2.1. BNF Original (com recursão à esquerda) conforme `BNF.md`

```bnf
Programa ::= Expressao

Expressao ::= ExpPipeline

ExpPipeline ::= ExpPipeline "|>" ExpComposicao
              | ExpComposicao

ExpComposicao ::= ExpComposicao ">>" ExpBinaria
                | ExpBinaria

ExpBinaria ::= ExpBinaria "+" ExpUnaria
             | ExpBinaria "-" ExpUnaria
             | ExpBinaria "and" ExpUnaria
             | ExpBinaria "or" ExpUnaria
             | ExpBinaria "==" ExpUnaria
             | ExpBinaria "++" ExpUnaria
             | ExpUnaria

ExpUnaria ::= "-" ExpUnaria
            | "not" ExpUnaria
            | "length" ExpUnaria
            | Valor
            | Id
            | Aplicacao
            | ExpDeclaracao
            | IfThenElse

Valor ::= ValorConcreto
        | ValorAbstrato

ValorAbstrato ::= ValorFuncao

ValorConcreto ::= ValorInteiro
                | ValorBooleano
                | ValorString

ValorFuncao ::= "fn" ListId "." Expressao

ExpDeclaracao ::= "let" DeclaracaoFuncional "in" Expressao

DeclaracaoFuncional ::= DecVariavel
                    | DecFuncao
                    | DecComposta

DecVariavel ::= "var" Id "=" Expressao

DecFuncao ::= "fun" ListId "=" Expressao

DecComposta ::= DeclaracaoFuncional "," DeclaracaoFuncional

ListId ::= Id
         | Id ListId

Aplicacao ::= Expressao "(" ListExp ")"

ListExp ::= Expressao
          | Expressao "," ListExp

IfThenElse ::= "if" Expressao "then" Expressao "else" Expressao
```

### 2.2. Precedência e Associatividade

A hierarquia formal de precedência (da menor para a maior) e associatividades:

| Nível | Operador(es) | Descrição | Precedência | Associatividade |
|---|---|---|---|---|
| 1 | `\|>` | Forward Pipeline | **Menor** | À esquerda |
| 2 | `>>` | Composição de funções | Intermediária baixa | À esquerda |
| 3 | `or`, `==`, `+`, `-`, `++`, `and` | Operadores binários (relacionais, aditivos, lógicos) | Intermediária alta | À esquerda |
| 4 | `-` (unário), `not`, `length` | Operadores unários | Alta | À direita |
| 5 | `f(...)` | Aplicação de Função | Mais alta | À esquerda |
| 6 | Literais, `Id`, `let..in`, `if..then..else`, `(...)` | Termos primários | Máxima | — |

**Consequência da precedência:**
* `x |> f >> g` é avaliado como `x |> (f >> g)`
* `1 + 2 |> f` é avaliado como `(1 + 2) |> f`
* `f >> g >> h` é avaliado como `(f >> g) >> h`
* `x |> f |> g` é avaliado como `(x |> f) |> g`

### 2.3. Gramática Livre de Recursão à Esquerda (LL(1) / EBNF para JavaCC)

```ebnf
PExpressao       ::= PExpPipeline
PExpPipeline     ::= PExpComposicao ( "|>" PExpComposicao )*
PExpComposicao   ::= PExpBinaria ( ">>" PExpBinaria )*
PExpBinaria      ::= PExpBinaria2 ( "==" PExpBinaria2 | "(" [ PListaExpr ] ")" )*
PExpBinaria2     ::= PExpBinaria3 ( ( "+" | "-" | "or" | "++" ) PExpBinaria3 )*
PExpBinaria3     ::= PExpUnaria ( "and" PExpUnaria )*
PExpUnaria       ::= PExpMenos | PExpNot | PExpLength | PExpDeclaracao | PExpCondicional | PExpPrimaria
PExpPrimaria     ::= PValor | LOOKAHEAD(PId() "(") PAplicacao | PId() | "(" PExpressao ")"
```

---

## 3. Arquitetura da AST (Árvore de Sintaxe Abstrata)

As novas construções sintáticas serão implementadas como nós de expressão herdando de `ExpBinaria` no pacote `lfp.plp.functional2.expression`:

### 3.1. `ExpPipeline`

* **Pacote:** `lfp.plp.functional2.expression`
* **Herança:** `public class ExpPipeline extends ExpBinaria`
* **Construtor:** `public ExpPipeline(Expressao esq, Expressao dir) { super(esq, dir, "|>"); }`
* **Campos Herdados:** `esq` (dados / entrada), `dir` (função receptora).

### 3.2. `ExpComposicao`

* **Pacote:** `lfp.plp.functional2.expression`
* **Herança:** `public class ExpComposicao extends ExpBinaria`
* **Construtor:** `public ExpComposicao(Expressao esq, Expressao dir) { super(esq, dir, ">>"); }`
* **Campos Herdados:** `esq` ($f$), `dir` ($g$).

---

## 4. Semântica Operacional (`avaliar`)

### 4.1. Semântica do Pipeline (`e1 |> e2`)

A execução de `e1 |> e2` aplica a função `e2` ao valor resultante de `e1`.
Isso é implementado de forma segura e idiomática via **desaçucaramento sintático** (*syntactic desugaring*), delegando diretamente para a classe `Aplicacao`:

$$\mathcal{E}\llbracket e_1 \triangleright e_2 \rrbracket \gamma = \mathcal{E}\llbracket e_2(e_1) \rrbracket \gamma$$

**Implementação em Java:**
```java
@Override
public Valor avaliar(AmbienteExecucao amb)
        throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
    Aplicacao app = new Aplicacao(getDir(), getEsq());
    return app.avaliar(amb);
}
```

### 4.2. Semântica da Composição (`e1 >> e2`)

A execução de $e_1 \gg e_2$ produz uma nova abstração funcional que representa a composição matemática de $e_1$ e $e_2$:

$$\mathcal{E}\llbracket e_1 \gg e_2 \rrbracket \gamma = \lambda x . e_2(e_1(x))$$

Para preservar a semântica estática de escopo (*lexical scoping*) e suportar fechamento de variáveis livres (*closures*):
1. Avaliam-se previamente `e1` e `e2` no ambiente de execução corrente $\gamma$, resultando nos valores de função $f$ e $g$.
2. Cria-se um identificador com nome seguro e único para o parâmetro formal (ex.: `$$pipeArg`).
3. Constrói-se a árvore de aplicação aninhada: $g(f(\text{param}))$.
4. Instancia-se e avalia-se `ValorFuncao` com o parâmetro e o corpo, garantindo a captura das variáveis do ambiente via método `reduzir(amb)`.

**Implementação em Java:**
```java
@Override
public Valor avaliar(AmbienteExecucao amb)
        throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
    Valor funcF = getEsq().avaliar(amb);
    Valor funcG = getDir().avaliar(amb);

    Id param = new Id("$$pipeArg");
    Expressao chamadaF = new Aplicacao(funcF, param);
    Expressao corpo = new Aplicacao(funcG, chamadaF);

    List<Id> args = Collections.singletonList(param);
    ValorFuncao funcComposta = new ValorFuncao(args, corpo);
    return funcComposta.avaliar(amb);
}
```

---

## 5. Sistema de Tipagem Estática

### 5.1. Regras Formais de Tipo

#### Regra do Pipeline:
$$\frac{\Gamma \vdash e_1 : T_1 \quad \Gamma \vdash e_2 : T_1 \rightarrow T_2}{\Gamma \vdash e_1 \triangleright e_2 : T_2}$$

#### Regra da Composição:
$$\frac{\Gamma \vdash e_1 : T_1 \rightarrow T_2 \quad \Gamma \vdash e_2 : T_2 \rightarrow T_3}{\Gamma \vdash e_1 \gg e_2 : T_1 \rightarrow T_3}$$

### 5.2. Checagem de Tipo e Inferência em `ExpPipeline`

1. **`checaTipoElementoTerminal(AmbienteCompilacao amb)`:**
   - Obtém o tipo de `dir`: `Tipo tipoDir = getDir().getTipo(amb);`.
   - Se `tipoDir` for `TipoFuncao`: valida se a aridade é 1 e se o tipo do parâmetro é compatível com `getEsq().getTipo(amb)` usando `((TipoFuncao) tipoDir).checaTipo(amb, Collections.singletonList(getEsq()))`.
   - Se `tipoDir` for `TipoPolimorfico`: aceita como válido para permitir inferência posterior.
   - Caso contrário: retorna `false`.
2. **`getTipo(AmbienteCompilacao amb)`:**
   - Se `tipoDir` for `TipoFuncao`: retorna o tipo inferido da imagem através de `((TipoFuncao) tipoDir).getTipo(amb, Collections.singletonList(getEsq()))`.
   - Alternativamente, delega para `new Aplicacao(getDir(), getEsq()).getTipo(amb)`.

### 5.3. Checagem de Tipo e Inferência em `ExpComposicao`

1. **`checaTipoElementoTerminal(AmbienteCompilacao amb)`:**
   - Obtém `tEsq = getEsq().getTipo(amb)` e `tDir = getDir().getTipo(amb)`.
   - Se ambos forem `TipoFuncao`:
     - Valida se `tfEsq.getDominio().size() == 1` e `tfDir.getDominio().size() == 1`.
     - Valida compatibilidade: `tfEsq.getImagem().eIgual(tfDir.getDominio().get(0))`.
   - Se algum for `TipoPolimorfico`: retorna `true` (deferido para resolução polimórfica).
   - Caso contrário: retorna `false`.
2. **`getTipo(AmbienteCompilacao amb)`:**
   - Se ambos forem `TipoFuncao`: retorna `new TipoFuncao(tfEsq.getDominio(), tfDir.getImagem())`.
   - Se envolver `TipoPolimorfico`: retorna tipo função composto ou `TipoPolimorfico.CURINGA`.

---

## 6. Modificações na Gramática JavaCC (`Funcional2.jj`)

**Arquivo:** `PLP-master/FuncionalPipeline/src/lfp/plp/functional2/parser/Funcional2.jj`

### 6.1. Novos Imports no Cabeçalho
```java
import lfp.plp.functional2.expression.ExpPipeline;
import lfp.plp.functional2.expression.ExpComposicao;
```

### 6.2. Declaração dos Tokens Léxicos
Declarados no bloco `TOKEN : /* OPERATORS */` antes de operadores que possuem prefixos idênticos (`>` e `|`):

```java
TOKEN : /* OPERATORS */
{
  < PIPELINE : "|>" >
| < COMPOSE : ">>" >
| < ASSIGN : "=" >
| < GT : ">" >
...
```

### 6.3. Produções Sintáticas no Parser

```java
Expressao PExpressao() :
{
  Expressao retorno;
}
{
  retorno = PExpPipeline()
  {
    return retorno;
  }
}

Expressao PExpPipeline() :
{
  Expressao retorno, param2;
}
{
  retorno = PExpComposicao()
  (
    < PIPELINE > param2 = PExpComposicao()
    {
      retorno = new ExpPipeline(retorno, param2);
    }
  )*
  {
    return retorno;
  }
}

Expressao PExpComposicao() :
{
  Expressao retorno, param2;
}
{
  retorno = PExpBinaria()
  (
    < COMPOSE > param2 = PExpBinaria()
    {
      retorno = new ExpComposicao(retorno, param2);
    }
  )*
  {
    return retorno;
  }
}
```

---

## 7. Atualização do Visitor de Instanciação Parcial

**Arquivo:** `PLP-master/FuncionalPipeline/src/lfp/plp/functional2/util/PartialInstantiatorVisitor.java`

Adicionar os métodos reflexivos `_visitExpPipeline` e `_visitExpComposicao` para preservar o suporte a reduções simbólicas:

```java
public Expressao _visitExpPipeline(ExpPipeline expressao,
        AmbienteExecucao ambiente, Set<Id> localVariables) {
    Expressao esquerda = visit(expressao.getEsq(), ambiente, localVariables);
    Expressao direita = visit(expressao.getDir(), ambiente, localVariables);
    return new ExpPipeline(esquerda, direita);
}

public Expressao _visitExpComposicao(ExpComposicao expressao,
        AmbienteExecucao ambiente, Set<Id> localVariables) {
    Expressao esquerda = visit(expressao.getEsq(), ambiente, localVariables);
    Expressao direita = visit(expressao.getDir(), ambiente, localVariables);
    return new ExpComposicao(esquerda, direita);
}
```

---

## 8. Plano de Testes e Casos de Validação

### 8.1. Casos de Teste Positivos (Execução Correta)

#### Teste 1: Pipeline Simples com Inteiro
```text
let fun inc x = x + 1 in
5 |> inc
```
- **Resultado esperado:** `6`

#### Teste 2: Pipeline Encadeado (Associatividade à Esquerda)
```text
let fun inc x = x + 1,
    fun dobro x = x + x
in 10 |> inc |> dobro
```
- **Avaliação esperada:** `dobro(inc(10))` $\rightarrow$ `dobro(11)` $\rightarrow$ `22`

#### Teste 3: Precedência entre Aritmética e Pipeline
```text
let fun dobro x = x + x in
2 + 3 |> dobro
```
- **Avaliação esperada:** `dobro(2 + 3)` $\rightarrow$ `dobro(5)` $\rightarrow$ `10` (não deve somar 2 com `3 |> dobro`)

#### Teste 4: Composição de Funções Simples
```text
let fun inc x = x + 1,
    fun mult3 x = x * 3,
    fun pipeline = inc >> mult3
in 4 |> pipeline
```
- **Avaliação esperada:** `mult3(inc(4))` $\rightarrow$ `mult3(5)` $\rightarrow$ `15`

#### Teste 5: Precedência de Composição sobre Pipeline
```text
let fun inc x = x + 1,
    fun dobro x = x + x
in 5 |> inc >> dobro
```
- **Avaliação esperada:** Interpretado como `5 |> (inc >> dobro)` $\rightarrow$ `12`

#### Teste 6: Pipeline com Funções Anônimas (`fn`)
```text
5 |> fn x . x * 2 |> fn y . y + 10
```
- **Resultado esperado:** `20`

#### Teste 7: Pipeline com Strings
```text
"hello" |> fn s . s ++ " world" |> fn s . length s
```
- **Resultado esperado:** `11`

### 8.2. Casos de Teste Negativos (Erros de Tipagem Estática)

#### Erro 1: Pipeline em Não-Função
```text
5 |> 10
```
- **Esperado:** `checaTipo()` retorna `false` ou lança exceção de tipo.

#### Erro 2: Tipo de Entrada Incompatível no Pipeline
```text
let fun inc x = x + 1 in
"texto" |> inc
```
- **Esperado:** Falha na verificação de tipo (`checaTipo() == false`).

#### Erro 3: Incompatibilidade entre Imagem de $f$ e Domínio de $g$ na Composição
```text
let fun ePar x = x == 0,
    fun inc x = x + 1
in ePar >> inc
```
- **Esperado:** Falha na verificação de tipos, pois a saída de `ePar` é Booleano, mas `inc` requer Inteiro.

---

## 9. Checklist de Implementação

| # | Arquivo | Responsabilidade | Status |
|---|---|---|---|
| 1 | `src/lfp/plp/functional2/expression/ExpPipeline.java` | Criar classe AST do Pipeline (`avaliar`, `checaTipo`, `getTipo`, `clone`) | A Fazer |
| 2 | `src/lfp/plp/functional2/expression/ExpComposicao.java` | Criar classe AST da Composição (`avaliar`, `checaTipo`, `getTipo`, `clone`) | A Fazer |
| 3 | `src/lfp/plp/functional2/parser/Funcional2.jj` | Inserir tokens `< PIPELINE >` e `< COMPOSE >` | A Fazer |
| 4 | `src/lfp/plp/functional2/parser/Funcional2.jj` | Inserir produções `PExpPipeline` e `PExpComposicao` e atualizar `PExpressao` | A Fazer |
| 5 | `src/lfp/plp/functional2/util/PartialInstantiatorVisitor.java` | Adicionar suporte reflexivo para `ExpPipeline` e `ExpComposicao` | A Fazer |
| 6 | `input` / `input2` / `Testes/` | Criar programas de teste cobrindo pipeline e composição | A Fazer |
| 7 | Build Maven (`mvn clean compile`) | Validar regeneração do JavaCC e compilação completa sem warnings ou erros | A Fazer |
