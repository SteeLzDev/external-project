package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GrupoServicoElementMap {
	
	@FindBy(linkText = "Novo grupo de serviço")
	public WebElement botaoCriar;
	
	@FindBy(id = "tgsIdentificador")
	public WebElement codigoIdentificador;
	
	@FindBy(id = "tgsGrupo")
	public WebElement descricao;
	
	@FindBy(id = "tgsQuantidade")
	public WebElement quantidadeGeral;
	
	@FindBy(id = "tgsQuantidadePorCsa")
	public WebElement quantidadePorCsa;
	
	@FindBy(linkText = "Salvar")
	public WebElement botaoSalvar;	
	
	@FindBy(linkText = "Editar")
	public WebElement editar;	
	
	@FindBy(linkText = "Excluir")
	public WebElement excluir;	
	
	@FindBy(linkText = "Serviço")
	public WebElement servico;		
}
