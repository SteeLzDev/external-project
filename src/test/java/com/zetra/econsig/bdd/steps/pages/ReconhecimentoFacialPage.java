package com.zetra.econsig.bdd.steps.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.zetra.econsig.bdd.steps.maps.ReconhecimentoFacialElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ReconhecimentoFacialPage extends BasePage{

    private final ReconhecimentoFacialElementMap reconhecimentoFacialElementMap;

    public ReconhecimentoFacialPage(WebDriver webDriver) {
        super(webDriver);
        reconhecimentoFacialElementMap = PageFactory.initElements(webDriver, ReconhecimentoFacialElementMap.class);
    }

	public void preencherMatriculaServidor() {
	    reconhecimentoFacialElementMap.matriculaServidor.sendKeys("123456");
	}

	public void selecionaSolicitarEmprestimo() {
	    reconhecimentoFacialElementMap.soliciarEmprestimo.click();
	}

	public void selecionaConsignataria() {
	    reconhecimentoFacialElementMap.bancoBrasil.click();
	}

	public void preencherValorPrestacao(String vlrPrestacao) {
	    reconhecimentoFacialElementMap.preencherValorPrestacaoEmprestimo.sendKeys(vlrPrestacao);
	}

	public void preencherValorNumeroPrestacao(String vlrNumeroPrestacao) {
	    reconhecimentoFacialElementMap.preencherNumeroPrestacoes.sendKeys(vlrNumeroPrestacao);
	}

	public void confirmarSolicitacaoEmprestimo() {
	    reconhecimentoFacialElementMap.confirmarSolicitacaoEmprestimo.click();
	}

	public boolean validarReconhecimentoFacialSimularConsignacao() {
	    try {
	        await.pollDelay(1, TimeUnit.SECONDS).until(() -> reconhecimentoFacialElementMap.reconhecimentoFacialModal.isEnabled());
            await.pollDelay(1, TimeUnit.SECONDS).until(() -> reconhecimentoFacialElementMap.reconhecimentoFacialModal.isDisplayed());
	        return reconhecimentoFacialElementMap.reconhecimentoFacialModal.isDisplayed();
	    } catch (Exception ex) {
	        log.error(ex.getMessage(), ex);
	        return false;
	    }
	}
}