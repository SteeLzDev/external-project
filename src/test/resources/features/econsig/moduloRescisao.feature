#language:pt

Funcionalidade: Módulo de Rescisão
Permite inserir servidor para informe de rescisão e reter verba rescisória.

	
	Contexto: Que o usuario Suporte tenha funcao para informar rescisao 
			Dado que tenha  incluido a funcao informar rescisao  ao usuario suporte
			E que tenha incluido o item de menu Informar rescisao no favoritos
			
	
	Cenário:  Excluir rescisão
			Dado que o usuario Suporte esteja logado
			E acessar menu Favoritos > informar rescisao
			Quando adiciona o servidor "004.503.189-40" para informar rescisao
			Quando verifica se o servidor "004.503.189-40" esta incluido na lista
			Entao exclui o mesmo e valida se nao esta bloqueado no sistema

	Cenário: Informar rescisão
			Dado  que o usuario Suporte esteja logado
			E acessar menu Favoritos > informar rescisao
			Quando adiciona o servidor "317.670.890-40" para informar rescisao
			E verifica se o servidor "317.670.890-40" esta incluido na lista
			E confirma o servidor para rescisao contratual
			Entao verifica se o mesmo foi bloqueado no sistema

	
	Esquema do Cenário: Reter verba rescisória com valor menor que o saldo devedor
			Dado que servidor "<login>" possua contratos com natureza empréstimo
			E que o usuário servidor "<login>" esteja na lista para rescisao contratual
			E saldo devedor "<saldoDevedor>" informado
			E que o usuario Suporte esteja logado
			Quando acessar menu Rescisão > Reter Verba Rescisória
			E informar verba rescisória para "<login>"
			E realizar a retenção da verba rescisória com valor "<valorRetido>"
			Entao retenção é concluida
			E exibe dados do contrato "<login>" com valor "<valorRetido>"
			E novo contrato com retenção da verba é criado para "<login>"

			Exemplos:
			    | login  | valorRetido | saldoDevedor |
			    | 121314 |    1000,00   |   2500.00    |


	Esquema do Cenário: Reter verba rescisória com valor igual do saldo devedor
			Dado que servidor "<login>" possua contratos com natureza empréstimo
			E que o usuário servidor "<login>" esteja na lista para rescisao contratual
			E saldo devedor "<saldoDevedor>" informado
			E que o usuario Suporte esteja logado
			Quando acessar menu Rescisão > Reter Verba Rescisória
			E informar verba rescisória para "<login>"
			E realizar a retenção da verba rescisória com valor "<valorRetido>"
			Entao retenção é concluida
			E exibe dados do contrato "<login>" com valor "<valorRetido>"
			E novo contrato com retenção da verba é criado para "<login>"

			Exemplos:
			    | login  | valorRetido | saldoDevedor |
			    | 121314 |   2000,00   |   2000.00    |
			    
		   
	Esquema do Cenário: Reter verba rescisória com valor maior que o saldo devedor
			Dado que servidor "<login>" possua contratos com natureza empréstimo
			E que o usuário servidor "<login>" esteja na lista para rescisao contratual
			E saldo devedor "<saldoDevedor>" informado
			E que o usuario Suporte esteja logado
			Quando acessar menu Rescisão > Reter Verba Rescisória
			E informar verba rescisória para "<login>"
			E realizar retenção com valor "<valorRetido>" maior que o saldo devedor "<saldoDevedor>"
			Entao retenção é concluida
			E exibe dados do contrato "<login>" com valor "<saldoDevedor>"
			E novo contrato com retenção da verba é criado para "<login>"

			Exemplos:
			    | login  | valorRetido | saldoDevedor |
			    | 121314 |    2300,00  |     1900     |		    
			    

	Cenário: Reter verba rescisória sem o saldo devedor informado 
			Dado que o sistema está configurado para calcular o saldo devedor automatico
			E que servidor "121314" possua contratos com natureza empréstimo
			E que o usuario Suporte esteja logado
			Quando acessar menu Favoritos > informar rescisao
			E adicionar servidor "121314" na lista para rescisao contratual e confirmar
			Entao saldo devedor é calculado automaticamente e retenção concluida para servidor "121314"
			E novo contrato com retenção da verba é criado para "121314"

			    


