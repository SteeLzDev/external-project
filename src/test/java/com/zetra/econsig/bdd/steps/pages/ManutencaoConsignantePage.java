package com.zetra.econsig.bdd.steps.pages;

import static org.hamcrest.CoreMatchers.is;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.zetra.econsig.bdd.steps.maps.ManutencaoConsignanteElementMap;
import com.zetra.econsig.bdd.steps.maps.PerfilElementMap;
import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.tdd.tests.pages.BasePage;

public class ManutencaoConsignantePage extends BasePage {

    private final ManutencaoConsignanteElementMap manutencaoConsignanteElementMap;
    private final PerfilElementMap perfilElementMap;

    public ManutencaoConsignantePage(WebDriver webDriver) {
        super(webDriver);
        manutencaoConsignanteElementMap = PageFactory.initElements(webDriver, ManutencaoConsignanteElementMap.class);
        perfilElementMap = PageFactory.initElements(webDriver, PerfilElementMap.class);
    }

    public void clicarBotaoNovo() {
		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getCurrentUrl().contains("listarPerfil"));

		perfilElementMap.botaoNovo.click();
	}

	public void preencherDescricaoPerfil(String descricaoPerfil) {
		await.pollDelay(2, TimeUnit.SECONDS)
			.until(() -> perfilElementMap.campoDescricao.getAttribute("placeholder"), is("Digite aqui a descrição"));

		while (!perfilElementMap.campoDescricao.getDomProperty("value").matches(descricaoPerfil)) {
			perfilElementMap.campoDescricao.clear();
			perfilElementMap.campoDescricao.sendKeys(descricaoPerfil);
		}
	}

	public void clicarBotaoDesmarcarTudo() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(perfilElementMap.botaoDesmarcarTudo));

		js.executeScript("arguments[0].click()", perfilElementMap.botaoDesmarcarTudo);
	}

	public void clicarBotaoMarcarTudo() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(perfilElementMap.botaoMarcarTudo));
		js.executeScript("arguments[0].click()", perfilElementMap.botaoMarcarTudo);
	}

	public void clicarBotaoSalvar() {
		waitDriver.until(ExpectedConditions.elementToBeClickable(perfilElementMap.botaoSalvar));
		js.executeScript("arguments[0].click()", perfilElementMap.botaoSalvar);
	}

	public void clicarSalvarConfiguracoes() {
		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.salvarConfiguracoes);
	}

	public void gravarNovoPerfilComFuncoesAdministrativas(String descricaoPerfil) {
		preencherDescricaoPerfil(descricaoPerfil);
		clicarBotaoMarcarTudo();
		clicarBotaoSalvar();

		if (SeleniumHelper.isAlertPresent(webDriver)) {
			popUpAceita("Foram atribuídas funções do perfil Administrador para o perfil.");
		}
	}

	public void gravarNovoPerfilSemFuncoes(String descricaoPerfil) {
		preencherDescricaoPerfil(descricaoPerfil);
		clicarBotaoDesmarcarTudo();
		clicarBotaoSalvar();

		waitDriver.until(ExpectedConditions.textToBePresentInElement(perfilElementMap.txtMensagem,
				"Perfil criado com sucesso."));
	}

	public void clicarOpcoes(String nomePerfil) throws Throwable {
		final WebElement botaoOpcoes = waitDriver.until(ExpectedConditions
				.visibilityOf(webDriver.findElement(By.id(nomePerfil.toUpperCase()))));

		while (!botaoOpcoes.getDomAttribute("aria-expanded").equals("true")) {
			botaoOpcoes.click();
		}
	}

	public void perfilBloquear(String nomePerfil) throws Throwable {
		clicarOpcoes(nomePerfil);

		perfilElementMap.botaoBloquear.click();

		popUpAceita("Todos os usuários que pertencem a este perfil serão bloqueados.");
	}

	public void perfilDesbloquear(String nomePerfil) throws Throwable {
		clicarOpcoes(nomePerfil);

		perfilElementMap.botaoDesbloquear.click();
		// aceita popup e verifica se mensagem está correta
		popUpAceita("Todos os usuários que pertencem a este perfil serão desbloqueados.");
	}

	public void perfilEditar(String nomePerfil) throws Throwable {
		clicarOpcoes(nomePerfil);
		js.executeScript("arguments[0].click()", perfilElementMap.botaoEditar);

		waitDriver.until(
				ExpectedConditions.textToBePresentInElement(perfilElementMap.tituloTelaEditar, "Edição de dados"));

		perfilElementMap.campoDescricao.clear();
		preencherDescricaoPerfil("Descricao Editado");

		clicarBotaoDesmarcarTudo();

		// clica no salvar
		js.executeScript("arguments[0].click()", perfilElementMap.botaoSalvar);
	}

	public void perfilExcluir(String nomePerfil) throws Throwable {
		clicarOpcoes(nomePerfil);

		perfilElementMap.botaoExcluir.click();
		// aceita popup e verifica se mensagem está correta
		popUpAceita("Confirma a exclusão do perfil \"" + nomePerfil + "\"");
	}

	/**
	 * Clica no icone de listar perfis
	 */
	public void clicarListarPerfilUsuario() {
		final WebElement botaoOpcoes = waitDriver.pollingEvery(Duration.ofSeconds(1))
				.until(ExpectedConditions.visibilityOf(perfilElementMap.botaoAcoes));

		while (!botaoOpcoes.getDomAttribute("aria-expanded").equals("true")) {
			botaoOpcoes.click();
		}

		perfilElementMap.listarPerfil.click();
	}

	public void filtroPerfil(String descricao, String filtro) {
		await.until(() -> webDriver.getPageSource().contains("Pesquisar"));

		while (!perfilElementMap.campoFiltro.getDomProperty("value").matches(descricao)) {
			perfilElementMap.campoFiltro.clear();
			perfilElementMap.campoFiltro.sendKeys(descricao);
		}

		final Select select = new Select(perfilElementMap.comboFiltro);
		select.selectByVisibleText(filtro);

		perfilElementMap.btnPesquisar.click();
		await.pollDelay(3, TimeUnit.SECONDS).ignoreExceptions().until(() -> true);
		waitDriver.until(ExpectedConditions.textToBePresentInElement(perfilElementMap.tituloPagina, "Lista de"));
	}

	public void selecionarNatureza(String natureza, String filtro) {
		waitDriver.until(ExpectedConditions.elementToBeClickable(perfilElementMap.btnPesquisar));

		Select select = new Select(perfilElementMap.comboFiltro);
		select.selectByVisibleText(filtro);

		select = new Select(perfilElementMap.natureza);
		select.selectByVisibleText(natureza);
		perfilElementMap.btnPesquisar.click();

		waitDriver.until(ExpectedConditions.textToBePresentInElement(perfilElementMap.tituloPagina, "Lista de"));
	}

	public void clicarEditarPerfil(String nomePerfil) throws Throwable {
		clicarOpcoes(nomePerfil);

		perfilElementMap.botaoEditar.click();

		waitDriver.until(
				ExpectedConditions.textToBePresentInElement(perfilElementMap.tituloTelaEditar, "Edição de dados"));

		perfilElementMap.campoDescricao.clear();
		preencherDescricaoPerfil("Descricao Editado");
	}

	public void clicarCancelar() throws Throwable {
		js.executeScript("arguments[0].click()", perfilElementMap.botaoCancelar);
	}

	public boolean verificarTelaPerfil() throws Throwable {
		return perfilElementMap.botaoNovo.isDisplayed();
	}

	public void preencherResponsavel(String responsavel) {
		manutencaoConsignanteElementMap.cseResponsavel.clear();
		manutencaoConsignanteElementMap.cseResponsavel.sendKeys(responsavel);
	}

	public void preencherCargo(String cargo) {
		manutencaoConsignanteElementMap.cargoCse.clear();
		manutencaoConsignanteElementMap.cargoCse.sendKeys(cargo);
	}

	public void preencherCEP(String cep) {
		manutencaoConsignanteElementMap.cep.clear();
		manutencaoConsignanteElementMap.cep.sendKeys(cep);
	}

	public void clicarBloquear() {
		manutencaoConsignanteElementMap.bloquear.click();
	}

	public void clicarDesbloquear() {
		manutencaoConsignanteElementMap.desbloquear.click();
	}

	public void clicarAlterarConfiguracoesMargem() {
		manutencaoConsignanteElementMap.configuracoesMargem.click();
	}

	public void clicarAlterarParametros() {
		manutencaoConsignanteElementMap.alterarParametros.click();
	}

	public void clicarConfigurarAuditoria() {
		manutencaoConsignanteElementMap.configurarAuditoria.click();
	}

	public void preencherMotivo(String motivo) {
		waitDriver.until(ExpectedConditions.textToBePresentInElement(manutencaoConsignanteElementMap.modalTitulo,
				"Bloquear consignante"));

		manutencaoConsignanteElementMap.motivo.sendKeys(motivo);
	}

	public void clicarConfirmar() {
		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.confirmar);
	}

	public void selecionarExibeOrgaoMargem1(String exibe) {
		final Select select = new Select(manutencaoConsignanteElementMap.exibeOrgaoMargem1);
		select.selectByVisibleText(exibe);
	}

	public void selecionarExibeCorrespondenteMargem1(String exibe) {
		final Select select = new Select(manutencaoConsignanteElementMap.exibeCorMargem1);
		select.selectByVisibleText(exibe);
	}

	public void selecionarExibeSuporteMargem1(String exibe) {
		final Select select = new Select(manutencaoConsignanteElementMap.exibeSupMargem1);
		select.selectByVisibleText(exibe);
	}

	public void preencherPorcentagemMargem2(String porcentagem) {
		manutencaoConsignanteElementMap.porcentagemMargem2.sendKeys(porcentagem);
	}

	public void selecionarExibeServidorMargem2(String exibe) {
		final Select select = new Select(manutencaoConsignanteElementMap.exibeSerMargem2);
		select.selectByVisibleText(exibe);
	}

	public void selecionarExibeConsignatariaMargem3(String exibe) {
		final Select select = new Select(manutencaoConsignanteElementMap.exibeCsaMargem3);
		select.selectByVisibleText(exibe);
	}

	public void alterarNomeMargem3(String nome) {
		manutencaoConsignanteElementMap.nomeMargem3.clear();
		manutencaoConsignanteElementMap.nomeMargem3.sendKeys(nome);
	}

	public String getNomeCardMargem() {
		return manutencaoConsignanteElementMap.card2NomeMargem.getText();
	}

	public void selecionarFuncoesCse() {
		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.todosGeral);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.todosOperacional);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.funcaoAtualizarProcessoPortabilidade);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.funcaoConfirmarSolicitacao);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.funcaoEditarPostos);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.todosManutencaoServidor);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.todosManutencaoServicos);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.todosIntegracaoFolha);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.todosAdministracaoCse);

	}

	public void selecionarFuncoesSuporte() {
		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.todosGeral);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.funcaoAtualizarProcessoPortabilidade);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.funcaoRelatorioComunicacao);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.todosManutencaoOrgao);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.funcaoCriarGrupoConsignataria);

		js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.todosAdministracao);
	}

	public void alterarPrazoExpiracaoSenhaServidor(String prazo) {

		while (!manutencaoConsignanteElementMap.prazoExpiracaoSenhaServidor.getDomProperty("value").matches(prazo)) {
			manutencaoConsignanteElementMap.prazoExpiracaoSenhaServidor.clear();
			manutencaoConsignanteElementMap.prazoExpiracaoSenhaServidor.sendKeys(prazo);
		}
	}

	public void alterarQuantidadeMensagensExibidasAposLogin(String quantidade) {

		while (!manutencaoConsignanteElementMap.quantidadeMensagensExibidasAposLogin.getDomProperty("value")
				.matches(quantidade)) {
			manutencaoConsignanteElementMap.quantidadeMensagensExibidasAposLogin.clear();
			manutencaoConsignanteElementMap.quantidadeMensagensExibidasAposLogin.sendKeys(quantidade);
		}
	}

	public void marcarExigeCertificadoDigitalParaConsignante() {
        waitDriver.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(manutencaoConsignanteElementMap.exigeCertificadoDigitalParaConsignante));
		while (!manutencaoConsignanteElementMap.exigeCertificadoDigitalParaConsignante.isSelected()) {
			// manutencaoConsignanteElementMap.exigeCertificadoDigitalParaConsignante.click();
            js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.exigeCertificadoDigitalParaConsignante);
		}
	}

	public void marcarExigeCertificadoDigitalParaConsignataria() {
	    waitDriver.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(manutencaoConsignanteElementMap.exigeCertificadoDigitalParaConsignataria));
		while (!manutencaoConsignanteElementMap.exigeCertificadoDigitalParaConsignataria.isSelected()) {
			// manutencaoConsignanteElementMap.exigeCertificadoDigitalParaConsignataria.click();
			js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.exigeCertificadoDigitalParaConsignataria);
		}
	}

	public void marcarVerificaCadastroIPAcessoUsuarioConsignante() {
		waitDriver.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(manutencaoConsignanteElementMap.verificarCadastroEnderecoAcesso));
		while (!manutencaoConsignanteElementMap.verificarCadastroEnderecoAcesso.isSelected()) {
			js.executeScript("arguments[0].click()", manutencaoConsignanteElementMap.verificarCadastroEnderecoAcesso);
		}
	}
}
