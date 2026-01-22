#language:pt

Funcionalidade: Simular Alteração de Plano de Benefícios
	Permite simular alteração de plano de benefícios

Cenário: Simular alteração de plano de benefícios
		Dado que tenha incluido o item de menu Simular alteracao de plano no favoritos para usuario Suporte
   	E que o usuario Suporte esteja logado
   	Quando acessar o menu Beneficios > Simular alteracao de plano
    E pesquisar o servidor "181818"
    Quando clicar em Plano De Saude
    Quando usuario seleciona operadora para alteracao "UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA"
    Então exibe lista de planos "ENFERMARIA - REDE RESTRITA"
    Quando selecionar plano saude para alteracao
    Então seleciona o beneficiario saude para alteracao
    E clicar em continuar
    Então exibe tela com "Resultado por beneficiário"
    E clicar em continuar
    Então exibe a mensagem "Simulação realizada com sucesso!"