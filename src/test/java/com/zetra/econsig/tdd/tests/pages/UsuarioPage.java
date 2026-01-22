package com.zetra.econsig.tdd.tests.pages;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.tdd.tests.maps.UsuarioElementMap;

public class UsuarioPage extends BasePage {

	private static final String usuNome = "Usuario Selenium";
	private static final String usuTelefone = "3136257896";
	private final SecureRandom random = new SecureRandom();

    private final UsuarioElementMap usuarioElementMap;

	public UsuarioPage(WebDriver webDriver) {
	    super(webDriver);
	    usuarioElementMap = PageFactory.initElements(webDriver, UsuarioElementMap.class);
	}

	public void clicarListarUsuarios() {
		while (!usuarioElementMap.maisAcoes.getDomAttribute("aria-expanded").equals("true")) {
			usuarioElementMap.maisAcoes.click();
		}

		usuarioElementMap.listarUsuarios.click();
	}

	public void clicarCriarNovoUsuario() {
		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("Lista de usuários"));

		usuarioElementMap.criarNovoUsuario.click();
	}

	public void preencherNomeUsuario(String nome) {
		waitDriver.until(ExpectedConditions.visibilityOf(usuarioElementMap.nomeUsuario));

		while (!usuarioElementMap.nomeUsuario.getDomProperty("value").contains(nome)) {
			usuarioElementMap.nomeUsuario.sendKeys(nome);
		}
	}

	public void preencherUsuarioLogin(String usuario) {
		while (usuarioElementMap.usuarioLogin.getDomProperty("value").isEmpty()) {
			usuarioElementMap.usuarioLogin.sendKeys(usuario);
		}
	}

	public void preencherEmail(String email) {
		usuarioElementMap.email.sendKeys(email);
	}

	public void preencherDicaSenha(String cpf) {
		usuarioElementMap.cpf.sendKeys(cpf);
	}

	public void preencherCPF(String cpf) {
		usuarioElementMap.cpf.sendKeys(cpf);
	}

	public void preencherTelefone(String telefone) {
		usuarioElementMap.telefone.sendKeys(telefone);
	}

	public void verificarCamposDesabilitados() {
		assertFalse(usuarioElementMap.nomeUsuario.isEnabled());
		assertFalse(usuarioElementMap.email.isEnabled());
		assertFalse(usuarioElementMap.usuarioLogin.isEnabled());
		assertFalse(usuarioElementMap.cpf.isEnabled());
		assertFalse(usuarioElementMap.dicaSenha.isEnabled());
		assertFalse(usuarioElementMap.telefone.isEnabled());
		assertFalse(usuarioElementMap.perfil.isEnabled());
	}

	public void incluirIPsAcessoAtual() {
		clicarIncluirIPsAcessoAtual();
		clicarIncluirIPsAcesso();

		if (SeleniumHelper.isAlertPresent(webDriver)) {
			webDriver.switchTo().alert().accept();
		}
	}

	public void incluirIPsAcessoAtualCSE() {
		await.until(() -> webDriver.getPageSource(), containsString("Manutenção de consignante"));

		Select select = new Select(usuarioElementMap.listaIps);

		while (select.getOptions().size() == 0) {
			clicarIncluirIPsAcessoAtual();
			clicarIncluirIPsAcessoCSE();

			if (SeleniumHelper.isAlertPresent(webDriver)) {
				webDriver.switchTo().alert().accept();
			}
		}
	}

	public void incluirIPsAcessoAtualCSACaso1() {
		clicarIncluirIPsAcessoAtual();
		clicarIncluirIPsAcessoCSACaso1();

		if (SeleniumHelper.isAlertPresent(webDriver)) {
			webDriver.switchTo().alert().accept();
		}
	}

	public void incluirIPsAcessoAtualCSACaso2() {
        clicarIncluirIPsAcessoAtual();
        clicarIncluirIPsAcessoCSACaso2();

        if (SeleniumHelper.isAlertPresent(webDriver)) {
            webDriver.switchTo().alert().accept();
        }
    }

	public void preencherIPsAcessoAtual(String ip) {
		usuarioElementMap.ipAcessoAtual.sendKeys(ip);
		clicarIncluirIPsAcesso();
	}

	public void clicarIncluirIPsAcessoAtual() {
		js.executeScript("arguments[0].click()", usuarioElementMap.btnIPsAcessoAtual);
	}

	public void clicarIncluirIPsAcesso() {
		usuarioElementMap.btnIncluirIPsAcesso.click();
	}

	public void clicarIncluirIPsAcessoCSACaso1() {
		js.executeScript("arguments[0].click()", usuarioElementMap.btnIncluirIPsAcessoCsaCaso1);
	}

	public void clicarIncluirIPsAcessoCSACaso2() {
        js.executeScript("arguments[0].click()", usuarioElementMap.btnIncluirIPsAcessoCsaCaso2);
    }

	public void clicarIncluirIPsAcessoCSE() {
		js.executeScript("arguments[0].click()", usuarioElementMap.btnIncluirIPsAcessoCse);
	}

	public void preencherEnderecoAcesso(String enderecoAcesso) {
		usuarioElementMap.enderecoAcesso.sendKeys(enderecoAcesso);
		usuarioElementMap.btnIncluirEnderecoAcesso.click();
	}

	public void selecionarPerfil(String perfil) {
		Select select = new Select(usuarioElementMap.perfil);
		select.selectByVisibleText(perfil);
	}

	public void clicarSalvar() {
	    js.executeScript("arguments[0].click()",usuarioElementMap.botaoSalvar);
	}

	public void clicarValidar() {
		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getPageSource().contains("SISTEMA DIGITAL DE CONSIGNA"));

		usuarioElementMap.validar.click();
	}

	public void clicarSalvarDesfazerCancelamento() {
		usuarioElementMap.salvarDesfazerCancelamento.click();
	}

	public void clicarCancelar() {
		waitDriver.until(ExpectedConditions.visibilityOf(usuarioElementMap.cancelar));

		while (SeleniumHelper.isElementPresent(webDriver, usuarioElementMap.cancelar)) {
			usuarioElementMap.cancelar.click();
		}
	}

	public String retornarSenha() {
		String pagina = usuarioElementMap.novaSenha.getText();

		return pagina.substring(pagina.indexOf("Senha do usuário: ") + 18);
	}

	public String getSenhaNova() {
		String novaSenha = usuarioElementMap.senhaReinicializada.getText();

		return novaSenha.substring(novaSenha.indexOf(" Nova senha: ") + 13);
	}

	public void selecionarMotivoOperacao(String motivo) {
		await.until(() -> webDriver.getPageSource(), containsString("Motivo da operação"));

		while (usuarioElementMap.motivoOperacao.getDomProperty("value").isEmpty()) {

		    js.executeScript("arguments[0].click()", usuarioElementMap.motivoOperacao);
			usuarioElementMap.motivoOperacao.sendKeys(motivo);
		}
	}

	public void preencherObservacao(String observacao) {
		usuarioElementMap.observacao.sendKeys(observacao);
	}

	public void preencherDadosUsuario(String usuarioLogin, String cpf) {
		preencherNomeUsuario(usuNome);
		preencherUsuarioLogin(usuarioLogin);
		preencherEmail("usuario" + random.nextInt() + "@econsig.com.br");
		preencherCPF(cpf);
		preencherTelefone(usuTelefone);
	}

	public void filtroUsuario(String filtro, String filtrarPor) {
		await.until(() -> webDriver.getPageSource(), containsString("Lista de usuários "));

		usuarioElementMap.filtroUsuario.clear();
		usuarioElementMap.filtroUsuario.sendKeys(filtro);
		usuarioElementMap.filtrarPor.sendKeys(filtrarPor);
	}

	public void clicarPesquisar() {
		usuarioElementMap.pesquisar.click();
	}

	public String getMensagemSucesso() {
		await.until(() -> usuarioElementMap.txtMensagemSucesso.getText(), notNullValue());

		return usuarioElementMap.txtMensagemSucesso.getText();
	}

	public String getMensagemaAlerta() {
		await.until(() -> usuarioElementMap.txtMensagemAlerta.getText(), notNullValue());

		return usuarioElementMap.txtMensagemAlerta.getText();
	}

	public void isUsuarioCentralizador(boolean usuarioCentralizador) {
		if (usuarioCentralizador) {
			usuarioElementMap.usuarioCentralizadorSim.click();
		} else {
			usuarioElementMap.usuarioCentralizadorNao.click();
		}
	}

	public void isExigeCertificadoDigital(boolean exigeCertificado) {
		if (exigeCertificado) {
		    js.executeScript("arguments[0].click()", usuarioElementMap.exigeCertificadoDigitalSim);
		} else {
		    js.executeScript("arguments[0].click()", usuarioElementMap.exigeCertificadoDigitalNao);
		}
	}

	public void preencherDataValido(String data) {
		while (usuarioElementMap.dataValido.getDomProperty("value").matches(data)) {
			usuarioElementMap.dataValido.clear();
			usuarioElementMap.dataValido.sendKeys(data);
		}
	}

	public void selecionarAlteracaoAvancadaConsignacao() {
		await.until(() -> webDriver.getPageSource(),
				containsString("Bloquear funções de usuário de"));

		Select select = new Select(usuarioElementMap.alteracaoAvancadaConsignacao);
		select.selectByVisibleText("PLANO DE SAUDE SUBSIDIO DEP 1 MEDICO");
		select.selectByVisibleText("EMPRESTIMO MARGEM 3");
		select.selectByVisibleText("PLANO DE SAÚDE DEPENDENTE 1");
	}

	public void selecionarConfirmarSolicitacao() {
		Select select = new Select(usuarioElementMap.confirmarSolicitacao);
		select.selectByVisibleText("PLANO DE SAUDE SUBSIDIO DEP 3 MEDICO");
		select.selectByVisibleText("ALUGUEL");
		select.selectByVisibleText("EMPRÉSTIMO");
	}

	public void selecionarEditarAnexosConsignacao() {
		Select select = new Select(usuarioElementMap.editarAnexosConsignacao);
		select.selectByVisibleText("CRÉDITO PRO RATA PLANO DE SAÚDE DEPENDENTE 1");
		select.selectByVisibleText("EMPRESTIMO ALONGADO");
		select.selectByVisibleText("FINANCIAMENTO DE DÍVIDA");
	}

	public void selecionarRenegociarContratoTerceiros() {
		Select select = new Select(usuarioElementMap.renegociarContratoTerceiros);
		select.selectByVisibleText("CARTAO DE CREDITO - LANCAMENTO");
		select.selectByVisibleText("COPARTICIPAÇÃO TITULAR");
		select.selectByVisibleText("EMPRÉSTIMO");
	}

	public void selecionarReservarMargem() {
		Select select = new Select(usuarioElementMap.reservarMargem);
		select.selectByVisibleText("DESPESA INDIVIDUAL");
		select.selectByVisibleText("EMPRESTIMO MARGEM 3");
		select.selectByVisibleText("RESERVA CARTAO");
	}

	public void preencherFiltro(String filtro) {
		await.until(() -> webDriver.getCurrentUrl(),
				containsString("manterRestricaoAcessoUsuario"));

		while (!usuarioElementMap.filtro.getDomProperty("value").matches(filtro)) {
			usuarioElementMap.filtro.clear();
			usuarioElementMap.filtro.sendKeys(filtro);
		}
	}

	public void selecionarTipoFiltro(String tipoFiltro) {
		Select select = new Select(usuarioElementMap.filtroTipo);
		select.selectByVisibleText(tipoFiltro);
	}

	public void clicarFiltrar() {
		usuarioElementMap.botaoFiltrar.click();
	}

	public String getFuncao() {
		await.until(() -> webDriver.getPageSource(), containsString("Registro(s) 1 a 1 de 1"));
		return usuarioElementMap.txtFuncao.getText();
	}

	public String getDescriao() {
		return usuarioElementMap.txtDescricao.getText();
	}

	public void incluirFuncoes() {
		selecionarPerfil("MASTER");
		selecionarMotivoOperacao("Outros");
		preencherObservacao("Observacao Automacao");
		clicarSalvar();

		while (SeleniumHelper.isAlertPresent(webDriver)) {
			webDriver.switchTo().alert().accept();
		}
	}

	public void editarRestricoes() {
		await.until(() -> webDriver.getPageSource(),
				containsString("Para incluir um endereço IP, digite-o no campo de"));

		while (!usuarioElementMap.enderecoAcesso.getDomProperty("value").matches("localhost")) {
			usuarioElementMap.enderecoAcesso.clear();
			usuarioElementMap.enderecoAcesso.sendKeys("localhost");
		}
		usuarioElementMap.btnIncluirEnderecoAcessoRestricaoFuncao.click();

		clicarIncluirIPsAcessoAtual();
		usuarioElementMap.btnIncluirIPsAcessoRestricaoFuncao.click();

		if (SeleniumHelper.isAlertPresent(webDriver)) {
			webDriver.switchTo().alert().accept();
		}
	}
}