package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.bdd.steps.pages.ManutencaoConsignantePage;
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.helper.GeradorDocumentoHelper;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.ItemMenuFavoritoService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.RecuperarSenhaPage;
import com.zetra.econsig.tdd.tests.pages.UsuarioPage;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ItemMenuEnum;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class UsuarioSuporteTest extends BaseTest {

	private LoginPage loginPage;
	private MenuPage menuPage;
	private UsuarioPage usuarioPage;
	private AcoesUsuarioPage acoesUsuarioPage;
	private ManutencaoConsignantePage manutencaoConsignantePage;
	private RecuperarSenhaPage recuperarSenhaPage;

	private final LoginInfo loginSup = LoginValues.suporte;

	private String senhaNovoUsuario = null;
	private String usuarioSup = null;

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@BeforeEach
	public void beforeEach() throws Exception {
		super.setUp();
		loginPage = new LoginPage(webDriver);
		menuPage = new MenuPage(webDriver);
		usuarioPage = new UsuarioPage(webDriver);
		acoesUsuarioPage = new AcoesUsuarioPage(webDriver);
		manutencaoConsignantePage = new ManutencaoConsignantePage(webDriver);
		recuperarSenhaPage = new RecuperarSenhaPage(webDriver);

		itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginSup.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.USUARIOS_SUPORTE.getCodigo()));
	}

	@AfterEach
	public void afterEach() throws Exception {
		super.tearDown();
		alterarParametroNivelSegurancaSenha("3", "N", "8", "12");
	}

	@Test
	public void criarUsuarioSuporteComSenhaNivelSegurancaMedioSenhaNaTela() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio e exibe senha na tela");

		String novoUsuario = "sup10";
		alterarParametroNivelSegurancaSenha("3", "N", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// criar usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Usuário criado com sucesso."));

		senhaNovoUsuario = usuarioPage.retornarSenha();
		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(novoUsuario, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(usuarioPage.getMensagemSucesso()
				.contains("Prezado Usuario Selenium, enviamos uma confirmação para o email "));
	}

	@Test
	public void criarUsuarioSuporteComSenhaNivelSegurancaMedioSenhaViaEmail() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio com envio do link via email");

		String novoUsuario = "sup12";
		String senha = "Cse12345";

		alterarParametroNivelSegurancaSenha("3", "S", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// criar usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Usuário criado com sucesso."));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver)
				.contains("Um e-mail foi enviado ao novo usuário com instruções de acesso ao sistema."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(novoUsuario);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(novoUsuario);
		recuperarSenhaPage.preencherSenha("123456");
		recuperarSenhaPage.preencherConfirmarSenha("123456");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(novoUsuario);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(novoUsuario, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(usuarioPage.getMensagemSucesso()
				.contains("Prezado Usuario Selenium, enviamos uma confirmação para o email "));
	}

	@Test
	public void criarUsuarioSuporteComSenhaNivelSegurancaMuitoAltoSenhaNaTela() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio e com IP e Endereço Acesso");

		String novoUsuario = "sup14";

		alterarParametroNivelSegurancaSenha("5", "N", "8", "12");

		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// criar usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Usuário criado com sucesso."));

		senhaNovoUsuario = usuarioPage.retornarSenha();
		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(novoUsuario, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(usuarioPage.getMensagemSucesso()
				.contains("Prezado Usuario Selenium, enviamos uma confirmação para o email "));
	}

	@Test
	public void criarUsuarioSuporteComSenhaNivelSegurancaMuitoAltoSenhaViaEmail() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio com envio do link via email");

		String novoUsuario = "sup16";
		String senha = "yw@526398Ad&";

		alterarParametroNivelSegurancaSenha("5", "S", "8", "12");

		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// criar usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Usuário criado com sucesso."));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver)
				.contains("Um e-mail foi enviado ao novo usuário com instruções de acesso ao sistema."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(novoUsuario);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(novoUsuario);
		recuperarSenhaPage.preencherSenha("cse12345");
		recuperarSenhaPage.preencherConfirmarSenha("cse12345");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(novoUsuario);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(novoUsuario, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Usuario Selenium, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMedioSenhaNaTela() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha na tela");

		alterarParametroNivelSegurancaSenha("3", "N", "4", "8");
		usuarioSup = "sup19";

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioSup(usuarioSup, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// reiniciar senha
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioSup, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMedioSenhaViaEmail() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha via email");

		usuarioSup = "sup25";
		String senha = "Adf85296";
		alterarParametroNivelSegurancaSenha("3", "S", "4", "8");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioSup(usuarioSup, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// reiniciar senha
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains(
				"Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(usuarioSup);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(usuarioSup);
		recuperarSenhaPage.preencherSenha("123456");
		recuperarSenhaPage.preencherConfirmarSenha("123456");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(usuarioSup);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioSup, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Automacao, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMuitoAltoSenhaNaTela() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha na tela");
		usuarioSup = "sup21";

		alterarParametroNivelSegurancaSenha("5", "N", "8", "12");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioSup(usuarioSup, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// reiniciar senha
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioSup, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMuitoAltoSenhaViaEmail() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha via email");

		String usuarioSup = "sup22";
		String senha = "AS$123456Ad&";
		alterarParametroNivelSegurancaSenha("5", "S", "8", "12");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioSup(usuarioSup, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// reiniciar senha
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains(
				"Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(usuarioSup);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(usuarioSup);
		recuperarSenhaPage.preencherSenha("cse12345");
		recuperarSenhaPage.preencherConfirmarSenha("cse12345");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(usuarioSup);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioSup, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Automacao, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void tentarCriarNovoUsuarioSuporteComLoginExistente() {
		log.info("Tentar criar novo usuario suporte com login existente");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// criar usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("zetra_igor", "667.974.260-90");
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"Não foi possível criar este usuário, pois já existe outro com o mesmo login cadastrado no sistema.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void tentarCriarNovoUsuarioSuporteComCPFExistente() {
		log.info("Tentar criar novo usuario suporte com CPF existente");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// criar usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("sup13", "079.527.206-51");
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"Não foi possível realizar esta operação pois existe usuário de suporte com o mesmo número de CPF.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void excluirUsuarioSuporteComSucesso() {
		log.info("Excluir Usuario com sucesso");
		usuarioSup = "sup11";
		// criar usuario para excluir
		usuarioService.criarUsuarioSup(usuarioSup, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// excluir usuario
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarExcluir();

		assertEquals("Confirma a exclusão do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário removido com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		assertTrue(webDriver.getPageSource().contains("Usuário removido com sucesso."));

		// acessar o usuario para verificar os campos desabilitados
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup + "(*)");
		acoesUsuarioPage.clicarEditar();

		usuarioPage.verificarCamposDesabilitados();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioSup, "cse12345");

		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void tentarExcluirUsuarioSuporteLogadoComEleProprio() {
		log.info("Tentar excluir usuario suporte logado com ele proprio");

		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// excluir usuario
		acoesUsuarioPage.clicarOpcoesUsuarios("zetra_igor");
		acoesUsuarioPage.clicarExcluir();

		assertEquals("Confirma a exclusão do usuário \"ZETRASOFT - IGOR LUCAS\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("O USUÁRIO NÃO PODE SER EXCLUÍDO POR ELE PRÓPRIO.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void editarUsuarioSuporteComSucesso() {
		log.info("Editar usuario suporte com sucesso");
		usuarioSup = "sup31";
		// criar usuario para editar
		usuarioService.criarUsuarioSup(usuarioSup, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();
		// filtrar
		usuarioPage.filtroUsuario(usuarioSup, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarEditar();
		usuarioPage.preencherDicaSenha("Senha Automacao");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Alterações salvas com sucesso."));
	}

	@Test
	public void alterarParametroExigeCertificadoDigitalParaSuporte() {
		log.info("Alterar o parametro exige certificado digital para suporte");

		usuarioSup = "sup30";

		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarEditar();
		usuarioPage.isExigeCertificadoDigital(true);
		acoesUsuarioPage.clicarSalvar();

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Alterações salvas com sucesso."));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioSup, "cse12345");

		assertEquals(
				"NÃO É POSSÍVEL ACESSAR O SISTEMA POIS A UTILIZAÇÃO DO CERTIFICADO DIGITAL É OBRIGATÓRIA PARA SEU USUÁRIO.",
				econsigHelper.getMensagemSessaoExpirada(webDriver));

	}

	@Test
	public void bloquearUsuarioSuporteComSucesso() {
		log.info("Bloquear usuario suporte com sucesso");
		usuarioSup = "sup17";
		// criar novo usuario para bloquear
		usuarioService.criarUsuarioSup(usuarioSup, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// bloquear usuario
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o bloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário bloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioSup, "cse12345");

		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));

		// verificar status no banco
		assertEquals(CodedValues.STU_BLOQUEADO_POR_CSE, usuarioService.getUsuario(usuarioSup).getStuCodigo());
	}

	@Test
	public void tentarBloquearUsuarioSuporteLogadoComEleProprio() {
		log.info("Tentar bloquear usuario suporte logado com ele proprio");

		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// bloquear usuario
		acoesUsuarioPage.clicarOpcoesUsuarios("zetra_igor");
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o bloqueio do usuário \"ZETRASOFT - IGOR LUCAS\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("O USUÁRIO NÃO PODE SER BLOQUEADO POR ELE PRÓPRIO.", econsigHelper.getMensagemErro(webDriver));

	}

	@Test
	public void desbloquearUsuarioSuporteComSucesso() {
		log.info("Desbloquear usuario suporte com sucesso");
		usuarioSup = "sup18";
		// criar novo usuario bloqueado para desbloquear
		usuarioService.criarUsuarioSup(usuarioSup, GeradorDocumentoHelper.gerarCPF(true), "4");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// desbloquear usuario
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o desbloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário desbloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioSup, "cse12345");

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email"));

		// verificar status no banco
		assertEquals(CodedValues.STU_ATIVO, usuarioService.getUsuario(usuarioSup).getStuCodigo());
	}

	@Test
	public void tentarDesbloquearUsuarioSuporteCadastradoComCPFExistente() {
		log.info("Tentar desbloquear usuario suporte cadastrado com CPF existente");
		usuarioSup = "sup23";
		// criar novo usuario bloqueado para desbloquear
		usuarioService.criarUsuarioSup(usuarioSup, "218.939.300-09", "4");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// desbloquear usuario
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o desbloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"NÃO FOI POSSÍVEL REALIZAR ESTA OPERAÇÃO POIS EXISTE USUÁRIO DE SUPORTE COM O MESMO NÚMERO DE CPF.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void exibirHistoricoDoUsuarioSuporteComSucesso() {
		log.info("Exibir historico do usuario suporte com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();
		// exibir historico
		acoesUsuarioPage.clicarOpcoesUsuarios("zetra_igor");
		acoesUsuarioPage.clicarExibirHistorico();

		econsigHelper.verificaTextoPagina(webDriver, "Aceitação do Termo de Uso");
		assertTrue(webDriver.getPageSource().contains("127.0.0.1"));
		assertTrue(webDriver.getPageSource().contains("Carlota Joaquina"));
		assertTrue(webDriver.getPageSource().contains("ZETRASOFT - IGOR LUCAS"));
		assertTrue(webDriver.getPageSource().contains("zetra_igor"));
		assertTrue(webDriver.getPageSource().contains("079.527.206-51"));
		assertTrue(webDriver.getPageSource().contains("automacaoncp@gmail.com"));
	}

	@Test
	public void editarFuncoesComSucesso() {
		log.info("Editar funções com sucesso");
		usuarioSup = "sup20";
		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioSup(usuarioSup, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();
		// editar funcoes
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioSup);
		acoesUsuarioPage.clicarEditar();
		acoesUsuarioPage.clicarMaisAcoes();
		acoesUsuarioPage.clicarEditarFuncoes();
		usuarioPage.selecionarAlteracaoAvancadaConsignacao();
		usuarioPage.selecionarEditarAnexosConsignacao();
		usuarioPage.selecionarRenegociarContratoTerceiros();
		usuarioPage.selecionarReservarMargem();
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void editarRestricoesAcessoComSucesso() {
		log.info("Editar restrições de acesso com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();
		// incluir as funções
		acoesUsuarioPage.clicarOpcoesUsuarios("zetra_igor");
		acoesUsuarioPage.clicarEditar();

		acoesUsuarioPage.clicarMaisAcoes();
		acoesUsuarioPage.clicarEditarRestricoesAcesso();
		usuarioPage.preencherFiltro("Consultar Taxa de Juros");
		usuarioPage.selecionarTipoFiltro("Descrição");
		usuarioPage.clicarFiltrar();

		assertEquals("211", usuarioPage.getFuncao());
		assertEquals("Consultar Taxa de Juros", usuarioPage.getDescriao());

		// pesquisar por codigo
		usuarioPage.preencherFiltro("339");
		usuarioPage.selecionarTipoFiltro("Código");
		usuarioPage.clicarFiltrar();

		assertEquals("339", usuarioPage.getFuncao());
		assertEquals("Consultar Usuários de Correspondente", usuarioPage.getDescriao());

		// editar restriçoes
		acoesUsuarioPage.clicarEditar();
		usuarioPage.editarRestricoes();
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Alterações salvas com sucesso.", econsigHelper.getMensagemSucesso(webDriver));
	}

	@Test
	public void pesquisarUsuarioSuporte() {
		log.info("Pesquisar usuario suporte");
		// loga no sistema
		loginPage.loginSimples(loginSup);

		// acessa menu
		menuPage.acessarMenuFavoritos();
		menuPage.acessarFavoritosSuporte();

		// pesquisar usuario
		manutencaoConsignantePage.filtroPerfil("ZETRASOFT - IGOR LUCAS", "Nome");

		// verifica que exibe somente o usuario selecionado
		assertFalse(webDriver.getPageSource().contains("USUÁRIO SUPORTE"));
		assertTrue(webDriver.getPageSource().contains("ZETRASOFT - IGOR LUCAS"));
	}

	private void alterarParametroNivelSegurancaSenha(String nivelSeguranca, String enviaEmail, String tamMin,
			String tamMax) {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL,
				nivelSeguranca);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_SUP,
				enviaEmail);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EMAIL_REINICIALIZACAO_SENHA, enviaEmail);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MIN_SENHA_USUARIOS, tamMin);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, tamMax);

		EConsigInitializer.limparCache();
	}
}
