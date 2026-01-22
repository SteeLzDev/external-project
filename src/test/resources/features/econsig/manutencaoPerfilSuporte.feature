#language:pt

Funcionalidade: Manutenção Perfil Suporte
	Permite criar, editar, excluir, bloquear e desbloquear perfil.
	
	
		Contexto: Incluir item menu Perfil Suporte no favoritos
		Dado que tenha incluido o item de menu Perfil Suporte no favoritos para usuario suporte

	Cenário: Criar perfil suporte sem funções
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Sistema > Perfis Suporte
    E clicar em adicionar novo perfil
    E preencher a descricao, desmarcar as funcoes e salvar
    Então exibe a mensagem "Perfil criado com sucesso."
     
  Cenário: Criar perfil suporte com funções
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Sistema > Perfis Suporte
    E clicar em adicionar novo perfil
    E preencher a descricao, marcar as funcoes e salvar
    Então exibe a mensagem "Perfil criado com sucesso."
    
  Esquema do Cenário: Bloquear perfil suporte  
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Sistema > Perfis Suporte
    E bloquear perfil com a descricao "<perfilDescricao>"
    Entao o perfil suporte "<perfilDescricao>" e bloqueado
    E exibe a mensagem "Perfil bloqueado com sucesso."

		Exemplos:
	    |    perfilDescricao       |
	    | Bloquear Sem Funcoes Sup |
	    | Bloquear Com Funcoes Sup |  
    
  Esquema do Cenário: Desbloquear perfil suporte
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Sistema > Perfis Suporte
    E desbloquear perfil com a descricao "<perfilDescricao>"
    Entao o perfil suporte "<perfilDescricao>" e desbloqueado
    E exibe a mensagem "Perfil desbloqueado com sucesso."

		Exemplos:
	    |      perfilDescricao        |
	    | Desbloquear Sem Funcoes Sup |
	    | Desbloquear Com Funcoes Sup |
        
  Esquema do Cenário: Excluir Perfil suporte
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Sistema > Perfis Suporte
    E excluir perfil com a descricao "<perfilDescricao>"
    Entao o perfil "<perfilDescricao>" e excluido
    E exibe a mensagem "Perfil removido com sucesso."
    
  	Exemplos:
	    |       perfilDescricao           |
	    | Excluir Perfil Bloqueado Sup    |
	    | Excluir Perfil Desbloqueado Sup |
       
  Esquema do Cenário: Editar Perfil suporte
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Sistema > Perfis Suporte
    E editar perfil com a descricao "<perfilDescricao>"
    Então exibe a mensagem "Alterações salvas com sucesso."
    
	  Exemplos:
	    |       perfilDescricao          |
	    | Editar Perfil Bloqueado Sup    |
	    | Editar Perfil Desbloqueado Sup | 
	    
	Cenário: Filtrar perfil suporte e cancelar edição
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Sistema > Perfis Suporte
    E filtrar perfil com a descricao "Desbloquear Sem Funcoes Sup"
    Então sera exibido os perfis que contem a descricao "Desbloquear Sem Funcoes Sup"
    E nao sera exibido os perfis que contem outras descricoes "Bloquear Sem Funcoes Sup"  
    Quando clicar em editar perfil com a descricao "Desbloquear Sem Funcoes Sup" 
    E clicar em Cancelar
    Então retorna para a tela de pesquisa
  
    