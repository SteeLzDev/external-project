package com.zetra.econsig.tdd.tests.pages;

import static org.hamcrest.CoreMatchers.containsString;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.tdd.tests.maps.ServidoresElementMap;

public class ServidoresPage extends BasePage {

    private final ServidoresElementMap servidoresElementMap;

	public ServidoresPage(WebDriver webDriver) {
	    super(webDriver);
        servidoresElementMap = PageFactory.initElements(webDriver, ServidoresElementMap.class);
	}

	public void preencherMatricula(String matricula) {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(servidoresElementMap.txtInformacao,
				"matrícula ou CPF do servidor para a pesquisa"));

		while (!servidoresElementMap.matricula.getDomProperty("value").matches(matricula)) {
			servidoresElementMap.matricula.clear();
			servidoresElementMap.matricula.sendKeys(matricula);
		}
	}

	public void preencherCpf(String cpf) {
		while (servidoresElementMap.cpf.getDomProperty("value").isEmpty()) {
			servidoresElementMap.cpf.sendKeys(cpf);
		}
	}

	public void clicarPesquisar() {
		servidoresElementMap.botaoPesquisar.click();
	}

	public void clicarPesquisarMargem() {
		servidoresElementMap.botaoPesquisarMargem.click();
	}

	public void clicarPerquisarSemInformarCampos() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(servidoresElementMap.txtInformacao,
				"matrícula ou CPF do servidor para a pesquisa"));

		while (!SeleniumHelper.isAlertPresent(webDriver)) {
			servidoresElementMap.botaoPesquisar.click();
		}
	}

	public void consultarServidor(String matricula, String cpf) {
		preencherMatricula(matricula);
		preencherCpf(cpf);
		clicarPesquisar();

		waitDriver.until(ExpectedConditions.textToBePresentInElement(servidoresElementMap.textoInformacao,
				"Informações Gerais"));
	}

	public void criarServidor(String matricula, String cpf) {

		selecionarTitulo();
		servidoresElementMap.cadastrarServidorNome.sendKeys("Antonio");
		servidoresElementMap.cadastrarServidorNomeDoMeio.sendKeys("Carlos");
		servidoresElementMap.cadastrarServidorUltimoNome.sendKeys("Santo");
		servidoresElementMap.cadastrarServidorNomeCompleto.sendKeys("Antonio Carlos Santo");
		servidoresElementMap.cadastrarServidorNomePai.sendKeys("Marco Carlos Santo");
		servidoresElementMap.cadastrarServidorNomeMae.sendKeys("Maria Silva Santo");
		servidoresElementMap.cadastrarServidorDataNascimento.sendKeys("12/05/2000");
		servidoresElementMap.cadastrarServidorNacionalidade.sendKeys("Brasileira");
		selecionarEstadoCivil();
		js.executeScript("arguments[0].click()", servidoresElementMap.cadastrarServidorSexo);
		servidoresElementMap.cadastrarCpfServidor.sendKeys(cpf);
		cadastrarEndereco();
		servidoresElementMap.cadastrarServidorMatricula.sendKeys(matricula);
		selecionarOrgao();
	}

	public void selecionarTitulo() {
		Select select = new Select(servidoresElementMap.cadastrarServidorTitulo);
		select.selectByVisibleText("Sr.");
	}

	public void selecionarEstadoCivil() {
		Select select = new Select(servidoresElementMap.cadastrarServidorEstadoCivil);
		select.selectByVisibleText("Solteiro(a)");
	}

	public void selecionarOrgao() {
		Select select = new Select(servidoresElementMap.cadastrarServidorOrgao);
		select.selectByVisibleText("Carlota Joaquina 21.346.414/0001-47 - 213464140");
	}

	public void cadastrarEndereco() {
		servidoresElementMap.cadastrarServidorLogradouro.sendKeys("Avenida Brasil");
		servidoresElementMap.cadastrarServidorNumero.sendKeys("125");
		servidoresElementMap.cadastrarServidorBairro.sendKeys("Centro");
		servidoresElementMap.cadastrarServidorCidade.sendKeys("Belo Horizonte");
		servidoresElementMap.cadastrarServidorUF.sendKeys("MG");
		servidoresElementMap.cadastrarServidorCep.sendKeys("31710400");
	}

	public String getMatricula() {
		return servidoresElementMap.servidorMatricula.getDomProperty("value");
	}

	public String getCPF() {
		return servidoresElementMap.servidorCPF.getDomProperty("value");
	}

	public String getNomeCompleto() {
		return servidoresElementMap.servidorNome.getDomProperty("value");
	}

	public void editarServidor() {
		servidoresElementMap.servidorDataNascimento.clear();
		servidoresElementMap.servidorDataNascimento.sendKeys("01/02/1987");
		selecionarMotivoOperacao("Outros");
		preencherObservacao("Testes automatizados");
	}

	public void clicarConcluir() {
		servidoresElementMap.botaoConcluir.click();
	}

	public void preencherServico(String quantidade) {
		servidoresElementMap.servicoEmprestimo.sendKeys(quantidade);
	}

	public void preencherServicoPorNatureza(String quantidade) {
		servidoresElementMap.servicoPorNaturezaNatureza.sendKeys(quantidade);
	}

	public void marcarTransferenciaParcial() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(servidoresElementMap.transParcial));

		while (!servidoresElementMap.transParcial.isSelected()) {
			servidoresElementMap.transParcial.click();
		}
	}

	public void marcarTermoTransferencia() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(servidoresElementMap.marcarTermoTransferencia));

		while (!servidoresElementMap.marcarTermoTransferencia.isSelected()) {
			servidoresElementMap.marcarTermoTransferencia.click();
		}
	}

	public void preencherValorTransferencia(String valor) {
		servidoresElementMap.valorTrans.clear();
		servidoresElementMap.valorTrans.sendKeys(valor);
	}

	public void preencherConvenioEmprestimo(String quantidade) {
		await.until(() -> webDriver.getPageSource(), containsString("Lista de convênios"));
		servidoresElementMap.convenioEmprestimo.sendKeys(quantidade);
	}

	public void clicarMaisAcoes() {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(servidoresElementMap.botaoAcoes, "Mais ações"));

		while (servidoresElementMap.botaoAcoes.getDomAttribute("aria-expanded").contains("false")) {
			servidoresElementMap.botaoAcoes.click();
		}
	}

	public void clicarAcaoExibirOcorrencia() {
		servidoresElementMap.acaoExibirOcorrencia.click();
	}

	public void clicarAcaoBloquearServicos() {
		servidoresElementMap.acaoBloquearServicos.click();
	}

	public void clicarAcaoConsultarServicos() {
		servidoresElementMap.acaoConsultarServicos.click();
	}

	public void clicarAcaoBloquearServicosPorNatureza() {
		servidoresElementMap.acaoBloquearServicosPorNatureza.click();
	}

	public void clicarAcaoConsultarServicosPorNatureza() {
		servidoresElementMap.acaoConsultarServicosPorNatureza.click();
	}

	public void clicarAcaoBloquearVerbas() {
		servidoresElementMap.acaoBloquearVerbas.click();
	}

	public void clicarAcaoConsultarVerbas() {
		servidoresElementMap.acaoConsultarVerbas.click();
	}

	public void clicarAcaoTransferirValoresEntreMargens() {
		servidoresElementMap.acaoTransferirValoresEntreMargens.click();
	}

	public void clicarAcaoEditarEnderecosDesteServidor() {
		servidoresElementMap.acaoEditarEnderecos.click();

		await.until(() -> webDriver.getPageSource(),
				containsString("Manutenção de Endereços do Servidor"));
	}

	public void clicarAcaoCadastrarDispensaValidacaoDigitalDesteServidor() {
		servidoresElementMap.acaoCadastrarDispensaValidacaoDigital.click();
	}

	public void clicarAcaoConsultarContracheques() {
		servidoresElementMap.acaoConsultarContracheques.click();

		await.until(() -> webDriver.getPageSource(),
				containsString("O contracheque deste período não está disponível."));
	}

	public void clicarAcaoSolicitarSaldoDevedor() {
		servidoresElementMap.acaoSolicitarSaldoDevedor.click();
	}

	public boolean isCampoCPFHabilitado() {
		return servidoresElementMap.servidorCPF.isEnabled();
	}

    public void selecionarMotivoOperacao(String motivo) {
        await.until(() -> webDriver.getPageSource(), containsString("Motivo da operação"));

        while (servidoresElementMap.motivoOperacao.getDomProperty("value").isEmpty()) {
            js.executeScript("arguments[0].click()", servidoresElementMap.motivoOperacao);
            servidoresElementMap.motivoOperacao.sendKeys(motivo);
        }
    }

    public void preencherObservacao(String observacao) {
        servidoresElementMap.observacao.sendKeys(observacao);
    }

}