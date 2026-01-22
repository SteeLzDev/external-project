package com.zetra.econsig.tdd.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.LoginPage;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

	private final LoginInfo loginCse = LoginValues.cse1;
	private final LoginInfo loginCsa = LoginValues.csa2;
	private final LoginInfo loginSer = LoginValues.servidor1;
	private final LoginInfo loginCor = LoginValues.cor1;
	private final LoginInfo loginOrg = LoginValues.org1;
	private final LoginInfo loginSup = LoginValues.suporte;
	
	@Autowired
	private EconsigHelper econsigHelper;
	
	@Autowired
	private UsuarioServiceTest usuarioService;

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
        loginPage = new LoginPage(webDriver);
	}

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

	@Test
	public void fazerLoginComSucessoConsignante() throws Exception {
		log.info("Login Correto Consignante: {}", loginCse.getLogin());

		loginPage.loginSimples(loginCse);

		assertTrue(webDriver.getTitle().contains("eConsig - Principal"));
	}

	@Test
	public void fazerLoginComSucessoConsignataria() throws Exception {
		log.info("Login Correto Consignataria: {}", loginCsa.getLogin());

		loginPage.loginSimples(loginCsa);

		assertTrue(webDriver.getTitle().contains("eConsig - Principal"));
	}

	@Test
	public void fazerLoginComSucessoServidor() throws Exception {
		log.info("Login Correto Servidor: {}", loginSer.getLogin());

		loginPage.acessarTelaLoginServidor();
		loginPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47");
		loginPage.preencherUsuario(loginSer.getLogin());
		loginPage.preencherSenha(loginSer.getSenha());
		loginPage.clicarEntrar();

		assertTrue(webDriver.getTitle().contains("eConsig - Principal"));
	}

	@Test
	public void fazerLoginComUsuarioCSEInexistente() throws Exception {
		log.info("Login Com usuario CSE inexistente");

		loginPage.preencherUsuario("cse3");
		loginPage.preencherSenha(loginCse.getSenha());
		loginPage.clicarEntrar();

		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void fazerLoginComSenhaCSEIncorreto() throws Exception {
		log.info("Login com senha CSE incorreto");

		loginPage.preencherUsuario(loginCse.getSenha());
		loginPage.preencherSenha("cse654321");
		loginPage.clicarEntrar();

		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void fazerLoginComUsuarioServidorInexistente() throws Exception {
		log.info("Login com usuario servidor inexistente");

		loginPage.acessarTelaLoginServidor();
		loginPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47");
		loginPage.preencherUsuario("654321");
		loginPage.preencherSenha(loginSer.getSenha());
		loginPage.clicarEntrar();

		assertEquals(
				"ÓRGÃO, MATRÍCULA OU SENHA INVÁLIDOS. *** CERTIFIQUE-SE QUE SUA SENHA FOI ATUALIZADA NA PÁGINA DO CONTRACHEQUE.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void fazerLoginComSenhaServidorIncorreto() throws Exception {
		log.info("Login com senha servidor incorreto");

		loginPage.acessarTelaLoginServidor();
		loginPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47");
		loginPage.preencherUsuario("654321");
		loginPage.preencherSenha(loginSer.getSenha());
		loginPage.clicarEntrar();

		assertEquals(
				"ÓRGÃO, MATRÍCULA OU SENHA INVÁLIDOS. *** CERTIFIQUE-SE QUE SUA SENHA FOI ATUALIZADA NA PÁGINA DO CONTRACHEQUE.",
				econsigHelper.getMensagemErro(webDriver));
	}

	@Test
	public void fazerLoginSemInformarUsuario() throws Exception {
		log.info("Login sem informar usuario");

		loginPage.acessarTelaLogin();
		loginPage.preencherUsuario("");

		assertEquals("O usuário deve ser informado.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void fazerLoginSemInformarUsuarioServidor() throws Exception {
		log.info("Login sem informar usuario servidor");

		loginPage.acessarTelaLoginServidor();
		loginPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47");
		loginPage.preencherUsuario("");

		assertEquals("A matrícula deve ser informada.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void fazerLoginSemInformarOrgaoParaServidor() throws Exception {
		log.info("Login sem informar orgao para servidor");

		loginPage.acessarTelaLoginServidor();
		loginPage.selecionarOrgao("");
		loginPage.preencherUsuario(loginSer.getLogin());

		assertEquals("Selecione um estabelecimento.", econsigHelper.getMensagemPopUp(webDriver));
	}

	@Test
	public void fazerLoginComOrgaoIncorretoParaServidor() throws Exception {
		log.info("Login sem informar orgao para servidor");

		loginPage.acessarTelaLoginServidor();
		loginPage.selecionarOrgao("Estabelecimento Pomodori");
		loginPage.preencherUsuario(loginSer.getLogin());
		loginPage.preencherSenha(loginSer.getSenha());
		loginPage.clicarEntrar();

		assertEquals(
				"ÓRGÃO, MATRÍCULA OU SENHA INVÁLIDOS. *** CERTIFIQUE-SE QUE SUA SENHA FOI ATUALIZADA NA PÁGINA DO CONTRACHEQUE.",
				econsigHelper.getMensagemErro(webDriver));
	}

    @Test
	public void fazerLoginComUsuarioCseBloqueado() {
		log.info("Login Consignante Bloqueado: {}", loginCse.getLogin());
		
		// ajuste de status para "2", bloqueado
		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "2");
		loginPage.login(loginCse.getLogin(), loginCse.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioCorBloqueado() {
		log.info("Login Correspondente Bloqueado: {}", loginCor.getLogin());
		
		// ajuste de status para "2", bloqueado
		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "2");
		loginPage.login(loginCor.getLogin(), loginCor.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioOrgBloqueado() {
		log.info("Login Órgão Bloqueado: {}", loginOrg.getLogin());
		
		// ajuste de status para "2", bloqueado
		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "2");
		loginPage.login(loginOrg.getLogin(), loginOrg.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioSupBloqueado() {
		log.info("Login Suporte Bloqueado: {}", loginSup.getLogin());

		// ajuste de status para "2", bloqueado
		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "2");
		loginPage.login(loginSup.getLogin(), loginSup.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioCsaBloqueado() {
		log.info("Login Consignatário Bloqueado: {}", loginCsa.getLogin());
		
		// ajuste de status para "2", bloqueado
		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "2");
		loginPage.login(loginCsa.getLogin(), loginCsa.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioCseExcluido() {
		log.info("Login Consignante Excluído: {}", loginCse.getLogin());
		
		// ajuste de status para "3", excluído
		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "3");
		loginPage.login(loginCse.getLogin(), loginCse.getSenha());

		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioCorExcluido() {
		log.info("Login Correspondente Excluído: {}", loginCor.getLogin());
		
		// ajuste de status para "3", excluído
		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "3");
		loginPage.login(loginCor.getLogin(), loginCor.getSenha());
		
		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioOrgExcluido() {
		log.info("Login Órgão Excluído: {}", loginOrg.getLogin());
		
		// ajuste de status para "3", excluído
		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "3");
		loginPage.login(loginOrg.getLogin(), loginOrg.getSenha());
		
		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioSupExcluido() {
		log.info("Login Suporte Excluído: {}", loginSup.getLogin());

		// ajuste de status para "3", excluído
		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "3");
		loginPage.login(loginSup.getLogin(), loginSup.getSenha());
		
		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioCsaExcluido() {
		log.info("Login Consignatário Excluído: {}", loginCsa.getLogin());
		
		// ajuste de status para "3", excluído
		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "3");
		loginPage.login(loginCsa.getLogin(), loginCsa.getSenha());
		
		assertEquals("USUÁRIO OU SENHA INVÁLIDA.", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioCseBloqueadoPeloCse() {
		log.info("Login Consignante Bloqueado Pelo Consignante: {}", loginCse.getLogin());
		
		// ajuste de status para "4", bloqueado pelo consignante
		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "4");
		loginPage.login(loginCse.getLogin(), loginCse.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioCorBloqueadoPeloCse() {
		log.info("Login Correspondente Bloqueado Pelo Consignante: {}", loginCor.getLogin());
		
		// ajuste de status para "4", bloqueado pelo consignante
		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "4");
		loginPage.login(loginCor.getLogin(), loginCor.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioOrgBloqueadoPeloCse() {
		log.info("Login Órgão Bloqueado Pelo Consignante: {}", loginOrg.getLogin());
		
		// ajuste de status para "4", bloqueado pelo consignante
		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "4");
		loginPage.login(loginOrg.getLogin(), loginOrg.getSenha());
		log.info("a");
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioSupBloqueadoPeloCse() {
		log.info("Login Suporte Bloqueado Pelo Consignante: {}", loginSup.getLogin());

		// ajuste de status para "4", bloqueado pelo consignante
		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "4");
		loginPage.login(loginSup.getLogin(), loginSup.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioCsaBloqueadoPeloCse() {
		log.info("Login Consignatário Bloqueado Pelo Consignante: {}", loginCsa.getLogin());

		// ajuste de status para "4", bloqueado pelo consignante
		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "4");
		loginPage.login(loginCsa.getLogin(), loginCsa.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "1");
	}
    
    @Test
	public void fazerLoginComUsuarioCseBloqueadoAutomaticamente() {
		log.info("Login Consignante Bloqueado Automaticamente: {}", loginCse.getLogin());
		
		// ajuste de status para "5", bloqueado automaticamente
		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "5");
		loginPage.login(loginCse.getLogin(), loginCse.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioCorBloqueadoAutomaticamente() {
		log.info("Login Correspondente Bloqueado Automaticamente: {}", loginCor.getLogin());
		
		// ajuste de status para "5", bloqueado automaticamente
		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "5");
		loginPage.login(loginCor.getLogin(), loginCor.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioOrgBloqueadoAutomaticamente() {
		log.info("Login Órgão Bloqueado Automaticamente: {}", loginOrg.getLogin());
		
		// ajuste de status para "5", bloqueado automaticamente
		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "5");
		loginPage.login(loginOrg.getLogin(), loginOrg.getSenha());
		log.info("a");
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioSupBloqueadoAutomaticamente() {
		log.info("Login Suporte Bloqueado Automaticamente: {}", loginSup.getLogin());

		// ajuste de status para "5", bloqueado automaticamente
		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "5");
		loginPage.login(loginSup.getLogin(), loginSup.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "1");
	}
	
	@Test
	public void fazerLoginComUsuarioCsaBloqueadoAutomaticamente() {
		log.info("Login Consignatário Bloqueado Automaticamente: {}", loginCsa.getLogin());

		// ajuste de status para "5", bloqueado automaticamente
		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "5");
		loginPage.login(loginCsa.getLogin(), loginCsa.getSenha());
		
		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
		
		// retorno do status para "1", ativo
		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "1");
	}
    
    @Test
   	public void fazerLoginComUsuarioCseBloqueadoAutomaticamentePorSeguranca() {
   		log.info("Login Consignante Bloqueado Automaticamente Por Segurança: {}", loginCse.getLogin());
   		
   		// ajuste de status para "7", bloqueado automaticamente por segurança
   		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "7");
   		loginPage.login(loginCse.getLogin(), loginCse.getSenha());
   		
   		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
   		
   		// retorno do status para "1", ativo
   		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "1");
   	}
   	
   	@Test
   	public void fazerLoginComUsuarioCorBloqueadoAutomaticamentePorSeguranca() {
   		log.info("Login Correspondente Bloqueado Automaticamente Por Segurança: {}", loginCor.getLogin());
   		
   		// ajuste de status para "7", bloqueado automaticamente por segurança
   		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "7");
   		loginPage.login(loginCor.getLogin(), loginCor.getSenha());
   		
   		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
   		
   		// retorno do status para "1", ativo
   		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "1");
   	}
   	
   	@Test
   	public void fazerLoginComUsuarioOrgBloqueadoAutomaticamentePorSeguranca() {
   		log.info("Login Órgão Bloqueado Automaticamente Por Segurança: {}", loginOrg.getLogin());
   		
   		// ajuste de status para "7", bloqueado automaticamente por segurança
   		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "7");
   		loginPage.login(loginOrg.getLogin(), loginOrg.getSenha());
   		log.info("a");
   		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
   		
   		// retorno do status para "1", ativo
   		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "1");
   	}
   	
   	@Test
   	public void fazerLoginComUsuarioSupBloqueadoAutomaticamentePorSeguranca() {
   		log.info("Login Suporte Bloqueado Automaticamente Por Segurança: {}", loginSup.getLogin());

   		// ajuste de status para "7", bloqueado automaticamente por segurança
   		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "7");
   		loginPage.login(loginSup.getLogin(), loginSup.getSenha());
   		
   		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
   		
   		// retorno do status para "1", ativo
   		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "1");
   	}
   	
   	@Test
   	public void fazerLoginComUsuarioCsaBloqueadoAutomaticamentePorSeguranca() {
   		log.info("Login Consignatário Bloqueado Automaticamente Por Segurança: {}", loginCsa.getLogin());

   		// ajuste de status para "7", bloqueado automaticamente por segurança
   		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "7");
   		loginPage.login(loginCsa.getLogin(), loginCsa.getSenha());
   		
   		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
   		
   		// retorno do status para "1", ativo
   		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "1");
   	}
    
    @Test
   	public void fazerLoginComUsuarioCseBloqueadoAutomaticamentePorFimDeVigencia() {
   		log.info("Login Consignante Bloqueado Automaticamente Por Fim De Vigência: {}", loginCse.getLogin());
   		
   		// ajuste de status para "8", bloqueado automaticamente por fim de vigência
   		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "8");
   		loginPage.login(loginCse.getLogin(), loginCse.getSenha());
   		
   		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
   		
   		// retorno do status para "1", ativo
   		usuarioService.alterarStatusUsuario(loginCse.getLogin(), "1");
   	}
   	
   	@Test
   	public void fazerLoginComUsuarioCorBloqueadoAutomaticamentePorFimDeVigencia() {
   		log.info("Login Correspondente Bloqueado Automaticamente Por Fim De Vigência: {}", loginCor.getLogin());
   		
   		// ajuste de status para "8", bloqueado automaticamente por fim de vigência
   		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "8");
   		loginPage.login(loginCor.getLogin(), loginCor.getSenha());
   		
   		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
   		
   		// retorno do status para "1", ativo
   		usuarioService.alterarStatusUsuario(loginCor.getLogin(), "1");
   	}
   	
   	@Test
   	public void fazerLoginComUsuarioOrgBloqueadoAutomaticamentePorFimDeVigencia() {
   		log.info("Login Órgão Bloqueado Automaticamente Por Fim De Vigência: {}", loginOrg.getLogin());
   		
   		// ajuste de status para "8", bloqueado automaticamente por fim de vigência
   		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "8");
   		loginPage.login(loginOrg.getLogin(), loginOrg.getSenha());
   		log.info("a");
   		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
   		
   		// retorno do status para "1", ativo
   		usuarioService.alterarStatusUsuario(loginOrg.getLogin(), "1");
   	}
   	
   	@Test
   	public void fazerLoginComUsuarioSupBloqueadoAutomaticamentePorFimDeVigencia() {
   		log.info("Login Suporte Bloqueado Automaticamente Por Fim De Vigência: {}", loginSup.getLogin());

   		// ajuste de status para "8", bloqueado automaticamente por fim de vigência
   		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "8");
   		loginPage.login(loginSup.getLogin(), loginSup.getSenha());
   		
   		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
   		
   		// retorno do status para "1", ativo
   		usuarioService.alterarStatusUsuario(loginSup.getLogin(), "1");
   	}
   	
   	@Test
   	public void fazerLoginComUsuarioCsaBloqueadoAutomaticamentePorFimDeVigencia() {
   		log.info("Login Consignante Bloqueado Automaticamente Por Fim De Vigência: {}", loginCsa.getLogin());

   		// ajuste de status para "8", bloqueado automaticamente por fim de vigência
   		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "8");
   		loginPage.login(loginCsa.getLogin(), loginCsa.getSenha());
   		
   		assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(webDriver));
   		
   		// retorno do status para "1", ativo
   		usuarioService.alterarStatusUsuario(loginCsa.getLogin(), "1");
   	}    
}
