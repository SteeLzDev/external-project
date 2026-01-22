package com.zetra.econsig.bdd.steps.pages;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.ReservarMargemElementMap;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ReservarMargemPage extends BasePage {

    private final ReservarMargemElementMap reservarMargemElementMap;

    public ReservarMargemPage(WebDriver webDriver) {
        super(webDriver);
        reservarMargemElementMap = PageFactory.initElements(webDriver, ReservarMargemElementMap.class);
    }

    public void selecionarServicoPeloIdentificador(String servicoId) {
        await.pollDelay(1, TimeUnit.SECONDS).until(() -> reservarMargemElementMap.comboServico.getDomProperty("value"), is(""));

        SeleniumHelper.selectByVisibleText(reservarMargemElementMap.comboServico, servicoId);
    }

    public void selecionarServico(String servico) {
        await.pollDelay(1, TimeUnit.SECONDS).until(() -> reservarMargemElementMap.comboServico.getDomProperty("value"), is(""));

        for (int i = 0; i < 10; i++) {
            if (reservarMargemElementMap.comboServico.getDomProperty("value").isEmpty()) {
                reservarMargemElementMap.comboServico.sendKeys(servico);
            } else {
                break;
            }
        }
    }

    public void preencherMatricula(String matricula) {
        await.pollDelay(2, TimeUnit.SECONDS).until(() -> webDriver.getPageSource().contains("Dados da consignação"));
        waitDriver.until(ExpectedConditions.visibilityOf(reservarMargemElementMap.campoMatricula));

        reservarMargemElementMap.campoMatricula.sendKeys(matricula);
    }

    public void clicarPesquisar() {
        waitDriver.until(ExpectedConditions.elementToBeClickable(reservarMargemElementMap.btnPesquisar));
        js.executeScript("arguments[0].click()", reservarMargemElementMap.btnPesquisar);

    }

    public void preencherValorPrestacao(String valorParcela) {
        waitDriver.until(ExpectedConditions.visibilityOf(reservarMargemElementMap.parcela));

        while (!reservarMargemElementMap.parcela.getDomProperty("value").contains(valorParcela)) {
            reservarMargemElementMap.parcela.sendKeys(valorParcela);
        }
    }

    public void preencherValorLiquidoLiberado(String valorLiquido) {
        waitDriver.until(ExpectedConditions.visibilityOf(reservarMargemElementMap.valorLiquido));

        while (!reservarMargemElementMap.valorLiquido.getDomProperty("value").contains(valorLiquido)) {
            reservarMargemElementMap.valorLiquido.sendKeys(valorLiquido);
        }
    }

    public void preencherNumeroPrestacao(String nroPrestacao) {
        reservarMargemElementMap.numeroPrestacoes.sendKeys(nroPrestacao);
    }

    public void selecionarNumeroPrestacao(String nroPrestacao) {
        SeleniumHelper.selectByVisibleText(reservarMargemElementMap.numeroPrestacoesSelect, nroPrestacao);
    }

    public void preencherValorCarencia(String valorCarencia) {
        reservarMargemElementMap.carencia.clear();
        reservarMargemElementMap.carencia.sendKeys(valorCarencia);
    }

    public void preencherValorCET(String valorCET) {
        js.executeScript("arguments[0].click()", reservarMargemElementMap.valorCET);
        reservarMargemElementMap.valorCET.sendKeys(valorCET);
    }

    public void marcarPrazoIndeterminado() {
        await.until(() -> webDriver.getPageSource().contains("Prazo indeterminado"));

        while (reservarMargemElementMap.numeroPrestacoes.isEnabled()) {
            reservarMargemElementMap.prazoIndeterminado.click();
            actions.sendKeys(Keys.TAB);
        }
    }

    public void clicarConfirmar() {
        waitDriver.until(ExpectedConditions.elementToBeClickable(reservarMargemElementMap.btnConfirmar));
        js.executeScript("arguments[0].click()", reservarMargemElementMap.btnConfirmar);
    }

    public void clicarConfirmarComErro() {
        waitDriver.until(ExpectedConditions.elementToBeClickable(reservarMargemElementMap.btnConfirmar));

        while (!SeleniumHelper.isAlertPresent(webDriver)) {
            js.executeScript("arguments[0].click()", reservarMargemElementMap.btnConfirmar);
        }
    }

    public void clicarEnviar() {
        await.pollDelay(2, TimeUnit.SECONDS).until(() -> reservarMargemElementMap.txtMensagemInfo.getText(), is("Favor verificar e confirmar as informações abaixo"));
        js.executeScript("arguments[0].click()", reservarMargemElementMap.btnEnviar);
        await.until(() -> webDriver.getPageSource(), containsString("Dados do consignante"));
    }

    public void clicarEnviarComErro() {
        waitDriver.until(ExpectedConditions.textToBePresentInElement(reservarMargemElementMap.txtMensagemInfo, "Favor verificar e confirmar as informações abaixo"));

        reservarMargemElementMap.btnEnviar.submit();

        await.until(() -> webDriver.getPageSource(), not("Dados do consignante"));
    }

    public String retornarPrestacaoTelaConfirmar() {
        waitDriver.until(ExpectedConditions.textToBePresentInElement(reservarMargemElementMap.txtMensagemInfo, "Favor verificar e confirmar as informações abaixo"));

        return reservarMargemElementMap.txtValorPrestacaoTelaConfirmar.getDomProperty("value");
    }

    public String retornarPrazoTelaConfirmar() {
        return reservarMargemElementMap.txtPrazoTelaConfirmar.getDomProperty("value");
    }

    public String retornarPrazoIndeterminadoTelaConfirmar() {
        waitDriver.until(ExpectedConditions.textToBePresentInElement(reservarMargemElementMap.txtMensagemInfo, "Favor verificar e confirmar as informações abaixo"));

        return reservarMargemElementMap.prazoIndeterminado.getDomProperty("value");
    }

    public String retornarValorLiquidoTelaConfirmar() {
        return reservarMargemElementMap.txtValorLiquidoTelaConfirmar.getDomProperty("value");
    }

    public String retornarValorCarenciaTelaConfirmar() {
        return reservarMargemElementMap.txtValorCarenciaTelaConfirmar.getText();
    }

    public String retornarValorCETTelaConfirmar() {
        return reservarMargemElementMap.txtValorCETTelaConfirmar.getDomProperty("value");
    }

    public String retornarPrestacaoTelaSucesso() {
        return reservarMargemElementMap.txtValorPrestacaoTelaSucesso.getText();
    }

    public String retornarPrazoTelaSucesso() {
        return reservarMargemElementMap.txtPrazoTelaSucesso.getText();
    }

    public String retornarValorLiquidoTelaSucesso() {
        return reservarMargemElementMap.txtValorLiquidoTelaSucesso.getText();
    }

    public String retornarSituacao() {
        return reservarMargemElementMap.txtSituacaoTelaSucesso.getText();
    }

    public String retornarValorCarenciaTelaSucesso() {
        return reservarMargemElementMap.txtValorCarenciaTelaSucesso.getText();
    }

    public String retornarValorCETTelaSucesso() {
        return reservarMargemElementMap.txtValorCETTelaSucesso.getText();
    }

    public String retornarValorCETAnualTelaSucesso() {
        return reservarMargemElementMap.txtValorCETAnualTelaSucesso.getText();
    }

    public String retornarMensagemErro() {
        await.until(() -> reservarMargemElementMap.txtMensagemErro.getText(), notNullValue());

        return reservarMargemElementMap.txtMensagemErro.getText();
    }

    public String retornarPrazoUsuarioCorTelaConfirmar() {
        return reservarMargemElementMap.txtPrazoUsuarioCorTelaConfirmar.getText();
    }

    public void selecionarConsignataria(String consignataria) {
        await.until(() -> webDriver.getPageSource().contains("Reservar Margem Consignável"));

        Select select = new Select(reservarMargemElementMap.consignataria);
        select.selectByVisibleText(consignataria);
    }

    public void selecionarOrgao(String orgao) {
        await.until(() -> webDriver.getPageSource().contains("Filtros da pesquisa"));

        Select select = new Select(reservarMargemElementMap.orgao);
        select.selectByVisibleText(orgao);
    }

    public void selecionarIndice(String indice) {
        if (reservarMargemElementMap.indice.isEnabled() && reservarMargemElementMap.indice.getTagName().equalsIgnoreCase("select")) {
            Select select = new Select(reservarMargemElementMap.indice);
            select.selectByVisibleText(indice);
        }
    }

    public void preencherSenhaServidor(String senha) {
        await.pollDelay(2, TimeUnit.SECONDS).until(() -> reservarMargemElementMap.txtMensagemInfo.getText(), is("Favor verificar e confirmar as informações abaixo"));

        reservarMargemElementMap.senhaServidor.sendKeys(senha);
    }

    public void criarReserva(String loginSer) {
        selecionarServico("EMPRÉSTIMO - 001");
        preencherMatricula(loginSer);
        clicarPesquisar();
        preencherValorPrestacao("10");
        preencherValorLiquidoLiberado("200");
        marcarPrazoIndeterminado();
        clicarConfirmar();
        clicarEnviar();
    }

    public String criarReservaMargem(String loginSer) {
        selecionarServico("EMPRÉSTIMO - 001");
        preencherMatricula(loginSer);
        clicarPesquisar();
        preencherValorPrestacao("10");
        preencherValorLiquidoLiberado("200");
        preencherNumeroPrestacao("9");
        clicarConfirmar();
        clicarEnviar();

        return reservarMargemElementMap.txtAde.getText();
    }

    public String criarReservaMargemComSenhaServidor(LoginInfo loginSer) {
        selecionarServico("EMPRÉSTIMO - 001");
        preencherMatricula(loginSer.getLogin());
        clicarPesquisar();
        preencherValorPrestacao("10");
        preencherValorLiquidoLiberado("200");
        preencherNumeroPrestacao("9");
        clicarConfirmar();
        preencherSenhaServidor(loginSer.getSenha());
        clicarEnviar();

        return reservarMargemElementMap.txtAde.getText();
    }
}
