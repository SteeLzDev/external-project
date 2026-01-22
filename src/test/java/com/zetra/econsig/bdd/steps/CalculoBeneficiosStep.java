package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.BeneficiariosPage;
import com.zetra.econsig.bdd.steps.pages.CalculoBeneficiosPage;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CalculoBeneficiosStep {

	@Autowired
	private EconsigHelper econsigHelper;

    private MenuPage menuPage;
    private AcoesUsuarioPage acoesUsuarioPage;
    private BeneficiariosPage beneficiariosPage;
    private CalculoBeneficiosPage calculoBeneficiosPage;

    @Before
    public void setUp() throws Exception {
        menuPage = new MenuPage(getWebDriver());
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
        beneficiariosPage = new BeneficiariosPage(getWebDriver());
        calculoBeneficiosPage = new CalculoBeneficiosPage(getWebDriver());
    }

	@Dado("possuir tabela ativa")
	public void possuiTabelaAtiva() throws Throwable {
		log.info("Quando possuir tabela ativa");

		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosCalculoBeneficio();
		calculoBeneficiosPage.ativarTabela();
		// retorna para pagina principal
		menuPage.acessarPaginaInicial();
	}

	@Quando("cadastrar calculo beneficio")
	public void cadastrarCalculoBeneficio() throws Throwable {
		log.info("Quando cadastrar cálculo beneficio");

		calculoBeneficiosPage.clicarMaisAcoes();
		calculoBeneficiosPage.clicarNovoCalculo();
		calculoBeneficiosPage.cadastrarCalculoBeneficio();
	}

	@Quando("editar calculo beneficio")
	public void editarCalculoBeneficio() throws Throwable {
		log.info("Quando editar cálculo beneficio");

		acoesUsuarioPage.clicarOpcoes("Dependente", "5");
		beneficiariosPage.clicarEditar();
		calculoBeneficiosPage.editarCalculoBeneficio();
	}

	@Quando("excluir calculo beneficio")
	public void excluirCalculoBeneficio() throws Throwable {
		log.info("Quando excluir cálculo beneficio");

		acoesUsuarioPage.clicarOpcoes("ENFERMARIA ( REDE AMPLA )", "4");
		beneficiariosPage.clicarExcluir();

		assertEquals("Confirma exclusão do cálculo de benefício?", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("ativar tabela")
	public void ativarTabela() throws Throwable {
		log.info("Quando ativar tabela");

		calculoBeneficiosPage.clicarAtivarTabela();

		assertEquals("Confirma a ativação da nova tabela de cálculo benefício?", econsigHelper.getMensagemPopUp(getWebDriver()));

		econsigHelper.verificaTextoPagina(getWebDriver(), "Nenhum registro encontrado.");
	}

	@Quando("iniciar tabela")
	public void iniciarTabela() throws Throwable {
		log.info("Quando iniciar tabela");

		calculoBeneficiosPage.clicarIniciarTabela();

		econsigHelper.verificaTextoPagina(getWebDriver(), "1,00");
		assertTrue(getWebDriver().getPageSource().contains("99"));
		assertTrue(getWebDriver().getPageSource().contains("999999,00"));
		assertTrue(getWebDriver().getPageSource().contains("131,00"));
		assertEquals("Carlota Joaquina 21.346.414/0001-47", calculoBeneficiosPage.getOrgao());
		assertEquals("ENFERMARIA - REDE RESTRITA", calculoBeneficiosPage.getBeneficioTitular());
	}

	@Quando("exclui tabela iniciada")
	public void excluirTabelaIniciada() throws Throwable {
		log.info("Quando exclui tabela iniciada");

		calculoBeneficiosPage.clicarExcluirTabelaIniciada();

		assertEquals("Confirma remoção da tabela iniciada?", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("acessar tela Inclusao de novo calculo beneficio")
	public void acessarTelaNovoCalculo() throws Throwable {
		log.info("Quando acessar tela Inclusão de novo cálculo benefício");

		calculoBeneficiosPage.clicarMaisAcoes();
		calculoBeneficiosPage.clicarNovoCalculo();
	}

	@Quando("aplicar o reajuste")
	public void aplicarReajuste() throws Throwable {
		log.info("Quando aplicar o reajuste");

		calculoBeneficiosPage.selecionarTipoBeneficiario("Titular");
		calculoBeneficiosPage.clicarPesquisar();
		calculoBeneficiosPage.preencherPercentualReajuste("10");
		calculoBeneficiosPage.selecionarValorBeneficio();
		calculoBeneficiosPage.selecionarValorSubsidio();
		calculoBeneficiosPage.selecionarFaixaSalarial();
		calculoBeneficiosPage.selecionarCalculoBeneficio();
		calculoBeneficiosPage.clicarAplicarReajusteCalculo();

		assertEquals("Confirma a aplicação de reajuste na tabela de cálculo de benefício?",
				econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("tentar aplicar reajuste sem informar campos obrigatorios")
	public void tentarAplicarReajuste() throws Throwable {
		log.info("Quando tentar aplicar reajuste sem informar campos obrigatórios");

		calculoBeneficiosPage.selecionarTipoBeneficiario("Titular");
		calculoBeneficiosPage.clicarPesquisar();
		calculoBeneficiosPage.clicarAplicarReajusteCalculoSemSucesso();
		assertEquals("O percentual para o reajuste deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		calculoBeneficiosPage.preencherPercentualReajuste("10");
		calculoBeneficiosPage.clicarAplicarReajusteCalculoSemSucesso();
		assertEquals("Os campos de valores que sofrerão reajuste devem ser informados.", econsigHelper.getMensagemPopUp(getWebDriver()));

		calculoBeneficiosPage.selecionarValorBeneficio();
		calculoBeneficiosPage.clicarAplicarReajusteCalculoSemSucesso();
		assertEquals("Nenhum item de cálculo de benefício foi selecionado para reajuste.", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("acessar a tela Aplicar reajuste")
	public void acessarTelaAplicarReajuste() throws Throwable {
		log.info("Quando acessar a tela Aplicar reajuste");

		// iniciar a tabela, para exibir a opção de reajuste
		calculoBeneficiosPage.iniciarTabela();

		calculoBeneficiosPage.clicarMaisAcoes();
		calculoBeneficiosPage.clicarAplicarReajuste();
	}

	@Entao("tentar cadastrar novo calculo de beneficios sem informar os campos obrigatorios")
	public void tentarCadastrarCalculoBeneficio() throws Throwable {
		log.info("Então tentar cadastrar novo cálculo de benefícios sem informar os campos obrigatórios");

		calculoBeneficiosPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47");
		beneficiariosPage.clicarSalvar();
		assertEquals("O benefício deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		calculoBeneficiosPage.selecionarBeneficio("REFERENCIAL FAMILIAR ( APARTAMENTO )");
		beneficiariosPage.clicarSalvar();
		assertEquals("O valor da mensalidade deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Entao("verificar se valores foram alterados")
	public void verificarValoresAlterados() throws Throwable {
		log.info("Então verificar se valores foram alterados");

		calculoBeneficiosPage.selecionarTipoBeneficiario("Titular");
		calculoBeneficiosPage.clicarPesquisar();

		//verifica campos
		econsigHelper.verificaTextoPagina(getWebDriver(), "Lista de cálculo de benefícios");
		assertEquals("1,10", calculoBeneficiosPage.getInicioFaixaTitular());
		assertEquals("1099998,88", calculoBeneficiosPage.getFimFaixaTitular());
		assertEquals("165,00", calculoBeneficiosPage.getValorBeneficioTitular());
		assertEquals("144,10", calculoBeneficiosPage.getValorSubsidioTitular());
	}
}
