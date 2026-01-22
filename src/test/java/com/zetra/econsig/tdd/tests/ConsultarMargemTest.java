package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.dao.RegistroServidorDao;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.ServidoresPage;
import com.zetra.econsig.values.ItemMenuEnum;

import lombok.extern.log4j.Log4j2;;

@Log4j2
public class ConsultarMargemTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;
    private ServidoresPage servidoresPage;

	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginServidor = LoginValues.servidor2;

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private RegistroServidorDao registroServidorDao;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
	    loginPage = new LoginPage(webDriver);
        menuPage = new MenuPage(webDriver);
	    servidoresPage = new ServidoresPage(webDriver);

		itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCsa.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.CONSULTAR_MARGEM.getCodigo()));
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	@Test
	public void consultaMargemComSucesso() {
		log.info("Consultar Margem Retida com sucesso");
		// pegar margem no banco
		BigDecimal margemRest1 = registroServidorDao.findByRseMatricula(loginServidor.getLogin()).getRseMargemRest();
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCsa);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuConsultarMargem();

		// pesquisar servido
		servidoresPage.preencherMatricula(loginServidor.getLogin());
		servidoresPage.clicarPesquisarMargem();

		assertTrue(econsigHelper.getMensagemSucesso(webDriver)
				.contains("MARGEM 1: R$ " + margemRest1.toString().replace(".", ",")));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("MARGEM 2: R$ 5000,00"));
	}
	
	
	@Test
	public void consultarMargemInformandoMatriculaIncorreta() {
		log.info("Consultar Margem : Informando matricula invalida");
		
		// loga no sistema
		loginPage.acessarTelaLogin();
		loginPage.loginSimples(loginCsa);
		
		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuConsultarMargem();
		
		// pesquisar servido
		servidoresPage.preencherMatricula("444444");
		servidoresPage.clicarPesquisarMargem();
		
        assertEquals("Nenhum registro encontrado para a pesquisa:Matrícula: 444444", econsigHelper.getMensagemErro(webDriver).replace("\n", "").replace("\r", ""));

	}
}