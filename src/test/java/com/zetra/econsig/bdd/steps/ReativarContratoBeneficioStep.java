package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.BeneficiariosPage;
import com.zetra.econsig.bdd.steps.pages.ConsultarContratoBeneficiosPage;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.bdd.steps.pages.ReativarContratoBeneficioPage;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.ConsultarConsignacaoPage;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ReativarContratoBeneficioStep {

	@Autowired
	private EconsigHelper econsigHelper;

    private MenuPage menuPage;
    private AcoesUsuarioPage acoesUsuarioPage;
    private ConsultarConsignacaoPage consultarConsignacaoPage;
    private BeneficiariosPage beneficiariosPage;
    private ConsultarContratoBeneficiosPage consultarContratoBeneficiosPage;
    private ReativarContratoBeneficioPage reativarContratoBeneficioPage;

    @Before
    public void setUp() throws Exception {
        menuPage = new MenuPage(getWebDriver());
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
        consultarConsignacaoPage = new ConsultarConsignacaoPage(getWebDriver());
        beneficiariosPage = new BeneficiariosPage(getWebDriver());
        consultarContratoBeneficiosPage = new ConsultarContratoBeneficiosPage(getWebDriver());
        reativarContratoBeneficioPage = new ReativarContratoBeneficioPage(getWebDriver());
    }

	@Quando("usuario seleciona uma operadora {string}")
	public void selecionarOperadora(String operadora) throws Throwable {
		log.info("Quando usuario seleciona operadora {}", operadora);

		reativarContratoBeneficioPage.selecionarConsignataria(operadora);
	}

	@Quando("reativa contrato beneficio")
	public void cadastrarNovoBeneficio() throws Throwable {
		log.info("Quando reativa contrato benefício");

		reativarContratoBeneficioPage.selecionarPlano();
		reativarContratoBeneficioPage.selecionarBeneficiario();
		beneficiariosPage.aguardaModalSimulacaoFicarInvisivel();
		reativarContratoBeneficioPage.clicarContinuar();

		//verificar que é obrigatorio selecionar contrato
		econsigHelper.verificaTextoPagina(getWebDriver(), "Resultado por beneficiário");
		reativarContratoBeneficioPage.clicarContinuar();

		assertEquals("Gentileza selecionar o contrato de beneficío Saúde para continuar.", econsigHelper.getMensagemPopUp(getWebDriver()));
		reativarContratoBeneficioPage.selecionarContrato();
		reativarContratoBeneficioPage.clicarContinuar();
	}

	@Entao("verificar que exibe o contrato do beneficiario ativo {string}")
	public void verificarContratoAtivo(String matricula) {
		log.info("Entao verificar que exibe o contrato do beneficiário ativo {}", matricula);

		//acessar consultar contrato
		menuPage.acessarMenuBeneficios();
		menuPage.acessarItemMenuConsultarContratoBeneficios();
		//pesquisa o usuario
		consultarConsignacaoPage.preencherMatricula(matricula);
		consultarConsignacaoPage.clicarPesquisar();
		// clicar detalhar
		acoesUsuarioPage.clicarOpcoes("PLANO DE SAÚDE", "2");
		consultarContratoBeneficiosPage.clicarDetalhar();

		//verificar o contrato 00060502196064517
		econsigHelper.verificaTextoPagina(getWebDriver(), "00060502196064517");
		assertTrue(getWebDriver().getPageSource().contains("VANDA IZABEL ANTAO"));
	}
}
