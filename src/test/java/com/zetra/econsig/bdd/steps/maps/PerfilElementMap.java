package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class PerfilElementMap {
	
	@FindBy(id = "acoes")
	public WebElement botaoAcoes;
	
	@FindBy(css = ".show > .dropdown-item:nth-child(3)")
	public WebElement listarPerfil;
	
	@FindBy(id = "idMsgSuccessSession")
	public WebElement txtMensagem;	
	
	@FindBy(linkText = "Criar novo Perfil")
	public WebElement botaoNovo;	
	
	@FindBy(name = "PER_DESCRICAO")
	public WebElement campoDescricao;	
	
	@FindBy(linkText = "Desmarcar tudo")
	public WebElement botaoDesmarcarTudo;
	
	@FindBy(linkText = "Marcar tudo")
	public WebElement botaoMarcarTudo;	
	
	@FindBy(linkText = "Salvar")
	public WebElement botaoSalvar;	
	
	@FindBy(linkText = "Cancelar")
	public WebElement botaoCancelar;
	
	@FindBy(linkText = "Bloquear")
	public WebElement botaoBloquear;
	
	@FindBy(linkText = "Desbloquear")
	public WebElement botaoDesbloquear;
	
	@FindBy(linkText = "Excluir")
	public WebElement botaoExcluir;
	
	@FindBy(linkText = "Editar")
	public WebElement botaoEditar;
	
	@FindBy(id = "FILTRO")
	public WebElement campoFiltro;
	
	@FindBy(id = "FILTRO_TIPO")
	public WebElement comboFiltro;
	
	@FindBy(id = "NCA_CODIGO")
	public WebElement natureza;
		
	@FindBy(partialLinkText = "Pesquisar")
	public WebElement btnPesquisar;
	
	@FindBy(id = "idMsgSuccessSession")
	public WebElement txtMensagemSucesso;	
	
	@FindBy(css = "h2[class='card-header-title']")
	public WebElement tituloTelaEditar;	
	
	@FindBy(className = "page-title")
	public WebElement tituloPagina;	
}
