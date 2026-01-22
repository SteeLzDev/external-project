#language:pt

Funcionalidade: Manutenção Perfil Consignante
	Permite criar, editar, excluir, bloquear e desbloquear perfil.

	Cenário: Criar perfil sem funções
   	Dado que o usuario CSE esteja logado
    Quando acessar menu Manutencao > Consignante
    E clicar em Lista de Perfil
    E clicar em adicionar novo perfil
    E preencher a descricao, desmarcar as funcoes e salvar
    Então exibe a mensagem "Perfil criado com sucesso."
        
  Cenário: Criar perfil com funções
    Dado que o usuario CSE esteja logado
    Quando acessar menu Manutencao > Consignante
    E clicar em Lista de Perfil
    E clicar em adicionar novo perfil
    E preencher a descricao, marcar as funcoes e salvar
    Então exibe a mensagem "Perfil criado com sucesso."
 
  Esquema do Cenário: Bloquear perfil consignante  
		Dado que o usuario CSE esteja logado
    Quando acessar menu Manutencao > Consignante
    E clicar em Lista de Perfil
    E bloquear perfil com a descricao "<perfilDescricao>"
    Entao o perfil "<perfilDescricao>" e bloqueado
    E exibe a mensagem "Perfil bloqueado com sucesso."

		Exemplos:
	    | perfilDescricao |
	    | Bloquear Sem Funcoes |
	    | Bloquear Com Funcoes |  
    
  Esquema do Cenário: Desbloquear perfil consignante
		Dado que o usuario CSE esteja logado
    Quando acessar menu Manutencao > Consignante
    E clicar em Lista de Perfil
    E desbloquear perfil com a descricao "<perfilDescricao>"
    Entao o perfil "<perfilDescricao>" e desbloqueado
    E exibe a mensagem "Perfil desbloqueado com sucesso."

		Exemplos:
	    | perfilDescricao |
	    | Desbloquear Sem Funcoes |
	    | Desbloquear Com Funcoes |  
        
  Esquema do Cenário: Excluir Perfil
    Dado que o usuario CSE esteja logado
    Quando acessar menu Manutencao > Consignante
    E clicar em Lista de Perfil
    E excluir perfil com a descricao "<perfilDescricao>"
    Entao o perfil "<perfilDescricao>" e excluido
    E exibe a mensagem "Perfil removido com sucesso."
    
  	Exemplos:
	    | perfilDescricao |
	    | Excluir Sem Funcoes |
	    | Excluir Com Funcoes |   
       
  Esquema do Cenário: Editar Perfil
    Dado que o usuario CSE esteja logado
    Quando acessar menu Manutencao > Consignante
    E clicar em Lista de Perfil
    E editar perfil com a descricao "<perfilDescricao>"
    Então exibe a mensagem "Alterações salvas com sucesso."
    
	  Exemplos:
	    | perfilDescricao |
	    | Editar Perfil Bloqueado |
	    | Editar Perfil Desbloqueado |  
	    
	Cenário: Filtrar Perfil e Cancelar edição
    Dado que o usuario CSE esteja logado
    Quando acessar menu Manutencao > Consignante
    E clicar em Lista de Perfil
    E filtrar perfil com a descricao "Desbloquear Sem Funcoes"
    Então sera exibido os perfis que contem a descricao "Desbloquear Sem Funcoes"
    E nao sera exibido os perfis que contem outras descricoes "Bloquear Sem Funcoes"  
    Quando clicar em editar perfil com a descricao "Desbloquear Sem Funcoes" 
    E clicar em Cancelar
    Então retorna para a tela de pesquisa
  
    