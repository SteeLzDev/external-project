#language:pt

Funcionalidade: Manutenção Postos
	Permite listar e editar postos.

	Contexto: Incluir item menu Posto no favoritos
		Dado que tenha incluido o item de menu Posto no favoritos para usuario CSE
		
	Cenário: Listar posto
   	Dado que o usuario CSE esteja logado
    Quando acessar menu Manutencao > Posto
    Então exibe a lista de postos
    Quando acessar opcao Editar do codigo "026"
    E alterar o campo descricao "Sargento 026X"
    E alterar o campo valor soldo
    E alterar o campo percentualtaxacond "111,00"
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
   
  Cenário: Listar posto e alterar
   	Dado que o usuario CSE esteja logado
    Quando acessar menu Manutencao > Posto
    Quando acessar opcao Editar do codigo "030"
    E alterar o campo codigo "026"
    E alterar o campo descricao "Sargento 030ZZ"
    E alterar o campo valor soldo
    E alterar o campo percentualtaxacond "15,00"
    E clicar em salvar
    Então Sistema exibe mensagem com o erro "JÁ EXISTE UM POSTO COM O CÓDIGO INFORMADO."
    