package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.service.RelacionamentoAutorizacaoService;
import com.zetra.econsig.tdd.tests.pages.ConsignacaoPage;
import com.zetra.econsig.tdd.tests.pages.ConsultarConsignacaoPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AlongarConsignacaoTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;
    private ConsultarConsignacaoPage consultarConsignacaoPage;
    private ConsignacaoPage consignacaoPage;

	private final LoginInfo loginCse = LoginValues.cse1;
	private final LoginInfo loginServidor = LoginValues.servidor2;
	private String adeNumero = null;

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
	    loginPage = new LoginPage(webDriver);
	    menuPage = new MenuPage(webDriver);
	    consultarConsignacaoPage = new ConsultarConsignacaoPage(webDriver);
	    consignacaoPage = new ConsignacaoPage(webDriver);
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	@Test
	public void alongarConsignacaoComSucesso() throws InterruptedException {
		log.info("Alongar Consignação com sucesso");
		// criar ade
		adeNumero = "13";

		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlongarConsignacao();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// alongar contrato
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarAlongarContrato();

		consignacaoPage.alongarContrato("90", "10");

		assertEquals("Confirma o alongamento do contrato?", econsigHelper.getMensagemPopUp(webDriver));
		econsigHelper.verificaTextoPagina(webDriver, "Controle de renegociação - ADE " + adeNumero);
		assertEquals("90,00", consignacaoPage.getValorPretacao());
		assertEquals("10", consignacaoPage.getNumeroPrestacao());

		// Confere se a ocorrencia de relacionamento foi gerada com sucesso.
		assertTrue(relacionamentoAutorizacaoService.getRelacionamentoAutorizacao(Long.valueOf(adeNumero))
				.getTntCodigo().contains(CodedValues.TNT_CONTROLE_RENEGOCIACAO));
	}
}
