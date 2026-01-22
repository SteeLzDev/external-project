package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.BeneficiariosPage;
import com.zetra.econsig.bdd.steps.pages.CalculoBeneficiosPage;
import com.zetra.econsig.bdd.steps.pages.ConsultarContratoBeneficiosPage;
import com.zetra.econsig.bdd.steps.pages.FaturamentoBeneficiosPage;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FaturamentoBeneficiosStep {

	@Autowired
	private EconsigHelper econsigHelper;

    private AcoesUsuarioPage acoesUsuarioPage;
    private BeneficiariosPage beneficiariosPage;
    private FaturamentoBeneficiosPage faturamentoBeneficiosPage;
    private CalculoBeneficiosPage calculoBeneficiosPage;
    private ConsultarContratoBeneficiosPage consultarContratoBeneficiosPage;

    @Before
    public void setUp() throws Exception {
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
        beneficiariosPage = new BeneficiariosPage(getWebDriver());
        faturamentoBeneficiosPage = new FaturamentoBeneficiosPage(getWebDriver());
        calculoBeneficiosPage = new CalculoBeneficiosPage(getWebDriver());
        consultarContratoBeneficiosPage = new ConsultarContratoBeneficiosPage(getWebDriver());
    }

	@Quando("detalhar faturamento")
	public void detalharFaturamento() throws Throwable {
		log.info("Quando detalhar faturamento");

		acoesUsuarioPage.clicarOpcoes("8001", "2");
		faturamentoBeneficiosPage.clicarDetalharFaturamento();
	}

	@Quando("acessar notas fiscais do faturamento {string}")
	public void acessarNotasFiscais(String faturamento) throws Throwable {
		log.info("Quando acessar notas fiscais");

		faturamentoBeneficiosPage.acessarNotasFiscais(faturamento);
	}

	@Quando("editar nota fiscal")
	public void editarNotaFiscal() throws Throwable {
		log.info("Quando editar nota fiscal");

		faturamentoBeneficiosPage.acessarNotasFiscais("8001");

		acoesUsuarioPage.clicarOpcoes("9001", "0");
		acoesUsuarioPage.clicarEditar();

		faturamentoBeneficiosPage.editarNotaFiscal();
	}

	@Quando("excluir nota fiscal")
	public void excluirNotaFiscal() throws Throwable {
		log.info("Quando excluir nota fiscal");

		faturamentoBeneficiosPage.acessarNotasFiscais("8001");

		acoesUsuarioPage.clicarOpcoes("7201", "0");
		acoesUsuarioPage.clicarExcluir();

		assertEquals("Deseja excluir a nota fiscal?", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("gerar faturamento de beneficio")
	public void gerarFaturamentoBeneficio() throws Throwable {
		log.info("Quando gerar faturamento de benefício");

		faturamentoBeneficiosPage.acessarDetalharFaturamento("8001");

		calculoBeneficiosPage.clicarMaisAcoes();
		faturamentoBeneficiosPage.clicarGerarFaturamentoBeneficio();
	}

	@Quando("excluir arquivo de faturamento")
	public void excluirArquivoFaturamentoBeneficio() throws Throwable {
		log.info("Quando excluir arquivo de faturamento");

		faturamentoBeneficiosPage.acessarDetalharFaturamento("8001");
		acoesUsuarioPage.clicarOpcoes("carlotajoaquina_20200501_20210318103454.zip", "0");
		beneficiariosPage.clicarExcluir();
	}

	@Quando("consultar faturamento de beneficio")
	public void consultarFaturamentoBeneficio() throws Throwable {
		log.info("Quando consultar faturamento de benefício");

		faturamentoBeneficiosPage.acessarDetalharFaturamento("8001");
		calculoBeneficiosPage.clicarMaisAcoes();
		faturamentoBeneficiosPage.clicarConsultaFaturamentoBeneficio();
		faturamentoBeneficiosPage.listarArquivos("579771");
	}

	@Quando("editar item faturamento de beneficio")
	public void editarItemFaturamentoBeneficio() throws Throwable {
		log.info("Quando editar item faturamento de benefício");

		faturamentoBeneficiosPage.acessarDetalharFaturamento("8001");
		calculoBeneficiosPage.clicarMaisAcoes();
		faturamentoBeneficiosPage.clicarConsultaFaturamentoBeneficio();
		faturamentoBeneficiosPage.listarArquivos("579771");
		acoesUsuarioPage.clicarOpcoes("00060502196064509", "2");
		beneficiariosPage.clicarEditar();

		faturamentoBeneficiosPage.editarItemFaturamento();
	}

	@Quando("excluir item faturamento de beneficio")
	public void excluirItemFaturamentoBeneficio() throws Throwable {
		log.info("Quando excluir item faturamento de benefício");

		faturamentoBeneficiosPage.acessarDetalharFaturamento("8001");
		calculoBeneficiosPage.clicarMaisAcoes();
		faturamentoBeneficiosPage.clicarConsultaFaturamentoBeneficio();
		faturamentoBeneficiosPage.listarArquivos("579771");
		acoesUsuarioPage.clicarOpcoes("00060502196064100", "2");
		beneficiariosPage.clicarExcluir();

		assertEquals("Tem certeza que deseja excluir este registro?", econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("validar faturamento de beneficio")
	public void validarFaturamentoBeneficio() throws Throwable {
		log.info("Quando validar faturamento de benefício");

		faturamentoBeneficiosPage.acessarDetalharFaturamento("8001");
		calculoBeneficiosPage.clicarMaisAcoes();
		faturamentoBeneficiosPage.clicarValidarFaturamentoBeneficio();
		consultarContratoBeneficiosPage.clicarConfirmar();

		assertEquals("Deseja continuar a validação de faturamento sem utilizar arquivos de prévia?",
				econsigHelper.getMensagemPopUp(getWebDriver()));
	}

	@Quando("incluir nota fiscal")
	public void incluirNotaFiscal() throws Throwable {
		log.info("Quando incluir nota fiscal");

		faturamentoBeneficiosPage.acessarNotasFiscais("8001");

		faturamentoBeneficiosPage.clicarIncluirNotaFiscal();
		faturamentoBeneficiosPage.incluirNotaFiscal();
	}

	@Quando("acessar tela Inclusao de nota fiscal")
	public void acessarTelaInclusao() throws Throwable {
		log.info("Quando acessar tela Inclusão de nota fiscal");

		faturamentoBeneficiosPage.acessarNotasFiscais("8036");
		faturamentoBeneficiosPage.clicarIncluirNotaFiscal();
	}

	@Entao("tentar cadastrar nota fiscal sem informar os campos obrigatorios")
	public void tentarCadastrarNotaFiscal() throws Throwable {
		log.info("Então tentar cadastrar nota fiscal sem informar os campos obrigatórios");

		beneficiariosPage.clicarSalvar();
		assertEquals("Código do Contrato é obrigatório", econsigHelper.getMensagemErro(getWebDriver()));

		faturamentoBeneficiosPage.preencherCodigoContrato("5201");
		beneficiariosPage.clicarSalvar();
		assertEquals("Número NF é obrigatório", econsigHelper.getMensagemErro(getWebDriver()));

		faturamentoBeneficiosPage.preencherNumeroNF("52631963");
		beneficiariosPage.clicarSalvar();
		assertEquals("Número Título é obrigatório", econsigHelper.getMensagemErro(getWebDriver()));

		faturamentoBeneficiosPage.preencherNumeroTitulo("523631");
		beneficiariosPage.clicarSalvar();
		assertEquals("Data de vencimento é obrigatório", econsigHelper.getMensagemErro(getWebDriver()));

		faturamentoBeneficiosPage.preencherDataVencimento("21/01/2021");
		beneficiariosPage.clicarSalvar();
		assertEquals("Valor ISS é obrigatório", econsigHelper.getMensagemErro(getWebDriver()));

		faturamentoBeneficiosPage.preencherValorISS("10");
		beneficiariosPage.clicarSalvar();
		assertEquals("Valor IR é obrigatório", econsigHelper.getMensagemErro(getWebDriver()));

		faturamentoBeneficiosPage.preencherValorIR("99");
		beneficiariosPage.clicarSalvar();
		assertEquals("Valor PIS COFINS é obrigatório", econsigHelper.getMensagemErro(getWebDriver()));

		faturamentoBeneficiosPage.preencherValorPisConfins("5");
		beneficiariosPage.clicarSalvar();
		assertEquals("Valor Bruto é obrigatório", econsigHelper.getMensagemErro(getWebDriver()));

		faturamentoBeneficiosPage.preencherValorBruto("52631");
		beneficiariosPage.clicarSalvar();
		assertEquals("Valor Líquido é obrigatório", econsigHelper.getMensagemErro(getWebDriver()));
	}

	@Entao("exibe link para download")
	public void exibeLinkDownload() throws Throwable {
		log.info("Então exibe link para download");

		econsigHelper.verificaTextoPagina(getWebDriver(), "22 B");
		acoesUsuarioPage.clicarOpcoes("22 B", "2");

		assertTrue(beneficiariosPage.verificarDownload());
	}

	@Entao("sera listado as notas fiscais cadastradas para o faturamento selecionado")
	public void listaNotasFiscais() throws Throwable {
		log.info("Então será listado as notas fiscais cadastradas para o faturamento selecionado");

		econsigHelper.verificaTextoPagina(getWebDriver(), "9001");
		assertTrue(getWebDriver().getPageSource().contains("12597845"));
		assertTrue(getWebDriver().getPageSource().contains("859641"));
		assertTrue(getWebDriver().getPageSource().contains("27/02/2021"));
	}

	@Entao("sera listados os registros de faturamento de beneficios criados durante o processamento de retorno")
	public void listarFaturamento() throws Throwable {
		log.info(
				"Entao será listados os registros de faturamento de benefícios criados durante o processamento de retorno");

		assertTrue(getWebDriver().getPageSource().contains("UNIMED BH COOPERATIVA DE TRABALHO M"));
		assertTrue(getWebDriver().getPageSource().contains("8001"));
		assertTrue(getWebDriver().getPageSource().contains("08/06/2020"));
		assertTrue(getWebDriver().getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
		assertTrue(getWebDriver().getPageSource().contains("8036"));
		assertTrue(getWebDriver().getPageSource().contains("08/06/2020"));
	}

	@Entao("e exibido os detalhes do faturamento de beneficio selecionado")
	public void exibirDetalhesFaturamento() throws Throwable {
		log.info("Então é exibido os detalhes do faturamento de benefício selecionado");

		assertEquals("05/2020", faturamentoBeneficiosPage.getPeriodo());
		assertTrue(faturamentoBeneficiosPage.getOperadora().contains("UNIMED BH COOPERATIVA DE TRABALHO M"));
		assertEquals("01/05/2020 00:00:00", faturamentoBeneficiosPage.getDataFaturamento());
	}

	@Entao("e exibida uma lista de itens do arquivo de faturamento de acordo com os filtros aplicados")
	public void exibirListaArquivoFaturamento() throws Throwable {
		log.info("Então é exibida uma lista de itens do arquivo de faturamento de acordo com os filtros aplicados");

		assertTrue(getWebDriver().getPageSource().contains("00060502196064002"));
		assertTrue(getWebDriver().getPageSource().contains("049.803.926-95"));
		assertTrue(getWebDriver().getPageSource().contains("Mensalidade plano sa"));
		assertTrue(getWebDriver().getPageSource().contains("00060502196064509"));
		assertTrue(getWebDriver().getPageSource().contains("154.465.656-49"));
		assertTrue(getWebDriver().getPageSource().contains("108,82"));
		assertTrue(getWebDriver().getPageSource().contains("190,26"));
	}

	@Entao("exibe a mensagem de sucesso {string}")
	public void exibe_mensagem_sucesso(String mensagem) throws Throwable {
		log.info("Quando exibe a mensagem de sucesso {}", mensagem);

		if (!econsigHelper.isMensagemSucesso(getWebDriver())) {
		    calculoBeneficiosPage.clicarMaisAcoes();
			faturamentoBeneficiosPage.clicarGerarFaturamentoBeneficio();
		}

		assertTrue(acoesUsuarioPage.getLinhasTabela() > 1);
		assertEquals(mensagem, econsigHelper.getMensagemSucesso(getWebDriver()));
	}
}
