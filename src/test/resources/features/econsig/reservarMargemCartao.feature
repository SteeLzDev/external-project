#language:pt

Funcionalidade: Reservar Margem de Cartão Lançamento
  Reserva de Margem de Cartao de crédito no sistema econsig

Contexto: Sistema permite senhaAutorizacaoServidor opcional e Convenio para Servico Emprestimo
  Dado Que o sistema permite senhaAutorizacaoServidor opcional
  E Parametro Registro Servidor com valores zerados

  Cenário: Reservar Margem de cartão para servidor com prazo Indeterminado e com situação Deferida.
    Dado servidor com margem de cartao suficiente
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    E UsuCsa com permissao para confirmar reserva e deferimento automatico de cartao
    E UsuCsa com Convenio para Servico Cartao
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "100"
    E Usuario marca prazo indeterminado
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se prazo Indeterminado esta correto
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "100", "Indeterminado" e "Deferida"


  Cenário: Reservar Margem de cartão para servidor com prazo Determinado e com situação Deferida.
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    Dado servidor com margem de cartao suficiente
    E UsuCsa com permissao para confirmar reserva e deferimento automatico de cartao
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "150"
    E Usuario preenche campo numero prestacoes "5"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "150" e "5"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "150", "5" e "Deferida"


  Cenário: Reservar Margem para servidor com margem suficiente e com situação Aguardando Confirmação.
    Dado servidor com margem de cartao suficiente
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    E UsuCsa sem permissao para confirmar reserva
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "10"
    E Usuario marca prazo indeterminado
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se prazo Indeterminado esta correto
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "10", "Indeterminado" e "Aguard. Confirmação"


  Cenário: Reservar Margem de cartão para servidor com prazo Determinado e com situação Aguardando Deferimento.
    Dado servidor com margem de cartao suficiente
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    E UsuCsa com permissao para confirmar reserva e deferimento manual com cartao
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "15"
    E Usuario preenche campo numero prestacoes "5"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "15" e "5"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "15", "5" e "Aguard. Deferimento"


  Cenário: Reservar Margem de Cartao para servidor com margem insuficiente
    Dado Usuario com margem de credito para cartao insuficiente "5100"
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "5100"
    E Usuario marca prazo indeterminado
    E Usuario clica no botao Confirmar para prosseguir
    Entao Sistema exibe mensagem de erro "O valor da parcela não pode ser maior do que a margem disponível."


  Cenário: Reservar Margem de Cartao para servidor sem informar prazo
    Dado que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Quando Usuario preenche campo numero prestacoes "10"
    E Usuario clica no botao Confirmar
    Entao Sistema exibe mensagem de erro "O valor da consignação deve ser informado."


  Cenário: Reservar Margem de Cartao para servidor sem informar parcela
    Dado que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario marca prazo indeterminado
    E Usuario clica no botao Confirmar para prosseguir
    Entao Sistema exibe mensagem de erro "O valor da consignação deve ser informado."


  Cenário: Reservar Margem de Cartao com usuário Correspondente e com situação Aguardando Deferimento.
    Dado UsuCor com Convenio para Servico Cartao
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "cor"
    E UsuCor com permissao para confirmar reserva e deferimento manual com cartao
    Quando usuario correspondente "cor" esteja logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "15"
    E Usuario marca prazo indeterminado
    E Usuario clica no botao Confirmar para prosseguir
    E UsuCor verifica se dados estao corretos "15" e "Indeterminado"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "15", "Indeterminado" e "Aguard. Deferimento"


  Cenário: Reservar Margem de Cartao com usuário Correspondente e com situação Deferida.
    Dado UsuCor com Convenio para Servico Cartao
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "cor"
    E UsuCor com permissao para confirmar reserva e deferimento automatico de cartao
    Quando usuario correspondente "cor" esteja logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "15"
    E Usuario marca prazo indeterminado
    E Usuario clica no botao Confirmar para prosseguir
    E UsuCor verifica se dados estao corretos "15" e "Indeterminado"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "15", "Indeterminado" e "Deferida"


  Cenário:  Reservar Margem de Cartao com usuário Correspondente e com situação Aguardando Confirmação.
    Dado UsuCor com Convenio para Servico Cartao
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "cor"
    E UsuCor sem permissao para confirmar reserva
    Quando usuario correspondente "cor" esteja logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "10"
    E Usuario marca prazo indeterminado
    E Usuario clica no botao Confirmar para prosseguir
    E UsuCor verifica se dados estao corretos "10" e "Indeterminado"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "10", "Indeterminado" e "Aguard. Confirmação"


  Cenário: Reservar Margem de Cartao com usuário Correspondente com margem insuficiente
    Dado Usuario com margem de credito para cartao insuficiente "5100"
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "cor"
    Quando usuario correspondente "cor" esteja logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Cartao de Credito
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "5100"
    E Usuario marca prazo indeterminado
    E Usuario clica no botao Confirmar para prosseguir
    Entao Sistema lanca mensagem de erro de margem "VALOR DA PARCELA INFORMADO MAIOR DO QUE A MARGEM DISPONÍVEL."


  Esquema do Cenário: Reservar Margem com sucesso para servidor ativo, margem suficiente e servico de cartao reserva
    Dado Servico ativo de Identificador "<servicoId>" e Natureza "<nseCodigo>"
    E Parametro de Servico "2" com valor "0" para o Servico "<servicoId>" para "não integrar folha"
    E Parametro de Servico "3" com valor "1" para o Servico "<servicoId>" para "incidir na margem 1"
    E Parametro de Servico "7" com valor "0" para o Servico "<servicoId>" para "prazo indeterminado"
    E Parametro de Servico "8" com valor "0" para o Servico "<servicoId>" para "não exigir deferimento manual"
    E Parametro de Servico "12" com valor "0" para o Servico "<servicoId>" para "não exigir senha do servidor"
    E Orgao ativo de Identificador "<orgaoId>" no Estabelecimento "<estabelecimentoId>"
    E Convenio ativo de Verba "<verba>" para o Servico "<servicoId>", Consignataria "<consignatariaId>" e Orgao "<orgaoId>"
    E Servidor ativo de CPF "<cpf>" e Matricula "<matricula>" no Orgao "<orgaoId>"
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico com identificador "<servicoId>"
    E Usuario preenche matricula de servidor com "<matricula>"
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se prazo Indeterminado esta correto
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>" e "<situacao>"

    Exemplos:
    | cpf            | matricula | orgaoId | estabelecimentoId | servicoId | nseCodigo | consignatariaId | verba | valorPrestacao | prazo         | situacao |
    | 123.456.789-00 | 987754321 | 9877    | 01                | 989       | 7         | 001             | 989   | 300            | Indeterminado | Deferida |


  Esquema do Cenário: Reservar Margem sem sucesso para servidor ativo, servico de cartao lancamento com valor acima do reservado
    Dado Servico ativo de Identificador "<servicoId>" e Natureza "<nseCodigo>"
    E Relacionamento do Servico Origem "<servicoOrigemId>" para o Servico Destino "<servicoId>" de Natureza "3"
    E Parametro de Servico "2" com valor "1" para o Servico "<servicoId>" para "integrar folha"
    E Parametro de Servico "3" com valor "0" para o Servico "<servicoId>" para "não incidir na margem"
    E Parametro de Servico "7" com valor "1" para o Servico "<servicoId>" para "prazo máximo igual a 1"
    E Parametro de Servico "8" com valor "0" para o Servico "<servicoId>" para "não exigir deferimento manual"
    E Parametro de Servico "12" com valor "0" para o Servico "<servicoId>" para "não exigir senha do servidor"
    E Parametro de Servico "53" com valor "1" para o Servico "<servicoId>" para "prazo fixo"
    E Orgao ativo de Identificador "<orgaoId>" no Estabelecimento "<estabelecimentoId>"
    E Convenio ativo de Verba "<verba>" para o Servico "<servicoId>", Consignataria "<consignatariaId>" e Orgao "<orgaoId>"
    E Servidor ativo de CPF "<cpf>" e Matricula "<matricula>" no Orgao "<orgaoId>"
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico com identificador "<servicoId>"
    E Usuario preenche matricula de servidor com "<matricula>"
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "<valorPrestacao>" e "<prazo>"
    E Usuario clica em Concluir para prosseguir
    Entao Sistema exibe mensagem com o erro "O VALOR SOLICITADO ESTÁ FORA DA MARGEM RESERVADA"

    Exemplos:
    | cpf            | matricula | orgaoId | estabelecimentoId | servicoId | servicoOrigemId | nseCodigo | consignatariaId | verba | valorPrestacao | prazo |
    | 123.456.789-00 | 987754321 | 9877    | 01                | 990       | 989             | 7         | 001             | 989   | 301            | 1     |


  Esquema do Cenário: Reservar Margem com sucesso para servidor ativo, servico de cartao lancamento com valor abaixo do reservado
    Dado Servico ativo de Identificador "<servicoId>" e Natureza "<nseCodigo>"
    E Relacionamento do Servico Origem "<servicoOrigemId>" para o Servico Destino "<servicoId>" de Natureza "3"
    E Parametro de Servico "2" com valor "1" para o Servico "<servicoId>" para "integrar folha"
    E Parametro de Servico "3" com valor "0" para o Servico "<servicoId>" para "não incidir na margem"
    E Parametro de Servico "7" com valor "1" para o Servico "<servicoId>" para "prazo máximo igual a 1"
    E Parametro de Servico "8" com valor "0" para o Servico "<servicoId>" para "não exigir deferimento manual"
    E Parametro de Servico "12" com valor "0" para o Servico "<servicoId>" para "não exigir senha do servidor"
    E Parametro de Servico "53" com valor "1" para o Servico "<servicoId>" para "prazo fixo"
    E Orgao ativo de Identificador "<orgaoId>" no Estabelecimento "<estabelecimentoId>"
    E Convenio ativo de Verba "<verba>" para o Servico "<servicoId>", Consignataria "<consignatariaId>" e Orgao "<orgaoId>"
    E Servidor ativo de CPF "<cpf>" e Matricula "<matricula>" no Orgao "<orgaoId>"
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico com identificador "<servicoId>"
    E Usuario preenche matricula de servidor com "<matricula>"
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "<valorPrestacao>" e "<prazo>"
    E Usuario clica em Concluir
    Entao Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>" e "<situacao>"

    Exemplos:
    | cpf            | matricula | orgaoId | estabelecimentoId | servicoId | servicoOrigemId | nseCodigo | consignatariaId | verba | valorPrestacao | prazo | situacao |
    | 123.456.789-00 | 987754321 | 9877    | 01                | 990       | 989             | 7         | 001             | 989   | 250            | 1     | Deferida |

