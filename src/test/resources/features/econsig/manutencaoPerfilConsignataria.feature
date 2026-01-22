#language:pt

Funcionalidade: Manutenção Perfil Consignatária
	Permite criar, editar, excluir, bloquear e desbloquear perfil.

	Cenário: Criar perfil consignatária sem funções
   	Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa" esteja logado
    Quando acessar menu Manutencao > Consignantaria
    E clicar em Listar perfis de usuarios
    E clicar em adicionar novo perfil
    E preencher a descricao, desmarcar as funcoes e salvar
    Então exibe a mensagem "Perfil criado com sucesso."
     
  Cenário: Criar perfil consignatária com funções
    Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa" esteja logado
    Quando acessar menu Manutencao > Consignantaria
    E clicar em Listar perfis de usuarios
    E clicar em adicionar novo perfil
    E preencher a descricao, marcar as funcoes e salvar
    Então exibe a mensagem "Perfil criado com sucesso."
    
  Esquema do Cenário: Bloquear perfil consignatária  
		Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa" esteja logado
    Quando acessar menu Manutencao > Consignantaria
    E clicar em Listar perfis de usuarios
    E bloquear perfil com a descricao "<perfilDescricao>"
    Entao o perfil consignataria "<perfilDescricao>" e bloqueado
    E exibe a mensagem "Perfil bloqueado com sucesso."

		Exemplos:
	    |    perfilDescricao       |
	    | Bloquear Sem Funcoes Csa |
	    | Bloquear Com Funcoes Csa |  
    
  Esquema do Cenário: Desbloquear perfil consignatária
		Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa" esteja logado
    Quando acessar menu Manutencao > Consignantaria
    E clicar em Listar perfis de usuarios
    E desbloquear perfil com a descricao "<perfilDescricao>"
    Entao o perfil consignataria "<perfilDescricao>" e desbloqueado
    E exibe a mensagem "Perfil desbloqueado com sucesso."

		Exemplos:
	    |      perfilDescricao        |
	    | Desbloquear Sem Funcoes Csa |
	    | Desbloquear Com Funcoes Csa |
        
  Esquema do Cenário: Excluir Perfil consignatária
    Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa" esteja logado
    Quando acessar menu Manutencao > Consignantaria
    E clicar em Listar perfis de usuarios
    E excluir perfil com a descricao "<perfilDescricao>"
    Entao o perfil "<perfilDescricao>" e excluido
    E exibe a mensagem "Perfil removido com sucesso."
    
  	Exemplos:
	    |       perfilDescricao           |
	    | Excluir Perfil Bloqueado Csa    |
	    | Excluir Perfil Desbloqueado Csa |
       
  Esquema do Cenário: Editar Perfil consignatária
    Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa" esteja logado
    Quando acessar menu Manutencao > Consignantaria
    E clicar em Listar perfis de usuarios
    E editar perfil com a descricao "<perfilDescricao>"
    Então exibe a mensagem "Alterações salvas com sucesso."
    
	  Exemplos:
	    |       perfilDescricao          |
	    | Editar Perfil Bloqueado Csa    |
	    | Editar Perfil Desbloqueado Csa | 
	    
	Cenário: Filtrar perfil consignatária e cancelar edição
    Dado que tenha incluido o item de menu Consignantaria no favoritos para usuario CSA
   	E que o usuario csa "csa" esteja logado
    Quando acessar menu Manutencao > Consignantaria
    E clicar em Listar perfis de usuarios
    E filtrar perfil com a descricao "Desbloquear Sem Funcoes Csa"
    Então sera exibido os perfis que contem a descricao "Desbloquear Sem Funcoes Csa"
    E nao sera exibido os perfis que contem outras descricoes "Bloquear Sem Funcoes Csa"  
    Quando clicar em editar perfil com a descricao "Desbloquear Sem Funcoes Csa" 
    E clicar em Cancelar
    Então retorna para a tela de pesquisa
  
    