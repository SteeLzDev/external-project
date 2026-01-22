package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.BeneficiariosPage;
import com.zetra.econsig.bdd.steps.pages.BeneficiosPage;
import com.zetra.econsig.bdd.steps.pages.ConsultarContratoBeneficiosPage;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConsultarContratoBeneficioStep {

	@Autowired
	private EconsigHelper econsigHelper;

	private ConsultarContratoBeneficiosPage consultarContratoBeneficiosPage;
	private BeneficiosPage beneficiosPage;
	private BeneficiariosPage beneficiariosPage;
	private AcoesUsuarioPage acoesUsuarioPage;

    @Before
    public void setUp() throws Exception {
        consultarContratoBeneficiosPage = new ConsultarContratoBeneficiosPage(getWebDriver());
        beneficiosPage = new BeneficiosPage(getWebDriver());
        beneficiariosPage = new BeneficiariosPage(getWebDriver());
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
    }

	@Entao("lista os contratos de beneficios ativos e os cancelados")
	public void listarContratoBeneficio() throws Throwable {
		log.info("Entao lista os contratos de benefícios ativos e os cancelados");

		consultarContratoBeneficiosPage.aguardarTela();
		assertTrue(getWebDriver().getPageSource().contains("Benefícios ativos"));
		assertTrue(getWebDriver().getPageSource().contains("Benefícios cancelados"));
		assertTrue(getWebDriver().getPageSource().contains("APARTAMENTO (AMPLA)"));
		assertTrue(getWebDriver().getPageSource().contains("ODONTOPREV"));
	}

	@Entao("exibe os detalhes do plano e seus beneficiarios")
	public void listarDetalhesBeneficioAtivo() throws Throwable {
		log.info("Entao exibe os detalhes do plano e seus beneficiários");

		assertTrue(getWebDriver().getPageSource().contains("PLANO DE SAÚDE"));
		assertTrue(getWebDriver().getPageSource().contains("PEA001"));
		assertTrue(getWebDriver().getPageSource().contains("APARTAMENTO (AMPLA)"));
		assertTrue(
				getWebDriver().getPageSource().contains("UNIMED BH COOPERATIVA DE TRABALHO"));
		assertTrue(getWebDriver().getPageSource().contains("242241"));
		assertTrue(getWebDriver().getPageSource().contains("436186015"));
		assertTrue(getWebDriver().getPageSource().contains("00060502479563000"));
		assertTrue(getWebDriver().getPageSource().contains("Ativo"));
	}

	@Entao("exibe os detalhes do plano que foi cancelado e seus beneficiarios")
	public void listarDetalhesBeneficioCancelado() throws Throwable {
		log.info("Entao exibe os detalhes do plano que foi cancelado e seus beneficiários");

		assertTrue(getWebDriver().getPageSource().contains("PLANO ODONTOLÓGICO"));
		assertTrue(getWebDriver().getPageSource().contains("DEO0R1"));
		assertTrue(getWebDriver().getPageSource().contains("ODONTOPREV"));
		assertTrue(getWebDriver().getPageSource().contains("UNIMED BH COOPERATIVA DE TRABALHO"));
		assertTrue(getWebDriver().getPageSource().contains("242874"));
		assertTrue(getWebDriver().getPageSource().contains("456296078"));
		assertTrue(getWebDriver().getPageSource().contains("00060502479613015"));
		assertTrue(getWebDriver().getPageSource().contains("Cancelado"));
	}

	@Quando("clicar em Detalhar Beneficio ativo")
	public void clicarDetalharBeneficioAtivo() throws Throwable {
		log.info("Quando clicar em Detalhar Beneficio");

		acoesUsuarioPage.clicarOpcoes("PLANO DE SAÚDE", "2");
		consultarContratoBeneficiosPage.clicarDetalhar();
	}

	@Quando("clicar em Detalhar Beneficio cancelado")
	public void clicarDetalharBeneficioCancelado() throws Throwable {
		log.info("Quando clicar em Detalhar Beneficio");

		consultarContratoBeneficiosPage.clicarOpcoesBeneficioCancelado("ODONTOPREV");
		consultarContratoBeneficiosPage.clicarDetalhar();
	}

	@Quando("incluir novo beneficiario com CPF {string}")
	public void incluirNovoBeneficiario(String cpf) {
		log.info("Quando incluir novo beneficiario com CPF {}", cpf);

		beneficiariosPage.aguardaModalSimulacaoFicarInvisivel();
		beneficiariosPage.clicarNovoBeneficiario();
		beneficiariosPage.clicarMaisAcoes();
		beneficiariosPage.clicarNovoBeneficiario();
		beneficiariosPage.selecionarTipoBeneficiario("Dependente");
		beneficiariosPage.preencherNome("Jose Antonio de Almeida");
		beneficiariosPage.preencherCPF(cpf);
		beneficiariosPage.preencherRG("12369854");
		beneficiariosPage.marcarSexoFeminino();
		beneficiariosPage.preencherDadosContatos("32", "3265-9856", "32", "96985-6398");
		beneficiariosPage.preencherNomeMae("Maria Antonia de Almeida");
		beneficiariosPage.preencherGrauParentesco("Filho");
		beneficiariosPage.preencherDataNascimento("12/12/1989");
		beneficiariosPage.preencherNacionalidade("Brasileiro");
		beneficiariosPage.preencherEstadoCivil("Solteiro(a)");
	}

	@Quando("registrar ocorrencia")
	public void registrarOcorrencia() throws Throwable {
		log.info("Quando registrar ocorrência");

		acoesUsuarioPage.clicarOpcoes("00060502479563000", "2");
		consultarContratoBeneficiosPage.clicarRegistrarOcorrencia();
		consultarContratoBeneficiosPage.selecionarMotivoOperacao("Outros");
		consultarContratoBeneficiosPage.preencherObservacaoOcorrencia("Registrar ocorrencia");
		consultarContratoBeneficiosPage.clicarConfirmar();
	}

	@Quando("editar beneficio")
	public void editarBeneficio() throws Throwable {
		log.info("Quando editar beneficio");

		beneficiosPage.clicarEditarBeneficio("01/09/2017");
		consultarContratoBeneficiosPage.editarDadosBeneficio("01/09/2018", "4");
	}

	@Quando("cancelar beneficio")
	public void cancelarBeneficio() throws Throwable {
		log.info("Quando cancelar beneficio");

		acoesUsuarioPage.clicarOpcoes("00060502479563000", "2");
		consultarContratoBeneficiosPage.clicarCancelar();
		consultarContratoBeneficiosPage.selecionarMotivoOperacao("Outros");
		consultarContratoBeneficiosPage.preencherObservacao("Cancelar dependente");
	}

	@Quando("clicar listar lancamento")
	public void listarLancamento() throws Throwable {
		log.info("Quando clicar listar lançamento");

		acoesUsuarioPage.clicarOpcoes("00060502479563000", "2");
		consultarContratoBeneficiosPage.clicarListarLancamento();
	}

	@Quando("aprovar solicitacao")
	public void aprovarSolicatacao() throws Throwable {
		log.info("Quando aprovar solicitação");

		acoesUsuarioPage.clicarOpcoes("SARAH ANTAO DA SILVA", "3");
		consultarContratoBeneficiosPage.aprovarSolicitacao();
		consultarContratoBeneficiosPage.preencherObservacao("Aprovado");
		acoesUsuarioPage.clicarSalvar();
	}

	@Quando("rejeitar solicitacao")
	public void rejeitarSolicitacao() throws Throwable {
		log.info("Quando rejeitar solicitação");

		acoesUsuarioPage.clicarOpcoes("ANA LAURA ANTAO DA SILVA", "3");
		consultarContratoBeneficiosPage.rejeitarSolicitacao();
		consultarContratoBeneficiosPage.selecionarMotivoOperacao("Outros");
		consultarContratoBeneficiosPage.preencherObservacao("Rejeitado");
		acoesUsuarioPage.clicarSalvar();

	}

	@Entao("exibe o novo beneficiario na lista")
	public void exibeBeneficiarioNaLista() throws Throwable {
		log.info("Entao exibe o novo beneficíario na lista da simulação por beneficiário");

		assertTrue(getWebDriver().getPageSource().contains("Jose Antonio de Almeida"));
		assertTrue(getWebDriver().getPageSource().contains("Dependente"));
	}

	@Entao("verificar as informacoes do lancamento")
	public void exibeListaLancamento() throws Throwable {
		log.info("Entao verificar as informações do lançamento");

		econsigHelper.verificaTextoPagina(getWebDriver(), "Lançamentos");
		assertTrue(getWebDriver().getPageSource().contains("00060502479563000"));
		assertTrue(getWebDriver().getPageSource().contains("Nenhum registro encontrado."));
	}
}
