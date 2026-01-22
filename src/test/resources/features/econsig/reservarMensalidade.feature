#language:pt

Funcionalidade: Reservar Mensalidade

	Contexto: Sistema permite senhaAutorizacaoServidor opcional e Convenio para Servico Emprestimo
		  Dado Sistema permite CPF opcional reserva mensalidade
		  E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
	    E UsuCsa com Convenio para Servico Mensalidade
	    E Parametro de Sistema com valores zerados para Limite Ades
	   	E Parametro Registro Servidor com valores zerados
	   
	 Cenário: Reservar Margem de mensalidade informando valor de mensalidade percentual alteravel retem margem sem Base de Calculo
     Dado Servico Mensalidade com valor alteravel Percentual Que Retem Margem e Nao Possui Base de Calculo
	   Quando UsuCsa Logado
	   E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem   
	   E Usuario seleciona Servico Mensalidade
	   E Usuario preenche matricula de servidor
	   E Usuario clica no botao Pesquisar
	   E Usuario preenche campo valor parcela "100"
  	 E Usuario marca prazo indeterminado
	   E Usuario clica no botao Confirmar para prosseguir
	   E verifica se prazo Indeterminado esta correto
	   E Usuario clica em Concluir para prosseguir
	   Então Sistema lanca mensagem de erro de margem "NÃO FOI POSSÍVEL CALCULAR O VALOR DA RESERVA POIS A BASE DE CÁLCULO É INVÁLIDA."   

   Cenário: Reservar Margem de mensalidade com somatório de capital devido maior que o permitido
     Dado Que o valor maximo de capital devido para contratos aberto esteja configurado
	   Quando UsuCsa Logado
	   E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem   
	   E Usuario seleciona Servico Mensalidade
	   E Usuario preenche matricula de servidor
	   E Usuario clica no botao Pesquisar
	   E Usuario preenche campo valor parcela "500"
  	 E Usuario marca prazo indeterminado
	   E Usuario clica no botao Confirmar para prosseguir
	   Então Sistema lanca mensagem de erro de margem "O SOMATÓRIO DO CAPITAL DEVIDO DOS CONTRATOS ABERTOS DESTE SERVIDOR EXCEDE O TETO MÁXIMO PERMITIDO"

   Cenário: Reservar Margem de mensalidade informando valor maior que o máximo permitido
     Dado Servico Mensalidade com valor maximo de capital devido configurado
	   Quando UsuCsa Logado
	   E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem   
	   E Usuario seleciona Servico Mensalidade
	   E Usuario preenche matricula de servidor
	   E Usuario clica no botao Pesquisar
	   E Usuario preenche campo valor parcela "500"
  	 E Usuario marca prazo indeterminado
	   E Usuario clica no botao Confirmar para prosseguir
	   Então Sistema lanca mensagem de erro de margem "O CAPITAL DEVIDO MÁXIMO PERMITIDO PARA ESTE CONTRATO, OU SEJA O VALOR DA PARCELA MULTIPLICADO PELA QUANTIDADE DE PARCELAS, É R$ 1,00."
	   
	 Cenário: Reservar Margem de mensalidade informando valor de mensalidade monetario
	   Dado Servico Mensalidade com valor alteravel Monetario
	   Quando UsuCsa Logado
	   E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem   
	   E Usuario seleciona Servico Mensalidade
	   E Usuario preenche matricula de servidor
	   E Usuario clica no botao Pesquisar
	   E Usuario preenche campo valor parcela "130"
	   E Usuario marca prazo indeterminado
	   E Usuario clica no botao Confirmar para prosseguir
	   E verifica se prazo Indeterminado esta correto
	   E Usuario clica em Concluir
	   Entao Sistema mostra tela de conclusao com as informacoes "130", "Indeterminado" e "Deferida"
   
	Cenário: Reservar Margem de mensalidade informando valor de mensalidade percentual alteravel nao retem margem
	   Dado Servico Mensalidade com valor alteravel Percentual Que Nao Retem
	   Quando UsuCsa Logado
	   E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem   
	   E Usuario seleciona Servico Mensalidade
	   E Usuario preenche matricula de servidor
	   E Usuario clica no botao Pesquisar
	   E Usuario preenche campo valor parcela "10"
	   E Usuario marca prazo indeterminado
	   E Usuario clica no botao Confirmar para prosseguir
	   E verifica se prazo Indeterminado esta correto
	   E Usuario clica em Concluir
	   Entao Sistema mostra tela de conclusao com as informacoes "10", "Indeterminado" e "Deferida"
	   
  Cenário: Reservar Margem informando valor de mensalidade percentual alteravel retem margem
   	Dado Servico Mensalidade com valor alteravel Percentual Que Retem Margem e Possui Base de Calculo
   	E Que a base de calculo do parametro condiz com a tabela de base de calculo
  	Quando UsuCsa Logado
   	E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem   
   	E Usuario seleciona Servico Mensalidade
   	E Usuario preenche matricula de servidor 
   	E Usuario clica no botao Pesquisar
  	E Usuario preenche campo valor parcela "10"
  	E Usuario marca prazo indeterminado  	
   	E Usuario clica no botao Confirmar para prosseguir
   	E verifica se prazo Indeterminado esta correto
   	E Usuario clica em Concluir
   	Entao Sistema mostra tela de conclusao com as informacoes "300,00", "Indeterminado" e "Deferida"

   Cenário: Reservar Margem de mensalidade informando valor de mensalidade percentual alteravel retem margem com a Base de Calculo Padrao
   	Dado Servico Mensalidade com valor alteravel Percentual Que Retem Margem e Possui Base de Calculo
   	E Verifica que sua base de calculo do parametro condiz com a base de calculo padrao
   	Quando UsuCsa Logado
   	E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem   
   	E Usuario seleciona Servico Mensalidade
   	E Usuario preenche matricula de servidor   
   	E Usuario clica no botao Pesquisar
   	E Usuario preenche campo valor parcela "10"
  	E Usuario marca prazo indeterminado   	
   	E Usuario clica no botao Confirmar para prosseguir
   	E verifica se prazo Indeterminado esta correto
   	E Usuario clica em Concluir
   	Entao Sistema mostra tela de conclusao com as informacoes "10", "Indeterminado" e "Deferida"
        
   