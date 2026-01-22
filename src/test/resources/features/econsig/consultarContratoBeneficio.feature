#language:pt

Funcionalidade: Consultar Contrato de Benefício
	Consulta contratos de plano de saúde e odontológico para poder manter os dados desses planos.


	Cenário: Consultar Contrato de Benefício Ativo (Detalhar Benefício)
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Consultar Contrato de Plano de Saude e Odontologico
		E pesquisar o servidor "181818"
    Então lista os contratos de beneficios ativos e os cancelados  
		Quando clicar em Detalhar Beneficio ativo
  	Então exibe os detalhes do plano e seus beneficiarios

	Cenário: Consultar Contrato de Benefício Cancelado (Detalhar Benefício)
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Consultar Contrato de Plano de Saude e Odontologico
		E pesquisar o servidor "181818"
    Então lista os contratos de beneficios ativos e os cancelados  
		Quando clicar em Detalhar Beneficio cancelado
  	Então exibe os detalhes do plano que foi cancelado e seus beneficiarios
  		
  Cenário: Incluir novo beneficiário
  	Dado que tenha incluido o item de menu Calculo de Beneficios no favoritos para usuario Suporte
   	E que o usuario Suporte esteja logado
   	E possuir tabela ativa
    Quando acessar menu Beneficios > Incluir beneficiario em plano vigente
		E pesquisar o servidor "181818"
    E incluir novo beneficiario com CPF "699.395.690-09"
    E clicar no botao salvar
    Então exibe a mensagem "Beneficiário criado com sucesso."
    E exibe o novo beneficiario na lista
  	
  Cenário: Registrar Ocorrência
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Consultar Contrato de Plano de Saude e Odontologico
		E pesquisar o servidor "181818"
		E clicar em Detalhar Beneficio ativo
		E registrar ocorrencia
    Então exibe a mensagem "Registro gravado com sucesso"

  Cenário: Editar benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Consultar Contrato de Plano de Saude e Odontologico
		E pesquisar o servidor "181818"
		E clicar em Detalhar Beneficio ativo
		E editar beneficio
		E clicar no botao salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
  	
  Cenário: Cancelar benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Consultar Contrato de Plano de Saude e Odontologico
		E pesquisar o servidor "181818"
		E clicar em Detalhar Beneficio ativo
		E cancelar beneficio
		E clicar no botao salvar
    Então exibe a mensagem "Benefício cancelado com sucesso."

  Cenário: Listar lançamento
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Consultar Contrato de Plano de Saude e Odontologico
		E pesquisar o servidor "181818"
		E clicar em Detalhar Beneficio ativo
		E clicar listar lancamento
    Então verificar as informacoes do lancamento
         
  Cenário: Aprovar solicitação
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Consultar Contrato de Plano de Saude e Odontologico
		E pesquisar o servidor "579771"
		E clicar em Detalhar Beneficio ativo
		E aprovar solicitacao
    Então exibe a mensagem "Benefício aprovado com sucesso."
    
  Cenário: Rejeitar solicitação
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Consultar Contrato de Plano de Saude e Odontologico
		E pesquisar o servidor "579771"
		E clicar em Detalhar Beneficio ativo
		E rejeitar solicitacao
    Então exibe a mensagem "Benefício rejeitado com sucesso."
    
  	