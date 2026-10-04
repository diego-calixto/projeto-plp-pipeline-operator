Programa ::= Expressao

Expressao ::= Valor
      | ExpUnaria 
      | ExpBinaria
      | ExpDeclaracao
      | Id
      | Aplicacao
      | IfThenElse

Valor ::= ValorConcreto
      | ValorAbstrato

ValorAbstrato ::= ValorFuncao

ValorConcreto ::= ValorInteiro
      | ValorBooleano
      | ValorString

ValorFuncao ::= “fn” ListId “.” Expressao

ExpUnaria ::= “-“ Expressao
      | “not” Expressao
      | “length” Expressao

ExpBinaria ::= Expressao “+” Expressao
      | Expressao “-“ Expressao
      | Expressao “and” Expressao
      | Expressao “or” Expressao
      | Expressao “==” Expressao
      | Expressao “++” Expressao

ExpDeclaracao ::= “let” DeclaracaoFuncional “in” Expressao

DeclaracaoFuncional ::= DecVariavel
      | DecFuncao
      | DecComposta

DecVariavel ::= “var” Id “=” Expressao

DecFuncao ::= “fun” ListId “=” Expressao

DecComposta ::= DeclaracaoFuncional “,” DeclaracaoFuncional

ListId ::= Id | Id ListId

Aplicacao ::= Expressao “(“ ListExp “)”

ListExp ::= Expressao | Expressao “, “ ListExp

