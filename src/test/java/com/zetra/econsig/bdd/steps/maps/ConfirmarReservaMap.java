package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ConfirmarReservaMap {

	@FindBy(css = ".page-title")
	public WebElement pageTitle;

	@FindBy(id = "TMO_CODIGO")
	public WebElement tmoSelect;

	@FindBy(id = "ADE_OBS")
	public WebElement tmoObs;

	@FindBy(id = "btnEnvia")
	public WebElement btnEnvia;

}
