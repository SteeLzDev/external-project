package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.service.FuncaoSistemaService;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.values.CodedValues;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoginStep {

	@Autowired
	private FuncaoSistemaService funcaoSistemaService;

    private LoginPage loginPage;

    @Before
    public void setUp() throws Exception {
        loginPage = new LoginPage(getWebDriver());
    }

	@Dado("que o usuario CSE esteja logado")
	public void que_usuario_CSE_esteja_logado() throws Throwable {
		log.info("Dado que o usuário CSE esteja logado");

		loginPage.loginSimples(LoginValues.cse1);
	}

	@Dado("que o usuario cse ou sup {string} esteja logado")
	public void usuarioLogado(String usuario) throws Throwable {
		log.info("Dado que o usuário cse ou sup {} esteja logado", usuario);

		loginPage.login(usuario, LoginValues.cse1.getSenha());
	}

	@Dado("que o usuario csa {string} esteja logado")
	public void usuarioCsaEstejaLogado(String usuario) throws Throwable {
		log.info("Dado que o usuário csa {} esteja logado", usuario);

		loginPage.login(usuario, LoginValues.csa1.getSenha());
	}

	@Dado("que o usuario consignante Aut esteja logado")
	public void queUsuarioCseAutEstejaLogado() throws Throwable {
		log.info("Dado que o usuário CSE esteja logado");

		loginPage.loginSimples(LoginValues.cse2);
	}

	@Dado("que o usuario Suporte esteja logado")
	public void queUsuarioSuporteEstejaLogado() throws Throwable {
		log.info("Dado que o usuário Suporte esteja logado");

		loginPage.loginSimples(LoginValues.suporte);
	}

	@Dado("que o usuario Servidor esteja logado")
	public void queUsuarioServidorEstejaLogado() throws Throwable {
		log.info("Dado que o usuário Servidor esteja logado");

		loginPage.loginServidor(LoginValues.servidor1);
	}

	@Quando("UsuCsa Logado")
	public void usuCsaLogado() throws Throwable {
		log.info("Quando UsuCsa Logado");
		funcaoSistemaService.alteraExigeSegundaSenhaCsaFuncao(CodedValues.FUN_RES_MARGEM, "N");
		EConsigInitializer.limparCache();

		loginPage.acessarTelaLogin();
		loginPage.loginSimples(LoginValues.csa2);
	}

	@Quando("usuario correspondente {string} esteja logado")
	public void usuCorLogado(String usuario) throws Throwable {
		log.info("Quando usuário correspondente {} esteja logado", usuario);

		funcaoSistemaService.alteraExigeSegundaSenhaCorFuncao(CodedValues.FUN_RES_MARGEM, "N");
		EConsigInitializer.limparCache();

		loginPage.acessarTelaLogin();
		loginPage.login(usuario, LoginValues.cor1.getSenha());
	}

	@E("UsuCsa sem exigencia de segunda senha para confirmar solicitacao")
	public void naoExigeSegundaSenhaConfirmarSolicitacaoPapelCsa() {
		funcaoSistemaService.alteraExigeSegundaSenhaCsaFuncao(CodedValues.FUN_CONF_SOLICITACAO, "N");
		EConsigInitializer.limparCache();

	}
}
