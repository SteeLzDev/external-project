package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class EditarParametroPostoGraduacaoMap {

	@FindBy(className = "page-title")
	public WebElement tituloPagina;

	@FindBy(css = "div:nth-child(2) > div > div.card-header > h2")
	public WebElement tituloTela;

	@FindBy(id = "SVC_CODIGO")
	public WebElement svcCodigo;

	@FindBy(className = "needs-validation")
	public WebElement tabela;

	@FindBy(id = "141")
	public WebElement campoSoldado;

	@FindBy(id = "143")
	public WebElement campoTenente;

	@FindBy(id = "142")
	public WebElement campoCapitao;

	@FindBy(className = "btn-primary")
	public WebElement enviarDados;

	@FindBy(id = "idMsgSuccessSession")
	public WebElement mensagemDeSucesso;

	@FindBy(linkText = "Postos")
	public WebElement campoVerificaTabela;
}
