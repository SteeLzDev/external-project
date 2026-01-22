package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.service.AutDescontoService;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.ConsignacaoPage;
import com.zetra.econsig.tdd.tests.pages.ConsultarConsignacaoPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.UsuarioPage;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CancelarConsignacaoTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;
    private ConsignacaoPage consignacaoPage;
    private ConsultarConsignacaoPage consultarConsignacaoPage;
    private UsuarioPage usuarioPage;
    private AcoesUsuarioPage acoesUsuarioPage;

	private String adeNumero = null;
	private final LoginInfo loginCse = LoginValues.cse1;
	private final LoginInfo loginServidor1 = LoginValues.servidor1;
	private final LoginInfo loginServidor2 = LoginValues.servidor2;
	private String usuCodigo = null;

	@Autowired
	private AutDescontoService autDescontoService;

	@Autowired
	private RegistroServidorService registroServidor;

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
	    loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
	    consignacaoPage = new ConsignacaoPage(webDriver);
        consultarConsignacaoPage = new ConsultarConsignacaoPage(webDriver);
        usuarioPage = new UsuarioPage(webDriver);
        acoesUsuarioPage = new AcoesUsuarioPage(webDriver);

		usuCodigo = usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo();
		itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuCodigo,
				Integer.toString(ItemMenuEnum.CONSULTAR_CONSIGNACAO.getCodigo()));
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	@Test
	public void cancelarConsignacaoComSucesso() {
		log.info("Cancelar Consignacao com sucesso");
		// cria consignacao
		adeNumero = "14";

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// cancelar contrato
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarCancelarConsignacao();

		assertEquals("Confirma o cancelamento desta consignação?", econsigHelper.getMensagemPopUp(webDriver));

		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Automacao cancelar");
		acoesUsuarioPage.clicarSalvar();

		econsigHelper.verificaTextoPagina(webDriver, "Cancelada");

		assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void tentarCancelarConsignacaoComParcelaPaga() {
		log.info("Cancelar Consignacao com sucesso");
		// cria consignacao
		adeNumero = "15";
		// pagar parcelar
		AutDesconto ade = autDescontoService.alterarAutDescontoPorNumeroAde(Long.valueOf(adeNumero), "2", "1");
		autDescontoService.incluirParcelaDesconto(ade.getAdeCodigo(), CodedValues.SPD_EMPROCESSAMENTO,
				new BigDecimal("10"), "1");
		autDescontoService.incluirParcelaDescontoPeriodo(ade.getAdeCodigo(), CodedValues.SPD_EMPROCESSAMENTO,
				new BigDecimal("10"), "1");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuOperacional();
		menuPage.acessarItemMenuAlterarContrato();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();

		// cancelar contrato
		consignacaoPage.clicarOpcoes(adeNumero);
		consignacaoPage.clicarEditarContrato();
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarCancelarConsignacao();

		assertEquals("Confirma o cancelamento desta consignação?", econsigHelper.getMensagemPopUp(webDriver));

		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Automacao cancelar");
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Não é possível cancelar esta consignação pois existe parcela processada ou em processamento, se necessário utilize a função de liquidar consignação.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void desfazerCancelamentoConsignacao() {
		log.info("Desfazer cancelamento consignação com sucesso");
		// cria consignacao cancelada
		adeNumero = "16";

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa Consulta Consignacao
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor1.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();
		consultarConsignacaoPage.clicarVisualizar();

		// desfazer cancelamento
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarDesfazerCancelamentoConsignacao();
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Automacao cancelar");
		usuarioPage.clicarSalvarDesfazerCancelamento();

		assertEquals("Operação concluída com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void tentarDesfazerCancelamentoConsignacaoComMargemJaUtilizada() {
		log.info("Desfazer cancelamento consignação com sucesso");
		// cria consignacao cancelada
		adeNumero = "17";
		registroServidor.alterarRseMargemRest(loginServidor2.getLogin(), BigDecimal.ZERO);

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa Consulta Consignacao
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();

		// pesquisa consignacao
		consultarConsignacaoPage.preencherMatricula(loginServidor2.getLogin());
		consultarConsignacaoPage.preencherADE(adeNumero);
		consultarConsignacaoPage.clicarPesquisar();
		consultarConsignacaoPage.clicarVisualizar();

		// desfazer cancelamento
		consignacaoPage.clicarAcoes();
		consignacaoPage.clicarDesfazerCancelamentoConsignacao();
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Automacao cancelar");
		usuarioPage.clicarSalvarDesfazerCancelamento();

		assertEquals(
				"AUTORIZAÇÃO NÃO PODE SER DESCANCELADA PORQUE A MARGEM LIBERADA PELO CANCELAMENTO JÁ FOI UTILIZADA.",
				econsigHelper.getMensagemErro(webDriver));
	}
}
