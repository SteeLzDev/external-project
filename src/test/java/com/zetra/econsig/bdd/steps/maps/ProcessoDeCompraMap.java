package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProcessoDeCompraMap {

	@FindBy(id = "ADE_NUMERO")
	public WebElement adeNumero;

	@FindBy(id = "tipoPeriodo")
	public WebElement tipoPeriodo;

	@FindBy(id = "RSE_MATRICULA")
	public WebElement matricula;

	@FindBy(xpath = "//th[contains(.,'Vencimento')]")
	public WebElement tituloTabelaVencimento;

	@FindBy(id = "btnPesquisar")
	public WebElement pesquisar;

	@FindBy(xpath = "//td[12]")
	public WebElement campoSituacao;

	@FindBy(xpath = "//td[11]")
	public WebElement campoVencimentoValor;

	@FindBy(id = "cbOContratoOutraEnt")
	public WebElement cbOContratoOutraEnt;
}