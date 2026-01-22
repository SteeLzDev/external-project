package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ManutencaoCorrespondenteElementMap {

	@FindBy(linkText = "Criar novo correspondente")
	public WebElement novoCorrespondente;
	
	@FindBy(id = "iCNPJ")
	public WebElement cnpj;
	
	@FindBy(id = "btnEnvia")
	public WebElement pesquisar;
	
	@FindBy(id = "codigo")
	public WebElement codigoCorrespondente;
	
	@FindBy(id = "nome")
	public WebElement nome;
	
	@FindBy(id = "responsavel1")
	public WebElement responsavel1;
	
	@FindBy(id = "cargo1")
	public WebElement cargo1;
	
	@FindBy(id = "telefone1")
	public WebElement telefone1;
	
	@FindBy(id = "logradouro")
	public WebElement logradouro;
	
	@FindBy(id = "numero")
	public WebElement numero;
	
	@FindBy(id = "bairro")
	public WebElement bairro;
	
	@FindBy(id = "cidade")
	public WebElement cidade;
	
	@FindBy(name = "COR_UF")
	public WebElement uf;
	
	@FindBy(id = "cep")
	public WebElement cep;
	
	@FindBy(id = "fone")
	public WebElement contatoTel;
	
	@FindBy(id = "COR_EMAIL")
	public WebElement email;
	
	@FindBy(id = "751F8080808080808080808080809Z85")
	public WebElement codigoConvenio;

	@FindBy(linkText = "Configurar auditoria")
	public WebElement configurarAuditoria;
	
	@FindBy(id = "checkGrupo287")
	public WebElement todosGeral;

	@FindBy(id = "checkGrupo150")
	public WebElement todosOperacional;	
	
	@FindBy(id = "funcao339")
	public WebElement funcaoConsultarUsuarioCorrespondente;

	@FindBy(id = "funcao206")
	public WebElement funcaoValidacaoLote;
	
	@FindBy(id = "checkGrupo174")
	public WebElement todosManutencaoUsuarioServidores;

	@FindBy(id = "checkGrupo206")
	public WebElement todosIntegracaoFolha;

}
