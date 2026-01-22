package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.values.ItemMenuEnum;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RelatorioIntegracaoTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;

	private final LoginInfo loginCse = LoginValues.cse1;
	private String usuCodigo = null;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
        loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	/**
	 * Teste automatizado que faz o download do "Relatorio de Integracao", se
	 * existir arquivo disponivel, e recebe como parametros o tipo de usuario.
	 */
	@Test
	public void relatorioIntegracao() {
		log.info("Relatorio Integracao sem agendamento");

		usuCodigo = usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo();
		// inclui o relatorio como favorito
		itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuCodigo,
				Integer.toString(ItemMenuEnum.RELATORIO_INTEGRACAO.getCodigo()));

		// Acessa o sistema
		loginPage.loginSimples(loginCse);

		// Seleciona o item desejado no menu.
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosRelatorioIntegracao();

		assertTrue(webDriver.getPageSource().contains("Nenhum relatório encontrado."));
	}
}
