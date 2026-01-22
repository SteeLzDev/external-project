package com.zetra.econsig.tdd.tests.pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.zetra.econsig.tdd.tests.maps.EstabelecimentoElementMap;

public class EstabelecimentoPage extends BasePage {

    private static final String codigoIdenficador = "03";
    private static final String estNome = "Estabelecimento Teste Selenium";
    private static final String estCNPJ = "61.068.276/0001-04";

    private final EstabelecimentoElementMap estabelecimentoElementMap;

    public EstabelecimentoPage(WebDriver webDriver) {
        super(webDriver);
        estabelecimentoElementMap = PageFactory.initElements(webDriver, EstabelecimentoElementMap.class);
    }

    public void criarEstabelecimento() {
		clicarCriarEstabelecimento();
		preencherCodigoIdentificador(codigoIdenficador);
		preencherNome(estNome);
		preencherCnpj(estCNPJ);
		preencherResponsavel1("Responsavel Estabelecimento 1");
		preencherCargoResponsavel1("Analista de produção");
		preencherTelefoneResponsavel1("3132658974");
		preencherLogradouro("Avenida Brasil");
		preencherNumero("9685");
		preencherComplemento("Bloco B");
		preencherBairro("Centro");
		preencherCidade("Belo Horizonte");
		preencherUF("MG");
		preencherCEP("31750-520");
		preencherTelefoneContato("3135698541");
		preencherEmail("estabelecimento@gmail.com");
		clicarSalvar();
	}

	public void criarEstabelecimento(String codigoIdent, String nome, String cnpj) {
		limparCamposObrigatorios();
		preencherCodigoIdentificador(codigoIdent);
		preencherNome(nome);
		preencherCnpj(cnpj);
		clicarSalvar();
	}

	private void limparCamposObrigatorios() {
		waitDriver.until(ExpectedConditions.visibilityOf(estabelecimentoElementMap.codigoIdentificador));

		estabelecimentoElementMap.codigoIdentificador.clear();
		estabelecimentoElementMap.nome.clear();
		estabelecimentoElementMap.cnpj.clear();
	}

	public void preencherEndereco() {

		preencherLogradouro("Avenida Brasil");
		preencherNumero("9685");
		preencherComplemento("Bloco B");
		preencherBairro("Centro");
		preencherCidade("Belo Horizonte");
		preencherUF("MG");
		preencherCEP("31750-520");
		clicarSalvar();
	}

	public void clicarCriarEstabelecimento() {
		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> webDriver.getCurrentUrl().contains("manterEstabelecimento"));

		waitDriver.until(ExpectedConditions.elementToBeClickable(estabelecimentoElementMap.botaoCriarEstabelecimento));

		estabelecimentoElementMap.botaoCriarEstabelecimento.click();
	}

	public void preencherCodigoIdentificador(String codigo) {
		waitDriver.until(ExpectedConditions.visibilityOf(estabelecimentoElementMap.codigoIdentificador));

		while (!estabelecimentoElementMap.codigoIdentificador.getDomProperty("value").matches(codigo)) {
			estabelecimentoElementMap.codigoIdentificador.clear();
			estabelecimentoElementMap.codigoIdentificador.sendKeys(codigo);
		}
	}

	public void preencherNome(String nome) {
		estabelecimentoElementMap.nome.sendKeys(nome);
	}

	public void preencherCnpj(String cnpf) {
		estabelecimentoElementMap.cnpj.sendKeys(cnpf);
	}

	public void preencherResponsavel1(String responsavel) {
		estabelecimentoElementMap.responsavel1.sendKeys(responsavel);
	}

	public void preencherCargoResponsavel1(String cargo) {
		estabelecimentoElementMap.cargoResponsavel1.sendKeys(cargo);
	}

	public void preencherTelefoneResponsavel1(String telefone) {
		estabelecimentoElementMap.telefoneResponsavel1.sendKeys(telefone);
	}

	public void preencherLogradouro(String logradouro) {
		estabelecimentoElementMap.logradouro.sendKeys(logradouro);
	}

	public void preencherNumero(String numero) {
		estabelecimentoElementMap.numero.sendKeys(numero);
	}

	public void preencherComplemento(String complemento) {
		estabelecimentoElementMap.complemento.sendKeys(complemento);
	}

	public void preencherBairro(String bairro) {
		estabelecimentoElementMap.bairro.sendKeys(bairro);
	}

	public void preencherCidade(String cidade) {
		estabelecimentoElementMap.cidade.sendKeys(cidade);
	}

	public void preencherUF(String uf) {
		estabelecimentoElementMap.uf.sendKeys(uf);
	}

	public void preencherCEP(String cep) {
		estabelecimentoElementMap.cep.sendKeys(cep);
	}

	public void preencherTelefoneContato(String telefone) {
		estabelecimentoElementMap.telefoneContato.sendKeys(telefone);
	}

	public void preencherEmail(String email) {
		estabelecimentoElementMap.email.sendKeys(email);
	}

	public void clicarSalvar() {
		 js.executeScript("arguments[0].click()", estabelecimentoElementMap.botaoSalvar);
	}

	public void clicarOpcao(String nome) {
		waitDriver.until(ExpectedConditions.visibilityOf(estabelecimentoElementMap.botaoCriarEstabelecimento));

		clicarOpcoes(nome, "2");
	}

	public void clicarDesbloquearEstabelecimento() {
		estabelecimentoElementMap.desbloquear.click();
	}

	public void clicarBloquearEstabelecimento() {
		estabelecimentoElementMap.bloquear.click();
	}

	public void clicarExcluirEstabelecimento() {
		estabelecimentoElementMap.excluir.click();
	}

	public void clicarEditarEstabelecimento() {
		estabelecimentoElementMap.editar.click();
	}

	public void filtrarEstabelecimento(String filtro, String tipoFiltro) {
		waitDriver.until(ExpectedConditions.visibilityOf(estabelecimentoElementMap.campoFiltro));

		while (!estabelecimentoElementMap.campoFiltro.getDomProperty("value").matches(filtro)) {
			estabelecimentoElementMap.campoFiltro.clear();
			estabelecimentoElementMap.campoFiltro.sendKeys(filtro);
		}

		estabelecimentoElementMap.comboTipoFiltro.sendKeys(tipoFiltro);
	}

	public void clicarPesquisarEstabelecimento() {
		estabelecimentoElementMap.pesquisar.click();

		await.pollDelay(1, TimeUnit.SECONDS)
				.until(() -> !webDriver.getPageSource().contains("ESTABELECIMENTO POMODORI"));
	}
}
