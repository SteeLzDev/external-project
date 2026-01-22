#language:pt

Funcionalidade: Manutenção Consignante
	Permite criar, editar, excluir, bloquear e desbloquear usuário consignante.

	Contexto: Consignante está ativo
	  Dado que o consignante esteja ativo
        
	Esquema do Cenário: Editar consignante
		Dado que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignante
   	E editar os dados do consignante
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    
  Esquema do Cenário: Bloquear consignante
   	Dado que tenha incluido o item de menu Consignante no favoritos para usuario "<usuario>"
   	E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "<usuario>"
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Favoritos > Consignante
   	E bloquear o consignante
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que nao consegue reservar margem
      
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    
  Esquema do Cenário: Desbloquear consignante
   	Dado que tenha incluido o item de menu Consignante no favoritos para usuario "<usuario>"
   	E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "<usuario>"
		E que o consignante esteja bloqueado
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Favoritos > Consignante
   	E desbloquear o consignante
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que consegue reservar margem
            
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
        
  Esquema do Cenário: Alterar configurações de margem
   	Dado que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignante
   	E alterar configuracoes de margem
    E clica no botao salvar alteracoes
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica as alteracoes de margem realizadas
       
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
   
  Cenário: Alterar os parametros
   	Dado que o usuario cse ou sup "aut" esteja logado
    Quando acessar menu Manutencao > Consignante
   	E alterar os parametros
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
       
  Esquema do Cenário: Configurar auditoria
   	Dado que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignante
   	E configurar auditoria "<usuario>"
    Então exibe a mensagem "Atualizações salvas com sucesso."
        
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    
  Cenário: Alterar o parametro exige certificado digital para consignante
   	Dado que o usuario cse ou sup "aut" esteja logado
    Quando acessar menu Manutencao > Consignante
   	E alterar o parametro exige certificado digital para consignante
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    E usuario "cse" nao consegue logar
    
  Cenário: Alterar o parametro exige certificado digital para consignatária
   	Dado que o usuario cse ou sup "aut" esteja logado
    Quando acessar menu Manutencao > Consignante
   	E alterar o parametro exige certificado digital para consignataria
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    E usuario "csa2" nao consegue logar

  Cenário: Consignante tenta logar com cadastro de IP endereço de acesso no login ativo
   	Dado que o usuario cse ou sup "aut" esteja logado
    Quando acessar menu Manutencao > Consignante
    E alterar o parametro para verificar cadastro de IP endereco de acesso no login
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que cse "cse" nao consegue logar
    
	Cenário: Consignante loga com cadastro de IP endereço de acesso no login ativo e com Ip cadastro
   	Dado que o usuario cse ou sup "aut" esteja logado
    Quando acessar menu Manutencao > Consignante
    E cadastrar Ip de acesso para cse
    E alterar o parametro para verificar cadastro de IP endereco de acesso no login
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que cse "cse" consegue logar
    
    
    