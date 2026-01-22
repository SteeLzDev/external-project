#language:pt

Funcionalidade: Reativar Contrato Benefício
	Permite reativar contrato benefício para benefíciarios ativos.

	Contexto: Incluir item menu Reativar Contrato Benefício no favoritos
		Dado que tenha incluido o item de menu Reativar Contrato Beneficio no favoritos para usuario Suporte
		
	Cenário: Reativar Contrato Benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Reativar Contrato Beneficio
    E pesquisar o servidor "579771"
    E usuario seleciona uma operadora "UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA"
    E reativa contrato beneficio
    Então exibe a mensagem "Benefício Reativado com sucesso"
    E verificar que exibe o contrato do beneficiario ativo "579771"

	Cenário: Tentar reativar contrato para beneficiário que já tem benefício com a operadora
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Reativar Contrato Beneficio
    E pesquisar o servidor "181818"
    Então exibe a mensagem de erro "BENEFICIÁRIO JÁ TEM BENEFICIO(S) COM OPERADORA(S)."
    
