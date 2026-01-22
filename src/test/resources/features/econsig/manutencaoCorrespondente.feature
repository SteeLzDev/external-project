#language:pt

Funcionalidade: Manutenção Correspondente
	Permite criar, editar, excluir, bloquear e desbloquear usuário correspondente.

	Contexto: Correspondente está ativo
	  Dado que o correspondente esteja ativo
        
	Esquema do Cenário: Criar novo correspondente
		Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignatarias "17167412007983" > Correspondente
   	E criar novo correspondente "<cnpj>" e codigo "<codigo>"
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    
   Exemplos:
    | usuario    |         cnpj       | codigo |
    | cse        | 52.380.333/0001-97 |   025  |
    | zetra_igor | 59.799.076/0001-71 |   026  |
    
	Cenário: Tentar criar novo correspondente com código já cadastrado
		Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
		E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignatarias "17167412007983" > Correspondente
   	E criar novo correspondente "18.602.365/0001-51" e codigo "002"
    E clicar em salvar
    Então exibe a mensagem de erro "Não é possível criar este(a) correspondente. Existe outro(a) no sistema para o mesmo código e consignatária."

   Esquema do Cenário: Editar correspondente
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignatarias "17167412007983" > Correspondente
   	E editar os dados do correspondente "<nome>"
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    
   Exemplos:
    | usuario    |     nome     |
    | cse        | Alterado CSE |
    | zetra_igor | Alterado SUP |
  
  Cenário: Tentar editar correspondente com código já cadastrado
		Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "cse"
		E que possui o correspondente "09.833.109/0001-84" com codigo "29" criado
		E que o usuario cse ou sup "cse" esteja logado
    Quando acessar menu Manutencao > Consignatarias "17167412007983" > Correspondente
   	E editar os dados do correspondente com codigo "29"
    E clicar em salvar
    Então exibe a mensagem de erro "Não foi possível alterar este(a) correspondente pois existe outro no sistema com o mesmo código e consignatária."
 
 Esquema do Cenário: Excluir correspondente
 		Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
		E que possui o correspondente "<cnpj>" com codigo "<codigo>" criado
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignatarias "17167412007983" > Correspondente
   	E excluir os dados do correspondente "<codigo>"
    Então exibe a mensagem "Correspondente excluído(a) com sucesso."
    
   Exemplos:
    | usuario    |         cnpj       | codigo |
    | cse        | 48.822.226/0001-02 |   027  |
    | zetra_igor | 41.163.854/0001-76 |   028  |
  
    
  Esquema do Cenário: Bloquear correspondente
   	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignatarias "001" > Correspondente
   	E bloquear o correspondente "138"
    Então exibe a mensagem "Correspondente bloqueado(a) com sucesso."
    E verifica que correspondente nao consegue autenticar
      
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
   
   
  Esquema do Cenário: Desbloquear correspondente
  	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
   	E que o correspondente "<codigo>" esteja bloqueado
   	E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignatarias "001" > Correspondente
   	E desbloquear o correspondente "<codigo>"
    Então exibe a mensagem "Correspondente desbloqueado(a) com sucesso."
    E verifica que correspondente consegue autenticar
            
   Exemplos:
    | usuario    | codigo |
    | cse        |  138   |
    | zetra_igor |  138   |

    
  Esquema do Cenário: Bloquear convênios para correspondente
  	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
  	E que o convenio esteja desbloqueado
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignatarias "001" > Correspondente
   	E consultar convenios do correspondente "138" e bloquear
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que correspondente nao consegue fazer reserva
    
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    
   
  Esquema do Cenário: Desbloquear convênios para correspondente
  	Dado que tenha incluido o item de menu Consignantarias no favoritos para usuario "<usuario>"
  	E que o convenio esteja bloqueado
		E que o usuario cse ou sup "<usuario>" esteja logado
    Quando acessar menu Manutencao > Consignatarias "001" > Correspondente
   	E consultar convenios do correspondente "138" e desbloquear
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que correspondente consegue fazer reserva
    
   Exemplos:
    | usuario    |
    | cse        |
    | zetra_igor |
    
    
  Cenário: Criar novo correspondente com papel consignatária
		Dado que o usuario csa "csa2" esteja logado
    Quando acessar menu Manutencao > Correspondentes
   	E criar novo correspondente "32.305.403/0001-35" e codigo "40"
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    
   Cenário: Editar correspondente com papel consignatária
   	Dado que o usuario csa "csa" esteja logado
    Quando acessar menu Manutencao > Correspondentes
   	E editar os dados do correspondente "Alterado CSA"
    E clicar em salvar
    Então exibe a mensagem "Alterações salvas com sucesso."
    
   
   Cenário: Excluir correspondente com papel consignatária
 		Dado que possui o correspondente "79.004.214/0001-96" com codigo "41" criado
		E que o usuario csa "csa" esteja logado
    Quando acessar menu Manutencao > Correspondentes
   	E excluir os dados do correspondente "41"
    Então exibe a mensagem "Correspondente excluído(a) com sucesso."
    
  Cenário: Bloquear correspondente com papel consignatária
   	Dado que o usuario csa "csa2" esteja logado
    Quando acessar menu Manutencao > Correspondentes
   	E bloquear o correspondente "138"
    Então exibe a mensagem "Correspondente bloqueado(a) com sucesso."
    E verifica status no banco e que o correspondente nao consegue autenticar
   
  Cenário: Desbloquear correspondente com papel consignatária
  	Dado que o correspondente "138" esteja bloqueado pela consignataria
   	E que o usuario csa "csa2" esteja logado
    Quando acessar menu Manutencao > Correspondentes
   	E desbloquear o correspondente "138"
    Então exibe a mensagem "Correspondente desbloqueado(a) com sucesso."
    E verifica que correspondente consegue autenticar
    
  Cenário: Desbloquear correspondente com papel consignatária que foi bloqueado pelo consignante 
  	Dado que o correspondente "138" esteja bloqueado
   	E que o usuario csa "csa2" esteja logado
    Quando acessar menu Manutencao > Correspondentes
   	E desbloquear o correspondente "138"
    Então exibe a mensagem de erro "NÃO FOI POSSÍVEL DESBLOQUEAR ESTE(A) CORRESPONDENTE POIS FOI BLOQUEADO PELO CONSIGNANTE."

  Cenário: Bloquear convênios para correspondente com papel consignatária
  	Dado  que o convenio esteja desbloqueado
		E que o usuario csa "csa2" esteja logado
    Quando acessar menu Manutencao > Correspondentes
   	E consultar convenios do correspondente "138" e bloquear
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que correspondente nao consegue fazer reserva
    
  Cenário: Desbloquear convênios para correspondente com papel consignatária
  	Dado que o convenio esteja bloqueado
		E que o usuario csa "csa2" esteja logado
    Quando acessar menu Manutencao > Correspondentes
   	E consultar convenios do correspondente "138" e desbloquear
    Então exibe a mensagem "Alterações salvas com sucesso."
    E verifica que correspondente consegue fazer reserva
  
  Cenário: Configurar auditoria 
   	Dado usuario correspondente "cor30" esteja logado
    Quando acessar menu Manutencao > Correspondente
   	E configurar auditoria correspondente
    Então exibe a mensagem "Atualizações salvas com sucesso."    
    