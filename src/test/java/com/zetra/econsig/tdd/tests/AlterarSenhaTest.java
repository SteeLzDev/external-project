package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AlterarSenhaPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AlterarSenhaTest extends BaseTest {

	private LoginPage loginPage;
	private MenuPage menuPage;
	private AlterarSenhaPage alterarSenhaPage;

	private final LoginInfo loginCsa2 = LoginValues.csa2;
	private final LoginInfo loginCsa3 = LoginValues.csa3;
	private final LoginInfo loginCse2 = LoginValues.cse2;
	private final String senhaAlteradaNivelMedio = "Alt12345";
	private final String senhaAlteradaNivelMuitoAlto = "Cs@123456Ad&";

	@Autowired
	private EconsigHelper econsigHelper;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private UsuarioServiceTest usuarioService;

	@BeforeEach
	public void beforeEach() throws Exception {
		super.setUp();
		loginPage = new LoginPage(webDriver);
		menuPage = new MenuPage(webDriver);
		alterarSenhaPage = new AlterarSenhaPage(webDriver);
		

		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MIN_SENHA_USUARIOS, "8");
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, "12");
		EConsigInitializer.limparCache();
	}

	@AfterEach
	public void afterEach() throws Exception {
		super.tearDown();

		usuarioService.alterarSenhaUsuario(loginCsa3.getLogin(),
				"b850bae8f3bf720f52d5ea32003c0bba0d9b63f8b1ebd3bb8653982e39d3cdb9f1ac69056aa901e44cc38d9aac01171df75870b1871c3b40aa64ed341d370ccfea0107b5");
		usuarioService.alterarSenhaUsuario(loginCse2.getLogin(), "CSuHKShh7zktE");
		alterarParametroNivelSegurancaSenha("3");
	}

	@Test
	public void alterarSenhaUsuarioCSAComNivelSegurancaoMedio() throws InterruptedException {
		log.info("Alterar senha usuario CSA com Nível de Segurança Médio");

		alterarParametroNivelSegurancaSenha("3");

		loginPage.loginSimples(loginCsa3);

		// acessa Menu Sistema > Alterar Senha
		menuPage.acessarMenuSistema();
		menuPage.acessarItemMenuAlterarSenha();
		// executa a alteração
		alterarSenhaPage.alterarSenhaUsuario(loginCsa3.getSenha(), "12345678");
		assertEquals("Nível de segurança da nova senha: Baixo", alterarSenhaPage.retornarMensagemSeveridade());

		alterarSenhaPage.clicarSalvar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		alterarSenhaPage.alterarSenhaUsuario(loginCsa3.getSenha(), senhaAlteradaNivelMedio);
		alterarSenhaPage.clicarSalvar();
		assertEquals("Senha alterada com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// realizar novamente o login com a nova senha
		loginPage.acessarTelaLogin();
		loginPage.login(loginCsa3.getLogin(), senhaAlteradaNivelMedio);

		assertTrue(webDriver.getTitle().contains("eConsig - Principal"));
	}

	@Test
	public void alterarSenhaUsuarioCSAComNivelSegurancaoMuitoAlto() throws InterruptedException {
		log.info("Alterar senha usuario CSA com Nível de Segurança Muito Alto");

		alterarParametroNivelSegurancaSenha("5");

		loginPage.loginSimples(loginCsa3);

		// acessa Menu Sistema > Alterar Senha
		menuPage.acessarMenuSistema();
		menuPage.acessarItemMenuAlterarSenha();
		// tenta executar a alteração
		alterarSenhaPage.alterarSenhaUsuario(loginCsa3.getSenha(), "cse12345");
		assertEquals("Nível de segurança da nova senha: Baixo", alterarSenhaPage.retornarMensagemSeveridade());

		alterarSenhaPage.clicarSalvar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		alterarSenhaPage.alterarSenhaUsuario(loginCsa3.getSenha(), senhaAlteradaNivelMuitoAlto);
		alterarSenhaPage.clicarSalvar();

		assertEquals("Senha alterada com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// realizar novamente o login com a nova senha
		loginPage.acessarTelaLogin();
		loginPage.login(loginCsa3.getLogin(), senhaAlteradaNivelMuitoAlto);

		assertTrue(webDriver.getTitle().contains("eConsig - Principal"));
	}

	@Test
	public void alterarSenhaUsuarioCSEComNivelSegurancaoMuitoAlto() throws InterruptedException {
		log.info("Alterar senha usuario CSE com Nível de Segurança Muito Alto");

		alterarParametroNivelSegurancaSenha("5");

		loginPage.loginSimples(loginCse2);

		// acessa Menu Sistema > Alterar Senha
		menuPage.acessarMenuSistema();
		menuPage.acessarItemMenuAlterarSenha();
		// executa a alteração
		alterarSenhaPage.alterarSenhaUsuario(loginCse2.getSenha(), "Csecse1234");
		assertEquals("Nível de segurança da nova senha: Alto", alterarSenhaPage.retornarMensagemSeveridade());

		alterarSenhaPage.clicarSalvar();
		assertTrue(econsigHelper.getMensagemPopUp(webDriver)
				.contains("A senha informada não atende a requisitos mínimos de segurança."));

		alterarSenhaPage.alterarSenhaUsuario(loginCse2.getSenha(), senhaAlteradaNivelMuitoAlto);
		alterarSenhaPage.clicarSalvar();

		assertEquals("Senha alterada com sucesso.", econsigHelper.getMensagemSucesso(webDriver));

		// realizar novamente o login com a nova senha
		loginPage.acessarTelaLogin();
		loginPage.login(loginCse2.getLogin(), senhaAlteradaNivelMuitoAlto);

		assertTrue(webDriver.getTitle().contains("eConsig - Principal"));
	}

	@Test
	public void tentarAlterarSenhaUsuarioSemInformarCamposObrigatorios() throws InterruptedException {
		log.info("Tentar alterar senha usuario sem informar campos obrigatórios");

		loginPage.loginSimples(loginCsa2);

		// acessa Menu Sistema > Alterar Senha
		menuPage.acessarMenuSistema();
		menuPage.acessarItemMenuAlterarSenha();
		// tentar alterar sem informar senha atual
		alterarSenhaPage.alterarSenhaUsuario("", senhaAlteradaNivelMedio);
		alterarSenhaPage.clicarSalvar();
		assertEquals("Favor preencher os campos: Senha Atual, Nova Senha e Confirma Nova Senha.",
				econsigHelper.getMensagemPopUp(webDriver));

		alterarSenhaPage.alterarSenhaUsuario(loginCsa2.getSenha(), "", senhaAlteradaNivelMedio);
		assertEquals("Favor preencher os campos: Senha Atual, Nova Senha e Confirma Nova Senha.",
				econsigHelper.getMensagemPopUp(webDriver));

		alterarSenhaPage.alterarSenhaUsuario(loginCsa2.getSenha(), senhaAlteradaNivelMedio, "");
		assertEquals("Favor preencher os campos: Senha Atual, Nova Senha e Confirma Nova Senha.",
				econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void tentarAlterarSenhaUsuarioComSenhaAtualIncorreto() throws InterruptedException {
		log.info("Tentar alterar senha usuario com senha atual incorreto");

		loginPage.loginSimples(loginCsa2);

		// acessa Menu Sistema > Alterar Senha
		menuPage.acessarMenuSistema();
		menuPage.acessarItemMenuAlterarSenha();
		// tentar alterar
		alterarSenhaPage.alterarSenhaUsuario("Senha123", senhaAlteradaNivelMuitoAlto);
		alterarSenhaPage.clicarSalvar();

		assertEquals("A senha atual informada é inválida.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void tentarAlterarSenhaUsuarioComNovaSenhaDiferenteConfirmarSenha() throws InterruptedException {
		log.info("Tentar alterar senha usuario com nova senha diferente de confirmar senha");

		loginPage.loginSimples(loginCsa2);

		// acessa Menu Sistema > Alterar Senha
		menuPage.acessarMenuSistema();
		menuPage.acessarItemMenuAlterarSenha();
		// executa a alteração
		alterarSenhaPage.alterarSenhaUsuario(loginCsa2.getSenha(), "senha111", senhaAlteradaNivelMedio);

		assertEquals("Os campos 'Nova Senha' e 'Confirma Senha' devem ter o mesmo valor.",
				econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void tentarAlterarSenhaUsuarioComNovaSenhaInvalido() throws InterruptedException {
		log.info("Tentar alterar senha usuario com nova senha invalido");

		alterarParametroNivelSegurancaSenha("5");

		loginPage.loginSimples(loginCsa2);

		// acessa Menu Sistema > Alterar Senha
		menuPage.acessarMenuSistema();
		menuPage.acessarItemMenuAlterarSenha();
		// executa a alteração
		alterarSenhaPage.alterarSenhaUsuario(loginCsa2.getSenha(), "csa12", "Csa12");

		assertEquals("A nova senha deve ter pelo menos 8 caracteres.", econsigHelper.getMensagemPopUp(webDriver));
	}

	private void alterarParametroNivelSegurancaSenha(String nivelSeguranca) {
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL,
				nivelSeguranca);
		parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_CSA_COR_PWD_STRENGTH_LEVEL,
				nivelSeguranca);
		EConsigInitializer.limparCache();
	}
}
