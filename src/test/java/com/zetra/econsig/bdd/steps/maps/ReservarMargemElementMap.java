package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ReservarMargemElementMap {

	//pesquisa o servidor
    @FindBy(id = "SVC_CODIGO")
    public  WebElement comboServico;

    @FindBy(id = "RSE_MATRICULA")
    public  WebElement campoMatricula;

    @FindBy(id = "btnPesquisar")
    public  WebElement btnPesquisar;

    @FindBy(id = "adeVlr")
    public WebElement parcela;

    @FindBy(name = "adePrz")
    public WebElement prazo;

    @FindBy(css = "a[title='Confirmar']")
    public WebElement btnConfirmar;

    @FindBy(name = "RSE_CODIGO")
    public  WebElement rseCodigo;

    @FindBy(css = "button.btn.btn-primary")
    public WebElement btnVoltar;

    @FindBy(name = "adePrz")
    public  WebElement numeroPrestacoes;

    @FindBy(name = "adePrazoAux")
    public  WebElement numeroPrestacoesSelect;

    @FindBy(id = "adeVlrLiquido")
    public  WebElement valorLiquido;

    @FindBy(id = "adeCarencia")
    public  WebElement carencia;

    @FindBy(id = "adeTaxaJuros")
	public WebElement valorCET;

    @FindBy(id = "adeSemPrazo")
    public  WebElement prazoIndeterminado;

    @FindBy(id = "btnEnvia")
    public  WebElement btnEnviar;

    @FindBy(id = "numBanco")
    public  WebElement banco;

    @FindBy(id = "numAgencia")
    public  WebElement agencia;

    @FindBy(id = "numConta")
    public  WebElement conta;

    @FindBy(id = "senha")
    public  WebElement senhaAutorizacaoServidor;

    @FindBy(id = "acoes")
    public  WebElement btnAcoes;

    @FindBy(id = "idMsgErrorSession")
    public  WebElement txtMensagemErro;

    @FindBy(css = "li > .mb-0")
    public  WebElement txtMensagemInfo;

    //tela de confirmação
    @FindBy(id = "adePrazo")
    public WebElement txtPrazoTelaConfirmar;

//    @FindBy(css = ".col-6:nth-child(53)")
//    public WebElement txtPrazoIndeterminadoTelaConfirmar;

    @FindBy(css = ".col-6:nth-child(48)")
    public WebElement txtPrestacaoSemVlrLiquidoTelaConfirmar;

    @FindBy(id = "adeVlrLiquido")
    public WebElement txtValorLiquidoTelaConfirmar;

    @FindBy(id = "adeVlr")
    public WebElement txtValorPrestacaoTelaConfirmar;

    @FindBy(css = ".col-6:nth-child(63)")
	public WebElement txtValorCarenciaTelaConfirmar;

    @FindBy(id = "adeTaxaJuros")
	public WebElement txtValorCETTelaConfirmar;

    //tela de sucesso
    @FindBy(css = ".card:nth-child(1) .col-6:nth-child(6)")
    public WebElement txtSituacaoTelaSucesso;

    @FindBy(css = ".card:nth-child(1) .col-6:nth-child(12)")
    public WebElement txtPrazoTelaSucesso;

    @FindBy(css = ".col-6:nth-child(28)")
    public WebElement txtValorLiquidoTelaSucesso;

    @FindBy(css = ".card:nth-child(1) .col-6:nth-child(10)")
    public WebElement txtValorPrestacaoTelaSucesso;

    @FindBy(css = ".card:nth-child(1) .col-6:nth-child(14)")
	public WebElement txtValorCarenciaTelaSucesso;

    @FindBy(css = ".col-6:nth-child(30)")
	public WebElement txtValorCETTelaSucesso;

    @FindBy(css = ".col-6:nth-child(32)")
	public WebElement txtValorCETAnualTelaSucesso;

    @FindBy(css = ".col-sm:nth-child(1) .col-6:nth-child(2)")
	public WebElement txtAde;

    @FindBy(id = "CSA_CODIGO")
	public WebElement consignataria;

    @FindBy(id = "ORG_CODIGO")
	public WebElement orgao;

    //tela usuario correspondente
    @FindBy(css = ".col-6:nth-child(55)")
    public WebElement txtPrazoUsuarioCorTelaConfirmar;

    @FindBy(css = ".col-6:nth-child(50)")
    public WebElement txtPrestacaoUsuarioCorTelaConfirmar;

    @FindBy(id = "adeIndice")
	public WebElement indice;

    @FindBy(id = "senha")
	public WebElement senhaServidor;
}
