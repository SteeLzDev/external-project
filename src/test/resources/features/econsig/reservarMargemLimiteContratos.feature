#language:pt

Funcionalidade: Reservar Margem com limites de contratos  

	Contexto: Sistema permite senhaAutorizacaoServidor opcional e Convenio para Servico Emprestimo
		  Dado Sistema permite CPF opcional na pesquisaServidor e senhaAutorizacaoServidor opcional
		  E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
		  E UsuCsa com permissao para confirmar reserva e deferimento automatico
	    E UsuCsa com Convenio para Servico Emprestimo
	    E Sistema trabalha com CET ou correcao do valor presente
      E Parametro de Sistema com valores zerados para Limite Ades
      E Parametro Registro Servidor com valores zerados

  
  Esquema do Cenário: Reserva de margem com serviço limitando capital devido à base de cálculo, com ade de valor líquido dentro do limite
    Dado Servidor com contratos ativos para o servico
    E Servico com limita capital devido a base de calculo
    E Servidor com base de calculo cadastrado    
    E Usuario verifica o valor liquido e menor do que residual da base de calculo "<valorLiquido>"
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"	
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao | 
      |       90       |    1  |         500  | Deferida |      
   
    
	Esquema do Cenário: Reserva de margem de emprestimo dentro do limite de CSAs
    Dado Parametros de Sistema de limites de numero de CSAs nao configurados
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao |
      |       200      |   10  |      2000    | Deferida |                
      
      
	 Esquema do Cenário: Reserva de margem de emprestimo dentro do limite de ADEs para o servidor por serviço
    Dado Parametro de Sistema "<tpcCodigo>" com valor "<psiVlr>"
    E Servico Possui limite de ADE ativos maior que numero de ADEs do servidor 
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"		
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | tpcCodigo | psiVlr   | situacao | 
      |       200      |  10   |      2000    |    108    |    null  | Deferida |
   

  Cenário: Reserva de margem de emprestimo acima do limite de ADEs para o servidor por serviço
    Dado Parametro de Sistema "108" com valor "null"
    E Servico Possui limite de ADE ativos menor que numero de ADEs do servidor
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Então Sistema lanca mensagem de erro de margem "NÃO É POSSÍVEL INSERIR ESTA RESERVA POIS EXCEDE O LIMITE DE CONTRATOS PARA A MESMA MATRÍCULA E CONSIGNATÁRIA/VERBA"	
 
  Esquema do Cenário: Reserva de margem de emprestimo dentro do limite de ADEs por serviço, mas acima do limite de ADEs por convênio
    Dado Servico Possui limite de ADE ativos maior que numero de ADEs do servidor 
    E Limite de Ades do RSE por convenio menor que numero de ADEs do servidor    
    E Parametro de Sistema "<tpcCodigo>" com valor "<psiVlr>"
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"		
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | tpcCodigo | psiVlr | situacao |   
      |       200      |   10  |      2000    |    108    |  null  | Deferida |  
 	
  Esquema do Cenário: Reserva de margem de emprestimo acima do limite de ADEs para o servidor por convênio, mas abaixo do limite de ADEs por Serviço para o servidor
    Dado Parametro de Sistema "<tpcCodigo>" com valor "<psiVlr>"
    E Convenio Possui limite de ADE ativos acima do limite do numero de ADEs do servidor
    E Servico Possui limite de ADE ativos maior que numero de ADEs do servidor    
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"		
  Exemplos: 
      | valorPrestacao | prazo | valorLiquido | tpcCodigo | psiVlr | situacao |  
      |        200     |   10  |      2000    |     108   |   null | Deferida |
  
   Esquema do Cenário: Reserva de margem de emprestimo abaixo do limite de ADEs para o servidor por serviço
    Dado Servidor com contratos ativos para o servico
    E Servico Possui limite de ADE ativos acima do numero de ADEs do servidor para este   
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"	
  Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao |   
      |       200      |    10 |         2000 | Deferida |
   
  Cenário: Reserva de margem de emprestimo com bloqueio por parâmetro de serviço de limite de Ades por Serviço
    Dado Servico Bloqueado por parametro de servico por Rse "130" com valor zero  
    E Servidor com contratos ativos para o servico
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor 
    E Usuario clica no botao Pesquisar
    Então Sistema lanca mensagem de erro de margem "NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS O SERVIDOR ESTÁ BLOQUEADO PARA O CONVÊNIO ESCOLHIDO"	
  
  Esquema do Cenário: Reserva de margem de emprestimo abaixo do limite de ADEs para uma natureza de serviço
    Dado Servidor com contratos ativos para o servico
    E Natureza de Servico Possui limite de ADE acima do numero de ADEs do servidor para este   
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"	
	
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao |
      |        200     |    10 |         2000 | Deferida |       
   
	Esquema do Cenário: Reserva de margem de emprestimo acima do limite de ADEs para uma natureza de serviço,
  										 mas com sistema ignorando contratos a concluir
   	Dado Sistema ignora contratos a concluir no limite de Ades 
   	E Servidor com contratos ativos para o servico
   	E Servidor possui contrato ativo a concluir 
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"		
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao |
      |       200      |    10 |         2000 | Deferida |
  
  	  
  Cenário: Reserva de margem de emprestimo acima do limite de ADEs para uma natureza de serviço
    Dado Servidor com contratos ativos para o servico
    E Natureza de Servico bloqueado para novas reservas por parametro de servico "236"
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo    
    E Usuario preenche matricula de servidor       
    E Usuario clica no botao Pesquisar
    Então Sistema lanca mensagem de erro de margem "NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS O SERVIDOR ESTÁ BLOQUEADO PARA O CONVÊNIO ESCOLHIDO"	

  Esquema do Cenário: Reserva de margem de emprestimo além do período de restrição de novos contratos dado por parâmetro de serviço
    Dado Servidor com contratos ativos para o servico
    E Servidor sem Ade no periodo de restricao de novos ades 
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"	
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao | 
      |       200      |    10 |         2000 | Deferida |
  
	
  Cenário: Reserva de margem de emprestimo dentro do período de restrição de novos contratos dado por parâmetro de serviço,
                    e com contrato ativo já neste período de restrição
    Dado Servidor com Ade no periodo de restricao de novos ades
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo   
    E Usuario preenche matricula de servidor    
    E Usuario clica no botao Pesquisar
    Então Sistema lanca mensagem de erro de margem "NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS JÁ EXISTE OUTRO CONTRATO CADASTRADO DENTRO DO PERÍODO LIMITE."	
    	  	
  Esquema do Cenário: Reserva de margem de emprestimo com servidor com margem de limite para CSA dentro do limite de margem por CSA
   	Dado Reserva verificando limite de margem "<marCodigo>" para CSA
    E Servidor com contratos ativos para o servico        
    E Servidor possui margem de limite "<marCodigo>" para CSA cadastrado "<margem>"
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"	
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | marCodigo | situacao | margem |  
      |        200     |    10 |         2000 |     1     | Deferida |  10000 |
      
  Esquema do Cenário: Reserva de margem de emprestimo com servidor com margem de limite para CSA fora do limite de margem por CSA
   	Dado Reserva verificando limite de margem "<marCodigo>" para CSA
    E Servidor com contratos ativos para o servico        
    E Servidor possui margem de limite "<marCodigo>" para CSA cadastrado "<margem>" 
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"    
    E Usuario preenche campo numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"
    E Usuario clica no botao Confirmar para prosseguir
    Então Sistema lanca mensagem de erro de margem "NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA, POIS O VALOR DA PARCELA EXCEDE O LIMITE DE MARGEM DISPONÍVEL PARA ESTA CONSIGNATÁRIA."	
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | marCodigo | situacao | margem |
      |        200     |    10 |         2000 |     101   | Deferida |    0   |
           	
  Cenário: Reserva de margem de emprestimo com vínculo do servidor com bloqueio para um convênio
    Dado Servidor com contratos ativos para o servico
    E Servidor possui vinculo com convenio do Servico para a CSA
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor        
    E Usuario clica no botao Pesquisar
    Então Sistema lanca mensagem de erro de margem "NÃO É PERMITIDO RESERVAR MARGEM PARA O VÍNCULO DESTE SERVIDOR: VINCULADO"
      	
  Esquema do Cenário: Reserva de margem de emprestimo exigindo que o servidor seja correntista da CSA com este servidor devidamente correntista
    Dado Servidor com contratos ativos para o servico
    E Servidor correntista da Consignataria
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"	    
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao | 
      |       200      |    10 |         2000 | Deferida |
      	
  Cenário: Reserva de margem de emprestimo exigindo que o servidor seja correntista da CSA com este servidor não correntista
    Dado Servidor com contratos ativos para o servico
    E Servidor nao correntista da Consignataria
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor    
    E Usuario clica no botao Pesquisar
    Então Sistema lanca mensagem de erro de margem "NÃO FOI POSSÍVEL REALIZAR A OPERAÇÃO, POIS A CONSIGNATÁRIA ESTÁ HABILITADA A FAZER OPERAÇÕES APENAS PARA SERVIDORES QUE SEJAM CORRENTISTAS."	
      
   Esquema do Cenário: Reserva de margem verificando a data do último contrato liquidado do servidor fora do periodo de restrição
		Dado Servidor com contratos ativos para o servico            
    E Data de inclusao fora do periodo de restricao do servico apos ultima liquidacao de contrato do servidor
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao |  
      |       200      |    10 |         2000 | Deferida |
      
 
 Cenário: Reserva de margem verificando a data do último contrato liquidado do servidor dentro do periodo de restrinção
    Dado Servidor com contratos ativos para o servico            
    E Data de inclusao dentro do periodo de restricao do servico apos ultima liquidacao de contrato do servidor
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Então Sistema exibe mensagem com o erro "NÃO É POSSÍVEL INSERIR ESTA RESERVA POIS UMA LIQUIDAÇÃO FOI REALIZADA EM 01/09/2020 SENDO NECESSÁRIO AGUARDAR"
        
  Cenário: Reserva de margem de emprestimo com limite de ADEs por convênio menor que número de contratos do servidor para este convênio
    Dado Parametro de Sistema "108" com valor "null"
    E Nao possui limite de ADE ativos por Servico por servidor
    E Convenio Possui limite de ADE ativos menor que numero de ADEs do servidor  
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo    
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Então Sistema lanca mensagem de erro de margem "NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS O SERVIDOR ESTÁ BLOQUEADO PARA O CONVÊNIO ESCOLHIDO"		

 Esquema do Cenário: Reserva de margem de emprestimo sem limite de contratos por serviço e limite de contratos por convenio para o serviço acima do número de contratos do servidor
    Dado Servidor com contratos ativos para o servico
    E Nao possui limite de ADE ativos por Servico por servidor
    E Limite Ades Por Cnv Para o Servico maior que numero de ADEs do servidor 
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"	
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao |    
      |        200     |   1   |       2000   | Deferida |        
 
	Cenário: Reserva de margem de emprestimo com limite de ADEs para convênio maior que o permitido e limite de contratos por serviço menor do que o número de contratos do servidor
    Dado Servidor com contratos ativos para o servico
    E Limite Ades Por Cnv Para o Servico menor que numero de ADEs do servidor        
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Então Sistema lanca mensagem de erro de margem "NÃO É POSSÍVEL INSERIR ESTA RESERVA POIS EXCEDE O LIMITE DE CONTRATOS PARA A MESMA MATRÍCULA E CONSIGNATÁRIA/VERBA"
 
	Cenário: Reserva de margem de emprestimo acima do limite de ADEs para uma natureza de serviço
    Dado Servidor com contratos ativos para o servico
    E Natureza de Servico Possui limite de ADE no limite do numero de ADEs do servidor para este
    Quando  UsuCsa Logado       
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Então Sistema lanca mensagem de erro de margem "NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS EXCEDE O LIMITE DE CONTRATOS DO SERVIDOR PARA ESTA NATUREZA DE SERVIÇO."	

	Cenário: Reserva de margem de emprestimo acima do limite de ADEs para o servidor por serviço
    Dado Servidor com contratos ativos para o servico
    E Servico Possui limite de ADE ativos no limite do numero de ADEs do servidor para este
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar
    Então Sistema lanca mensagem de erro de margem "NÃO É POSSÍVEL INSERIR OU ALTERAR ESTA RESERVA POIS EXCEDE O LIMITE DE CONTRATOS DO SERVIDOR PARA ESTE SERVIÇO."	
 
	Esquema do Cenário: Reserva de margem com serviço limitando capital devido à base de cálculo, com ade de valor líquido além do limite
    Dado Servidor com contratos ativos para o servico
    E Servico com limita capital devido a base de calculo
    E Servidor com base de calculo cadastrado    
    E Usuario verifica que valor liquido mais o capital devido total e maior do que a base de calculo
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo    
   	E Usuario preenche matricula de servidor       
    E Usuario clica no botao Pesquisar
    E Usuario preenche campo valor parcela "<valorPrestacao>"    
    E Usuario preenche campo numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"
    E Usuario clica no botao Confirmar para prosseguir
    Então Sistema lanca mensagem de erro de margem "O SOMATÓRIO DO CAPITAL DEVIDO DOS CONTRATOS ABERTOS DESTE SERVIDOR EXCEDE O TETO MÁXIMO PERMITIDO"	
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido |
      |       200      |    10 |         2000 |                 
   
      	
  Esquema do Cenário: Reserva de margem de emprestimo acima do limite de ADEs para o servidor por convênio, mas com sistema ignorando contratos a
                    concluir na contagem
    Dado Sistema ignora contratos a concluir no limite de Ades
    E Servidor com contratos ativos para o servico    
    E Servidor possui contrato ativo a concluir    
    E Convenio Possui limite de ADE ativos acima do limite do numero de ADEs do servidor
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
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"	
  Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao |   
      |       200      |   10  |      2000    | Deferida |
  
	Esquema do Cenário: Reserva de margem verificando bloqueio de função, com função de reserva fora do período de bloqueio
    Dado Reserva verificacao bloqueio de funcao    
    E Servidor com contratos ativos para o servico
    E Alem do periodo de Bloqueio da Funcao Reservar
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor        
    E Usuario clica no botao Pesquisar   
    E Usuario preenche campo valor parcela "<valorPrestacao>"    
    E Usuario preenche campo numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"    
    E Usuario clica no botao Confirmar para prosseguir
    E Verifica que Bloqueio da Funcao Reserva Foi Removido 
    E verifica se dados estao corretos "<valorPrestacao>", "<prazo>" e "<valorLiquido>"
    E Usuario clica em Concluir
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<situacao>"	
	Exemplos: 
      | valorPrestacao | prazo | valorLiquido | situacao | 
      |       200      |    10 |         2000 | Deferida |
  
  Cenário: Reserva de margem verificando bloqueio de função, com função de reserva dentro do período de bloqueio
    Dado Reserva verificacao bloqueio de funcao    
    E Servidor com contratos ativos para o servico
    E Dentro do periodo de Bloqueio da Funcao Reservar  
    Quando UsuCsa Logado    
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor        
    E Usuario clica no botao Pesquisar   
    Então Sistema exibe mensagem com o erro "A OPERAÇÃO 'RESERVAR MARGEM' PARA ESTE SERVIDOR NÃO PODE SER REALIZADA ATÉ A DATA LIMITE"
   
      	  
   
