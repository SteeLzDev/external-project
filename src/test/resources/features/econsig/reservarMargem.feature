#language:pt

Funcionalidade: Reservar Margem para o Servidor
  Uma nova consignação cadastrada para o servidor nas possíveis situações:
  - Aguardando Confirmação: caso o usuário não possua permissão de confirmar reserva,
  - Aguardando Deferimento: caso o serviço esteja configurado para requerer deferimento manual e o usuário possua permissão de confirmar reserva
  - Deferida: caso o serviço esteja em deferimento automático e o usuário possua permissão de confirmar reserva.

  Contexto: Sistema permite CPF e senhaAutorizacaoServidor opcional e Convenio para Servico Emprestimo
    Dado Sistema permite CPF opcional na pesquisaServidor e senhaAutorizacaoServidor opcional
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    E UsuCsa com Convenio para Servico Emprestimo
    E Parametro Registro Servidor com valores zerados


  Esquema do Cenário: Reservar Margem para servidor com margem suficiente com situação Deferida.
    Dado Servico configurado para nao exibir campo CET
    E servidor com margem suficiente
    E UsuCsa com permissao para confirmar reserva e deferimento automatico
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario preenche campo numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "<valorPrestacao>", "<prazo>" e "<valorLiquido>"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"

    Exemplos:
    | valorPrestacao | prazo | valorLiquido | situacao |
    |        50      |   1   |      2000    | Deferida |


  Esquema do Cenário: Reservar Margem para servidor com margem suficiente com situação Aguardando Confirmação.
    Dado Servico configurado para nao exibir campo CET
    E servidor com margem suficiente
    E UsuCsa sem permissao para confirmar reserva
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario preenche campo numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "<valorPrestacao>", "<prazo>" e "<valorLiquido>"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"

    Exemplos:
    | valorPrestacao | prazo | valorLiquido |       situacao      |
    |        10      |   1   |      2000    | Aguard. Confirmação |


  Esquema do Cenário: Reservar Margem para servidor com margem suficiente com situação Aguardando Deferimento.
    Dado Servico configurado para nao exibir campo CET
    E servidor com margem suficiente
    E UsuCsa com permissao para confirmar reserva e deferimento manual
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario preenche campo numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "<valorPrestacao>", "<prazo>" e "<valorLiquido>"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"

    Exemplos:
    | valorPrestacao | prazo | valorLiquido |       situacao      |
    |        10      |   1   |      2000    | Aguard. Deferimento |


  Cenário: Reservar Margem para servidor com margem insuficiente
    Dado Servico configurado para nao exibir campo CET
    E servidor com margem insuficiente "8100"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "8100"
    E Usuario preenche valor liquido "8200"
    E Usuario preenche campo numero prestacoes "1"
    E Usuario clica no botao Confirmar para prosseguir
    Entao Sistema exibe mensagem de erro "O valor da parcela não pode ser maior do que a margem disponível."

	Cenário: Tenta Reservar Margem com matricula incorreta
    Dado Servico configurado para nao exibir campo CET
		Quando UsuCsa Logado
		E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
		E Usuario seleciona Servico Emprestimo
		E Usuario preenche matricula de servidor com "5555555"
		E Usuario clica no botao Pesquisar
		Entao Sistema informa mensagem de erro de matricula "Nenhum registro encontrado para a pesquisa:Matrícula: 5555555"
		
  Cenário: Reservar Margem para servidor com margem suficiente e com prazo maior que o permitido
    Dado Servico configurado para nao exibir campo CET
    E UsuCsa possui prazo Maximo
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "1000"
    E Usuario preenche valor liquido "2000"
    E Usuario preenche campo numero prestacoes "51"
    E Usuario clica no botao Confirmar para prosseguir
    Entao Sistema exibe alerta de erro "Quantidade de parcelas maior do que o permitido para este servidor. Quantidade máxima permitida (meses): 36"


  Cenário: Reservar Margem para servidor com margem suficiente e com prazo maior que o prazo maximo definido
    Dado Servico configurado para nao exibir campo CET
    E Servico com Prazo Maximo definido "15"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "100"
    E Usuario preenche valor liquido "2000"
    E Usuario preenche campo numero prestacoes "20"
    Entao Sistema exibe alerta de erro "Quantidade de parcelas maior do que o permitido para este serviço. Quantidade máxima permitida (meses): 15"


   Cenário: Reservar Margem para servidor com margem suficiente sem informar valor prestação
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche valor liquido "2000"
    E Usuario preenche campo numero prestacoes "1"
    E Usuario clica no botao Confirmar para prosseguir
    Entao Sistema exibe mensagem de erro "O valor da consignação deve ser informado."


 Cenário: Reservar Margem para servidor com margem suficiente sem informar valor liquido
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "100"
    E Usuario preenche campo numero prestacoes "1"
    E Usuario clica no botao Confirmar para prosseguir
    Entao Sistema exibe mensagem de erro "O valor líquido liberado deve ser informado."


 Cenário: Reservar Margem para servidor com margem suficiente sem informar prazo
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "100"
    E Usuario preenche valor liquido "200"
    E Usuario clica no botao Confirmar para prosseguir
    Entao Sistema exibe mensagem de erro "O número de parcelas deve ser informado."


  Esquema do Cenário: Reservar Margem de emprestimo com taxa de juros
    Dado Servico configurado para exibir campo CET
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario preenche campo numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"
    E Usuario preenche valor CET "<cet>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos para concluir reserva "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<cet>"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"
    E com as iformacoes da taxa de juros "<cet>" e "<cetAnual>"

    Exemplos:
    | valorPrestacao | prazo | valorLiquido | cet | cetAnual |      situacao       |
    |     100        |   5   |      2000    |  9  |  181,27  | Aguard. Deferimento |


  Cenário: Reservar Margem sem informar CET
    Dado Servico configurado para exibir campo CET
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "500"
    E Usuario preenche campo numero prestacoes "5"
    E Usuario preenche valor liquido "1300"
    E Usuario clica no botao Confirmar para prosseguir
    Então Sistema exibe alerta de erro "O CET (%) deve ser informado."


  Esquema do Cenário: Reservar Margem com sucesso para servidor ativo em servico que nao incide margem e requer deferimento manual
    Dado Servico ativo de Identificador "<servicoId>" e Natureza "<nseCodigo>"
    E Parametro de Servico "3" com valor "0" para o Servico "<servicoId>" para "não incidir na margem"
    E Parametro de Servico "8" com valor "1" para o Servico "<servicoId>" para "exigir deferimento manual"
    E Parametro de Servico "12" com valor "0" para o Servico "<servicoId>" para "não exigir senha do servidor"
    E Orgao ativo de Identificador "<orgaoId>" no Estabelecimento "<estabelecimentoId>"
    E Convenio ativo de Verba "<verba>" para o Servico "<servicoId>", Consignataria "<consignatariaId>" e Orgao "<orgaoId>"
    E Servidor ativo de CPF "<cpf>" e Matricula "<matricula>" no Orgao "<orgaoId>"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico com identificador "<servicoId>"
    E Usuario preenche matricula de servidor com "<matricula>"
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario preenche campo numero prestacoes "<prazo>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "<valorPrestacao>" e "<prazo>"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>" e "<situacao>"

    Exemplos:
    | cpf            | matricula | orgaoId | estabelecimentoId | servicoId | nseCodigo | consignatariaId | verba | valorPrestacao | prazo | situacao            |
    | 123.456.789-00 | 987654321 | 9876    | 01                | 988       | 99        | 001             | 988   | 2000           | 1     | Aguard. Deferimento |


  Esquema do Cenário: Reservar Margem com sucesso para servidor ativo, margem suficiente e servico sem validacao de taxa
    Dado Servico ativo de Identificador "<servicoId>" e Natureza "<nseCodigo>"
    E Parametro de Servico "3" com valor "1" para o Servico "<servicoId>" para "incidir na margem 1"
    E Parametro de Servico "8" com valor "0" para o Servico "<servicoId>" para "não exigir deferimento manual"
    E Parametro de Servico "12" com valor "0" para o Servico "<servicoId>" para "não exigir senha do servidor"
    E Parametro de Servico "59" com valor "0" para o Servico "<servicoId>" para "não validar taxa de juros"
    E Parametro de Servico "109" com valor "1" para o Servico "<servicoId>" para "exigir informação de valor liberado"
    E Orgao ativo de Identificador "<orgaoId>" no Estabelecimento "<estabelecimentoId>"
    E Convenio ativo de Verba "<verba>" para o Servico "<servicoId>", Consignataria "<consignatariaId>" e Orgao "<orgaoId>"
    E Servidor ativo de CPF "<cpf>" e Matricula "<matricula>" no Orgao "<orgaoId>"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico com identificador "<servicoId>"
    E Usuario preenche matricula de servidor com "<matricula>"
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario preenche campo numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "<valorPrestacao>", "<prazo>" e "<valorLiquido>"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"

    Exemplos:
    | cpf            | matricula | orgaoId | estabelecimentoId | servicoId | nseCodigo | consignatariaId | verba | valorPrestacao | prazo | valorLiquido | situacao |
    | 123.456.789-00 | 987654321 | 9876    | 01                | 987       | 1         | 001             | 987   | 100            | 12    | 1000         | Deferida |


  Esquema do Cenário: Reservar Margem sem sucesso para servidor ativo, margem suficiente e servico com validacao de taxa acima do permitido
    Dado Servico ativo de Identificador "<servicoId>" e Natureza "<nseCodigo>"
    E Parametro de Servico "3" com valor "1" para o Servico "<servicoId>" para "incidir na margem 1"
    E Parametro de Servico "8" com valor "0" para o Servico "<servicoId>" para "não exigir deferimento manual"
    E Parametro de Servico "12" com valor "0" para o Servico "<servicoId>" para "não exigir senha do servidor"
    E Parametro de Servico "59" com valor "1" para o Servico "<servicoId>" para "validar taxa de juros"
    E Parametro de Servico "109" com valor "1" para o Servico "<servicoId>" para "exigir informação de valor liberado"
    E Parametro de Servico de Consignataria "226" com valor "N" para o Servico "<servicoId>" e Consignataria "<consignatariaId>" para "não exibir boleto"
    E Orgao ativo de Identificador "<orgaoId>" no Estabelecimento "<estabelecimentoId>"
    E Convenio ativo de Verba "<verba>" para o Servico "<servicoId>", Consignataria "<consignatariaId>" e Orgao "<orgaoId>"
    E Taxa para o Prazo <prazo> cadastrada para o Servico "<servicoId>" e Consignataria "<consignatariaId>" com valor <cet>
    E Servidor ativo de CPF "<cpf>" e Matricula "<matricula>" no Orgao "<orgaoId>"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico com identificador "<servicoId>"
    E Usuario preenche matricula de servidor com "<matricula>"
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario seleciona numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "<valorPrestacao>", "<prazo>" e "<valorLiquido>"
    E Usuario clica em Concluir para prosseguir
    Entao Sistema exibe mensagem com o erro "VALOR DO CET CALCULADO ESTÁ SUPERIOR AO VALOR DO CET ANUNCIADO NO SISTEMA."

    Exemplos:
    | cpf            | matricula | orgaoId | estabelecimentoId | servicoId | nseCodigo | consignatariaId | verba | valorPrestacao | prazo | valorLiquido | cet  | situacao |
    | 123.456.789-00 | 987654321 | 9876    | 01                | 987       | 1         | 001             | 987   | 100            | 12    | 1000         | 1,70 | Deferida |


  Esquema do Cenário: Reservar Margem com sucesso para servidor ativo, margem suficiente e servico com validacao de taxa
    Dado Servico ativo de Identificador "<servicoId>" e Natureza "<nseCodigo>"
    E Parametro de Servico "3" com valor "1" para o Servico "<servicoId>" para "incidir na margem 1"
    E Parametro de Servico "8" com valor "0" para o Servico "<servicoId>" para "não exigir deferimento manual"
    E Parametro de Servico "12" com valor "0" para o Servico "<servicoId>" para "não exigir senha do servidor"
    E Parametro de Servico "59" com valor "1" para o Servico "<servicoId>" para "validar taxa de juros"
    E Parametro de Servico "109" com valor "1" para o Servico "<servicoId>" para "exigir informação de valor liberado"
    E Parametro de Servico de Consignataria "226" com valor "N" para o Servico "<servicoId>" e Consignataria "<consignatariaId>" para "não exibir boleto"
    E Orgao ativo de Identificador "<orgaoId>" no Estabelecimento "<estabelecimentoId>"
    E Convenio ativo de Verba "<verba>" para o Servico "<servicoId>", Consignataria "<consignatariaId>" e Orgao "<orgaoId>"
    E Taxa para o Prazo <prazo> cadastrada para o Servico "<servicoId>" e Consignataria "<consignatariaId>" com valor <cet>
    E Servidor ativo de CPF "<cpf>" e Matricula "<matricula>" no Orgao "<orgaoId>"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico com identificador "<servicoId>"
    E Usuario preenche matricula de servidor com "<matricula>"
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario seleciona numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "<valorPrestacao>", "<prazo>" e "<valorLiquido>"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"
    E Remove o servidor de CPF "<cpf>" e Matricula "<matricula>" no Orgao "<orgaoId>"
    E Remove o servico de Identificador "<servicoId>"
    E Remove o orgao de Identificador "<orgaoId>"

    Exemplos:
    | cpf            | matricula | orgaoId | estabelecimentoId | servicoId | nseCodigo | consignatariaId | verba | valorPrestacao | prazo | valorLiquido | cet  | situacao |
    | 123.456.789-00 | 987654321 | 9876    | 01                | 987       | 1         | 001             | 987   | 100            | 11    | 1000         | 1,70 | Deferida |
