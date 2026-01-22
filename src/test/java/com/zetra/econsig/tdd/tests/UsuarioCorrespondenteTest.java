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
import com.zetra.econsig.bdd.steps.pages.MenuPage;
import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.helper.GeradorDocumentoHelper;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.RecuperarSenhaPage;
import com.zetra.econsig.tdd.tests.pages.UsuarioPage;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class UsuarioCorrespondenteTest extends BaseTest {

	private LoginPage loginPage;
	private MenuPage menuPage;
	private UsuarioPage usuarioPage;
	private AcoesUsuarioPage acoesUsuarioPage;
	private RecuperarSenhaPage recuperarSenhaPage;

	private final LoginInfo loginCse = LoginValues.cse1;
	private final LoginInfo loginCsa = LoginValues.csa1;
	private final LoginInfo loginCor = LoginValues.cor2;

	private String senhaNovoUsuario = null;

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@BeforeEach
	public void beforeEach() throws Exception {
		super.setUp();
		loginPage = new LoginPage(webDriver);
		menuPage = new MenuPage(webDriver);
		usuarioPage = new UsuarioPage(webDriver);
		acoesUsuarioPage = new AcoesUsuarioPage(webDriver);
		recuperarSenhaPage = new RecuperarSenhaPage(webDriver);
	}

	@AfterEach
	public void afterEach() throws Exception {
		super.tearDown();
		alterarParametroNivelSegurancaSenha("3", "N", "8", "12");
	}

	@Test
	public void criarUsuarioCorrespondenteSenhaNivelMedioNaTelaLogadoComConsignante() {
		log.info(
				"Criar novo usuário correspondente com senha com nivel de seguranca Medio exibida na tela e logado com consignante");

		alterarParametroNivelSegurancaSenha("3", "N", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();

		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
		acoesUsuarioPage.clicarListarUsuariosCor();
		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// criar usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("cor10", GeradorDocumentoHelper.gerarCPF(true));
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
		loginPage.login("cor10", senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(usuarioPage.getMensagemSucesso()
				.contains("Prezado Usuario Selenium, enviamos uma confirmação para o email "));
	}

	@Test
	public void criarUsuarioCorrespondenteSenhaNivelMedioNaTelaLogadoComCorrespondente() {
		log.info(
				"Criar novo usuário correspondente com senha com nivel de seguranca Medio exibida na tela e logado com correspondente");

		alterarParametroNivelSegurancaSenha("5", "N", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginCor);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();

		// acessar lista de correspondente
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// criar usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("cor50", GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Usuário criado com sucesso."));

		senhaNovoUsuario = usuarioPage.retornarSenha();
		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login("cor50", senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(usuarioPage.getMensagemSucesso()
				.contains("Prezado Usuario Selenium, enviamos uma confirmação para o email "));
	}

	@Test
	public void criarUsuarioCorrespondenteComSenhaNivelSegurancaMedioSenhaViaEmail() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio com envio do link via email");

		String novoUsuario = "cor53";
		String senha = "12345DSs";

		alterarParametroNivelSegurancaSenha("3", "S", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginCor);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();

		// acessar lista de correspondente
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// criar usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

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
	public void criarUsuarioCorrespondenteComSenhaNivelSegurancaMuitoAltoSenhaViaEmail() {
		log.info("Criar novo usuário com senha com nivel de segurança Muito Alto com envio do link via email");

		String novoUsuario = "cor67";
		String senha = "yw@52639aAd&";

		alterarParametroNivelSegurancaSenha("5", "S", "8", "12");

		// loga no sistema
		loginPage.loginSimples(loginCor);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();

		// acessar lista de correspondente
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// criar usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

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
		recuperarSenhaPage.preencherSenha("cor12345");
		recuperarSenhaPage.preencherConfirmarSenha("cor12345");
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
	public void excluirUsuarioCorrespondenteLogadoComConsignante() {
		log.info("Excluir usuario correspondente logado com consignante");
		String usuarioCor = "cor11";
		// criar usuario para excluir
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
		acoesUsuarioPage.clicarListarUsuariosCor();
		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// excluir usuario
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarExcluir();
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Observacao Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma a exclusão do usuário \"cor11\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário removido com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		assertTrue(webDriver.getPageSource().contains("Usuário removido com sucesso."));

		// acessar o usuario para verificar os campos desabilitados
		acoesUsuarioPage.clicarOpcoesUsuarios("cor11(*)");
		acoesUsuarioPage.clicarEditar();

		usuarioPage.verificarCamposDesabilitados();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, "cse12345");

		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void excluirUsuarioCorrespondenteLogadoComCorrespondente() {
		log.info("Excluir Usuario com sucesso");
		String usuarioCor = "cor51";
		// criar usuario para excluir
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCor);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// excluir usuario
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarExcluir();
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Observacao Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma a exclusão do usuário \"cor51\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário removido com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		assertTrue(webDriver.getPageSource().contains("Usuário removido com sucesso."));

		// acessar o usuario para verificar os campos desabilitados
		acoesUsuarioPage.clicarOpcoesUsuarios("cor51(*)");
		acoesUsuarioPage.clicarEditar();

		usuarioPage.verificarCamposDesabilitados();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, "cse12345");

		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void editarUsuarioCorrespondenteLogadoComConsignante() {
		log.info("Editar usuario logado com consignante");
		String usuarioCor = "cor12";
		// criar usuario para excluir
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
		acoesUsuarioPage.clicarListarUsuariosCor();
		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarEditar();
		usuarioPage.preencherDicaSenha("Senha Automacao");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Alterações salvas com sucesso."));
	}

	@Test
	public void editarUsuarioCorrespondenteLogadoComCorrespondente() {
		log.info("Editar usuario logado com correspondente");
		String usuarioCor = "cor52";
		// criar usuario para excluir
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCor);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarEditar();
		usuarioPage.preencherDicaSenha("Senha Automacao");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Alterações salvas com sucesso."));
	}

	@Test
	public void bloquearUsuarioCorrespondenteLogadoComConsignataria() {
		log.info("Bloquear usuario correspondente logado com consignataria");
		String usuarioCor = "cor13";
		// criar usuario para bloquear
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCsa);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();

		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarBloquearDesbloquear();
		usuarioPage.selecionarMotivoOperacao("Bloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma o bloqueio do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário bloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, "cse12345");

		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void bloquearUsuarioCorrespondenteLogadoComCorrespondente() {
		log.info("Bloquear usuario correspondente logado com correspondente");
		String usuarioCor = "cor54";
		// criar usuario para bloquear
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCor);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();

		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarBloquearDesbloquear();
		usuarioPage.selecionarMotivoOperacao("Bloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma o bloqueio do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário bloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, "cse12345");

		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void bloquearUsuarioCorrespondenteLogadoComConsignante() {
		log.info("Bloquear usuario correspondente logado com consignante");
		String usuarioCor = "cor14";
		// criar usuario para bloquear
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
		acoesUsuarioPage.clicarListarUsuariosCor();
		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarBloquearDesbloquear();
		usuarioPage.selecionarMotivoOperacao("Bloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma o bloqueio do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário bloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, "cse12345");

		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void desbloquearUsuarioCorrespondenteBloqueadoPeloConsignante() {
		log.info("Desbloquear usuario correspondente bloqueado pelo consignante");
		String usuarioCor = "cor15";
		// criar usuario para bloquear
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true),
				CodedValues.STU_BLOQUEADO_POR_CSE, loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
		acoesUsuarioPage.clicarListarUsuariosCor();
		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarBloquearDesbloquear();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma o desbloqueio do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário desbloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, "cse12345");

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email"));
	}

	@Test
	public void desbloquearUsuarioCorrespondenteBloqueadoLogadoComConsignante() {
		log.info("Desbloquear usuario correspondente bloqueado logado com consignante");
		String usuarioCor = "cor16";
		// criar usuario para bloquear
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_BLOQUEADO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
		acoesUsuarioPage.clicarListarUsuariosCor();
		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarBloquearDesbloquear();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma o desbloqueio do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário desbloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, "cse12345");

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void tentarDesbloquearUsuarioCorrespondenteBloqueadoPeloConsignante() {
		log.info("Tentar desbloquear usuario correspondente bloqueado pelo consignante");
		String usuarioCor = "cor17";
		// criar usuario para desbloquear
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true),
				CodedValues.STU_BLOQUEADO_POR_CSE, loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCsa);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();

		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarBloquearDesbloquear();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma o desbloqueio do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("NÃO É POSSÍVEL DESBLOQUEAR O USUÁRIO 'COR17', POIS ELE FOI BLOQUEADO PELO CONSIGNANTE.",
				econsigHelper.getMensagemErro(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, "cse12345");

		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void desbloquearUsuarioCorrespondenteBloqueadoLogadoComConsignataria() {
		log.info("Desbloquear usuario correspondente bloqueado logado com consignataria");
		String usuarioCor = "cor18";
		// criar usuario para desbloquear
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_BLOQUEADO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCsa);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();

		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarBloquearDesbloquear();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma o desbloqueio do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário desbloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, "cse12345");

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email"));
	}

	@Test
	public void desbloquearUsuarioCorrespondenteBloqueadoLogadoComCorrespondente() {
		log.info("Desbloquear usuario correspondente bloqueado logado com correspondente");
		String usuarioCor = "cor58";
		// criar usuario para desbloquear
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_BLOQUEADO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCor);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();

		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarBloquearDesbloquear();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma o desbloqueio do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário desbloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, "cse12345");

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email"));
	}

	@Test
	public void tentarDesbloquearUsuarioCorrespondenteComCpfJaCadastrado() {
		log.info("Tentar desbloquear usuario correspondente com cpf já cadastrado");
		String usuarioCor = "cor19";
		// criar novo usuario bloqueado para desbloquear
		usuarioService.criarUsuarioCor(usuarioCor, "598.199.087-28", CodedValues.STU_BLOQUEADO_POR_CSE,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
		acoesUsuarioPage.clicarListarUsuariosCor();
		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarBloquearDesbloquear();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma o desbloqueio do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"NÃO FOI POSSÍVEL REALIZAR ESTA OPERAÇÃO POIS EXISTE USUÁRIO DE OUTRA ENTIDADE COM O MESMO NÚMERO DE CPF.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void exibirHistoricoDoUsuarioCorrespondenteComSucesso() {
		log.info("Exibir historico do usuario correspondente com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
		acoesUsuarioPage.clicarListarUsuariosCor();
		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario("cor30", "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios("cor30");
		acoesUsuarioPage.clicarExibirHistorico();

		econsigHelper.verificaTextoPagina(webDriver, "Inclusão de usuário");
		assertTrue(webDriver.getPageSource().contains("Carlota Joaquina"));
		assertTrue(webDriver.getPageSource().contains("Correspondente Automacao"));
		assertTrue(webDriver.getPageSource().contains("cor30"));
		assertTrue(webDriver.getPageSource().contains("598.199.087-28"));
		assertTrue(webDriver.getPageSource().contains("automacaoncp@gmail.com"));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMedioExibidoNaTelaLogadoComConsignante() {
		log.info("Reiniciar senha do usuario com nivel seguranca Medio exibido na tela logado com consignante");

		String usuarioCor = "cor37";

		alterarParametroNivelSegurancaSenha("3", "N", "4", "8");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
		acoesUsuarioPage.clicarListarUsuariosCor();
		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarReiniciarSenha();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Reinicializar a senha do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMuitoAltoSenhaNaTelaLogadoComCorrespondente() {
		log.info("Reiniciar senha do usuario com nivel seguranca Muito Alto exibido na tela logado com correspondente");
		String usuarioCor = "cor38";

		alterarParametroNivelSegurancaSenha("5", "N", "8", "12");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCor);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarReiniciarSenha();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Reinicializar a senha do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMedioSenhaViaEmail() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha via email");
		String usuarioCor = "cor31";
		String senha = "Adf85296";

		alterarParametroNivelSegurancaSenha("3", "S", "4", "8");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCor);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarReiniciarSenha();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Reinicializar a senha do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains(
				"Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(usuarioCor);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(usuarioCor);
		recuperarSenhaPage.preencherSenha("123456");
		recuperarSenhaPage.preencherConfirmarSenha("123456");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(usuarioCor);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Automacao, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMuitoAltoSenhaViaEmail() {
		log.info("Reiniciar Senha com nivel seguranca Muito Alto Senha via email");
		String usuarioCor = "cor32";
		String senha = "AS$123456Ad&";

		alterarParametroNivelSegurancaSenha("5", "S", "8", "12");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCor);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuCorrespondente();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCor);
		acoesUsuarioPage.clicarReiniciarSenha();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Reinicializar a senha do usuário \"" + usuarioCor + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains(
				"Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(usuarioCor);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(usuarioCor);
		recuperarSenhaPage.preencherSenha("cse12345");
		recuperarSenhaPage.preencherConfirmarSenha("cse12345");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(usuarioCor);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCor, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Automacao, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void pesquisarUsuarioCorrespondente() {
		log.info("Pesquisar usuario correspondente");
		String usuarioCor = "cor21";
		// criar usuario
		usuarioService.criarUsuarioCor(usuarioCor, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_BLOQUEADO,
				loginCor.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista de correspondente
		acoesUsuarioPage.clicarOpcoesConsignatarias("17167412007983");
		acoesUsuarioPage.clicarListarUsuariosCor();
		acoesUsuarioPage.clicarOpcoesCorrespondente("002");
		acoesUsuarioPage.clicarUsuarios();

		// pesquisar usuario por usuario
		usuarioPage.filtroUsuario(usuarioCor, "Usuário");
		usuarioPage.clicarPesquisar();
		// verifica que exibe somente o usuario selecionado
		econsigHelper.verificaTextoPagina(webDriver, usuarioCor);
		assertTrue(webDriver.getPageSource().contains(usuarioCor));
		assertFalse(webDriver.getPageSource().contains("cor30"));

		// pesquisar usuario por nome
		usuarioPage.filtroUsuario("CORRESPONDENTE AUTOMACAO", "Nome");
		usuarioPage.clicarPesquisar();
		// verifica que exibe somente o usuario selecionado
		econsigHelper.verificaTextoPagina(webDriver, "cor30");
		assertFalse(webDriver.getPageSource().contains(usuarioCor));
		assertTrue(webDriver.getPageSource().contains("cor30"));

		// pesquisar usuario por bloqueado
		usuarioPage.filtroUsuario("", "Bloqueado");
		usuarioPage.clicarPesquisar();
		// verifica que exibe somente o usuario selecionado
		econsigHelper.verificaTextoPagina(webDriver, usuarioCor);
		assertTrue(webDriver.getPageSource().contains(usuarioCor));
		assertFalse(webDriver.getPageSource().contains("cor30"));

		// pesquisar usuario por desbloqueado
		usuarioPage.filtroUsuario("", "Desbloqueado");
		usuarioPage.clicarPesquisar();
		// verifica que exibe somente o usuario selecionado
		econsigHelper.verificaTextoPagina(webDriver, "cor30");
		assertFalse(webDriver.getPageSource().contains(usuarioCor));
		assertTrue(webDriver.getPageSource().contains("cor30"));
	}

	private void alterarParametroNivelSegurancaSenha(String nivelSeguranca, String enviaEmail, String tamMin,
			String tamMax) {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_CSA_COR_PWD_STRENGTH_LEVEL,
				nivelSeguranca);
		parametroSistemaService
				.configurarParametroSistemaCse(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSA_COR, enviaEmail);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EMAIL_REINICIALIZACAO_SENHA, enviaEmail);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MIN_SENHA_USUARIOS, tamMin);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, tamMax);

		EConsigInitializer.limparCache();
	}
}
