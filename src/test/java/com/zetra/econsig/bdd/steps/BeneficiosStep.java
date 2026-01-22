package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.BeneficiosPage;
import com.zetra.econsig.helper.EconsigHelper;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BeneficiosStep {

	@Autowired
	private EconsigHelper econsigHelper;

    private BeneficiosPage beneficiosPage;

    @Before
    public void setUp() throws Exception {
        beneficiosPage = new BeneficiosPage(getWebDriver());
    }

	@Quando("cadastrar novo beneficio")
	public void cadastrarNovoBeneficio() throws Throwable {
		log.info("Quando cadastrar novo beneficio");

		beneficiosPage.clicarNovoBeneficio();
		beneficiosPage.preencherDadosBeneficios("A981808080808080808080808080AF85", "SAÚDE",
				"Benefícios Unimed", "123", "456", "789", "Dependente");
	}

	@Quando("editar cadastro beneficio")
	public void editarBeneficio() throws Throwable {
		log.info("Quando editar cadastro beneficio");

		beneficiosPage.clicarEditarBeneficio("APP001");
		beneficiosPage.preencherDadosBeneficios("A981808080808080808080808080AF85", "PLANO DE SAÚDE",
				"Benefícios Unimed", "APP001", "467109122", "242336", "Dependente");
	}

	@Quando("incluir servicos existentes")
	public void incluirServicos() throws Throwable {
		log.info("Quando incluir serviços existentes");

		beneficiosPage.clicarEditarBeneficio("PEA001");

		beneficiosPage.selecionarTipoBeneficiario("Dependente");
		beneficiosPage.selecionarServico();

		beneficiosPage.selecionarTipoBeneficiario("Dependente");
		beneficiosPage.selecionarServico();

		beneficiosPage.selecionarTipoBeneficiario("Dependente");
		beneficiosPage.selecionarServico();
	}

	@Quando("excluir cadastro beneficio")
	public void excluirBeneficio() throws Throwable {
		log.info("Quando clicar em Novo Benefício");

		beneficiosPage.clicarExcluirBeneficio("VEE001");

		assertEquals("Confirma exclusão do benefício?", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("bloquear beneficio")
	public void bloquearBeneficio() throws Throwable {
		log.info("Quando bloquear beneficio");

		beneficiosPage.clicarBloquearDesbloquearBeneficio("VEA001");

		assertEquals("Confirma o bloqueio de \"REFERENCIAL FAMILIAR ( APARTAMENTO )\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("desbloquear beneficio")
	public void desbloquearBeneficio() throws Throwable {
		log.info("Quando desbloquear beneficio");

		beneficiosPage.clicarBloquearDesbloquearBeneficio("ODONTO 02");

		assertEquals("Confirma o desbloqueio de \"ODONTO 02\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("clicar em Novo Beneficio")
	public void clicarNovoBeneficio() throws Throwable {
		log.info("Quando clicar em Novo Benefício");

		beneficiosPage.clicarNovoBeneficio();
	}

	@Entao("tentar cadastrar beneficio sem informar os campos obrigatorios")
	public void tentarCadastrarBeneficio() throws Throwable {
		log.info("Quando tentar cadastrar beneficio sem informar os campos obrigatórios");

		beneficiosPage.tentarCadastrarBeneficioSemOperadora();
		assertEquals("A operadora deve ser informada.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiosPage.tentarCadastrarBeneficioSemTipoNatureza();
		assertEquals("A natureza deve ser informada.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiosPage.tentarCadastrarBeneficioSemDescricao();
		assertEquals("A descrição deve ser informada.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiosPage.tentarCadastrarBeneficioSemCodigoPlano();
		assertEquals("O código do plano deve ser informado", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiosPage.tentarCadastrarBeneficioSemCodigoRegistro();
		assertEquals("O código do registro deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));

		beneficiosPage.tentarCadastrarBeneficioSemCodigoContrato();
		assertEquals("O código do contrato deve ser informado.", econsigHelper.getMensagemPopUp(getWebDriver()));
	}
}
