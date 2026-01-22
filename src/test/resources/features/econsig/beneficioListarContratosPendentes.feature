#language:pt

Funcionalidade: Listar Contratos Pendentes
	Permite listar contratos, cancelar benefícios por inadimplencia, incluir beneficiário, simular alteração de plano

	Contexto: Incluir item menu Listar Contratos Pendentes no favoritos
		Dado que tenha incluido o item de menu Listar Contratos Pendentes no favoritos para usuario Suporte
	
	Cenário: Listar contratos pendentes e alterar
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Listar Contratos Pendentes
    Quando usuario seleciona consignataria "A981808080808080808080808080AF85"
    Então exibe lista de contratos
    Quando acessar opcao Editar da matricula "123456"
    E alterar o campo numero do contrato "00060502195836005"
    E alterar o campo data inicio vigencia "30/08/2016"
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    
    
	Cenário: Listar contratos e alterar data de fim de vigencia menor que inicio de vigencia 
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Beneficios > Listar Contratos Pendentes
   	Quando usuario seleciona consignataria "A981808080808080808080808080AF85"
    Então exibe lista de contratos
    Quando acessar opcao Editar da matricula "123456"
    E alterar o campo data fim vigencia "01/08/2016"
    E clicar em salvar
    Então exibe o alerta com a mensagem "A data de início de vigência não pode ser maior que a data de fim de vigência." 

