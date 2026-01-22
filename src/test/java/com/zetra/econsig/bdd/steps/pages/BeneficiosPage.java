package com.zetra.econsig.bdd.steps.pages;

import static org.hamcrest.CoreMatchers.containsString;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.BeneficiosElementMap;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class BeneficiosPage extends BasePage {

    private final BeneficiosElementMap beneficiosElementMap;

    public BeneficiosPage(WebDriver webDriver) {
        super(webDriver);
        beneficiosElementMap = PageFactory.initElements(webDriver, BeneficiosElementMap.class);
    }

    public void clicarNovoBeneficio() {
        beneficiosElementMap.novoBeneficio.click();

        await.until(() -> webDriver.getPageSource(), containsString("Inclusão de novo benefício"));
    }

    public void clicarSalvar() {
        waitDriver.until(ExpectedConditions.elementToBeClickable(beneficiosElementMap.salvar));
        js.executeScript("arguments[0].click()", beneficiosElementMap.salvar);
    }

    public void clicarEditar() {
        await.pollDelay(Duration.ofSeconds(1)).until(() -> webDriver.getPageSource().contains("Beneficiário"));
        beneficiosElementMap.editar.click();
    }

    public void clicarExcluir() {
        waitDriver.until(ExpectedConditions.elementToBeClickable(beneficiosElementMap.excluir));
        js.executeScript("arguments[0].click()", beneficiosElementMap.excluir);
    }

    public void selecionarOperadora(String operadora) {
        Select select = new Select(beneficiosElementMap.operadora);
        select.selectByValue(operadora);
    }

    public void selecionarNatureza(String natureza) {
        Select select = new Select(beneficiosElementMap.natureza);
        select.selectByVisibleText(natureza);
    }

    public void selecionarServico() {
        beneficiosElementMap.servicoPlanoDependente.click();
    }

    public void preencherDescricao(String descricao) {
        while (!beneficiosElementMap.descricaoBeneficio.getDomProperty("value").contains(descricao)) {
            beneficiosElementMap.descricaoBeneficio.click();
            beneficiosElementMap.descricaoBeneficio.sendKeys(descricao);
        }
    }

    public void preencherCodigoPlano(String codigo) {
        beneficiosElementMap.codigoPlano.sendKeys(codigo);
    }

    public void preencherCodigoRegistro(String codigo) {
        beneficiosElementMap.codigoRegistro.sendKeys(codigo);
    }

    public void preencherCodigoContrato(String codigo) {
        beneficiosElementMap.codigoContrato.sendKeys(codigo);
    }

    public void selecionarTipoBeneficiario(String tipo) {
        Select select = new Select(beneficiosElementMap.tipoBeneficiario);
        select.selectByVisibleText(tipo);
    }

    public void preencherDadosBeneficios(String operadora, String natureza, String descricao, String codPlano, String codRegistro, String codContrato, String tipoBeneficiario) {
        limparDadosBeneficiario();
        selecionarOperadora(operadora);
        selecionarNatureza(natureza);
        preencherDescricao(descricao);
        preencherCodigoPlano(codPlano);
        preencherCodigoRegistro(codRegistro);
        preencherCodigoContrato(codContrato);
        selecionarTipoBeneficiario(tipoBeneficiario);
    }

    public void limparDadosBeneficiario() {
        beneficiosElementMap.descricaoBeneficio.clear();
        beneficiosElementMap.codigoPlano.clear();
        beneficiosElementMap.codigoRegistro.clear();
        beneficiosElementMap.codigoContrato.clear();
    }

    public void tentarCadastrarBeneficioSemOperadora() {
        preencherDescricao("Beneficios");
        preencherCodigoPlano("123");
        preencherCodigoRegistro("456");
        preencherCodigoContrato("789");

        clicarSalvar();
    }

    public void tentarCadastrarBeneficioSemTipoNatureza() {
        selecionarOperadora("A981808080808080808080808080AF85");
        preencherDescricao("Beneficios");
        preencherCodigoPlano("123");
        preencherCodigoRegistro("456");
        preencherCodigoContrato("789");

        clicarSalvar();
    }

    public void tentarCadastrarBeneficioSemDescricao() {
        preencherDadosBeneficios("A981808080808080808080808080AF85", "SAÚDE", "", "123", "456", "789", "Dependente");
        clicarSalvar();
    }

    public void tentarCadastrarBeneficioSemCodigoPlano() {
        preencherDadosBeneficios("A981808080808080808080808080AF85", "SAÚDE", "Beneficios", "", "456", "789", "Dependente");
        clicarSalvar();
    }

    public void tentarCadastrarBeneficioSemCodigoRegistro() {
        preencherDadosBeneficios("A981808080808080808080808080AF85", "SAÚDE", "Beneficios", "123", "", "789", "Dependente");
        clicarSalvar();
    }

    public void tentarCadastrarBeneficioSemCodigoContrato() {
        preencherDadosBeneficios("A981808080808080808080808080AF85", "SAÚDE", "Beneficios", "123", "456", "", "Dependente");
        clicarSalvar();
    }

    public void clicarEditarBeneficio(String codigo) {
        await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(), containsString(codigo));
        clicarOpcoes(codigo, "4");
        clicarEditar();
    }

    public void clicarExcluirBeneficio(String codigo) {
        clicarOpcoes(codigo, "4");
        clicarExcluir();
    }

    public void clicarBloquearDesbloquearBeneficio(String codigo) {
        clicarOpcoes(codigo, "4");
        js.executeScript("arguments[0].click()", beneficiosElementMap.bloquearDesbloquear);
    }
}
