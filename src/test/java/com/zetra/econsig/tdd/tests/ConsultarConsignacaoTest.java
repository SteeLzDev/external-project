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
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.ConsultarConsignacaoPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.values.ItemMenuEnum;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConsultarConsignacaoTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;
    private ConsultarConsignacaoPage consultarConsignacaoPage;

	private final LoginInfo loginCse = LoginValues.cse1;
	private final LoginInfo loginServidor = LoginValues.servidor1;
	private String usuCodigo = null;

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
	    consultarConsignacaoPage = new ConsultarConsignacaoPage(webDriver);

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
	public void consultarConsignacaoComSucesso() {
		log.info("Consultar Consignação com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();

		consultarConsignacaoPage.pesquisarConsinacao(loginServidor.getLogin(), "2");

		econsigHelper.verificaTextoPagina(webDriver, "001 - BB");
		assertTrue(webDriver.getPageSource().contains("14502 - EMPRÉSTIMO"));
		assertTrue(webDriver.getPageSource().contains("40,00"));
		assertTrue(webDriver.getPageSource().contains("10,00"));
	}

	@Test
	public void consultarConsignacaoComDadosInvalido() {
		log.info("Consultar Consignação com dados invalidos");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosConsultarConsignacao();

		// matricula inexistente
		consultarConsignacaoPage.pesquisarConsinacao("654321");
		assertTrue(econsigHelper.getMensagemErro(webDriver).contains("Nenhum registro encontrado para a pesquisa:"));

		// matricula invalida
		consultarConsignacaoPage.pesquisarConsinacao("3563");
		assertEquals("Preencha matrícula corretamente. Informe pelo menos 6 dígitos.",
				econsigHelper.getMensagemPopUp(webDriver));

		// nao preenche matricula
		consultarConsignacaoPage.limparMatricula();
		consultarConsignacaoPage.clicarPesquisar();
		assertEquals("A matrícula deve ser informada.", econsigHelper.getMensagemPopUp(webDriver));
	}
}
