#language:pt

@tag
Funcionalidade: Confirmar Consignacao
  Cenários de sucesso e erro do caso de uso de confirmar consignação,
  levando-se em conta parametrizacoes do sistema pertinentes a este.
  
  Contexto: Usuario CSA com Convenio para Servico Emprestimo e permissao para confirmar consignacao
	  Dado Sistema permite CPF opcional na pesquisaServidor e senhaAutorizacaoServidor opcional
	  E que tenha incluido o item de menu "8" no favoritos para usuário "csa"
	  E que tenha incluido o item de menu "10" no favoritos para usuário "csa"
    E UsuCsa com Convenio para Servico Emprestimo
    E Parametro Registro Servidor com valores zerados

  @ConfirmarConsignacaoSucesso
  Esquema do Cenário: Confirmar Consignacao com status Aguardando Confirmacao com deferimento automático.
    Dado a lista de adeNumeros
      | adeNumero |
      | 9767744   |
		E Contrato para servidor "<rseCodigo>" com "<adeNumero>" para "<cnvCodigo>" com status "<sadCodigo>"	cujo login responsavel "csa2"	    
    E UsuCsa com permissao para confirmar reserva e deferimento automatico
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu Confirmar Reserva
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Entao Carrega tela de listagem e escolha multipla de consignacoes
    Quando Usuario seleciona ades com adenumeros dados
    E Clica no botao confirmar de listagem de consignacao
    Entao exibe tela de confirmar reserva
    Quando Usuario seleciona motivo de operacao
    E clica em salvar na tela de confirmacao de reserva
    E exibe mensagem de confirmacao de contrato com sucesso
    E confere contrato com adeNumero "<adeNumero>" com status "<result>"    
		
		Exemplos: 
      | adeNumero        | rseCodigo                            | sadCodigo | situacao | cnvCodigo                        | ades        | result          |
      |     9767744      |   48178080808080808080808080808C80   |      1    | Deferida | 751F8080808080808080808080809Z85 |  9767744    |        4        |
      
  @ConfirmarConsignacaoSucesso
  Esquema do Cenário: Confirmar Consignacao com status Aguardando Confirmacao.
    Dado a lista de adeNumeros
      | adeNumero |
      | 9767745   |
		E Contrato para servidor "<rseCodigo>" com "<adeNumero>" para "<cnvCodigo>" com status "<sadCodigo>"	cujo login responsavel "csa2"	    
    E UsuCsa com permissão para confirmar reserva
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu Confirmar Reserva
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Entao Carrega tela de listagem e escolha multipla de consignacoes
    Quando Usuario seleciona ades com adenumeros dados
    E Clica no botao confirmar de listagem de consignacao
    Entao exibe tela de confirmar reserva
    Quando Usuario seleciona motivo de operacao
    E clica em salvar na tela de confirmacao de reserva
    E exibe mensagem de confirmacao de contrato com sucesso
    E confere contrato com adeNumero "<adeNumero>" com status "<result>"    
		
		Exemplos: 
      | adeNumero        | rseCodigo                            | sadCodigo | situacao | cnvCodigo                        | ades        | result          |
      |     9767745      |   48178080808080808080808080808C80   |      1    | Deferida | 751F8080808080808080808080809Z85 |  9767745    |        2        |
      
  @ConfirmarConsignacaoSemSucesso
  Esquema do Cenário: Confirmar Consignacao com status Solicitado.
    Dado a lista de adeNumeros
      | adeNumero |
      | 9767746   |
		E Contrato para servidor "<rseCodigo>" com "<adeNumero>" para "<cnvCodigo>" com status "<sadCodigo>"	cujo login responsavel "csa2"	    
    E UsuCsa com permissão para confirmar reserva
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu Confirmar Reserva
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Entao exibe mensagem de nenhuma consignacao encontrada para matricula "<matricula>"
		
		Exemplos: 
      | adeNumero        | rseCodigo                            | sadCodigo | situacao   | cnvCodigo                        | ades        | matricula | result         |
      |     9767746      |   48178080808080808080808080808C80   |      0    | Solicitado | 751F8080808080808080808080809Z85 |  9767746    |  123456   |   2            |

  @ConfirmarConsignacaoSucesso
  Esquema do Cenário: Confirmar Solicitacao via consultar consignacao.
    Dado a lista de adeNumeros
      | adeNumero |
      | 9767752   |
		E Contrato para servidor "<rseCodigo>" com "<adeNumero>" para "<cnvCodigo>" com status "<sadCodigo>"	cujo login responsavel "csa2"	    
    E UsuCsa com permissão para confirmar solicitacao
    E UsuCsa sem exigencia de segunda senha para confirmar solicitacao
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu Consultar Consignacao    
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Entao Carrega tela de listagem e escolha multipla de consignacoes
    Quando Usuario clica em visualizar contrato com adenumero "<adeNumero>"
    E exibe tela de detalhe da consignacao
    E Usuario clica no botao acoes
    E Usuario seleciona opcao "<acao>" no botao acoes
    E Clica Confirmacao da Solicitacao
    E clica em salvar na tela de confirmacao de solicitacao
    Entao exibe mensagem de sessao de sucesso "<msgSessao>"     
		
		Exemplos: 
      | adeNumero        | rseCodigo                            | sadCodigo | cnvCodigo                        | ades        | matricula | acao                  | result | msgSessao               |	
      |     9767752      |   48178080808080808080808080808C80   |      0    | 751F8080808080808080808080809Z85 |  9767752    |  123456   | Confirmar solicitação |    2   | Solicitação confirmada. |

  @ConfirmarConsignacaoSemSucesso
  Esquema do Cenário: Confirmar Consignacao com servidor em status pendente.
    Dado a lista de adeNumeros
      | adeNumero |
      | 9767747   |
		E Contrato para servidor "<rseCodigo>" com "<adeNumero>" para "<cnvCodigo>" com status "<sadCodigo>"	cujo login responsavel "csa2"
		E servidor "<rseCodigo>" com status "<srsCodigo>"
    E UsuCsa com permissão para confirmar reserva
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu Confirmar Reserva
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Entao Carrega tela de listagem e escolha multipla de consignacoes
    Quando Usuario seleciona ades com adenumeros dados
    E Clica no botao confirmar de listagem de consignacao
    Entao exibe tela de confirmar reserva
    Quando Usuario seleciona motivo de operacao
    E clica em salvar na tela de confirmacao de reserva    
    Entao exibe mensagem de sessao de erro "<msgSessao>"     
    E confere contrato com adeNumero "<adeNumero>" com status "<result>"   
		
		Exemplos: 
      | adeNumero        | rseCodigo                           | srsCodigo | sadCodigo | situacao | cnvCodigo                        | ades        | result          | msgSessao                                                                                 |
      |     9767747      |   48178080808080808080808080808C80  |    5      |      1    | Deferida | 751F8080808080808080808080809Z85 |  9767747    |        1        | Autorização não pode ser confirmada. O cadastro do servidor está com a situação Pendente. |

  @ConfirmarConsignacaoSucesso
  Esquema do Cenário: Confirmar Consignacao com servidor em status ativo.
    Dado a lista de adeNumeros
      | adeNumero |
      | 9767747   |
		E Contrato para servidor "<rseCodigo>" com "<adeNumero>" para "<cnvCodigo>" com status "<sadCodigo>"	cujo login responsavel "csa2"
		E servidor "<rseCodigo>" com status "<srsCodigo>"
    E UsuCsa com permissão para confirmar reserva
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu Confirmar Reserva
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Entao Carrega tela de listagem e escolha multipla de consignacoes
    Quando Usuario seleciona ades com adenumeros dados
    E Clica no botao confirmar de listagem de consignacao
    Entao exibe tela de confirmar reserva
    Quando Usuario seleciona motivo de operacao
    E clica em salvar na tela de confirmacao de reserva    
    Entao exibe mensagem de sessao de sucesso "<msgSessao>"
    E confere contrato com adeNumero "<adeNumero>" com status "<result>"   
		
		Exemplos: 
      | adeNumero        | rseCodigo                           | srsCodigo | sadCodigo | situacao | cnvCodigo                        | ades        | result          | msgSessao                       |
      |     9767747      |   48178080808080808080808080808C80  |    1      |      1    | Deferida | 751F8080808080808080808080809Z85 |  9767747    |        2        | Operação concluída com sucesso. |

  @ConfirmarConsignacaoSemSucesso
  Esquema do Cenário: Confirmar Consignacao destino de Renegociacao.    
		Dado Contrato para servidor "<rseCodigo>" com "<adeNumero>" para "<cnvCodigo>" com status "<sadCodigo>"	cujo login responsavel "csa2"
		E Contrato para servidor "<rseCodigo>" com "<adeNumeroOrigem>" para "<cnvCodigo>" com status "<sadCodigoOrigem>"	cujo login responsavel "csa2"
		E contrato "<adeNumero>" destino de relacionamento de contrato "<adeNumeroOrigem>" do tipo "<tntCodigo>"	
		E UsuCsa com permissão para confirmar reserva     
    E UsuCsa sem permissao para confirmar renegociacao
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu Confirmar Reserva
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Entao exibe mensagem de nenhuma consignacao encontrada para matricula "<matricula>"    
		
		Exemplos: 
      | adeNumero        | adeNumeroOrigem  | rseCodigo                            | sadCodigo | sadCodigoOrigem | cnvCodigo                        | ades        | tntCodigo       | matricula       |
      |     9767748      |    9767749       |   48178080808080808080808080808C80   |      1    | 11              | 751F8080808080808080808080809Z85 |  9767748    |    6            |   123456        |
      
  @ConfirmarConsignacaoSemSucesso
  Esquema do Cenário: Confirmar Consignacao destino de Compra.    
		Dado Contrato para servidor "<rseCodigo>" com "<adeNumero>" para "<cnvCodigo>" com status "<sadCodigo>"	cujo login responsavel "csa2"
		E Contrato para servidor "<rseCodigo>" com "<adeNumeroOrigem>" para "<cnvCodigoOrigem>" com status "<sadCodigoOrigem>"	cujo login responsavel "csa"
		E contrato "<adeNumero>" destino de relacionamento de contrato "<adeNumeroOrigem>" do tipo "<tntCodigo>"	
		E UsuCsa com permissão para confirmar reserva     
    E UsuCsa sem permissao para confirmar renegociacao
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu Confirmar Reserva
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Entao exibe mensagem de nenhuma consignacao encontrada para matricula "<matricula>"    
		
		Exemplos: 
      | adeNumero        | adeNumeroOrigem  | rseCodigo                            | sadCodigo | sadCodigoOrigem | cnvCodigo                        | tntCodigo       | matricula       | cnvCodigoOrigem                   |
      |     9767750      |    9767751       |   48178080808080808080808080808C80   |      1    | 11              | 751F8080808080808080808080809Z85 |    7            |   123456        |  751F8080808080808080808080809D80 |          
  
