package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.RelatorioMovimentoFinanceiroServidorPage;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.service.FuncaoService;
import com.zetra.econsig.service.UsuarioServiceTest;

import io.cucumber.java.Before;
import io.cucumber.java.es.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RelatorioMovimentoServidorStep {

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private FuncaoService funcaoService;

	@Autowired
	private EconsigHelper econsigHelper;

	private MenuPage menuPage;
	private RelatorioMovimentoFinanceiroServidorPage relatorioMovimentoFinanceiroServidorPage;

	@Before
    public void setUp() throws Exception {
        menuPage = new MenuPage(getWebDriver());
        relatorioMovimentoFinanceiroServidorPage = new RelatorioMovimentoFinanceiroServidorPage(getWebDriver());
	}

	@Dado("que o usuario csa tenha a permissao para gerar o relatorio de movimento financeiro do servidor")
	public void permissaoGerarRelatorioDeMovimentoFinanceiroDoServidor() {
		log.info("Dado que o usuario csa tenha a permissao para gerar o relatorio de movimento financeiro do servidor");
		funcaoService.criarFuncaoPerfilCsa(usuarioService.getUsuario(LoginValues.csa2.getLogin()).getUsuCodigo(), "549", usuarioService.getCsaCodigo(LoginValues.csa2.getLogin()));
	}

	@E("que o usuario cse tenha a permissao para gerar o relatorio de movimento finaceiro do servidor")
	public void permissaoGerarRelatorioDeMovimentoFinanceiroDoServidorUserCse() {
	    log.info("E que o usuario cse tenha a permissao para gerar o relatorio de movimento finaceiro do servidor");
	    funcaoService.criarFuncaoPerfilCsa(usuarioService.getUsuario(LoginValues.cse2.getLogin()).getUsuCodigo(), "549", usuarioService.getCsaCodigo(LoginValues.csa1.getLogin()));
	}

	@E("acessar menu favoritos > Relatorio movimento Financeiro do servidor")
	public void acessarRelatorioMovimentoFinanceiroDoServidor() {
		log.info("E acessar menu favoritos > Relatorio movimento Financeiro do servidor");
		menuPage.acessarMenuFavoritos();
		menuPage.acessarfavoritosRelatorioMovimentoFinanceiroDoServidor();
	}

	@Quando("solicita gerar o relatorio sem informar data inicial e data final")
	public void solicitaRelatorioInformandoDataInicial() {
		log.info("Quando solicita gerar o relatorio sem informar data inicial e data final");
		relatorioMovimentoFinanceiroServidorPage.preencherMatricula("123456");
		relatorioMovimentoFinanceiroServidorPage.selecionaFormatoRelatorio("PDF");
		relatorioMovimentoFinanceiroServidorPage.botaoConfirmarRelatorio();
	}

	@Entao("o sistema deve exibir mensagem de validacao: Informe inicio e fim da data de desconto.")
	public void validaMensagemCampoDatas() {
		log.info("Entao o sistema deve exibir mensagem de validacao: Informe inicio e fim da data de desconto.");
		assertEquals("Informe o início do período.", relatorioMovimentoFinanceiroServidorPage.mensagemInformacao());
		assertEquals("Informe o final do período.", relatorioMovimentoFinanceiroServidorPage.mensagemInformacaoDois());
	}

	@Quando("solicita gerar o relatorio sem informar matricula e cpf")
	public void solicitaRelatorioSemInformarDadosServidor() {
		log.info("Quando solicita gerar o relatorio sem informar matricula cpf");
		relatorioMovimentoFinanceiroServidorPage.preencherDataInicial("07/2020");
		relatorioMovimentoFinanceiroServidorPage.preencherDataFim("08/2020");
		relatorioMovimentoFinanceiroServidorPage.selecionaFormatoRelatorio("PDF");
		relatorioMovimentoFinanceiroServidorPage.botaoConfirmarRelatorio();
	}

	@Entao("o sistema deve exibir mensagem de validacao: Informe matricula e cpf.")
	public void validaMensagemCampoMatriculaOuCpf() {
		log.info("Entao o sistema deve exibir mensagem de validacao: Informe matricula e cpf.");
		assertEquals("O CPF deve ser informado.", relatorioMovimentoFinanceiroServidorPage.mensagemInformacao());
		assertEquals("A matrícula deve ser informada.", relatorioMovimentoFinanceiroServidorPage.mensagemInformacaoDois());
	}

	@Quando("solicita gerar o relatorio informando os dados corretamente")
	public void solicitaRelatorioInformandoOsDadosCorretamente() {
		log.info("Quando solicita gerar o relatorio informando os dados corretamente");
		relatorioMovimentoFinanceiroServidorPage.preencherDataInicial("01/2020");
		relatorioMovimentoFinanceiroServidorPage.preencherDataFim("07/2020");
		relatorioMovimentoFinanceiroServidorPage.preencherMatricula(LoginValues.servidor1.getLogin());
		relatorioMovimentoFinanceiroServidorPage.preencherCpf("092.459.399-79");
	}

	@Quando("solicita gerar o relatorio informando o cpf diferente do servidor informado na matricula")
	public void solicitaRelatorioInformandoCpfDiferenteDaMatricula() {
		log.info("Quando solicita gerar o relatorio informando o cpf diferente do servidor informado na matricula");
		relatorioMovimentoFinanceiroServidorPage.preencherDataInicial("01/2020");
		relatorioMovimentoFinanceiroServidorPage.preencherDataFim("07/2020");
		relatorioMovimentoFinanceiroServidorPage.preencherMatricula(LoginValues.servidor1.getLogin());
		relatorioMovimentoFinanceiroServidorPage.preencherCpf("051.882.360-10");
	}

	@Quando("solicita gerar o relatorio informando a matricula diferente do cpf do servidor")
	public void solicitaRelatorioInformandoMatriculaDiferenteDoCpf() {
		log.info("Quando solicita solicita gerar o relatorio informando a matricula diferente do cpf do servidor");
		relatorioMovimentoFinanceiroServidorPage.preencherDataInicial("01/2020");
		relatorioMovimentoFinanceiroServidorPage.preencherDataFim("07/2020");
		relatorioMovimentoFinanceiroServidorPage.preencherMatricula("151234");
		relatorioMovimentoFinanceiroServidorPage.preencherCpf("092.459.399-79");
	}

	@E("seleciona o formato {string} do relatorio de movimento financeiro do servidor")
	public void informaFormatoRelatorio(String formatoRelatorio) {
		log.info("seleciona o formato {} do relatorio de movimento financeiro do servidor", formatoRelatorio);
		relatorioMovimentoFinanceiroServidorPage.selecionaFormatoRelatorio(formatoRelatorio);
	}

	@E("confirma a criacao do relatorio de movimento financeiro do servidor")
	public void confirmaRelatorio() {
		log.info("confirma a criacao do relatório de moivimento financeiro do servidor");
		relatorioMovimentoFinanceiroServidorPage.botaoConfirmarRelatorio();
		}

	@E("que nao exige segunda senha ao solicitar o relatorio de movimento financeiro do servidor")
	public void alteraValoresExigeSegundaSenha() {
	    log.info("E que nao exige segunda senha ao solicitar o relatorio de movimento financeiro do servidor");
	    funcaoService.alteraExigeSegundaSenhaFuncao("549","N","N","N","N","N");
	}

	@Entao("o sistema deve gerar o relatorio de Movimento Financeiro do Servidor")
	public void gerarRelatorioMovimentoFinanceiroDoServidor() {
		log.info("Entao o sistema deve gerar o relatório de Movimento Financeiro do Servidor");
		assertEquals("Relatório gerado com sucesso.", econsigHelper.getMensagemSucesso(getWebDriver()));
	}

	@Entao("o sistema nao deve permitir gerar o relatorio de Movimento Financeiro do Servidor")
	public void naoGerarRelatorioMovimentoFinanceiroDoServidor() {
	    log.info("Entao o sistema nao deve permitir gerar o relatorio de Movimento Financeiro do Servidor");
	    assertEquals("Servidor não encontrado.", econsigHelper.getMensagemErro(getWebDriver()));
	}

	@Entao("o sistema deve solicitar que selecione o formato de arquivo")
	public void naoGerarRelatorioMovimentoFinanceiroDoServidorSemInformarArquivo() {
	    log.info("Entao o sistema deve solicitar que selecione o formato de arquivo");
        assertEquals("Selecione um formato.", relatorioMovimentoFinanceiroServidorPage.mensagemInformacao());
	}
}