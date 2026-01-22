package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ConsultarContratoBeneficioElementMap {

	@FindBy(linkText = "Detalhar benefício")
	public WebElement detalharBeneficio;
	
	@FindBy(linkText = "Registrar ocorrência")
	public WebElement registrarOcorrencia;
	
	@FindBy(id = "tmoCodigo")
	public WebElement motivoOperacao;
	
	@FindBy(id = "ocbObs")
	public WebElement observacaoOcorrencia;

	@FindBy(linkText = "Confirmar")
	public WebElement confirmar;

	@FindBy(id = "cbe_data_inicio_vigencia")
	public WebElement dataInicioVigencia;

	@FindBy(id = "dad_valor35")
	public WebElement periodoContribuicao;

	@FindBy(partialLinkText = "Cancelar benefício")
	public WebElement cancelar;

	@FindBy(name = "ocb_obs")
	public WebElement observacao;

	@FindBy(partialLinkText = "Listar lançamentos")
	public WebElement listarLancamento;
	
	@FindBy(xpath = "//*[@id=\"CBC_PERIODO\"]/option[1]")
	public WebElement primeiroPeriodo;

	@FindBy(partialLinkText = "Aprovar solicitação")
	public WebElement aprovar;
	
	@FindBy(partialLinkText = "Rejeitar solicitação")
	public WebElement rejeitar;
}