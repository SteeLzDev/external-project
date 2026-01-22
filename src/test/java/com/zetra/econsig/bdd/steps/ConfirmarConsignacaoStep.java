package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.awaitility.core.ConditionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.ConfirmarReservaPage;
import com.zetra.econsig.bdd.steps.pages.ConfirmarSolicitacaoPage;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.service.TextoSistemaService;

import io.cucumber.java.Before;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class ConfirmarConsignacaoStep {

	private final ConditionFactory await = await().atMost(50, TimeUnit.SECONDS);

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private TextoSistemaService textoSistemaService;

	private ConfirmarReservaPage confirmarReservaPage;
	private ConfirmarSolicitacaoPage confirmarSolicitacaoPage;

    @Before
    public void setUp() throws Exception {
        confirmarReservaPage = new ConfirmarReservaPage(getWebDriver());
        confirmarSolicitacaoPage = new ConfirmarSolicitacaoPage(getWebDriver());
    }

	@Entao("exibe tela de confirmar reserva")
	public void clica_botao_confirmar_na_lista_consignacoes() {
	    await.atLeast(Duration.ofSeconds(5));
		econsigHelper.verificaTextoPagina(getWebDriver(), "Dados da consignação");

		confirmarReservaPage.carregarPaginaConfirmarReserva();
	}

	@Quando("Usuario seleciona motivo de operacao")
	public void usuario_seleciona_motivo_operacao() {
	    confirmarReservaPage.selecionarTmo();
	}

	@E("clica em salvar na tela de confirmacao de reserva")
	public void clica_salvar_confirmar_reserva() {
	    confirmarReservaPage.btnEnvia();

		final String msgConfirmacao = econsigHelper.getMensagemPopUp(getWebDriver());

		assertEquals(msgConfirmacao.trim().replace('ç', 'c').replace('ã', 'a').replace('í', 'i'),
				textoSistemaService.findByTexChave("mensagem.confirmacao.reserva").getTexTexto().trim().replace('ç', 'c').replace('ã', 'a').replace('í', 'i'));
	}

	@E("clica em salvar na tela de confirmacao de solicitacao")
	public void clica_salvar_confirmar_solicitacao() {
	    confirmarSolicitacaoPage.carregarPaginaConfirmarSolicitacao();

		confirmarSolicitacaoPage.btnEnvia();

		final String msgConfirmacao = econsigHelper.getMensagemPopUp(getWebDriver());

		assertEquals(msgConfirmacao, textoSistemaService.findByTexChave("mensagem.confirmacao.confirmar.solicitacao").getTexTexto());
	}

	@Entao("exibe tela de erro com mensagem de servidor em situacao pendente")
	public void servidor_em_situacao_pendente() {
		await.atLeast(Duration.ofMillis(2000));

		assertEquals(textoSistemaService.findByTexChave("mensagem.erro.autorizacao.nao.pode.ser.confirmada.servidor.pendente").getTexTexto(), econsigHelper.getMensagemErro(getWebDriver()));
	}

	@E("Clica Confirmacao da Solicitacao")
	public void confirma_solicitacao() {
		final String msgConfirmacao = econsigHelper.getMensagemPopUp(getWebDriver());

		assertEquals(msgConfirmacao, textoSistemaService.findByTexChave("mensagem.confirmacao.solicitacao").getTexTexto());
	}
}
