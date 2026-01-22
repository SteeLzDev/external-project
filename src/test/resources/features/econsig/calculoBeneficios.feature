#language:pt

Funcionalidade: Listar Cálculo de Benefícios
	Armazena os valores de mensalidade dos planos de benefícios 
	disponíveis no sistema e seus respectivos valores de subsídio.

	Contexto: Incluir item menu Cálculo de Benefícios no favoritos
		Dado que tenha incluido o item de menu Calculo de Beneficios no favoritos para usuario Suporte
	
	Cenário: Cadastrar novo cálculo de benefícios
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Calculo de Beneficios
		E cadastrar calculo beneficio
    E clicar no botao salvar
    Então exibe a mensagem "Cálculo de benefício criado com sucesso."        
		
	Cenário: Tentar cadastrar cálculo de benefícios
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Calculo de Beneficios
   	E acessar tela Inclusao de novo calculo beneficio
    Então tentar cadastrar novo calculo de beneficios sem informar os campos obrigatorios   
    
  Cenário: Editar cálculo de benefícios
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Calculo de Beneficios
		E editar calculo beneficio
    E clicar no botao salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
     
  Cenário: Excluir cálculo de benefícios
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Calculo de Beneficios
		E excluir calculo beneficio
    Então exibe a mensagem "Cálculo de benefício deletado com sucesso."
      
  Cenário: Ativar tabela
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Calculo de Beneficios
		Então ativar tabela
	
	Cenário: Iniciar tabela
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Calculo de Beneficios
		Então iniciar tabela
		
	Cenário: Excluir tabela iniciada
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Calculo de Beneficios
		E exclui tabela iniciada
    Então exibe a mensagem "Tabela iniciada removida com sucesso." 
		
	Cenário: Aplicar reajuste
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Calculo de Beneficios
    E acessar a tela Aplicar reajuste
		E aplicar o reajuste
    Então exibe a mensagem "Reajuste aplicado com sucesso." 
    E verificar se valores foram alterados
    
	Cenário: Tentar aplicar reajuste sem informar campos obrigatórios
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Calculo de Beneficios
    E acessar a tela Aplicar reajuste
		Então tentar aplicar reajuste sem informar campos obrigatorios