package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.zetra.econsig.tdd.tests.pages.EstabelecimentoPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.values.ItemMenuEnum;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class EstabelecimentoTest extends BaseTest {

    private LoginPage loginPage;
    private MenuPage menuPage;
    private EstabelecimentoPage estabelecimentoPage;

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
	    estabelecimentoPage = new EstabelecimentoPage(webDriver);

		itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.ESTABELECIMENTOS.getCodigo()));
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	@Test
	public void criarEstabelecimentoComSucesso() throws Exception {
		log.info("Criar estabelecimento com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuEstabelecimentos();

		estabelecimentoPage.criarEstabelecimento();

		assertEquals("Estabelecimento criado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void tentarCriarEstabelecimentoSemInformarCamposObrigatorios() throws Exception {
		log.info("Tentar criar estabelecimento sem informar campos obrigatorios");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuEstabelecimentos();

		estabelecimentoPage.clicarCriarEstabelecimento();

		// não informa codigoIdentificador
		estabelecimentoPage.criarEstabelecimento("", "Nome Estabelecimento", "31.064.053/0001-08");
		assertEquals("O código do estabelecimento deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));

		// não informa nome estabelecimento
		estabelecimentoPage.criarEstabelecimento("123", "", "31.064.053/0001-08");
		assertEquals("A descrição do estabelecimento deve ser informada.", econsigHelper.getMensagemPopUp(webDriver));

		// não informa cnpj
		estabelecimentoPage.criarEstabelecimento("123", "Nome Estabelecimento", "");
		assertEquals("O número do CNPJ do estabelecimento deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void tentarCriarEstabelecimentoComCNPJJaExistente() throws Exception {
		log.info("Criar estabelecimento com CNPJ já existente");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuEstabelecimentos();

		estabelecimentoPage.clicarCriarEstabelecimento();

		// não informa codigoIdentificador
		estabelecimentoPage.criarEstabelecimento("2563", "Nome Estabelecimento", "51.091.447/0001-54");

		assertEquals("Não é possível criar este estabelecimento. Existe outro no sistema com o mesmo CNPJ.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void desbloquearEstabelecimentoComSucesso() throws Exception {
		log.info("Bloquear Estabelecimento com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuEstabelecimentos();

		// desbloquear estabelecimento
		estabelecimentoPage.clicarOpcao("ESTABELECIMENTO POMODORI");
		estabelecimentoPage.clicarDesbloquearEstabelecimento();

		assertEquals("Confirma o desbloqueio de \"Estabelecimento Pomodori\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Estabelecimento desbloqueado.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void bloquearEstabelecimentoComSucesso() throws Exception {
		log.info("Bloquear Estabelecimento com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuEstabelecimentos();

		// bloquear estabelecimento
		estabelecimentoPage.clicarOpcao("PREFEITURA MUNICIPAL DE FLORIANOPOLIS");
		estabelecimentoPage.clicarBloquearEstabelecimento();

		assertEquals("Confirma o bloqueio de \"PREFEITURA MUNICIPAL DE FLORIANOPOLIS\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Estabelecimento bloqueado.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void editarEstabelecimentoComSucesso() throws Exception {
		log.info("Editar Estabelecimento com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuEstabelecimentos();

		// editar estabelecimento
		estabelecimentoPage.clicarOpcao("CARLOTA JOAQUINA 21.346.414/0001-47");
		estabelecimentoPage.clicarEditarEstabelecimento();

		// alterar endereço
		estabelecimentoPage.preencherEndereco();

		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void excluirEstabelecimentoComSucesso() throws Exception {
		log.info("Excluir Estabelecimento com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuEstabelecimentos();

		// exclui estabelecimento
		estabelecimentoPage.clicarOpcao("ESTABELECIMENTO EXCLUSAO");
		estabelecimentoPage.clicarExcluirEstabelecimento();

		// confirmar exclusão
		assertEquals("Confirma a exclusão de \"Estabelecimento Exclusao\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Estabelecimento excluído com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void filtrarEstabelecimento() throws Exception {
		log.info("Filtrar Estabelecimento com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarItemMenuEstabelecimentos();

		// filtrar estabelecimento
		estabelecimentoPage.filtrarEstabelecimento("CARLOTA", "Nome");
		estabelecimentoPage.clicarPesquisarEstabelecimento();

		// verificar que só exibe o estabelecimento da pesquisa
		assertTrue(webDriver.getPageSource().contains("CARLOTA JOAQUINA 21.346.414/0001-47"));

		assertFalse(webDriver.getPageSource().contains("ESTABELECIMENTO POMODORI"));
	}
}
