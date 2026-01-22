package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.zetra.econsig.bdd.steps.pages.BeneficioListaContratoPage;

import io.cucumber.java.Before;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BeneficioListarContratoStep {

    private BeneficioListaContratoPage beneficioListaContratoPage;

    @Before
    public void setUp() throws Exception {
        beneficioListaContratoPage = new BeneficioListaContratoPage(getWebDriver());
    }

	@Quando("usuario seleciona consignataria {string}")
	public void selecionar_consignataria(String consignataria) throws Throwable {
		log.info("Quando usuario seleciona consignataria {}",consignataria);

		beneficioListaContratoPage.selecionarConsignataria(consignataria);
	}

	@Entao("exibe lista de contratos")
	public void exibe_lista_contratos() throws Throwable {
		log.info("Então exibe lista de contratos");

		assertTrue(getWebDriver().getPageSource().contains("56032"));
	}

	@Quando("acessar opcao Editar da matricula {string}")
	public void clicar_opcao_editar(String matricula) throws Throwable {
		log.info("Quando acessar opção Editar do código {}", matricula);

		beneficioListaContratoPage.clicarEditar(matricula);
	}

	@E("alterar o campo numero do contrato {string}")
	public void preencher_numero_contrato(String nrocontrato) throws Throwable {
		log.info("E alterar o campo codigo {}", nrocontrato);

		beneficioListaContratoPage.preencherNumeroContrato(nrocontrato);
	}

	@E("alterar o campo data inicio vigencia {string}")
	public void preencher_data_inicio_vigencia(String datainivigencia) throws Throwable {
		log.info("E alterar o campo data inicio vigencia {}", datainivigencia);

		beneficioListaContratoPage.preencherDataIniVigencia(datainivigencia);
	}

	@E("alterar o campo data fim vigencia {string}")
	public void preencher_data_fim_vigencia(String datafimvigencia) throws Throwable {
		log.info("E alterar o campo data fim vigencia {}", datafimvigencia);

		beneficioListaContratoPage.preencherDataFimVigencia(datafimvigencia);
	}
}
