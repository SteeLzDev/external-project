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
public class UsuarioConsignanteTest extends BaseTest {

	private LoginPage loginPage;
	private MenuPage menuPage;
	private UsuarioPage usuarioPage;
	private AcoesUsuarioPage acoesUsuarioPage;
	private ManutencaoConsignantePage manutencaoConsignantePage;
	private RecuperarSenhaPage recuperarSenhaPage;

	private String senhaNovoUsuario = null;
	private final LoginInfo loginCse = LoginValues.cse1;

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

		// inclui o relatorio como favorito
		itemMenuFavoritoService.incluirItemMenuFavorito(usuarioService.getUsuario(loginCse.getLogin()).getUsuCodigo(),
				Integer.toString(ItemMenuEnum.CONSIGNANTE.getCodigo()));
	}

	@AfterEach
	public void afterEach() throws Exception {
		super.tearDown();
		alterarParametroNivelSegurancaSenha("3", "N", "8", "12");
	}

	@Test
	public void criarUsuarioConsignanteComSenhaNivelSegurancaMedioSenhaNaTela() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio e com IP e Endereço Acesso");

		String novoUsuario = "cse10";
		alterarParametroNivelSegurancaSenha("3", "N", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// criar novo usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		usuarioPage.isExigeCertificadoDigital(true);
		usuarioPage.preencherDataValido("01/02/2029");
		usuarioPage.selecionarPerfil("MASTER");
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
	public void criarUsuarioConsignanteComSenhaNivelSegurancaMedioSenhaViaEmail() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio com envio do link via email");

		String novoUsuario = "csea12";
		String senha = "Cse12345";

		alterarParametroNivelSegurancaSenha("3", "S", "4", "8");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// criar novo usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.incluirIPsAcessoAtual();
		usuarioPage.isExigeCertificadoDigital(true);
		usuarioPage.preencherDataValido("01/02/2039");
		usuarioPage.selecionarPerfil("MASTER");
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
	public void criarUsuarioConsignanteComSenhaNivelSegurancaMuitoAltoSenhaNaTela() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio e com IP e Endereço Acesso");

		String novoUsuario = "cse14";

		alterarParametroNivelSegurancaSenha("5", "N", "8", "12");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// criar novo usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.selecionarPerfil("Perfil sem funcao para usuario");
		acoesUsuarioPage.clicarSalvar();

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
	public void criarUsuarioConsignanteComSenhaNivelSegurancaMuitoAltoSenhaViaEmail() {
		log.info("Criar novo usuário com senha com nivel de segurança Medio com envio do link via email");

		String novoUsuario = "cse16";
		String senha = "yw@526398Ad&";

		alterarParametroNivelSegurancaSenha("5", "S", "8", "12");

		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// criar novo usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario(novoUsuario, GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.selecionarPerfil("Perfil sem funcao para usuario");
		acoesUsuarioPage.clicarSalvar();

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
		String usuarioCse = "cse19";

		alterarParametroNivelSegurancaSenha("3", "N", "4", "8");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCse(usuarioCse, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoes(usuarioCse, "0");
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCse, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMedioSenhaViaEmail() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha via email");
		String usuarioCse = "cse20";
		String senha = "Adf85296";

		alterarParametroNivelSegurancaSenha("3", "S", "4", "8");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCse(usuarioCse, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoes(usuarioCse, "0");
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains(
				"Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(usuarioCse);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(usuarioCse);
		recuperarSenhaPage.preencherSenha("123456");
		recuperarSenhaPage.preencherConfirmarSenha("123456");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(usuarioCse);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCse, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Automacao, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMuitoAltoSenhaNaTela() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha na tela");
		String usuarioCse = "cse21";

		alterarParametroNivelSegurancaSenha("5", "N", "8", "12");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCse(usuarioCse, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoes(usuarioCse, "0");
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Senha reinicializada com sucesso."));

		senhaNovoUsuario = usuarioPage.getSenhaNova();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCse, senhaNovoUsuario);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void reiniciarSenhaComNivelSegurancaMuitoAltoSenhaViaEmail() {
		log.info("Reiniciar Senha com nivel seguranca Medio Senha via email");
		String usuarioCse = "cse22";
		String senha = "AS$123456Ad&";

		alterarParametroNivelSegurancaSenha("5", "S", "8", "12");

		// criar usuario para reiniciar senha
		usuarioService.criarUsuarioCse(usuarioCse, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();
		// reiniciar senha
		acoesUsuarioPage.clicarOpcoes(usuarioCse, "0");
		acoesUsuarioPage.clicarReiniciarSenha();

		assertEquals("Reinicializar a senha do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));
		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains(
				"Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha."));

		// Busca código de recuperação de senha
		final Usuario usuario = usuarioService.getUsuario(usuarioCse);
		assertNotNull(usuario);
		assertNotNull(usuario.getUsuChaveRecuperarSenha());

		recuperarSenhaPage.acessarTelaValidarSenhaUsuarioCSE(usuario.getUsuChaveRecuperarSenha());
		assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains(
				"A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

		recuperarSenhaPage.preencherUsuario(usuarioCse);
		recuperarSenhaPage.preencherSenha("cse12345");
		recuperarSenhaPage.preencherConfirmarSenha("cse12345");
		recuperarSenhaPage.preencherCaptcha();
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		recuperarSenhaPage.preencherUsuario(usuarioCse);
		recuperarSenhaPage.preencherSenha(senha);
		recuperarSenhaPage.preencherConfirmarSenha(senha);
		recuperarSenhaPage.clicarBotaoConfirmar();
		assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.",
				econsigHelper.getMensagemSucesso(webDriver));

		// logar com o usuario criado
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCse, senha);

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertEquals("Prezado Automacao, enviamos uma confirmação para o email " + usuario.getUsuEmail()
				+ ". Gentileza confirmá-lo para continuar.", usuarioPage.getMensagemSucesso());
	}

	@Test
	public void tentarCriarNovoUsuarioConsignanteComCPFjaCadastrado() {
		log.info("Tentar criar novo usuário com CPF já cadastrado");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// criar novo usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("cse121", "612.044.145-05");
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
	public void tentarCriarNovoUsuarioConsignanteComLoginJaCadastrado() {
		log.info("Tentar criar novo usuário com Login já cadastrado");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// tentar criar novo usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("cse", GeradorDocumentoHelper.gerarCPF(true));
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
	public void tentarCriarNovoUsuarioConsignanteComDoisIPsDeAcessoIguais() {
		log.info("Tentar criar novo usuario Cse com dois IPs de Acesso iguais");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// tentar criar novo usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("cse13", GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.incluirIPsAcessoAtual();
		usuarioPage.clicarIncluirIPsAcessoAtual();
		usuarioPage.clicarIncluirIPsAcesso();

		assertEquals("Este endereço já foi inserido na lista.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void tentarCriarNovoUsuarioConsignanteIncluindoIPdeAcessoInvalido() {
		log.info("Tentar criar novo usuário incluindo IP de acesso invalido");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// tentar criar novo usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("cse14", GeradorDocumentoHelper.gerarCPF(true));
		// inclui ip vazio
		usuarioPage.clicarIncluirIPsAcesso();
		assertEquals("Número de IP inválido.", econsigHelper.getMensagemPopUp(webDriver));

		usuarioPage.preencherIPsAcessoAtual("12");
		assertEquals("Número de IP inválido.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void tentarCriarNovoUsuarioConsignanteComDoisEnderecoDeAcessoIguais() {
		log.info("Tentar criar novo usuário com dois endereço de acesso iguais");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// tentar criar novo usuario
		usuarioPage.clicarCriarNovoUsuario();
		usuarioPage.preencherDadosUsuario("cse15", GeradorDocumentoHelper.gerarCPF(true));
		usuarioPage.preencherEnderecoAcesso("localhost");
		usuarioPage.preencherEnderecoAcesso("localhost");

		assertEquals("Este endereço já foi inserido na lista.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void bloquearUsuarioConsignanteComSucesso() {
		log.info("Bloquear Usuario com sucesso");
		String usuarioCse = "cse17";
		// criar novo usuario para bloquear
		usuarioService.criarUsuarioCse(usuarioCse, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// bloquear usuario
		acoesUsuarioPage.clicarOpcoes(usuarioCse, "0");
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o bloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário bloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCse, "cse12345");

		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void desbloquearUsuarioConsignanteComSucesso() {
		log.info("Desbloquear Usuario com sucesso");
		String usuarioCse = "cse18";
		// criar novo usuario bloqueado para desbloquear
		usuarioService.criarUsuarioCse(usuarioCse, GeradorDocumentoHelper.gerarCPF(true), "4");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();

		// desbloquear usuario
		acoesUsuarioPage.clicarOpcoes(usuarioCse, "0");
		acoesUsuarioPage.clicarBloquearDesbloquear();

		assertEquals("Confirma o desbloqueio do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário desbloqueado com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCse, "cse12345");

		// valida email
		assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", usuarioPage.getMensagemaAlerta());
		usuarioPage.clicarValidar();
		assertTrue(
				usuarioPage.getMensagemSucesso().contains("Prezado Automacao, enviamos uma confirmação para o email "));
	}

	@Test
	public void exibirHistoricoDoUsuarioConsignanteComSucesso() {
		log.info("Exibir Historico do Usuario com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();
		// exibir historico
		acoesUsuarioPage.clicarOpcoes("cse", "0");
		acoesUsuarioPage.clicarExibirHistorico();

		econsigHelper.verificaTextoPagina(webDriver, "Aceitação do Termo de Uso");
		assertTrue(webDriver.getPageSource().contains("127.0.0.1"));
		assertTrue(webDriver.getPageSource().contains("Carlota Joaquina"));
		assertTrue(webDriver.getPageSource().contains("Usuário Suporte ZetraSoft"));
		assertTrue(webDriver.getPageSource().contains("cse"));
		assertTrue(webDriver.getPageSource().contains("612.044.145-05"));
	}

	@Test
	public void excluirUsuarioConsignanteComSucesso() {
		log.info("Excluir Usuario com sucesso");
		String usuarioCse = "cse20";
		// criar usuario para excluir
		usuarioService.criarUsuarioCse(usuarioCse, GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();
		// excluir usuario
		acoesUsuarioPage.clicarOpcoes(usuarioCse, "0");
		acoesUsuarioPage.clicarExcluir();

		assertEquals("Confirma a exclusão do usuário \"AUTOMACAO\"?", econsigHelper.getMensagemPopUp(webDriver));

		assertEquals("Usuário removido com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// acessar o usuario para verificar os campos desabilitados
		acoesUsuarioPage.clicarOpcoes(usuarioCse + "(*)", "0");
		acoesUsuarioPage.clicarEditar();

		usuarioPage.verificarCamposDesabilitados();

		// tentar logar
		loginPage.acessarTelaLogin();
		loginPage.login(usuarioCse, "cse12345");

		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void editarUsuarioConsignanteComSucesso() {
		log.info("Editar Usuario com sucesso");
		// criar usuario para excluir
		usuarioService.criarUsuarioCse("cse21", GeradorDocumentoHelper.gerarCPF(true), "1");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();
		// editar usuario
		acoesUsuarioPage.clicarOpcoes("cse21", "0");
		acoesUsuarioPage.clicarEditar();
		usuarioPage.preencherDicaSenha("Senha Automacao");
		usuarioPage.incluirIPsAcessoAtual();
		usuarioPage.selecionarMotivoOperacao("Outros");
		usuarioPage.preencherObservacao("Observacao Automacao");
		acoesUsuarioPage.clicarSalvar();

		assertEquals(
				"ATENÇÃO: este usuário destina-se exclusivamente à utilização pelos gestores do sistema, não devendo ser disponibilizado às consignatárias. Para criar um usuário de consignatária, utilize a área de manutenção de consignatárias, incluindo o novo usuário associado a uma entidade existente.",
				econsigHelper.getMensagemPopUp(webDriver));

		assertTrue(econsigHelper.getMensagemSucesso(webDriver).contains("Alterações salvas com sucesso."));
	}

	@Test
	public void editarFuncoesComSucesso() {
		log.info("Editar funções com sucesso");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();
		// editar funcoes
		acoesUsuarioPage.clicarOpcoes("aut", "0");
		acoesUsuarioPage.clicarEditar();
		acoesUsuarioPage.clicarMaisAcoes();
		acoesUsuarioPage.clicarEditarFuncoes();
		usuarioPage.selecionarAlteracaoAvancadaConsignacao();
		usuarioPage.selecionarConfirmarSolicitacao();
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
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();
		// incluir as funções
		acoesUsuarioPage.clicarOpcoes("aut", "0");
		acoesUsuarioPage.clicarEditar();

		acoesUsuarioPage.clicarMaisAcoes();
		acoesUsuarioPage.clicarEditarRestricoesAcesso();
		usuarioPage.preencherFiltro("consultar margem");
		usuarioPage.selecionarTipoFiltro("Descrição");
		usuarioPage.clicarFiltrar();

		assertEquals("76", usuarioPage.getFuncao());
		assertEquals("Consultar Margem", usuarioPage.getDescriao());

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
	public void pesquisarUsuarioConsignante() {
		log.info("Pesquisar usuario consignante");
		// loga no sistema
		loginPage.loginSimples(loginCse);

		// acessa menu
		menuPage.acessarFavoritosConsignante();
		// acessar Listar usuários
		usuarioPage.clicarListarUsuarios();
		// pesquisar usuario
		manutencaoConsignantePage.filtroPerfil("AUTOMACAO", "Nome");

		// verifica que exibe somente o usuario selecionado
		assertTrue(webDriver.getPageSource().contains("AUTOMACAO"));
		assertFalse(webDriver.getPageSource().contains("USUÁRIO SUPORTE ZETRASOFT"));
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
