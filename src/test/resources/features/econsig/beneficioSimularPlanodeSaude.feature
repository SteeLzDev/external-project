#language:pt

Funcionalidade: Simular Plano de Saúde e Odontológico
	Permite simular contratos de plano de saúde e odontológico

	Contexto: Incluir item menu Simular plano de saúde e odontológico no favoritos
		Dado que tenha incluido o item de menu Simular plano de saude e odontologico no favoritos para usuario Suporte
		
	Cenário: Simular plano de saude
   	Dado que o usuario Suporte esteja logado
   	Quando acessar o menu Beneficios > Simular plano de saude e odontologico
    E pesquisar o servidor "123456"
    Quando usuario seleciona operadora "UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA"
    Então exibe lista de planos "APARTAMENTO (AMPLA)"
    Quando selecionar plano saude 
    Então seleciona o beneficiario saude
    E clicar em continuar
    Quando usuario seleciona operadora odonto "DENTAL UNI - COOPERATIVA ODONTOLOGICA"
    Então exibe lista de planos "DENTAL UNI"
    Quando selecionar plano odonto
    Então seleciona o beneficiario odonto
    E clicar em continuar
    Então exibe tela com "Total Simulação"
    E clicar em continuar
    Então exibe a mensagem "Simulação realizada com sucesso!"
      
  Cenário: Simular plano com servidor que já possui contrato
    Dado que o usuario Suporte esteja logado
   	Quando acessar o menu Beneficios > Simular plano de saude e odontologico
    E pesquisar o servidor "123456"
    Então Sistema exibe mensagem com o erro "BENEFICIÁRIO JÁ TEM BENEFICIO(S) COM OPERADORA(S)"
 

   
  
    