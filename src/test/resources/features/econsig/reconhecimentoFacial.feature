#language:pt

Funcionalidade: Validar reconhecimento facial

	Contexto: Configura parametros do sistema
		Dado que a funcao "79" esta configurada para o papel servidor
		E ativa o servico emprestimo para que o servidor possa utilizar
		
	Cenário: Solicitar/Simular Consignação - validação biométrica ao solicitar/simular consignação
		Dado que esta ativa a biometria facial
		E que esta ativa a biometria facial para emprestimo
		E que o usuario Servidor esteja logado
		Quando o servidor clica em solitar emprestimo e faz a solicitacao
		Entao o sistema solicita o reconhecimento facial
		
	Cenário: Solicitar Consignação - não solicitar validação biometria ao solicitar/simular consignação, pois parâmetro está DESABILITADO
		Dado que esta ativa a biometria facial
		E que esta desativada a biometria facial para emprestimo
		E que o usuario Servidor esteja logado
		Quando o servidor clica em solitar emprestimo e faz a solicitacao
		Entao o sistema nao solicita a biometria facial
		
	Cenário: Solicitar Consignação - não solicitar validação biometria ao solicitar/simular consignação, pois os parâmetros estão DESABILITADOS
		Dado que esta desativada a biometria facial
		E que esta desativada a biometria facial para emprestimo
		E que o usuario Servidor esteja logado
		Quando o servidor clica em solitar emprestimo e faz a solicitacao
		Entao o sistema nao solicita a biometria facial