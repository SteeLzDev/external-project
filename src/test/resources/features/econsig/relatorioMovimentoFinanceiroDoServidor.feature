#language:pt

Funcionalidade: Relatório Movimento Financeiro do Servidor
	
	Contexto: Que o usuario CSA tenha a permissao para gerar o relatorio de movimento do servidor
		Dado que o usuario csa tenha a permissao para gerar o relatorio de movimento financeiro do servidor
		Dado que tenha incluido o item de menu relatorio movimento financeiro do servidor nos favoritos para o usuario csa

		
	Cenário: Relatorio de Movimento Financeiro do Servidor: validar obrigatoriedade de data desconto inicio e fim
		Dado que o usuario csa "csa2" esteja logado
		E que nao exige segunda senha ao solicitar o relatorio de movimento financeiro do servidor
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio sem informar data inicial e data final
		Entao o sistema deve exibir mensagem de validacao: Informe inicio e fim da data de desconto.
	
	Cenário: Relatorio de Movimento Financeiro do Servidor: validar obrigatoriedade de matricula e/ou CPF
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio sem informar matricula e cpf
		Entao o sistema deve exibir mensagem de validacao: Informe matricula e cpf.
		
	Cenário: Relatorio de Movimento Financeiro do Servidor: Solicitar relatorio informando cpf diferente ao do servidor informado na matricula
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio informando o cpf diferente do servidor informado na matricula
		E seleciona o formato "PDF" do relatorio de movimento financeiro do servidor
		E confirma a criacao do relatorio de movimento financeiro do servidor
		Entao o sistema nao deve permitir gerar o relatorio de Movimento Financeiro do Servidor
		
	Cenário: Relatorio de Movimento Financeiro do Servidor: Solicitar relatorio informando matricula diferente ao do servidor informado no cpf
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio informando a matricula diferente do cpf do servidor
		E seleciona o formato "PDF" do relatorio de movimento financeiro do servidor
		E confirma a criacao do relatorio de movimento financeiro do servidor
		Entao o sistema nao deve permitir gerar o relatorio de Movimento Financeiro do Servidor
		
	Cenário: Relatorio de Movimento Financeiro do Servidor: gerar relatorio PDF
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio informando os dados corretamente
		E seleciona o formato "PDF" do relatorio de movimento financeiro do servidor
		E confirma a criacao do relatorio de movimento financeiro do servidor
		Entao o sistema deve gerar o relatorio de Movimento Financeiro do Servidor
	
	Cenário: Relatorio de Movimento Financeiro do Servidor: gerar relatorio TXT
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio informando os dados corretamente
		E seleciona o formato "TXT" do relatorio de movimento financeiro do servidor
		E confirma a criacao do relatorio de movimento financeiro do servidor
		Entao o sistema deve gerar o relatorio de Movimento Financeiro do Servidor
		
	Cenário: Relatorio de Movimento Financeiro do Servidor: gerar relatorio CSV
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio informando os dados corretamente
		E seleciona o formato "CSV" do relatorio de movimento financeiro do servidor
		E confirma a criacao do relatorio de movimento financeiro do servidor
		Entao o sistema deve gerar o relatorio de Movimento Financeiro do Servidor

	Cenário: Relatorio de Movimento Financeiro do Servidor: gerar relatorio DOC
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio informando os dados corretamente
		E seleciona o formato "DOC" do relatorio de movimento financeiro do servidor
		E confirma a criacao do relatorio de movimento financeiro do servidor
		Entao o sistema deve gerar o relatorio de Movimento Financeiro do Servidor
	
	Cenário: Relatorio de Movimento Financeiro do Servidor: gerar relatorio XLS
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio informando os dados corretamente
		E seleciona o formato "XLS" do relatorio de movimento financeiro do servidor
		E confirma a criacao do relatorio de movimento financeiro do servidor
		Entao o sistema deve gerar o relatorio de Movimento Financeiro do Servidor
	
	Cenário: Relatorio de Movimento Financeiro do Servidor: gerar relatorio XLSX
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio informando os dados corretamente
		E seleciona o formato "XLSX" do relatorio de movimento financeiro do servidor
		E confirma a criacao do relatorio de movimento financeiro do servidor
		Entao o sistema deve gerar o relatorio de Movimento Financeiro do Servidor
		
	Cenário: Relatorio de Movimento Financeiro do Servidor: gerar relatorio XML
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio informando os dados corretamente
		E seleciona o formato "XML" do relatorio de movimento financeiro do servidor
		E confirma a criacao do relatorio de movimento financeiro do servidor
		Entao o sistema deve gerar o relatorio de Movimento Financeiro do Servidor
		
	Cenário: Relatorio de Movimento Financeiro do Servidor: Solicita gerar o relatorio sem selecionar o formato do arquivo
		Dado que o usuario csa "csa2" esteja logado
		E acessar menu favoritos > Relatorio movimento Financeiro do servidor
		Quando solicita gerar o relatorio informando os dados corretamente
		E confirma a criacao do relatorio de movimento financeiro do servidor
		Entao o sistema deve solicitar que selecione o formato de arquivo