#language:pt

Funcionalidade: Manutenção Perfil Correspondente
	Permite criar, editar, excluir, bloquear e desbloquear perfil.

  Cenário: Criar perfil correspondente sem funções
   	Dado usuario correspondente "cor30" esteja logado
    Quando acessar menu Manutencao > Correspondente
    E clicar em Listar perfis de usuarios
    E clicar em adicionar novo perfil
    E preencher a descricao, desmarcar as funcoes e salvar
    Então exibe a mensagem "Perfil criado com sucesso."

  Cenário: Criar perfil correspondente com funções
   	Dado usuario correspondente "cor30" esteja logado
    Quando acessar menu Manutencao > Correspondente
    E clicar em Listar perfis de usuarios
    E clicar em adicionar novo perfil
    E preencher a descricao, marcar as funcoes e salvar
    Então exibe a mensagem "Perfil criado com sucesso."

  Esquema do Cenário: Bloquear perfil correspondente  
   	Dado usuario correspondente "cor30" esteja logado
    Quando acessar menu Manutencao > Correspondente
    E clicar em Listar perfis de usuarios
    E bloquear perfil com a descricao "<perfilDescricao>"
    Entao o perfil correspondente "<perfilDescricao>" e bloqueado
    E exibe a mensagem "Perfil bloqueado com sucesso."

		Exemplos:
	    |    perfilDescricao       |
	    | Bloquear Sem Funcoes Cor |
	    | Bloquear Com Funcoes Cor |  
    
  Esquema do Cenário: Desbloquear perfil correspondente
   	Dado usuario correspondente "cor30" esteja logado
    Quando acessar menu Manutencao > Correspondente
    E clicar em Listar perfis de usuarios
    E desbloquear perfil com a descricao "<perfilDescricao>"
    Entao o perfil correspondente "<perfilDescricao>" e desbloqueado
    E exibe a mensagem "Perfil desbloqueado com sucesso."

		Exemplos:
	    |      perfilDescricao        |
	    | Desbloquear Sem Funcoes Cor |
	    | Desbloquear Com Funcoes Cor |
        
  Esquema do Cenário: Excluir Perfil correspondente
   	Dado usuario correspondente "cor30" esteja logado
    Quando acessar menu Manutencao > Correspondente
    E clicar em Listar perfis de usuarios
    E excluir perfil com a descricao "<perfilDescricao>"
    Entao o perfil "<perfilDescricao>" e excluido
    E exibe a mensagem "Perfil removido com sucesso."
    
  	Exemplos:
	    |       perfilDescricao           |
	    | Excluir Perfil Bloqueado Cor    |
	    | Excluir Perfil Desbloqueado Cor |
       
  Esquema do Cenário: Editar Perfil correspondente
   	Dado usuario correspondente "cor30" esteja logado
    Quando acessar menu Manutencao > Correspondente
    E clicar em Listar perfis de usuarios
    E editar perfil com a descricao "<perfilDescricao>"
    Então exibe a mensagem "Alterações salvas com sucesso."
    
	  Exemplos:
	    |       perfilDescricao          |
	    | Editar Perfil Bloqueado Cor    |
	    | Editar Perfil Desbloqueado Cor | 
	    
  Cenário: Filtrar perfil correspondente e cancelar edição
   	Dado usuario correspondente "cor30" esteja logado
    Quando acessar menu Manutencao > Correspondente
    E clicar em Listar perfis de usuarios
    E filtrar perfil com a descricao "Desbloquear Sem Funcoes Cor"
    Então sera exibido os perfis que contem a descricao "Desbloquear Sem Funcoes Cor"
    E nao sera exibido os perfis que contem outras descricoes "Bloquear Sem Funcoes Cor"  
    Quando clicar em editar perfil com a descricao "Desbloquear Sem Funcoes Cor" 
    E clicar em Cancelar
    Então retorna para a tela de pesquisa
