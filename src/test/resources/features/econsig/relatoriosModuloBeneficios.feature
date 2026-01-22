#language:pt

Funcionalidade: Relatórios do módulo de benefícios de saúde
	Permite gerar os relatórios:
	 	- Relatório de Comissionamento e Agenciamento Analítico
		- Relatório Beneficiário por Data Nascimento
		- Relatório de Exclusão de Beneficiários por Período
		- Relatório de Contratos de Benefícios


	Cenário: Relatório de Comissionamento e Agenciamento Analítico Sem Agendamento
		Dado que tenha incluido o item de menu Comissionamento e Agenciamento Analitico no favoritos para usuario Suporte
   	E que o usuario Suporte esteja logado
    Quando acessar menu Relatorios > Comissionamento e Agenciamento Analitico
    E gerar relatorio Comissionamento e Agenciamento Analitico em varios formatos
    Então gera os relatorios com sucesso
    Quando excluir um relatorio
    Então exibe a mensagem "Arquivo removido com sucesso."

	Cenário: Relatório de Comissionamento e Agenciamento Analítico Com Agendamento
   	Dado que tenha incluido o item de menu Comissionamento e Agenciamento Analitico no favoritos para usuario Suporte
   	E que o usuario Suporte esteja logado
    Quando acessar menu Relatorios > Comissionamento e Agenciamento Analitico
    E gerar relatorio Comissionamento e Agenciamento Analitico com agendamento
    Então verificar que relatorio Comissionamento e Agenciamento Analitico foi agendado com sucesso
    Quando cancelar agendamento do relatorio
    Então verificar que o agendamento do relatorio Comissionamento e Agenciamento Analitico foi cancelado
    
  Cenário: Relatório Beneficiário por Data Nascimento Sem Agendamento
   	Dado que tenha incluido o item de menu Beneficiario por Data Nascimento no favoritos para usuario Suporte
   	E que o usuario Suporte esteja logado
    Quando acessar menu Relatorios > Beneficiario por Data Nascimento
    E gerar relatorio em varios formatos no periodo "01/02/1980" a "01/05/1980"
    Então gera os relatorios com sucesso
    Quando excluir um relatorio
    Então exibe a mensagem "Arquivo removido com sucesso."

	Cenário: Relatório Beneficiário por Data Nascimento Com Agendamento
   	Dado que tenha incluido o item de menu Beneficiario por Data Nascimento no favoritos para usuario Suporte
   	E que o usuario Suporte esteja logado
   	Quando acessar menu Relatorios > Beneficiario por Data Nascimento
   	E gerar relatorio com agendamento no periodo "01/02/1980" a "01/05/1980"
    Então verificar que relatorio Beneficiario por Data Nascimento foi agendado com sucesso
    Quando cancelar agendamento do relatorio
    Então verificar que o agendamento do relatorio Beneficiario por Data Nascimento foi cancelado

	Cenário: Relatório de Exclusão de Beneficiários por Período Sem Agendamento
   	Dado que tenha incluido o item de menu Exclusao de Beneficiarios por Periodo no favoritos para usuario Suporte
   	E que o usuario Suporte esteja logado
    Quando acessar menu Relatorios > Exclusao de Beneficiarios por Periodo
    E gerar relatorio em varios formatos no periodo "01/01/2021" a "01/04/2021"
    Então gera os relatorios com sucesso
    Quando excluir um relatorio
    Então exibe a mensagem "Arquivo removido com sucesso."

	Cenário: Relatório de Exclusão de Beneficiários por Período Com Agendamento
   	Dado que tenha incluido o item de menu Exclusao de Beneficiarios por Periodo no favoritos para usuario Suporte
   	E que o usuario Suporte esteja logado
    Quando acessar menu Relatorios > Exclusao de Beneficiarios por Periodo
    E gerar relatorio com agendamento no periodo "01/01/2021" a "01/04/2021"
    Então verificar que relatorio Exclusao de Beneficiarios por Periodo foi agendado com sucesso
    Quando cancelar agendamento do relatorio
    Então verificar que o agendamento do Exclusao de Beneficiarios por Periodo foi cancelado
    
  Cenário: Relatório de Contratos de Benefícios Sem Agendamento
   	Dado que tenha incluido o item de menu Contratos de Beneficios no favoritos para usuario Suporte
   	E que o usuario Suporte esteja logado
    Quando acessar menu Relatorios > Contratos de Beneficios
    E gerar relatorio em varios formatos no periodo "01/09/2020" a "01/11/2020"
    Então gera os relatorios com sucesso
    Quando excluir um relatorio
    Então exibe a mensagem "Arquivo removido com sucesso."

	Cenário: Relatório de Contratos de Benefícios Com Agendamento
   	Dado que tenha incluido o item de menu Contratos de Beneficios no favoritos para usuario Suporte
   	E que o usuario Suporte esteja logado
    Quando acessar menu Relatorios > Contratos de Beneficios
    E gerar relatorio com agendamento no periodo "01/09/2020" a "01/11/2020"
    Então verificar que relatorio Contratos de Beneficios foi agendado com sucesso
    Quando cancelar agendamento do relatorio
    Então verificar que o agendamento do relatorio Contratos de Beneficios foi cancelado
    
	