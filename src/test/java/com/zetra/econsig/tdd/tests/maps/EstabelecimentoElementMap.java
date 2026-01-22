package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class EstabelecimentoElementMap {
	
	@FindBy(linkText = "Criar novo estabelecimento")
	public WebElement botaoCriarEstabelecimento;
	
	@FindBy(name = "EST_IDENTIFICADOR")
	public WebElement codigoIdentificador;
	
	@FindBy(name = "EST_NOME")
	public WebElement nome;
	
	@FindBy(name = "EST_CNPJ")
	public WebElement cnpj;
	
	@FindBy(name = "EST_RESPONSAVEL")
	public WebElement responsavel1;	
	
	@FindBy(name = "EST_RESP_CARGO")
	public WebElement cargoResponsavel1;
	
	@FindBy(name = "EST_RESP_TELEFONE")
	public WebElement telefoneResponsavel1;	
	
	@FindBy(name = "EST_LOGRADOURO")
	public WebElement logradouro;
	
	@FindBy(name = "EST_NRO")
	public WebElement numero;
	
	@FindBy(name = "EST_COMPL")
	public WebElement complemento;
	
	@FindBy(name = "EST_BAIRRO")
	public WebElement bairro;
	
	@FindBy(name = "EST_CIDADE")
	public WebElement cidade;
	
	@FindBy(name = "EST_UF")
	public WebElement uf;
	
	@FindBy(name = "EST_CEP")
	public WebElement cep;
	
	@FindBy(name = "EST_TEL")
	public WebElement telefoneContato;
	
	@FindBy(name = "EST_EMAIL")
	public WebElement email;
	
	@FindBy(linkText = "Salvar")
	public WebElement botaoSalvar;
	
	@FindBy(css = "a[alt='Clique aqui para desbloquear este estabelecimento.']")
	public WebElement desbloquear;	
	
	@FindBy(partialLinkText = "Bloquear")
	public WebElement bloquear;	
	
	@FindBy(linkText = "Excluir")
	public WebElement excluir;	
	
	@FindBy(css = "a[alt='Clique aqui para editar este estabelecimento.']")
	public WebElement editar;	
	
	@FindBy(id = "FILTRO_TIPO")
	public WebElement comboTipoFiltro;	
	
	@FindBy(id = "FILTRO")
	public WebElement campoFiltro;	
	
	@FindBy(css = ".btn-action:nth-child(2) > .btn")
	public WebElement pesquisar;	
}
