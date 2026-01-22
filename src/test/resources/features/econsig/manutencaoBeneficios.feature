#language:pt

Funcionalidade: Manutenção Benefícios
	Permite cadastrar, editar, excluir, bloquear e desbloquear beneficíos.

	
	Cenário: Cadastrar novo benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficios
		E cadastrar novo beneficio
    E clicar no botao salvar
    Então exibe a mensagem "Benefício criado com sucesso."        
	
	Cenário: Tentar cadastrar benefício sem informar campos obrigatórios
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficios
		E clicar em Novo Beneficio
    Então tentar cadastrar beneficio sem informar os campos obrigatorios
    
  Cenário: Editar cadastro benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficios
		E editar cadastro beneficio
    E clicar no botao salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
   
  Cenário: Tentar editar benefício incluindo serviços já existente
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficios
		E incluir servicos existentes
    E clicar no botao salvar
    Então exibe a mensagem de erro "Não foi possível editar este benefício, pois o relacionamento já existe para o mesmo tipo de beneficiário."
    
  Cenário: Excluir cadastro benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficios
		E excluir cadastro beneficio
    Então exibe a mensagem "Benefício deletado com sucesso."
    
  Cenário: Bloquear cadastro benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficios
		E bloquear beneficio
    Então exibe a mensagem "REFERENCIAL FAMILIAR ( APARTAMENTO ) da consignatária UNIMED BH COOPERATIVA DE TRABALHO MÉDICO LTDA foi bloqueado com sucesso."
    
  Cenário: Desbloquear cadastro benefício
   	Dado que o usuario Suporte esteja logado
    Quando acessar menu Manutencao > Beneficios
		E desbloquear beneficio
    Então exibe a mensagem "ODONTO 02 da consignatária DENTAL UNI - COOPERATIVA ODONTOLOGICA foi desbloqueado com sucesso."
    
