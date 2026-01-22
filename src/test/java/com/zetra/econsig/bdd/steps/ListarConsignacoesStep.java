package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.awaitility.core.ConditionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.ListarConsignacoesPage;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.service.TextoSistemaService;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class ListarConsignacoesStep {

	private final ConditionFactory await = await().atMost(50, TimeUnit.SECONDS);

	private List<String> adeNumeros = new ArrayList<>();

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private TextoSistemaService textoSistemaService;

    private ListarConsignacoesPage listarConsignacoesPage;

    @Before
    public void setUp() throws Exception {
        listarConsignacoesPage = new ListarConsignacoesPage(getWebDriver());
    }

	@Dado("a lista de adeNumeros")
	public void seleciona_ades_lista_consignacao_por_adenumero(DataTable dataTable) {
		adeNumeros = dataTable.asList();
	}

	@Entao("Carrega tela de listagem e escolha multipla de consignacoes")
	public void carrega_tela_listagem_scolha_multipla_consignacoes() {
	    listarConsignacoesPage.carregarListagemConsignacoes();
	}

	@E("Clica no botao confirmar de listagem de consignacao")
    public void clica_botao_confirmar_na_lista_consignacoes() {
	    listarConsignacoesPage.clicarBotaoConfirmar();
		final String msgConfirmacao = econsigHelper.getMensagemPopUp(getWebDriver());

		assertEquals(msgConfirmacao, textoSistemaService.findByTexChave("mensagem.confirmacao.multiplo.confirmar").getTexTexto());
	}

	@E("exibe mensagem de confirmacao de contrato com sucesso")
	public void confere_mensagem_confirmacao_reserva_sucesso() throws Throwable {
		await.atLeast(Duration.ofMillis(3000));

		String mensagemAviso = econsigHelper.getEconsigInfo(getWebDriver());
		assertEquals(mensagemAviso.trim().replace('ç', 'c').replace('ã', 'a').replace('ã', 'a').replace('í', 'i').replace("\n", ""),
				textoSistemaService.findByTexChave("mensagem.confirmar.reserva.concluido.sucesso").getTexTexto().trim().replace('ç', 'c')
				.replace('ã', 'a').replace('ã', 'a').replace('í', 'i').replace("\n", ""));
	}

	@Entao("exibe mensagem de nenhuma consignacao encontrada para matricula {string}")
	public void exibe_mensagem_nenhuma_consginacao_encontrada(String matricula) throws Throwable {
		await.atLeast(Duration.ofMillis(2000));

		assertEquals(
				textoSistemaService.findByTexChave("mensagem.consultar.consignacao.erro.nenhum.registro").getTexTexto() + ":\n" +
				textoSistemaService.findByTexChave("rotulo.matricula.singular").getTexTexto() + ": " +
				matricula, econsigHelper.getMensagemErro(getWebDriver()));
	}

	@Quando("Usuario seleciona ades com adenumeros dados")
	public void seleciona_ades_lista_consignacao_por_adenumero() {
	    listarConsignacoesPage.selecionarConsignacoesAdeNumero(adeNumeros);
	}

	@Quando("Usuario clica em visualizar contrato com adenumero {string}")
	public void seleciona_ades_lista_consignacao_por_adenumero(String adeNumero) {
	    listarConsignacoesPage.detalharAdeListaConsignacoes(adeNumero);
	}
}
