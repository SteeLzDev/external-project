#language:pt

Funcionalidade: Manutenção Consignatária
	Permite manipular o cadastro de entidades consignatárias presentes no sistema, 
	como inclusão de novas entidades, bloqueio e alteração de cadastro.

	Contexto: Consignantaria está ativo
	  Dado que a consignataria esteja ativo
       
  Esquema do Cenário: Bloquear consignatária sem data para desbloqueio automático 
  	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E bloquear a consignataria sem data para desbloqueio automatico 
    Então exibe a mensagem "Consignatária bloqueada"
    E verifica que csa "csa" nao consegue reservar margem
   
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    
  Esquema do Cenário: Bloquear consignatária com data para desbloqueio automático 
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E bloquear a consignataria com data para desbloqueio automatico 
    Então exibe a mensagem "Consignatária bloqueada"
    E verifica que csa "csa" nao consegue reservar margem
   
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
         
  Esquema do Cenário: Desbloquear consignatária
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que a consignataria esteja bloqueado
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E desbloquear a consignataria
    Então exibe a mensagem "Consignatária desbloqueada"
    E verifica que csa consegue reservar margem
   
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
     
  Cenário: Tentar desbloquear consignatária que foi bloqueada por segurança com usuário cse
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que a consignataria esteja bloqueada por seguranca
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E desbloquear a consignataria
    Então exibe a mensagem de erro "Não é possível desbloquear a consignatária 'BANCO TREINAMENTO', pois ela foi bloqueada por segurança."

  #Cenário: Desbloquear consignatária que foi bloqueada por segurança com usuário Suporte
   	#Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "zetra_igor"
    #E que a consignataria esteja bloqueada por segurança
   	#E que o usuario cse ou sup "zetra_igor" esteja logado
    #Quando acessar menu Manutencao > Consignantarias
    #E desbloquear a consignataria
   	#Então exibe a mensagem "Consignatária desbloqueada"
    #E verifica que csa consegue reservar margem
    
   Esquema do Cenário: Editar consignatária com usuário cse e sup
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E editar a consignataria com usuario "<usuario>"
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    
  Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    
   
   Cenário: Editar consignatária com usuário csa
   	Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa" esteja logado
    Quando acessar menu Manutencao > Consignantaria
    E editar a consignataria  
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    
  Cenário: Editar consignatária com campos inválidos
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E acessar tela de edicao  
    Então tentar editar consignataria com campos invalidos
        
 	Esquema do Cenário: Editar consignatária para não permite incluir novas consignações
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E editar consignataria para nao permite incluir novas consignacoes
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que csa "csa" nao consegue incluir consignacao
     
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |  
    
 	Cenário: Tentar logar com cadastro de IP endereço de acesso no login ativo
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E editar consignataria para verificar cadastro de IP endereco de acesso no login
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que csa "csa" nao consegue logar
    
	Cenário: Logar csa com cadastro de IP de endereço de acesso no login ativo e com Ip cadastro
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E editar consignataria para verificar cadastro de IP endereco de acesso no login
    E cadastrar Ip de acesso
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que csa "csa" consegue logar
       
  Cenário: Alterar o parametro exige certificado digital para consignatária
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
   	E alterar o parametro exige certificado digital
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    E usuario "csa2" nao consegue logar
    
  Cenário: Tentar alterar o código consignatária para um já existente 
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
   	E alterar o codigo
    E clicar em salvar
    Então Sistema exibe mensagem com o erro "Não foi possível alterar esta consignatária pois existe outra no sistema com o mesmo código."    
    
  Cenário: Tentar excluir consignatária com dependentes
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
   	E excluir consignataria
    Então Sistema exibe mensagem com o erro "Não foi possível excluir a consignatária selecionada, pois ela possui dependentes no sistema."
       
 Cenário: Pesquisar consignatária
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
   	E pesquisar consignataria pelo filtro "Nome" com "BANCO TREINAMENTO"
   	Entao exibe as consignataria com o filtro Nome
    Quando pesquisar consignataria pelo filtro "Código" com "001"
   	Entao exibe as consignataria com o filtro Codigo
    Quando pesquisar consignataria pelo filtro "Código de Verba" com "145"
   	Entao exibe as consignataria com o filtro Codigo de Verba
    Quando pesquisar consignataria pelo filtro "Bloqueado" com ""
   	Entao exibe as consignataria com o filtro Bloqueado
    Quando pesquisar consignataria pelo filtro "Desbloqueado" com ""
   	Entao exibe as consignataria com o filtro Desbloqueado
   
  Esquema do Cenário: Liberação de arquivo de movimento
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
   	E acessar opcao Imprimir
    Então exibe a tela com a lista de consignatarias
  
  Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
      
  Esquema do Cenário: Criar consignatária com sucesso
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o campo Codigo Zetrasoft nao seja obrigatorio
    E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
   	E criar consignataria com codigo "<codigo>"
   	E clicar em salvar
    Então exibe a mensagem "Consignatária criada com sucesso."
            
  Exemplos:
    | usuario    | codigo |
    | cse        |   002  |
    | zetra_igor |   003  |

  Cenário: Tentar criar consignatária com código já existente
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
   	E criar consignataria com codigo "001"
   	E clicar em salvar
    Então exibe a mensagem de erro "Não é possível criar esta consignatária. Existe outra no sistema para o mesmo código."
   	
  Cenário: Tentar criar consignatária sem informar campos obrigatórios
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
   	Então tentar criar consignataria sem informar campos obrigatorios
 
  Esquema do Cenário: Bloquear serviços
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E bloquear um servico
   	Então exibe a mensagem "Alterações salvas com sucesso"
   	E verifica que nao exibe o servico bloqueado para a consignataria
  
    Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
  
  Esquema do Cenário: Desbloquear serviços
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E desbloquear um servico
   	Então exibe a mensagem "Alterações salvas com sucesso"
   	E verifica que exibe o servico desbloqueado para a consignataria
      	
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    
  Esquema do Cenário: Cadastrar novo índice	para o serviço
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E acessar indice do servico "<servico>"
    E cadastrar novo indice "<indice>"
   	Então exibe a mensagem "Alterações salvas com sucesso."
   	E csa reserva margem com o indice cadastrado "<indice>"
   	   	
 	Exemplos:
    | usuario    | servico | indice |
    | cse        |   001   |   19   |
    | zetra_igor |   001   |   12   |
    
   Cenário: Tentar cadastrar novo índice	para o serviço bloqueado
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E acessar indice do servico "PSASUB01"
   	Então Sistema exibe mensagem de erro "Para editar os índices deste serviço você deve desbloqueá-lo."
   		
   Cenário: Cadastrar vários índices	para o serviço
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E acessar indice do servico "020"
    E cadastrar varios indices
   	Então exibe a mensagem "Alterações salvas com sucesso."
   	E csa reserva margem com um dos indices cadastrado
  	 
  Esquema do Cenário: Cadastrar índice e tentar criar duas reservar de margem com o mesmo índice
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E acessar indice do servico "<servico>"
    E cadastrar novo indice "<indice>"
   	E exibe a mensagem "Alterações salvas com sucesso."
   	E csa reserva margem com o indice cadastrado "<indice>"
   	E csa tenta cadastrar mais uma reserva margem com o indice cadastrado "<indice>"
   	Então exibe a mensagem de erro "SERVIDOR JÁ POSSUI UM CONTRATO PARA ESTE ÍNDICE."
   	   	
    Exemplos:
    | usuario    | servico | indice |
    | cse        |   001   |   11   |
    | zetra_igor |   001   |   13   |
    
  Esquema do Cenário: Editar índice	para o serviço
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
   	E que possui indice "<indice>" criado para servico "<servico>"
    Quando acessar menu Manutencao > Consignantarias
    E acessar indice do servico "<servico>"
    E editar indice "<indice>"
   	Então exibe a mensagem "Alterações salvas com sucesso."
 
  Exemplos:
    | usuario    | servico | indice |
    | cse        |   036   |   05		|
    | zetra_igor |   036   |   07   |
       
  Esquema do Cenário: Excluir índice	para o serviço
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
   	E que possui indice "<indice>" criado para servico "<servico>"
    Quando acessar menu Manutencao > Consignantarias
    E acessar indice do servico "<servico>"
    E excluir indice "<indice>"
   	Então exibe a mensagem "Índice excluído com sucesso."
   
   Exemplos:
    | usuario    | servico | indice |
    | cse        |   036   |   06		|
    | zetra_igor |   036   |   08		|
   	
 Cenário: Tentar cadastrar índice com dados invalidos
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignantarias
    E acessar indice do servico "036"
   	Então tentar cadastrar indice com dados invalidos

  Esquema do Cenário: Inserir prazo para o serviço de consignatária
  	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que tenha incluido o item de menu Servicos no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
   	E acessar menu Manutencao > Servicos
   	E incluir prazo para servico "<servico>"
    Quando acessar menu Manutencao > Consignantarias
    E desbloquear os prazos do servico "<servico>"
   	Então csa pode criar reserva com os prazos cadastrados
   	E exibe mensagem de erro ao tentar cadastrar com prazo nao cadastrado "OS PRAZOS PERMITIDOS PARA ESTE SERVIÇO SÃO: 2, 3, 4, 5, 6, 7, 8."
 	 
 	 Exemplos:
    | usuario    | servico |
    | cse        |   019   |
    | zetra_igor |   019   |
    
  Esquema do Cenário: Bloquear um prazo para o serviço de consignatária
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que tenha incluido o item de menu Servicos no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
   	E acessar menu Manutencao > Servicos
   	E incluir prazo para servico "<servico>"
    Quando acessar menu Manutencao > Consignantarias
    E bloquear prazo "5" do servico "<servico>"
   	Então csa nao pode criar reserva com o prazo "5" bloqueado "OS PRAZOS PERMITIDOS PARA ESTE SERVIÇO SÃO: 2, 3, 4, 6, 7, 8."
 	  
 	  Exemplos:
    | usuario    | servico |
    | cse        |   019   |
    | zetra_igor |   019   |
     	 
  Esquema do Cenário: Bloquear todos os prazos para o serviço de consignatária
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que tenha incluido o item de menu Servicos no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
   	E acessar menu Manutencao > Servicos
   	E incluir prazo para servico "<servico>"
    Quando acessar menu Manutencao > Consignantarias
    E bloquear todos os prazos do servico "<servico>"
   	Então csa pode criar reserva com os prazos cadastrados
   	
 	Exemplos:
    | usuario    | servico |
    | cse        |   019   |
    | zetra_igor |   019   |
    
 	Cenário: Tentar bloquear e desbloquear prazo sem informar os valores dos prazos
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que tenha incluido o item de menu Servicos no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
   	E acessar menu Manutencao > Servicos
   	E incluir prazo para servico "022"
    Quando acessar menu Manutencao > Consignantarias
    Então tentar bloquear e desbloquear os prazos do servico "019"
   	
  Esquema do Cenário: Cadastrar penalidade para consignatária
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o usuario cse ou sup "<usuario>" esteja logado
   	Quando acessar menu Manutencao > Consignantarias
   	E incluir penalidade
    Então exibe a mensagem "Penalidade incluída com sucesso."
   
   Exemplos:
    | usuario    | 
    | cse        | 
    | zetra_igor | 
       
 	Cenário: Tentar cadastrar penalidade para consignatária sem informar dados obrigatórios
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
   	Quando acessar menu Manutencao > Consignantarias
   	E tentar incluir penalidade
   	
  Cenário: Cadastrar novo índice	para o serviço logado com CSA
   	Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa2" esteja logado
    Quando acessar menu Manutencao > Consignantaria
    E csa acessar indice do servico "145"
    E cadastrar novo indice "20"
   	Então exibe a mensagem "Alterações salvas com sucesso."
   	E csa reserva margem com o indice cadastrado "20"

	Cenário: Cadastrar índice e tentar criar duas reservar de margem com o mesmo índice logado com CSA
   	Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa2" esteja logado
    Quando acessar menu Manutencao > Consignantaria
    E csa acessar indice do servico "145"
    E cadastrar novo indice "21"
   	E exibe a mensagem "Alterações salvas com sucesso."
   	E csa reserva margem com o indice cadastrado "21"
   	E csa tenta cadastrar mais uma reserva margem com o indice cadastrado "21"
   	Então exibe a mensagem de erro "SERVIDOR JÁ POSSUI UM CONTRATO PARA ESTE ÍNDICE."

	Cenário: Editar índice	para o serviço logado com CSA
   	Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa2" esteja logado
   	E que possui indice "22" criado para servico "008"
    Quando acessar menu Manutencao > Consignantaria
    E csa acessar indice do servico "845"
    E editar indice "22"
   	Então exibe a mensagem "Alterações salvas com sucesso."
   
	Cenário: Excluir índice	para o serviço logado com CSA
   	Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa2" esteja logado
   	E que possui indice "23" criado para servico "008"
    Quando acessar menu Manutencao > Consignantaria
    E csa acessar indice do servico "845"
    E excluir indice "23"
   	Então exibe a mensagem "Índice excluído com sucesso."
   	
	Cenário: Configurar auditoria sobre operações logado com CSA
   	Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa2" esteja logado
    Quando acessar menu Manutencao > Consignantaria
   	E configurar auditoria sobre operacoes
    Então exibe a mensagem "Atualizações salvas com sucesso."   
    
    
    	
   	