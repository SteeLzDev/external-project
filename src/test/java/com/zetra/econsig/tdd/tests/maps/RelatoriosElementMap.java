package com.zetra.econsig.tdd.tests.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RelatoriosElementMap {
	
	@FindBy(id = "CSA_ATIVO_1")
	public WebElement situacaoAtivo;
	
	@FindBy(id = "CSA_ATIVO_0")
	public WebElement situacaoBloqueado;
	
	@FindBy(id = "formato")
	public WebElement formato;	
	
	@FindBy(name = "btnEnvia")
	public WebElement confirmar;	
	
	@FindBy(partialLinkText = "Opções")
	public WebElement opcoes;
	
	@FindBy(linkText = "Excluir")
	public WebElement excluir;
	
	@FindBy(linkText = "Download")
	public WebElement download;
	
	@FindBy(id = "agendadoSim")
	public WebElement agendamentoSim;
	
	@FindBy(id = "agendadoNao")
	public WebElement agendamentoNao;

	@FindBy(id = "tagCodigo")
	public WebElement tipoAgendamento;
	
	@FindBy(linkText = "Cancelar")
	public WebElement cancelar;

	@FindBy(id = "ecoCodigo")
	public WebElement empresaCorrespondente;
	
	@FindBy(id = "csaCodigo")
	public WebElement consignataria;
	
	@FindBy(id = "MAR_CODIGO")
	public WebElement margens;
	
	@FindBy(id = "SRS_CODIGO0")
	public WebElement servidorAtivo;
	
	@FindBy(id = "estCodigo")
	public WebElement estabelecimento;
	
	@FindBy(id = "orgCodigo")
	public WebElement orgao;
	
	@FindBy(id = "SINAL11")
	public WebElement sinalMargem1Positiva;

	@FindBy(id = "cseCodigo")
	public WebElement consignante;
	
	@FindBy(id = "periodoIni")
	public WebElement periodoInicial;

	@FindBy(id = "periodoFim")
	public WebElement periodoFim;
		
	@FindBy(id = "periodo")
	public WebElement periodo;
	
	@FindBy(id = "btnAgenda")
	public WebElement btAgendar;

	@FindBy(id = "matricula")
	public WebElement matricula;
	
	@FindBy(id = "grupoServico")
	public WebElement grupoServico;

}
