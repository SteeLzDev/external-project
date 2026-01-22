#language:pt

Funcionalidade: Manter Faturamento de Benefícios
	 Gera notas fiscais e faturamento após o processamento do 
	 arquivo de retorno para enviá-las às operadoras.
	
	Contexto: Incluir item menu Faturamento de Benefícios no favoritos
		Dado que tenha incluido o item de menu Faturamento de Beneficios no favoritos para usuario Suporte
	
	Cenário: Listar Faturamento de Benefícios
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
		Então sera listados os registros de faturamento de beneficios criados durante o processamento de retorno    
		
	Cenário: Detalhar faturamento
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
    E detalhar faturamento
		Então e exibido os detalhes do faturamento de beneficio selecionado   
	
	Cenário: Listar notas fiscais
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
		E acessar notas fiscais do faturamento "8001"
		Então sera listado as notas fiscais cadastradas para o faturamento selecionado
				
	Cenário: Incluir nota fiscal
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
		E incluir nota fiscal
		E clicar em salvar
		Então exibe a mensagem "Nota fiscal salvo com sucesso!"  
				
	Cenário: Tentar incluir nota fiscal sem informar campos obrigatórios
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
		E acessar tela Inclusao de nota fiscal
    Então tentar cadastrar nota fiscal sem informar os campos obrigatorios
		
	Cenário: Editar nota fiscal
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
		E editar nota fiscal
		E clicar em salvar
		Então exibe a mensagem "Nota fiscal salvo com sucesso!" 
		
	Cenário: Excluir nota fiscal
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
		E excluir nota fiscal
		Então exibe a mensagem "Nota fiscal excluída com sucesso!"    
		
	Cenário: Gerar faturamento de benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
    E gerar faturamento de beneficio
		Então exibe link para download
		
	Cenário: Excluir arquivo de faturamento
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
    E excluir arquivo de faturamento
		Então exibe a mensagem "Arquivo removido com sucesso."   
	
	Cenário: Consultar faturamento de benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
    E consultar faturamento de beneficio
		Então e exibida uma lista de itens do arquivo de faturamento de acordo com os filtros aplicados  
		
	Cenário: Editar item faturamento de benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
    E editar item faturamento de beneficio
    E clicar em salvar
		Então exibe a mensagem "Registro alterado com sucesso"  
	
	Cenário: Excluir item de faturamento de benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
    E excluir item faturamento de beneficio
		Então exibe a mensagem "Registro excluido com sucesso"  
		
	Cenário: Validar faturamento de benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Faturamento de Beneficios
    E validar faturamento de beneficio
		Então exibe a mensagem "Relatório gerado com sucesso."  
		
		
		
		