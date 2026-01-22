package com.zetra.econsig.bdd.steps.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.zetra.econsig.bdd.steps.maps.RelatorioMovimentoFinanceiroServidorElementMap;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class RelatorioMovimentoFinanceiroServidorPage extends BasePage{

    private final RelatorioMovimentoFinanceiroServidorElementMap relatorioMovimentoFinanceiroServidorElementMap;

    public RelatorioMovimentoFinanceiroServidorPage(WebDriver webDriver) {
        super(webDriver);
        relatorioMovimentoFinanceiroServidorElementMap = PageFactory.initElements(webDriver, RelatorioMovimentoFinanceiroServidorElementMap.class);
    }
	
	public void selecionaFormatoRelatorio(String formato) {
		js.executeScript("arguments[0].click()", relatorioMovimentoFinanceiroServidorElementMap.campoFormatoRelatorio);
		relatorioMovimentoFinanceiroServidorElementMap.campoFormatoRelatorio.sendKeys(formato);	
	}
	
	public void botaoConfirmarRelatorio() { 
		js.executeScript("arguments[0].click()", relatorioMovimentoFinanceiroServidorElementMap.confirmaRelatorioMovimentoServidor);
	}

	public void preencherDataInicial(String dataIni) {
		relatorioMovimentoFinanceiroServidorElementMap.dataIni.sendKeys(dataIni);
	}

	public void preencherDataFim(String dataFim) { 
		relatorioMovimentoFinanceiroServidorElementMap.dataFim.sendKeys(dataFim);
	}
	
	public void preencherMatricula(String matricula ) {
	    relatorioMovimentoFinanceiroServidorElementMap.campoMatricula.click();
	    relatorioMovimentoFinanceiroServidorElementMap.campoMatricula.clear();
		relatorioMovimentoFinanceiroServidorElementMap.campoMatricula.sendKeys(matricula);
	}
	
	public void preencherCpf(String cpf) { 
	    relatorioMovimentoFinanceiroServidorElementMap.campoCpf.click();
		relatorioMovimentoFinanceiroServidorElementMap.campoCpf.sendKeys(cpf);
	}
	
	public String mensagemInformacao() { 
		return relatorioMovimentoFinanceiroServidorElementMap.campoMensagemInformacao.getText();
	}
	
	public void preencherModalAutorizacao() { 
	    relatorioMovimentoFinanceiroServidorElementMap.campoAutorizacaoUsername.sendKeys("cse");
	    relatorioMovimentoFinanceiroServidorElementMap.campoAutorizacaoSenha.sendKeys(LoginValues.cse2.getSenha());
        js.executeScript("arguments[0].click()", relatorioMovimentoFinanceiroServidorElementMap.confirmaAutorizacao);
	}
	
	public String mensagemInformacaoDois() { 
		return relatorioMovimentoFinanceiroServidorElementMap.campoMensagemInformacaoDois.getText();
	}
	
	public String validaSeRelatorioFoiCriado() { 
		return relatorioMovimentoFinanceiroServidorElementMap.campoTabelaRelatorio.getText();
	}

}