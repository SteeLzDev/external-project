#language:pt

Funcionalidade: Manutenção usuários servidores
	Na manutenção de usuários servidores, o usuário gestor pode manipular o cadastro de usuários servidores. 
	O usuário servidor é um usuário no sistema que está ligado a um servidor, assim este pode acessar o sistema 
	para efetuar certas operações, como consulta de margem e consignações.

	Contexto: Usuário servidor está ativo
	  Dado que o usuario servidor esteja ativo
     
 	Esquema do Cenário: Cadastrar usuário servidor
   	Dado que tenha incluido o item de menu Usuarios Servidores no favoritos para usuario "<usuario>"
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Usuario Servidor
    E pesquisar o servidor "<matricula>"
   	E cadastrar usuario servidor "<matricula>"
    Então exibe a mensagem "Usuário criado com sucesso."
    E verifica que o novo usuario servidor autentica "<matricula>"
            
   Exemplos:
    | usuario    | matricula |
    | cse        |   260419  |   
    | zetra_igor |   180587  |
      
  Esquema do Cenário: Reinicializar senha do usuário servidor
   	Dado que tenha incluido o item de menu Usuarios Servidores no favoritos para usuario "<usuario>"
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Usuario Servidor
    E pesquisar o servidor "145985"
   	E reinicializar senha do usuario servidor
    Então exibe a nova senha "Senha reinicializada com sucesso."
    E verifica que o usuario servidor autentica com a nova senha
            
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    

 	Esquema do Cenário: Alterar senha do usuário servidor
   	Dado que tenha incluido o item de menu Usuarios Servidores no favoritos para usuario "<usuario>"
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Usuario Servidor
    E pesquisar o servidor "145985"
   	E alterar senha do usuario servidor
    Então exibe a mensagem "Senha alterada com sucesso"
    E verifica que o usuario servidor autentica com a nova senha
            
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    
    
  Esquema do Cenário: Bloquear usuário servidor
   	Dado que tenha incluido o item de menu Usuarios Servidores no favoritos para usuario "<usuario>"
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Usuario Servidor
    E pesquisar o servidor "123456"
   	E bloquear o usuario servidor
    Então exibe a mensagem "Usuário bloqueado com sucesso."
    E verifica que o usuario servidor nao consegue autenticar
      
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
   
  
  Esquema do Cenário: Desbloquear usuário servidor
  	Dado que tenha incluido o item de menu Usuarios Servidores no favoritos para usuario "<usuario>"
   	E que o usuario servidor esteja bloqueado
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Usuario Servidor
    E pesquisar o servidor "123456"
   	E desbloquear o usuario servidor
    Então exibe a mensagem "Usuário desbloqueado com sucesso."
    E verifica que usuario servidor consegue autenticar
            
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    
    
  Cenário: Tentar desbloquear usuário servidor que foi bloqueado por segurança com usuario consignante
  	Dado que tenha incluido o item de menu Usuarios Servidores no favoritos para usuario "cse"
   	E que o usuario servidor esteja bloqueado por seguranca
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Usuario Servidor
    E pesquisar o servidor "123456"
   	E desbloquear o usuario servidor
    Então exibe a mensagem de erro "NÃO É POSSÍVEL DESBLOQUEAR O USUÁRIO '092.459.399-79', POIS ELE FOI BLOQUEADO POR SEGURANÇA."
    
  Cenário: Tentar desbloquear usuário servidor que foi bloqueado por segurança
  	Dado que tenha incluido o item de menu Usuarios Servidores no favoritos para usuario "zetra_igor"
   	E que o usuario servidor esteja bloqueado por seguranca
   	E que o usuario cse ou sup "zetra_igor" esteja logado
    Quando acessar menu Manutencao > Usuario Servidor
    E pesquisar o servidor "123456"
   	E desbloquear o usuario servidor
    Então exibe a mensagem "Usuário desbloqueado com sucesso."
    E verifica que usuario servidor consegue autenticar
 
    
  # Na tarefa DESENV-16798 foi alterado o fluxo 
  #Esquema do Cenário: Tentar cadastrar usuario servidor com login já cadastrado
   	#Dado que tenha incluido o item de menu Usuarios Servidores no favoritos para usuario "<usuario>"
		#E que o usuario cse ou sup "<usuario>" esteja logado
    #Quando acessar menu Manutencao > Usuario Servidor
    #E pesquisar o servidor "121314"
   	#E cadastrar usuario servidor "121314"
    #Então exibe a mensagem de erro "NÃO FOI POSSÍVEL CRIAR ESTE USUÁRIO, POIS JÁ EXISTE OUTRO COM O MESMO LOGIN CADASTRADO NO SISTEMA."
            
   #Exemplos:
   # | usuario    |
   # | cse        |
   # | zetra_igor |
    
    