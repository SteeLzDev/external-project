package com.zetra.econsig.tdd.tests.pages;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.bdd.steps.maps.EconsigElementMap;
import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.tdd.tests.maps.ConsignacaoElementMap;

public class ConsignacaoPage extends BasePage {

    public static final String nomeArquivo = "arquivo_para_teste.pdf";
    public static final String nomeArquivoMaior200k = "arquivo_maior_200k.png";
    public static final String arquivo = "src/test/resources/files/arquivo_para_teste.pdf";
    public static final String arquivoMaior200k = "src/test/resources/files/arquivo_maior_200k.png";

    private final ConsignacaoElementMap consignacaoElementMap;
    private final EconsigElementMap econsigElementMap;

    public ConsignacaoPage(WebDriver webDriver) {
        super(webDriver);
        consignacaoElementMap = PageFactory.initElements(webDriver, ConsignacaoElementMap.class);
        econsigElementMap = PageFactory.initElements(webDriver, EconsigElementMap.class);
    }

	public void clicarOpcoes(String numeroAde) {
		await.until(() -> webDriver.getPageSource(), containsString(numeroAde));

		clicarOpcoes(numeroAde, "3");
	}

	public void clicarOpcoesAlterar(String numeroAde) {
		await.until(() -> webDriver.getPageSource(), containsString(numeroAde));

		clicarOpcoesAlterar(numeroAde, "3");
	}

	public void clicarAlongarContrato() {
		consignacaoElementMap.alongarContrato.click();
	}

	public void clicarAlterarContrato() {
		consignacaoElementMap.alterarContrato.click();
	}

	public void clicarEditarContrato() {
		consignacaoElementMap.editarContrato.click();
	}

	public void clicarAcoes() {
		await.until(() -> webDriver.getPageSource(), containsString("Visualizar Consignação"));

		while (consignacaoElementMap.botaoAcoes.getDomAttribute("aria-expanded").contains("false")) {
			consignacaoElementMap.botaoAcoes.click();
		}
	}

	public void clicarCancelarConsignacao() {
		consignacaoElementMap.cancelarConsignacao.click();
	}

	public void clicarLiquidarConsignacao() {
		consignacaoElementMap.liquidarConsignacao.click();
	}

	public void clicarDesliquidarConsignacao() {
		consignacaoElementMap.desliquidarConsignacao.click();
	}

	public void clicarLiquidarParcela() {
		consignacaoElementMap.liquidarParcela.click();
	}

	public void clicarLiquidar() {
		await.until(() -> webDriver.getPageSource(), containsString("Selecione a(s) parcela(s) a ser(em) liquidada(s):"));
		consignacaoElementMap.liquidar.click();
	}

	public void preencherValorPrestacao(String valorPrestacao) {
		await.until(() -> webDriver.getPageSource(), containsString("nova autorização"));

		while (!consignacaoElementMap.valorPrestacao.getDomProperty("value").contains(valorPrestacao)) {
			consignacaoElementMap.valorPrestacao.sendKeys(valorPrestacao);
		}
	}

	public void preencherNumeroPrestacao(String numeroPrestacao) {
		while (!consignacaoElementMap.numeroPrestacao.getDomProperty("value").contains(numeroPrestacao)) {
			consignacaoElementMap.numeroPrestacao.sendKeys(numeroPrestacao);
		}
	}

	public void preencherValorLiquido(String valorLiquido) {
		while (!consignacaoElementMap.valorLiquido.getDomProperty("value").contains(valorLiquido)) {
			consignacaoElementMap.valorLiquido.sendKeys(valorLiquido);
		}
	}

	public void preencherNroPrestacaoAlterarContrato(String numeroPrestacao) {
		limparNroPrestacaoAlterarContrato();
		while (consignacaoElementMap.nroPrestacaoAlterarContrato.getDomProperty("value").isEmpty()) {
			consignacaoElementMap.nroPrestacaoAlterarContrato.sendKeys(numeroPrestacao);
		}
	}

	public void limparNroPrestacaoAlterarContrato() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(consignacaoElementMap.botaoSalvar));

		consignacaoElementMap.nroPrestacaoAlterarContrato.clear();
	}

	public void clicarConfirmarModal() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(consignacaoElementMap.confirmarModal));
		 js.executeScript("arguments[0].click()", consignacaoElementMap.confirmarModal);
	}

	public void clicarConfirmar() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(consignacaoElementMap.botaoConfirmar));
		 js.executeScript("arguments[0].click()", consignacaoElementMap.botaoConfirmar);
	}

	public void clicarConfirmarAnexo() {
		await.pollDelay(Duration.ofSeconds(1))
				.until(() -> webDriver.getPageSource().contains("Editar Anexo de Consignação"));

		consignacaoElementMap.botaoConfirmar.click();
	}

	public  boolean novoValorPrestacaoDisable() {
		  try {
				consignacaoElementMap.valorPrestacao.click();
		        return false;
		    } catch (ElementNotInteractableException e) {
		        return true;
		    }
	}

	public void clicarConfirmarReimplantacao() {
		js.executeScript("arguments[0].click()", consignacaoElementMap.confirmarReimplantacao);
	}

	public void clicarConfirmarSemParcela() {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Selecione a(s) parcela(s) a ser(em) liquidada(s):"));
		js.executeScript("arguments[0].click()", consignacaoElementMap.botaoConfirmar);
	}

	public void alongarContrato(String valorPrestacao, String numeroPrestacao) {
		preencherValorPrestacao(valorPrestacao);
		preencherNumeroPrestacao(numeroPrestacao);
		clicarConfirmar();
	}

	public void renegociarContrato(String valorPrestacao, String numeroPrestacao, String valorLiquido) {
		preencherValorPrestacao(valorPrestacao);
		preencherNumeroPrestacao(numeroPrestacao);
		preencherValorLiquido(valorLiquido);
		clicarConfirmar();
	}

	public void limparCamposRenegociarContrato() {
		await.until(() -> webDriver.getPageSource(), containsString("nova autorização"));

		consignacaoElementMap.valorPrestacao.clear();
		consignacaoElementMap.numeroPrestacao.clear();
		consignacaoElementMap.valorLiquido.clear();

	}

	public String getValorPretacao() {
		await.until(() -> consignacaoElementMap.txtValorPrestacao.getText(), notNullValue());

		return consignacaoElementMap.txtValorPrestacao.getText();
	}

	public String getNumeroPrestacao() {
		return consignacaoElementMap.txtNumeroPrestacao.getText();
	}

	public String getValorLiquido() {
		return consignacaoElementMap.txtValorLiquido.getText();
	}

	public void alterarContrato(String numeroPrestacao) {
		preencherNroPrestacaoAlterarContrato(numeroPrestacao);
		clicarSalvar();
	}

	public void clicarSalvar() {
	    js.executeScript("arguments[0].click()", consignacaoElementMap.botaoSalvar);
	}

	public void clicarDesfazerCancelamentoConsignacao() {
		await.until(() -> webDriver.getPageSource(), containsString("Visualizar Consignação"));

		consignacaoElementMap.desfazerCancelamentoConsignacao.click();
	}

	public void clicarEditarAnexoConsignacao() {
		consignacaoElementMap.editarAnexoConsignacao.click();
	}

	public void clicarRegistrarOcorrencia() {
		consignacaoElementMap.registrarOcorrencia.click();

		await.until(() -> webDriver.getPageSource(), containsString("Informações da operação"));
	}

	public void clicarSuspenderConsignacao() {
		consignacaoElementMap.suspenderConsignacao.click();
	}

	public void clicarReativarConsignacao() {
		consignacaoElementMap.reativarConsignacao.click();
	}

	public void clicarSolicitarSaldoDevedorInformativo() {
		consignacaoElementMap.solicitarSaldoDevedorInformativo.click();
	}

	public void clicarSolicitarSaldoDevedorParaLiquidacao() {
		consignacaoElementMap.solicitarSaldoDevedorParaLiquidacao.click();
	}

	public void clicarDeferirConsignacao() {
		consignacaoElementMap.deferirConsignacao.click();
	}

	public void clicarRenegociarConsignacao() {
		consignacaoElementMap.renegociarConsignacao.click();
	}

	public void clicarConfirmarSolicitarSaldo() {
		consignacaoElementMap.botacoConfirmarSolicitarSaldo.click();
	}

	public void clicarConfirmarSolicitarSaldoLiquidacao() {
		consignacaoElementMap.botacoConfirmarSolicitarSaldoLiq.click();
	}

	public void clicarVisualizarAutorizacaoDesconto() {
		waitDriver.until(ExpectedConditions.visibilityOf(consignacaoElementMap.visualizarAutorizacaoDesconto));

		consignacaoElementMap.visualizarAutorizacaoDesconto.click();

		await.until(() -> webDriver.getPageSource(), containsString("Autorização de Desconto"));

	}

	public void clicarReimplantarConsignacao() {
		consignacaoElementMap.reimplantarConsignacao.click();
	}

	public void anexarArquivo() throws IOException, AWTException {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Anexar arquivo"));

		consignacaoElementMap.selecionarArquivo.click();
		webDriver.switchTo().activeElement().sendKeys(new File(arquivo).getCanonicalPath());

		fecharJanelaWindows();
	}

	public void anexarArquivoMaiorPermitido() throws IOException {
		await.pollDelay(1, TimeUnit.SECONDS).until(() -> webDriver.getPageSource(),
				containsString("Anexar arquivo"));

		consignacaoElementMap.selecionarArquivo.click();
		webDriver.switchTo().activeElement().sendKeys(new File(arquivoMaior200k).getCanonicalPath());

		fecharJanelaWindows();
	}

	private static void fecharJanelaWindows() {
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_ESCAPE);
			robot.keyRelease(KeyEvent.VK_ESCAPE);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void preencherDescricao(String descricao) {
		await.until(() -> webDriver.getPageSource(),
				containsString("Anexar arquivo"));
		consignacaoElementMap.descricaoDoArquivo.sendKeys(descricao);

		actions.sendKeys(Keys.TAB);
	}

	public void selecionarTipoOcorrencia(String tipoOcorrencia) {
		consignacaoElementMap.tipoOcorrencia.sendKeys(tipoOcorrencia);
	}

	public void preencherObservacao(String observacao) {
		while (consignacaoElementMap.observacao.getDomProperty("value").isEmpty()) {
			consignacaoElementMap.observacao.sendKeys(observacao);
		}
	}

	public void clicarConfirmarSemObservacao() {
		while (!SeleniumHelper.isAlertPresent(webDriver)) {
			consignacaoElementMap.botaoConfirmar.click();
		}
	}

	public void preencherDataReativacao(String data) {
		while (!consignacaoElementMap.dataReativacao.getDomProperty("value").matches(data)) {
			consignacaoElementMap.dataReativacao.clear();
			consignacaoElementMap.dataReativacao.sendKeys(data);
		}
	}

	public void editarAnexo(String descricao) {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(econsigElementMap.txtMensagemSucesso,
				"Anexo foi incluído com sucesso."));

		clicarOpcoes();
		js.executeScript("arguments[0].click()", consignacaoElementMap.editarAnexo);
		consignacaoElementMap.descricaoDoArquivoAlterar.clear();
		consignacaoElementMap.descricaoDoArquivoAlterar.sendKeys(descricao);
		clicarConfirmarModal();

	}

	public void clicarOpcoes() {
		while (consignacaoElementMap.opcoes.getDomAttribute("aria-expanded").contains("false")) {
			js.executeScript("arguments[0].click()", consignacaoElementMap.opcoes);
		}
	}

	public void bloquearAnexo() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(econsigElementMap.txtMensagemSucesso,
				"Anexo foi incluído com sucesso."));

		clicarOpcoes();
		js.executeScript("arguments[0].click()", consignacaoElementMap.bloquearAnexo);
	}

	public void desbloquearAnexo() {
		await.until(() -> webDriver.getPageSource(), containsString("Bloqueado"));
		clicarOpcoes();
		js.executeScript("arguments[0].click()", consignacaoElementMap.desbloquearAnexo);
	}

	public void removerAnexo() {
		waitDriver.until(ExpectedConditions.attributeContains(consignacaoElementMap.file1, "value", nomeArquivo));

		consignacaoElementMap.removerAnexo.click();

		waitDriver.until(ExpectedConditions.alertIsPresent());
	}

	public void clicarAdeAntiga() {
		consignacaoElementMap.adeAntigo.click();
	}

	public void preencherObservacaoReimplantar(String observacao) {
		await.until(() -> webDriver.getPageSource().contains("Informações da operação"));

		while (consignacaoElementMap.observacaoReimplantar.getDomProperty("value").isEmpty()) {
			consignacaoElementMap.observacaoReimplantar.sendKeys(observacao);
		}
	}

	public void conferirMensagemSucesso() {
        waitDriver.until(ExpectedConditions.textToBePresentInElement(econsigElementMap.txtMensagemSucesso,
                "Operação concluída com sucesso."));
	}
}
