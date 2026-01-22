package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class UsuarioOrgaoTest extends BaseTest {

	private LoginPage loginPage;
	private MenuPage menuPage;
	private UsuarioPage usuarioPage;
	private AcoesUsuarioPage acoesUsuarioPage;
	private RecuperarSenhaPage recuperarSenhaPage;

	private final LoginInfo loginCse = LoginValues.cse1;
	private String senhaNovoUsuario = null;

	@Autowired
	private ItemMenuFavoritoService itemMenuFavoritoService;

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

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSE_ORG, "N");

		// inclui o menu orgao
		itemMenuFavoritoService.excluirItemMenuFavoritos();
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.ORGAOS.getCodigo()));
	}

	@AfterEach
	public void afterEach() throws Exception {
		super.tearDown();
		alterarParametroNivelSegurancaSenha("3", "N", "8", "12");
	}

	@Test
	public void criarUsuarioOrgaoComSenhaNivelSegurancaMedioSenhaNaTela() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio e com IP e Endereço Acesso");

		String novoUsuario = "org30";
		alterarParametroNivelSegurancaSenha("3", "N", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();

		// criar novo usuario
		acoesUsuarioPage.clicarOpcoesOrgao("0001");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherUsuarioLogin(novoUsuario);
		usuarioPage.preencherEmail("teste@gmail.com");
		usuarioPage.incluirIPsAcessoAtual();
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.preencherDataValido("01/02/2029");
		usuarioPage.selecionarPerfil("Personalizado");
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"ATENÇÃO: este usuário destina-se exclusivamente à utilização pelos gestores do sistema, não devendo ser disponibilizado às consignatárias. Para criar um usuário de consignatária, utilize a área de manutenção de consignatárias, incluindo o novo usuário associado a uma entidade existente.",
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
	public void criarUsuarioOrgaoComSenhaNivelSegurancaMedioSenhaViaEmail() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio com envio do link via email");

		String novoUsuario = "org10";
		String senha = "Cse12345";

		alterarParametroNivelSegurancaSenha("3", "S", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();

		// criar novo usuario
		acoesUsuarioPage.clicarOpcoesOrgao("0001");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherUsuarioLogin(novoUsuario);
		usuarioPage.preencherEmail("testeorgao3@gmail.com");
		usuarioPage.selecionarPerfil("Personalizado");
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"ATENÇÃO: este usuário destina-se exclusivamente à utilização pelos gestores do sistema, não devendo ser disponibilizado às consignatárias. Para criar um usuário de consignatária, utilize a área de manutenção de consignatárias, incluindo o novo usuário associado a uma entidade existente.",
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
	public void criarUsuarioOrgaoComSenhaNivelSegurancaMuitoAltoSenhaNaTela() {
		log.info("Criar novo usuário com senha com nivel de segurança Muito Alto e com IP e Endereço Acesso");

		String novoUsuario = "org31";

		alterarParametroNivelSegurancaSenha("5", "N", "8", "12");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();

		// criar novo usuario
		acoesUsuarioPage.clicarOpcoesOrgao("0001");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherUsuarioLogin(novoUsuario);
		usuarioPage.preencherEmail("testeorgao2@gmail.com");
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"ATENÇÃO: este usuário destina-se exclusivamente à utilização pelos gestores do sistema, não devendo ser disponibilizado às consignatárias. Para criar um usuário de consignatária, utilize a área de manutenção de consignatárias, incluindo o novo usuário associado a uma entidade existente.",
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
	public void criarUsuarioOrgaoComSenhaNivelSegurancaMuitoAltoSenhaViaEmail() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio com envio do link via email");

		String novoUsuario = "org32";
		String senha = "yw@526398Ad&";

		alterarParametroNivelSegurancaSenha("5", "S", "8", "12");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();

		// criar novo usuario
		acoesUsuarioPage.clicarOpcoesOrgao("0001");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherUsuarioLogin(novoUsuario);
		usuarioPage.preencherEmail("testeorgao1@gmail.com");
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"ATENÇÃO: este usuário destina-se exclusivamente à utilização pelos gestores do sistema, não devendo ser disponibilizado às consignatárias. Para criar um usuário de consignatária, utilize a área de manutenção de consignatárias, incluindo o novo usuário associado a uma entidade existente.",
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
		recuperarSenhaPage.preencherSenha("12org345");
		recuperarSenhaPage.preencherConfirmarSenha("12org345");
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
	public void tentarCriarNovoUsuarioOrgaoComCpfJaCadastrado() {
		log.info("Tentar criar novo usuário com CPF já cadastrado");
		String usuarioOrg = "org20";
		// criar usuario para excluir
		usuarioService.criarUsuarioOrg(usuarioOrg, "377.973.790-68", "1", "org");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();

		// criar novo usuario
		acoesUsuarioPage.clicarOpcoesOrgao("0001");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("org21", "377.973.790-68");
		usuarioPage.preencherUsuarioLogin("org21");
		usuarioPage.preencherEmail("teste@gmail.com");
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"ATENÇÃO: este usuário destina-se exclusivamente à utilização pelos gestores do sistema, não devendo ser disponibilizado às consignatárias. Para criar um usuário de consignatária, utilize a área de manutenção de consignatárias, incluindo o novo usuário associado a uma entidade existente.",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemErro(webDriver).contains(
				"Não foi possível realizar esta operação pois existe usuário gestor com o mesmo número de CPF."));

	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMedioSenhaNaTela() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha na tela");
		String usuarioOrg = "org19";

		alterarParametroNivelSegurancaSenha("3", "N", "4", "8");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioOrg(usuarioOrg, GeradorDocumentoHelper.gerarCPF(true), "1", "org");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();
		// excluir usuario
		acoesUsuarioPage.clicarOpcoesOrgao("213464140");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioOrg);
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoes(usuarioOrg, "0");
		acoesUsuarioPage.clicarReiniciarSenha();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Reinicializar a senha do usuário \"" + usuarioOrg + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioOrg, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMedioSenhaViaEmail() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha via email");
		String usuarioOrg = "org24";
		String senha = "Adf85296";

		alterarParametroNivelSegurancaSenha("3", "S", "4", "8");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioOrg(usuarioOrg, GeradorDocumentoHelper.gerarCPF(true), "1", "org");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();
		// excluir usuario
		acoesUsuarioPage.clicarOpcoesOrgao("213464140");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioOrg);
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoes(usuarioOrg, "0");
		acoesUsuarioPage.clicarReiniciarSenha();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Reinicializar a senha do usuário \"" + usuarioOrg + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains(
				"Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(usuarioOrg);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(usuarioOrg);
		recuperarSenhaPage.preencherSenha("123456");
		recuperarSenhaPage.preencherConfirmarSenha("123456");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(usuarioOrg);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioOrg, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Automacao, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMuitoAltoSenhaNaTela() {
		log.info("Reiniciar Senha com nivel seguranca Muito Alto Senha na tela");
		String usuarioOrg = "org25";

		alterarParametroNivelSegurancaSenha("5", "N", "8", "12");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioOrg(usuarioOrg, GeradorDocumentoHelper.gerarCPF(true), "1", "org");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();
		// excluir usuario
		acoesUsuarioPage.clicarOpcoesOrgao("213464140");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioOrg);
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoes(usuarioOrg, "0");
		acoesUsuarioPage.clicarReiniciarSenha();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Reinicializar a senha do usuário \"" + usuarioOrg + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioOrg, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMuitoAltoSenhaViaEmail() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha via email");
		String usuarioOrg = "org22";
		String senha = "AS$123456Ad&";

		alterarParametroNivelSegurancaSenha("5", "S", "8", "12");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioOrg(usuarioOrg, GeradorDocumentoHelper.gerarCPF(true), "1", "org");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();
		// excluir usuario
		acoesUsuarioPage.clicarOpcoesOrgao("213464140");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioOrg);
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoes(usuarioOrg, "0");
		acoesUsuarioPage.clicarReiniciarSenha();
		usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
		usuarioPage.preencherObservacao("Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Reinicializar a senha do usuário \"" + usuarioOrg + "\"?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains(
				"Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(usuarioOrg);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(usuarioOrg);
		recuperarSenhaPage.preencherSenha("cse12345");
		recuperarSenhaPage.preencherConfirmarSenha("cse12345");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(usuarioOrg);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioOrg, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Automacao, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void tentarCriarNovoUsuarioOrgaoComLoginJaCadastrado() {
		log.info("Tentar criar novo usuário com Login já cadastrado");
		String usuarioOrg = "org12";
		// criar usuario para excluir
		usuarioService.criarUsuarioOrg(usuarioOrg, GeradorDocumentoHelper.gerarCPF(true), "1", "org");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();

		// criar novo usuario
		acoesUsuarioPage.clicarOpcoesOrgao("0001");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("org12", GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherUsuarioLogin("org12");
		usuarioPage.preencherEmail("teste@gmail.com");
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"Foram atribuídas funções do perfil administrador para o usuário. Deseja continuar com a operação?",
				econsigHelper.getMensagemPopUp(webDriver));

		assertEquals(
				"ATENÇÃO: este usuário destina-se exclusivamente à utilização pelos gestores do sistema, não devendo ser disponibilizado às consignatárias. Para criar um usuário de consignatária, utilize a área de manutenção de consignatárias, incluindo o novo usuário associado a uma entidade existente.",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemErro(webDriver).contains(
				"Não foi possível criar este usuário, pois já existe outro com o mesmo login cadastrado no sistema."));

	}

	@Test
	public void tentarCriarNovoUsuarioOrgaoComDoisIPsDeAcessoIguais() {
		log.info("Tentar criar novo usuario Cse com dois IPs de Acesso iguais");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();

		// criar novo usuario
		acoesUsuarioPage.clicarOpcoesOrgao("0001");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("org13", GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherUsuarioLogin("org13");
		usuarioPage.preencherEmail("teste@gmail.com");
		usuarioPage.incluirIPsAcessoAtual();
		usuarioPage.clicarIncluirIPsAcessoAtual();
		usuarioPage.clicarIncluirIPsAcesso();

		assertEquals("Este endereço já foi inserido na lista.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void tentarCriarNovoUsuarioOrgaoIncluindoIPdeAcessoInvalido() {
		log.info("Tentar criar novo usuário incluindo IP de acesso invalido");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();

		// tentar criar novo usuario
		acoesUsuarioPage.clicarOpcoesOrgao("0001");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("org14", GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherUsuarioLogin("org14");
		usuarioPage.preencherEmail("teste@gmail.com");
		// inclui ip vazio
		usuarioPage.clicarIncluirIPsAcesso();
		assertEquals("Número de IP inválido.", econsigHelper.getMensagemPopUp(webDriver));
		// inclui IP inválido
		usuarioPage.preencherIPsAcessoAtual("12");
		assertEquals("Número de IP inválido.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void tentarCriarNovoUsuarioOrgaoComDoisEnderecoDeAcessoIguais() {
		log.info("Tentar criar novo usuário com dois endereço de acesso iguais");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();

		// tentar criar novo usuario
		acoesUsuarioPage.clicarOpcoesOrgao("0001");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("org15", GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherUsuarioLogin("org15");
		usuarioPage.preencherEmail("teste@gmail.com");
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.preencherEnderecoAcesso("localhost");

		assertEquals("Este endereço já foi inserido na lista.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void excluirUsuarioOrgaoComSucesso() {
		log.info("Excluir Usuario com sucesso");
		String usuarioOrg = "org16";
		// criar usuario para excluir
		usuarioService.criarUsuarioOrg(usuarioOrg, GeradorDocumentoHelper.gerarCPF(true), "1", "org");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();
		// excluir usuario
		acoesUsuarioPage.clicarOpcoesOrgao("213464140");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioOrg);
		acoesUsuarioPage.clicarExcluir();
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Observacao Automacao excluir usuário");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma a exclusão do usuário \"org16\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário removido com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		assertTrue(webDriver.getPageSource().contains("Usuário removido com sucesso."));

		// acessar o usuario para verificar os campos desabilitados
		acoesUsuarioPage.clicarOpcoesUsuarios(usuarioOrg + "(*)");
		acoesUsuarioPage.clicarEditar();

		usuarioPage.verificarCamposDesabilitados();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioOrg, "cse12345");

		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void tentarExcluirUsuarioComConfiguracoesAuditoria() {
		log.info("Tentar excluir usuario com configurações de auditoria");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarMenuManutencao();
		menuPage.acessarItemMenuOrgao();
		// tentar criar usuario
		acoesUsuarioPage.clicarOpcoesOrgao("213464140");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		acoesUsuarioPage.clicarOpcoesUsuarios("org");
		acoesUsuarioPage.clicarExcluir();
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Observacao Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals("Confirma a exclusão do usuário \"org\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("USUÁRIO NÃO PODE SER REMOVIDO, POIS A ENTIDADE POSSUI CONFIGURAÇÕES DE AUDITORIA.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void editarUsuarioOrgaoComSucesso() {
		log.info("Editar Usuario com sucesso");
		// criar usuario para excluir
		usuarioService.criarUsuarioOrg("org17", GeradorDocumentoHelper.gerarCPF(true), "1", "org");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosOrgao();
		// listar usuarios
		acoesUsuarioPage.clicarOpcoesOrgao("213464140");
		acoesUsuarioPage.clicarListarUsuariosOrg();
		// filtrar
		usuarioPage.filtroUsuario("org17", "Usuário");
		usuarioPage.clicarPesquisar();
		// editar
		acoesUsuarioPage.clicarOpcoesUsuarios("org17");
		acoesUsuarioPage.clicarEditar();
		usuarioPage.preencherUsuarioLogin("org17");
		usuarioPage.preencherEmail("teste@gmail.com");
		usuarioPage.preencherDicaSenha("Senha Automacao");
		usuarioPage.incluirIPsAcessoAtual();
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"ATENÇÃO: este usuário destina-se exclusivamente à utilização pelos gestores do sistema, não devendo ser disponibilizado às consignatárias. Para criar um usuário de consignatária, utilize a área de manutenção de consignatárias, incluindo o novo usuário associado a uma entidade existente.",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Alterações salvas com sucesso."));
	}

	private void alterarParametroNivelSegurancaSenha(String nivelSeguranca, String enviaEmail, String tamMin,
			String tamMax) {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL,
				nivelSeguranca);
		parametroSistemaService
				.configurarParametroSistemaCse(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSE_ORG, enviaEmail);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_EMAIL_REINICIALIZACAO_SENHA, enviaEmail);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MIN_SENHA_USUARIOS, tamMin);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, tamMax);

		EConsigInitializer.limparCache();
	}
}
