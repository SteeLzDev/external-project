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
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.RecuperarSenhaPage;
import com.zetra.econsig.tdd.tests.pages.UsuarioPage;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class UsuarioConsignatariaTest extends BaseTest {

	private LoginPage loginPage;
	private MenuPage menuPage;
	private UsuarioPage usuarioPage;
	private AcoesUsuarioPage acoesUsuarioPage;
	private ManutencaoConsignantePage manutencaoConsignantePage;
	private RecuperarSenhaPage recuperarSenhaPage;

	private final LoginInfo loginCse = LoginValues.cse1;
	private final LoginInfo loginCsa1 = LoginValues.csa1;
	private final LoginInfo loginCsa2 = LoginValues.csa2;

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
		manutencaoConsignantePage = new ManutencaoConsignantePage(webDriver);
		recuperarSenhaPage = new RecuperarSenhaPage(webDriver);
	}

	@AfterEach
	public void afterEach() throws Exception {
		super.tearDown();
		alterarParametroNivelSegurancaSenha("3", "N", "8", "12");
	}

	// @Test
	public void criarUsuarioConsignatariaComSenhaNivelSegurancaMedioSenhaNaTela() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio e com IP e Endereço Acesso");

		String novoUsuario = "csa10";
		alterarParametroNivelSegurancaSenha("3", "N", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();

		// criar novo usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		usuarioPage.isExigeCertificadoDigital(true);
		usuarioPage.preencherDataValido("01/02/2029");
		usuarioPage.selecionarPerfil("MASTER");
		acoesUsuarioPage.clicarSalvar();

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Usuário criado com sucesso."));

		senhaNovoUsuario = usuarioPage.retornarSenha();
		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(novoUsuario, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(usuarioPage.getMensagemSucesso()
				.contains("Prezado Usuario Selenium, enviamos uma confirmação para o email"));
	}

	// @Test
	public void criarUsuarioConsignatariaComSenhaNivelSegurancaMedioSenhaViaEmail() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio com envio do link via email");

		String novoUsuario = "csa11";
		String senha = "12345DSs";
		alterarParametroNivelSegurancaSenha("3", "S", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();

		// criar novo usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.selecionarPerfil("SEILA__C");
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
	public void criarUsuarioConsignatariaComSenhaNivelSegurancaMuitoAltoSenhaNaTela() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio e com IP e Endereço Acesso");

		String novoUsuario = "csa14";

		alterarParametroNivelSegurancaSenha("5", "N", "8", "12");

		// loga no sistema com usuario CSA
		loginPage.loginSimples(loginCsa2);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignataria();

		// criar novo usuario
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		usuarioPage.isExigeCertificadoDigital(true);
		usuarioPage.preencherDataValido("01/02/2029");
		usuarioPage.selecionarPerfil("MASTER");
		acoesUsuarioPage.clicarSalvar();

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
	public void criarUsuarioConsignatariaComSenhaNivelSegurancaMuitoAltoSenhaViaEmail() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio com envio do link via email");

		String novoUsuario = "cse566";
		String senha = "yw@52639aAd&";

		alterarParametroNivelSegurancaSenha("5", "S", "8", "12");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();

		// criar novo usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
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
	public void tentarCriarUsuarioConsignatariaComLoginJaCadastrado() {
		log.info("Tentar criar novo usuário com Login já cadastrado");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// tentar criar novo usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("csa2", GeradorDocumentoHelper.gerarCPF(true));
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemErro(webDriver).contains(
				"Não foi possível criar este usuário, pois já existe outro com o mesmo login cadastrado no sistema."));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMedioSenhaNaTela() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha na tela");
		String usuarioCsa = "csa19";

		alterarParametroNivelSegurancaSenha("3", "N", "4", "8");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCsa2.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMedioSenhaViaEmail() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha via email");
		String usuarioCsa = "csa20";
		String senha = "Adf85296";

		alterarParametroNivelSegurancaSenha("3", "S", "4", "8");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCsa2.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains(
				"Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(usuarioCsa);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(usuarioCsa);
		recuperarSenhaPage.preencherSenha("123456");
		recuperarSenhaPage.preencherConfirmarSenha("123456");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(usuarioCsa);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Automacao, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMuitoAltoSenhaNaTela() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha na tela");
		String usuarioCsa = "csa21";

		alterarParametroNivelSegurancaSenha("5", "N", "8", "12");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCsa2.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMuitoAltoSenhaViaEmail() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha via email");
		String usuarioCsa = "csa29";
		String senha = "AS$123456Ad&";

		alterarParametroNivelSegurancaSenha("5", "S", "8", "12");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCsa2.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains(
				"Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(usuarioCsa);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(usuarioCsa);
		recuperarSenhaPage.preencherSenha("cse12345");
		recuperarSenhaPage.preencherConfirmarSenha("cse12345");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(usuarioCsa);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Automacao, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void tentarCriarNovoUsuarioConsignatariaComDoisIPsDeAcessoIguais() {
		log.info("Criar novo usuário com Login já cadastrado");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// tentar criar novo usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("csa13", GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.incluirIPsAcessoAtual();
		usuarioPage.clicarIncluirIPsAcessoAtual();
		usuarioPage.clicarIncluirIPsAcesso();

		assertEquals("Este endereço já foi inserido na lista.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void tentarCriarNovoUsuarioConsignatariaIncluindoIPdeAcessoInvalido() {
		log.info("Tentar criar novo usuário incluindo IP de acesso invalido");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// tentar criar novo usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("csa14", GeradorDocumentoHelper.gerarCPF(true));
		// inclui ip vazio
		usuarioPage.clicarIncluirIPsAcesso();
		assertEquals("Número de IP inválido.", econsigHelper.getMensagemPopUp(webDriver));

		usuarioPage.preencherIPsAcessoAtual("12");
		assertEquals("Número de IP inválido.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void tentarCriarNovoUsuarioConsignatariaComDoisEnderecoDeAcessoIguais() {
		log.info("Criar novo usuário com dois endereço de acesso iguais");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// tentar criar novo usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("csa15", GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.preencherEnderecoAcesso("localhost");

		assertEquals("Este endereço já foi inserido na lista.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void bloquearUsuarioConsignatariaComSucesso() {
		log.info("Bloquear Usuario com sucesso");
		String usuarioCsa = "csa17";
		// criar novo usuario para bloquear
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				"csa2");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// bloquear usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o bloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário bloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, "cse12345");

		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void desbloquearUsuarioConsignatariaComSucesso() {
		log.info("Desbloquear Usuario com sucesso");
		String usuarioCsa = "csa18";
		// criar novo usuario bloqueado para desbloquear
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true),
				CodedValues.STU_BLOQUEADO_POR_CSE, "csa2");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// desbloquear usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o desbloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário desbloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, "cse12345");

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void desbloquearUsuarioConsignatariaQueFoiBloqueadaPorCSA() {
		log.info("Desbloquear Usuario consignataria que foi bloqueada por CSA");
		String usuarioCsa = "csa40";
		// criar novo usuario bloqueado para desbloquear
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_BLOQUEADO,
				loginCsa2.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// desbloquear usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o desbloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário desbloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, "cse12345");

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void tentarDesbloquearUsuarioConsignatariaComCpfJaCadastrado() {
		log.info("Tentar desbloquear usuario com cpf já cadastrado");
		String usuarioCsa = "csa22";
		// criar novo usuario bloqueado para desbloquear
		usuarioService.criarUsuarioCsa(usuarioCsa, "031.699.490-12", CodedValues.STU_BLOQUEADO_POR_CSE, "csa2");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// desbloquear usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o desbloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"NÃO FOI POSSÍVEL REALIZAR ESTA OPERAÇÃO POIS EXISTE USUÁRIO DE OUTRA ENTIDADE COM O MESMO NÚMERO DE CPF.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void tentarDesbloquearUsuarioConsignatariaQueNaoPossuiEmailCadastrado() {
		log.info("Tentar desbloquear usuario que não possui email cadastrado");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// desbloquear usuario
		acoesUsuarioPage.clicarOpcoesConsignatarias("8036");
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios("MASTER8036");
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o desbloqueio do usuário \"DENTAL UNI - COOPERATIVA ODONTOLOGICA\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("NÃO FOI POSSÍVEL REALIZAR ESTA OPERAÇÃO POIS O E-MAIL DO USUÁRIO DEVE SER INFORMADO.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void exibirHistoricoDoUsuarioConsignatariaComSucesso() {
		log.info("Exibir Historico do Usuario com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// exibir historico
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios("csa2");
		acoesUsuarioPage.clicarExibirHistorico();

		econsigHelper.verificaTextoPagina(webDriver, "Aceitação do Termo de Uso");
		assertTrue(webDriver.getPageSource().contains("127.0.0.1"));
		assertTrue(webDriver.getPageSource().contains("Carlota Joaquina"));
		assertTrue(webDriver.getPageSource().contains("csa2"));
		assertTrue(webDriver.getPageSource().contains("031.699.490-12"));
	}

	@Test
	public void excluirUsuarioConsignatariaComSucesso() {
		log.info("Excluir Usuario com sucesso");
		String usuarioCsa = "csa20";
		// criar usuario para excluir
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				"csa2");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista usuarios
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		// excluir usuario
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarExcluir();

		assertEquals("Confirma a exclusão do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário removido com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// acessar o usuario para verificar os campos desabilitados
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa + "(*)");
		acoesUsuarioPage.clicarEditar();

		usuarioPage.verificarCamposDesabilitados();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, "cse12345");

		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void editarUsuarioConsignatariaLogadoComCse() {
		log.info("Editar usuario logado com cse");
		// criar usuario para excluir
		usuarioService.criarUsuarioCsa("csa21", GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO, "csa2");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista usuarios
		acoesUsuarioPage.clicarOpcoesConsignatarias("001");
		acoesUsuarioPage.clicarUsuarios();
		// filtrar
		usuarioPage.filtroUsuario("csa21", "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios("csa21");
		acoesUsuarioPage.clicarEditar();
		usuarioPage.preencherDicaSenha("Senha Automacao");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Alterações salvas com sucesso."));
	}

	@Test
	public void editarUsuarioConsignatariaLogadoComCsa() {
		log.info("Editar usuario logado com csa");

		// loga no sistema
		loginPage.loginSimples(loginCsa1);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignataria();
		// acessar lista usuarios
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios("csa");
		acoesUsuarioPage.clicarEditar();
		usuarioPage.preencherDicaSenha("Senha Automacao");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Alterações salvas com sucesso."));
	}

	@Test
	public void pesquisarUsuarioConsignataria() {
		log.info("Pesquisar usuario consignatária");
		// criar novo usuario desbloqueado
		usuarioService.criarUsuarioCsa("csa23", GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				"MASTER8036");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignatarias();
		// acessar lista usuarios
		acoesUsuarioPage.clicarOpcoesConsignatarias("8036");
		acoesUsuarioPage.clicarUsuarios();

		// pesquisar usuario por nome
		manutencaoConsignantePage.filtroPerfil("DENTAL UNI", "Nome");
		// verifica que exibe somente o usuario selecionado
		assertTrue(webDriver.getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
		assertFalse(webDriver.getPageSource().contains("AUTOMACAO"));

		manutencaoConsignantePage.filtroPerfil("AUTOMACAO", "Nome");
		// verifica que exibe somente o usuario selecionado
		assertFalse(webDriver.getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
		assertTrue(webDriver.getPageSource().contains("AUTOMACAO"));

		// pesquisar usuario por Usuario
		manutencaoConsignantePage.filtroPerfil("MASTER8036", "Usuário");
		// verifica que exibe somente o usuario selecionado
		assertTrue(webDriver.getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
		assertFalse(webDriver.getPageSource().contains("AUTOMACAO"));

		// pesquisar usuario por bloqueado
		manutencaoConsignantePage.filtroPerfil("", "Bloqueado");
		// verifica que exibe somente o usuario selecionado
		assertTrue(webDriver.getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
		assertFalse(webDriver.getPageSource().contains("AUTOMACAO"));

		// pesquisar usuario por desbloqueado
		manutencaoConsignantePage.filtroPerfil("", "Desbloqueado");
		// verifica que exibe somente o usuario selecionado
		assertFalse(webDriver.getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
		assertTrue(webDriver.getPageSource().contains("AUTOMACAO"));

		// pesquisar usuario por perfil
		manutencaoConsignantePage.filtroPerfil("", "Personalizado");
		// verifica que exibe somente o usuario selecionado
		assertTrue(webDriver.getPageSource().contains("DENTAL UNI - COOPERATIVA ODONTOLOGICA"));
		assertTrue(webDriver.getPageSource().contains("AUTOMACAO"));
	}

	@Test
	public void bloquearUsuarioConsignatariaLogadoComUsuarioCSA() {
		log.info("Bloquear usuario consignataria logado com usuario CSA");
		String usuarioCsa = "csa31";
		// criar novo usuario para bloquear
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCsa2.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCsa2);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignataria();
		// bloquear usuario
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o bloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário bloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, "cse12345");

		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void tentarBloquearOProprioUsuarioConsignataria() {
		log.info("Tentar bloquear usuario o proprio usuario consignataria");

		loginPage.loginSimples(loginCsa2);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignataria();
		// bloquear usuario
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(loginCsa2.getLogin());
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o bloqueio do usuário \"CSA\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("O USUÁRIO NÃO PODE SER BLOQUEADO POR ELE PRÓPRIO.", econsigHelper.getMensagemErro(webDriver));

	}

	@Test
	public void desbloquearUsuarioConsignatariaLogadoComUsuarioCSA() {
		log.info("Desbloquear usuario consignataria logado com usuario CSA");
		String usuarioCsa = "csa32";
		// criar novo usuario bloqueado para desbloquear
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_BLOQUEADO,
				loginCsa2.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCsa2);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignataria();
		// desbloquear usuario
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o desbloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário desbloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, "cse12345");

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void tentarDesbloquearUsuarioConsignatariaComCSAQueFoiBloqueadoPeloCSE() {
		log.info("Tentar desbloquear usuario consignataria com CSA que foi bloqueado pelo CSE");
		String usuarioCsa = "csa33";
		// criar novo usuario bloqueado para desbloquear
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true),
				CodedValues.STU_BLOQUEADO_POR_CSE, loginCsa2.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCsa2);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignataria();
		// desbloquear usuario
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o desbloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("NÃO É POSSÍVEL DESBLOQUEAR O USUÁRIO 'CSA33', POIS ELE FOI BLOQUEADO PELO CONSIGNANTE.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void reiniciarSenhaDoUsuarioConsignatariaLogadoComUsuarioCSA() {
		log.info("Reiniciar Senha do Usuario consignataria logado com usuario CSA");
		String usuarioCsa = "csa34";
		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCsa2.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCsa2);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignataria();
		// reiniciar senha
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void excluirUsuarioConsignatariaLogadoComUsuarioCSA() {
		log.info("Excluir Usuario consignataria logado com usuario CSA");
		String usuarioCsa = "csa35";
		// criar usuario para excluir
		usuarioService.criarUsuarioCsa(usuarioCsa, GeradorDocumentoHelper.gerarCPF(true), CodedValues.STU_ATIVO,
				loginCsa2.getLogin());
		// loga no sistema
		loginPage.loginSimples(loginCsa2);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuConsignataria();
		// acessar lista usuarios
		acoesUsuarioPage.clicarAcoes();
		acoesUsuarioPage.clicarUsuarios();
		// excluir usuario
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa);
		acoesUsuarioPage.clicarExcluir();

		assertEquals("Confirma a exclusão do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário removido com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// acessar o usuario para verificar os campos desabilitados
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioCsa + "(*)");
		acoesUsuarioPage.clicarEditar();

		usuarioPage.verificarCamposDesabilitados();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCsa, "cse12345");

		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
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
