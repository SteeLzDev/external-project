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
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.GrupoServicoPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.values.ItemMenuEnum;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class GrupoServicoTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;
    private GrupoServicoPage grupoServicoPage;
    private AcoesUsuarioPage acoesUsuarioPage;

	private final LoginInfo loginCse = LoginValues.cse1;

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
	    grupoServicoPage = new GrupoServicoPage(webDriver);
	    acoesUsuarioPage = new AcoesUsuarioPage(webDriver);

		itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.GRUPO_DE_SERVICOS.getCodigo()));
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	@Test
	public void criarGrupoServicoComSucesso() throws Exception {
		log.info("Criar grupo serviço com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuGrupoServicos();

		grupoServicoPage.criarNovoGrupoServico();

		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void editarGrupoServicoComSucesso() throws Exception {
		log.info("Editar grupo serviço com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuGrupoServicos();

		acoesUsuarioPage.clicarOpcoes("003", "1");
		grupoServicoPage.clicarEditar();
		grupoServicoPage.preencherQuantidadePorCsa("99");
		grupoServicoPage.clicarSalvar();

		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void excluirGrupoServicoComSucesso() throws Exception {
		log.info("Excluir grupo serviço com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuGrupoServicos();

		acoesUsuarioPage.clicarOpcoes("002", "1");
		grupoServicoPage.clicarExcluir();

		assertEquals("Confirma a exclusão de \"Grupo Servico Excluir\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Grupo de serviço excluído com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void listarServicoComSucesso() throws Exception {
		log.info("Listar serviço para grupo de serviço com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuGrupoServicos();

		acoesUsuarioPage.clicarOpcoes("003", "1");
		grupoServicoPage.clicarServico();

		assertTrue(webDriver.getPageSource().contains("UNIODONTO (DESPESAS)"));
		assertTrue(webDriver.getPageSource().contains("TESTE SERVICO I"));
		assertTrue(webDriver.getPageSource().contains("TESTE SERVICO II"));
	}
}
