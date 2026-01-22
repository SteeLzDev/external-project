#language:pt

Funcionalidade: Manutenção Órgão
  Permite criar, editar, excluir, bloquear e desbloquear usuário consignante
    
 	Cenário: Editar órgão
		Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
		Dado que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
   	E editar os dados do orgao
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
     
 
  Cenário: Bloquear órgão 
  	Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
    Quando bloquear o orgao 
    Então exibe a mensagem "Órgão bloqueado."

    
  Cenário: Desbloquear órgão 
  	Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
    Quando desbloquear o orgao 
    Então exibe a mensagem "Órgão desbloqueado."
  
  Cenário: Excluir órgão
		Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
		Dado que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
    E pesquisar orgao pelo filtro "Nome" com "EXCLUSAO"
   	E excluir orgao
    Então exibe a mensagem "Órgão excluído com sucesso."
 
    
  Cenário: Editar órgão com campos inválidos
   	Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
    Então tentar editar os dados do orgao invalidos
                
  Cenário: Tentar alterar o código órgão para um já existente 
   	Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
   	E alterar o codigo orgao
    E clicar em salvar
    Então Sistema exibe mensagem com o erro "Não foi possível alterar este órgão pois existe outro cadastrado no sistema com o mesmo código no mesmo estabelecimento."    
    
  Cenário: Tentar excluir órgão com dependentes
   	Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
   	E excluir orgao com dependentes
    Então Sistema exibe mensagem com o erro "Não foi possível excluir o órgão selecionado, pois ele possui dependentes no sistema."
       
  Cenário: Pesquisar órgão
   	Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
   	E pesquisar orgao pelo filtro "Nome" com "CARLOTA"
   	Entao exibe os orgaos com o filtro Nome
    Quando pesquisar orgao pelo filtro "Código" com "0001"
   	Entao exibe os orgaos com o filtro Codigo
    Quando pesquisar orgao pelo filtro "Código estabelecimento" com "001"
   	Entao exibe os orgaos com o filtro Codigo estabelecimento
    Quando pesquisar orgao pelo filtro "Bloqueado" com ""
   	Entao exibe os orgaos com o filtro Bloqueado
    Quando pesquisar orgao pelo filtro "Desbloqueado" com ""
   	Entao exibe os orgaos com o filtro Desbloqueado
        
  Cenário: Criar órgão com sucesso
   	Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
   	E criar orgao com codigo "70"
    Então exibe a mensagem "Alterações salvas com sucesso."

  Cenário: Tentar criar órgão com código já existente
   	Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
   	E criar orgao com codigo "0001"
    Então exibe a mensagem de erro "Não é possível criar este órgão. Existe outro no sistema com o mesmo código neste estabelecimento."
   	
  Cenário: Tentar criar órgão sem informar campos obrigatórios
   	Dado que tenha incluido o item de menu Orgaos no favoritos para usuario "cse"
   	E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Orgaos
   	Então tentar criar orgao sem informar campos obrigatorios  	
   	