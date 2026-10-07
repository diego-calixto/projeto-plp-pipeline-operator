    Programa ::= Expressao

    Expressao ::= ExpPipeline

    ExpPipeline ::= ExpPipeline "|>" ExpComposicao
                | ArgumentosPipeline "|>" ExpComposicao
                | ExpComposicao

    ArgumentosPipeline ::= "(" ListExp ")"

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
                | "(" Expressao ")"

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