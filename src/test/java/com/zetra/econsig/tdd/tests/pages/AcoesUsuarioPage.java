package com.zetra.econsig.tdd.tests.pages;

import static org.hamcrest.CoreMatchers.containsString;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.tdd.tests.maps.UsuarioElementMap;

public class AcoesUsuarioPage extends BasePage {

    private final UsuarioElementMap usuarioElementMap;

    public AcoesUsuarioPage(WebDriver webDriver) {
        super(webDriver);
        usuarioElementMap = PageFactory.initElements(webDriver, UsuarioElementMap.class);
    }

    public void clicarOpcoesConsignatarias(String codigo) {
        await.until(() -> webDriver.getPageSource(), containsString("Lista de consignatárias"));

        clicarOpcoes(codigo, "0");
    }

    public void clicarOpcoesCorrespondente(String codigo) {
        await.until(() -> webDriver.getPageSource(), containsString("Lista de correspondentes"));

        clicarOpcoes(codigo, "0");
    }

    public void clicarOpcoesUsuarios(String codigo) {
        await.until(() -> webDriver.getPageSource(), containsString("Lista de usuários"));

        clicarOpcoes(codigo, "0");
    }

    public void clicarOpcoesRegraTaxaJuros() {
        await.until(() -> webDriver.getPageSource(), containsString("Lista de regra de taxa de juros"));
        js.executeScript("arguments[0].click()", usuarioElementMap.opcoes);
    }

    public int getLinhasTabela() {
        await.until(() -> webDriver.getPageSource().contains("Opções"));

        final WebElement main = webDriver.findElement(By.cssSelector(".table"));
        final List<WebElement> rows = main.findElements(By.xpath(".//tbody/tr"));

        return rows.size();
    }

    public void clicarOpcoesOrgao(String codigo) {
        await.until(() -> webDriver.getPageSource(), containsString("Lista de órgãos"));

        clicarOpcoes(codigo, "0");
    }

    public void clicarBloquearDesbloquear() {
        usuarioElementMap.opcaoBloquearDesbloquear.click();
    }

    public void clicarBloquearDesbloquearServidor() {
        usuarioElementMap.opcaoBloquearDesbloquearServidor.click();
    }

    public void clicarEditar() {
        js.executeScript("arguments[0].click()", usuarioElementMap.opcaoEditar);
    }

    public void clicarExibirHistorico() {
        usuarioElementMap.opcaoExibirHistorico.click();
    }

    public void clicarExcluir() {
        waitDriver.until(ExpectedConditions.elementToBeClickable(usuarioElementMap.opcaoExcluir));

        usuarioElementMap.opcaoExcluir.click();
    }

    public void clicarReiniciarSenha() {
        usuarioElementMap.opcaoReinicializarSenha.click();
    }

    public void clicarAlterarSenha() {
        usuarioElementMap.opcaoAlterarSenha.click();
    }

    public void clicarUsuarios() {
        usuarioElementMap.usuarios.click();
    }

    public void clicarListarUsuariosOrg() {
        usuarioElementMap.listaUsuariosOrg.click();
    }

    public void clicarListarUsuariosCor() {
        usuarioElementMap.correspondente.click();
    }

    public void clicarAcoes() {
        while (usuarioElementMap.botaoAcoes.getDomAttribute("aria-expanded").contains("false")) {
            usuarioElementMap.botaoAcoes.click();
        }
    }

    public void clicarSalvar() {
        js.executeScript("arguments[0].click()", usuarioElementMap.botaoSalvar);
    }

    public void clicarConfirmar() {
        js.executeScript("arguments[0].click()", usuarioElementMap.botaoConfirmar);
    }

    public void clicarEditarFuncoes() {
        usuarioElementMap.editarFuncoes.click();
    }

    public void clicarMaisAcoes() {
        while (!"true".equals(usuarioElementMap.maisAcoes.getDomAttribute("aria-expanded"))) {
            usuarioElementMap.maisAcoes.click();
        }
    }

    public void clicarEditarRestricoesAcesso() {
        usuarioElementMap.editarRestricoesAcesso.click();
    }

    public void clicarServicos() {
        usuarioElementMap.servicos.click();

        await.pollDelay(Duration.ofSeconds(1)).until(() -> webDriver.getPageSource(),
                                                     containsString("Lista de serviços"));
    }

    public void clicarListarPerfilUsuario() {
        usuarioElementMap.listarPerfilUsuario.click();
    }

    public void clicarConsultarConvenios() {
        usuarioElementMap.consultarConvenios.click();

        await.until(() -> webDriver.getPageSource(), containsString("Lista de serviços"));
    }
}
