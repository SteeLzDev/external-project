package com.zetra.econsig.tdd.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.tdd.tests.maps.RelatoriosElementMap;

public class RelatoriosPage extends BasePage {

    private final RelatoriosElementMap relatorioElementMap;

    public RelatoriosPage(WebDriver webDriver) {
        super(webDriver);
        relatorioElementMap = PageFactory.initElements(webDriver, RelatoriosElementMap.class);
    }

    public void marcarSituacaoAtivo() {
        clickWebElement(relatorioElementMap.situacaoAtivo);
    }

    public void marcarSituacaoBloqueado() {
        clickWebElement(relatorioElementMap.situacaoBloqueado);
    }

    public void marcarAgendamento() {
        while (!relatorioElementMap.agendamentoSim.isSelected()) {
            clickWebElement(relatorioElementMap.agendamentoSim);
        }
    }

    public void selecionarTipoAgendamento(String tipo) {
        waitDriver.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(relatorioElementMap.tipoAgendamento));

        while (relatorioElementMap.tipoAgendamento.getDomProperty("value").isEmpty()) {
            clickWebElement(relatorioElementMap.tipoAgendamento);
            setWebElementValue(relatorioElementMap.tipoAgendamento, tipo);
        }
    }

    public void clicarConfirmar() {
        clickWebElement(relatorioElementMap.confirmar);
    }

    public void clicarAgendar() {
        clickWebElement(relatorioElementMap.btAgendar);
    }

    public void clicarCancelarAgendamento() {
        waitDriver.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(relatorioElementMap.cancelar));

        while (!SeleniumHelper.isAlertPresent(webDriver)) {
            clickWebElement(relatorioElementMap.cancelar);
        }
    }

    public void selecionarFormato(String formato) {
        while (relatorioElementMap.formato.getDomProperty("value").isEmpty()) {
            clickWebElement(relatorioElementMap.formato);
            setWebElementValue(relatorioElementMap.formato, formato);
            actions.sendKeys(Keys.TAB);
        }
    }

    public void clicarOpcao() {
        while (relatorioElementMap.opcoes.getDomAttribute("aria-expanded").contains("false")) {
            clickWebElement(relatorioElementMap.opcoes);
        }
    }

    public void excluiRelatorioNaoAgendadoInterface() {
        clickWebElement(relatorioElementMap.excluir);
    }

    public boolean verificarDownloadRelatorio() {
        return relatorioElementMap.download.isEnabled();
    }

    public void selecionarEmpresaCorrespondente(String empresa) {
        clickWebElement(relatorioElementMap.empresaCorrespondente);
        setWebElementValue(relatorioElementMap.empresaCorrespondente, empresa);
    }

    public void selecionarConsignataria(String csa) {
        clickWebElement(relatorioElementMap.consignataria);
        setWebElementValue(relatorioElementMap.consignataria, csa);
    }

    public int quantidadeRelatorios() {
        WebElement main = webDriver.findElement(By.cssSelector(".table"));
        return main.findElements(By.xpath(".//tbody/tr")).size();
    }

    public void selecionarMargens(String margem) {
        clickWebElement(relatorioElementMap.margens);
        setWebElementValue(relatorioElementMap.margens, margem);
    }

    public void marcarSituacaoServidorAtivo() {
        clickWebElement(relatorioElementMap.servidorAtivo);
    }

    public void marcarSinalMargem1Positiva() {
        clickWebElement(relatorioElementMap.sinalMargem1Positiva);
    }

    public void selecionarEstabelecimento(String estabelecimento) {
    	waitDriver.until(ExpectedConditions.elementToBeClickable(relatorioElementMap.estabelecimento));
    	
        clickWebElement(relatorioElementMap.estabelecimento);
        setWebElementValue(relatorioElementMap.estabelecimento, estabelecimento);
    }

    public void selecionarOrgao(String orgao) {
        clickWebElement(relatorioElementMap.orgao);
        setWebElementValue(relatorioElementMap.orgao, orgao);
    }

    public void preencherPeriodoInicial(String data) {
        clickWebElement(relatorioElementMap.periodoInicial);
        setWebElementValue(relatorioElementMap.periodoInicial, data);
    }

    public void preencherPeriodoFinal(String data) {
        clickWebElement(relatorioElementMap.periodoFim);
        setWebElementValue(relatorioElementMap.periodoFim, data);
    }

    public void preencherPeriodo(String mesAno) {
        clickWebElement(relatorioElementMap.periodo);
        setWebElementValue(relatorioElementMap.periodo, mesAno);
    }

    public void selecionarConsignante(String cse) {
        clickWebElement(relatorioElementMap.consignante);
        setWebElementValue(relatorioElementMap.consignante, cse);
    }

    public void preencherMatricula(String matricula) {
        clickWebElement(relatorioElementMap.matricula);
        setWebElementValue(relatorioElementMap.matricula, matricula);
    }

    public void selecionarGrupoServico(String grupoSvc) {
        clickWebElement(relatorioElementMap.grupoServico);
        setWebElementValue(relatorioElementMap.grupoServico, grupoSvc);
    }

    private void clickWebElement(WebElement element) {
        // Espera o elemento estar visível
        waitDriver.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(element));

        js.executeScript("arguments[0].click()", element);
    }

    private void setWebElementValue(WebElement element, String value) {
        // Se <input type="text"> limpa o campo antes de enviar os dados
        if ("input".equalsIgnoreCase(element.getTagName()) &&
                "text".equalsIgnoreCase(element.getDomProperty("type"))) {
            element.clear();
        }
        element.sendKeys(value);
    }
}
