#language:pt


Funcionalidade: Acompanhar Compra Contrato
	

Cenário: Verifica título da coluna na tabela 
	Dado que o usuario csa "csa" esteja logado
	Quando entra no menu Acompanhar Portabilidade De margem consignável
	E realiza busca na tela de Acompanhar portabilidade de margem consignável "123456"
	Então verifica se existe o titulo vencimento na tabela
	

Cenário: Acompanhar portabilidade de margem consignável - campo Vencimento para status compra "Aguardando Informação Saldo Devedor"
	Dado que o usuario csa "csa" esteja logado
	E que há um processo de compra com status Aguardando Informação Saldo Devedor
	Quando entra no menu Acompanhar Portabilidade De margem consignável
	E realiza busca na tela de Acompanhar portabilidade de margem consignável "123456"
	Então o campo de vencimento será a data de compra mais valor do parâmetro 149 menos um dia
	E exclui processo de compra com status Aguardando Informação Saldo Devedor
	

Cenário: Acompanhar portabilidade de margem consignável - campo Vencimento para status compra "Aguardando Pagamento Saldo Devedor"
	Dado que o usuario csa "csa" esteja logado
	E que há um processo de compra com status Aguardando Pagamento Saldo Devedor
	Quando entra no menu Acompanhar Portabilidade De margem consignável
	E realiza busca na tela de Acompanhar portabilidade de margem consignável "123456"
	Então o campo vencimento será a data de compra mais valor do parametro 150  menos um dia
	E exclui processo de compra com status Aguardando Pagamento Saldo Devedor
	
Cenário: Acompanhar portabilidade de margem consignável - campo Vencimento para status compra "Aguardando Liquidação"
	Dado que o usuario csa "csa" esteja logado
	E que há um processo de compra com status Aguardando Liquidação
	Quando entra no menu Acompanhar Portabilidade De margem consignável
	E realiza busca na tela de Acompanhar portabilidade de margem consignável "123456"
	Então o campo Vencimento será a data de compra mais o parametro 151 menos um dia
	E exclui processo de compra com status Aguardando Liquidação

Cenário: Acompanhar portabilidade de margem consignável - campo Vencimento para status compra diferente de "Aguardando Informação Saldo Devedor", "Aguardando Pagamento Saldo Devedor" e "Aguardando Liquidação"
	Dado que o usuario csa "csa" esteja logado
	E que há um processo de compra com status diferente de Aguardando Informação Saldo Devedor, Aguardando Pagamento Saldo Devedor e Aguardando Liquidação
	Quando entra no menu Acompanhar Portabilidade De margem consignável
	E realiza busca na tela de Acompanhar portabilidade de margem consignável "123456"
	Então no campo Vencimento deve ser exibido um traço
	E exclui processo de compra com status diferente de Aguardando Informação Saldo Devedor, Aguardando Pagamento Saldo Devedor e Aguardando Liquidação