package com.zetra.econsig.bdd.steps;

import static com.zetra.econsig.bdd.steps.CucumberSeleniumManager.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.values.LoginValues;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.AcoesUsuarioPage;
import com.zetra.econsig.tdd.tests.pages.AlterarSenhaPage;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.UsuarioPage;
import com.zetra.econsig.values.CodedValues;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ManutencaoUsuariosServidoresStep {

    private final LoginInfo loginServidor1 = LoginValues.servidor1;
    private final LoginInfo loginServidor2 = LoginValues.servidor2;

    private String senhaNovoUsuario;

    @Autowired
    private EconsigHelper econsigHelper;

    @Autowired
    private UsuarioServiceTest usuarioService;

    private AcoesUsuarioPage acoesUsuarioPage;
    private LoginPage loginPage;
    private UsuarioPage usuarioPage;
    private AlterarSenhaPage alterarSenhaPage;

    @Before
    public void setUp() throws Exception {
        acoesUsuarioPage = new AcoesUsuarioPage(getWebDriver());
        loginPage = new LoginPage(getWebDriver());
        usuarioPage = new UsuarioPage(getWebDriver());
        alterarSenhaPage = new AlterarSenhaPage(getWebDriver());
    }

    @Dado("que o usuario servidor esteja ativo")
    public void usuarioServidorAtivo() {
        log.info("Dado que o usuário servidor esteja ativo");

        usuarioService.alterarStatusUsuario("213464140-123456", CodedValues.STU_ATIVO);
        usuarioService.alterarSenhaUsuario("213464140-145985", "8240a76a4d539df664e0cc81ffe18b847fcaa6895c3c4117274effba34c0763e8269a9754e4d6f78dbb2bb060b99b5b8b1362cb9e67ccd0e32450c6bf745a7107e081c7b");
    }

    @Dado("que o usuario servidor esteja bloqueado")
    public void usuarioServidorBloqueado() {
        log.info("Dado que o usuário servidor esteja bloqueado");

        usuarioService.alterarStatusUsuario("213464140-123456", CodedValues.STU_BLOQUEADO_POR_CSE);
    }

    @Dado("que o usuario servidor esteja bloqueado por seguranca")
    public void usuarioServidorBloqueadoPorSeguranca() {
        log.info("Dado que o usuário servidor esteja bloqueado por seguranca");

        usuarioService.alterarStatusUsuario("213464140-123456", CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
    }

    @Quando("bloquear o usuario servidor")
    public void bloquearUsuarioServidor() {
        log.info("Quando bloquear o usuário servidor");

        acoesUsuarioPage.clicarOpcoes("123456", "4");
        acoesUsuarioPage.clicarBloquearDesbloquearServidor();
        usuarioPage.selecionarMotivoOperacao("Bloqueio de Usuário");
        usuarioPage.preencherObservacao("Automacao");
        acoesUsuarioPage.clicarSalvar();

        assertEquals("Confirma o bloqueio do usuário \"213464140-123456\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("desbloquear o usuario servidor")
    public void desbloquearUsuarioServidor() {
        log.info("Quando desbloquear o usuário servidor");

        acoesUsuarioPage.clicarOpcoes("123456", "4");
        acoesUsuarioPage.clicarBloquearDesbloquearServidor();
        usuarioPage.selecionarMotivoOperacao("Desbloqueio de Usuário");
        usuarioPage.preencherObservacao("Automacao");
        acoesUsuarioPage.clicarSalvar();

        assertEquals("Confirma o desbloqueio do usuário \"213464140-123456\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("alterar senha do usuario servidor")
    public void alterarSenhaUsuarioServidor() {
        log.info("Quando alterar senha do usuário servidor");

        senhaNovoUsuario = "1&34@Ser";

        acoesUsuarioPage.clicarOpcoes("145985", "4");
        acoesUsuarioPage.clicarAlterarSenha();
        alterarSenhaPage.preencherNovaSenha(senhaNovoUsuario);
        alterarSenhaPage.preencherConfirmarNovaSenha(senhaNovoUsuario);
        acoesUsuarioPage.clicarSalvar();
    }

    @Quando("reinicializar senha do usuario servidor")
    public void reinicializarSenhaUsuarioServidor() {
        log.info("Quando reinicializar senha do usuário servidor");

        acoesUsuarioPage.clicarOpcoes("145985", "4");
        acoesUsuarioPage.clicarReiniciarSenha();
        usuarioPage.selecionarMotivoOperacao("Outros");
        usuarioPage.preencherObservacao("Automacao");
        acoesUsuarioPage.clicarSalvar();

        assertEquals("Reinicializar a senha do usuário \"213464140-145985\"?", econsigHelper.getMensagemPopUp(getWebDriver()));
    }

    @Quando("cadastrar usuario servidor {string}")
    public void cadastrarUsuarioServidor(String matricula) {
        log.info("Quando cadastrar usuário servidor");

        senhaNovoUsuario = "Ser1&34@";

        acoesUsuarioPage.clicarOpcoes(matricula, "4");
        usuarioPage.clicarCriarNovoUsuario();
        alterarSenhaPage.preencherNovaSenha(senhaNovoUsuario);
        alterarSenhaPage.preencherConfirmarNovaSenha(senhaNovoUsuario);
        acoesUsuarioPage.clicarSalvar();
    }

    @Entao("exibe a nova senha {string}")
    public void exibeNovaSenha(String mensagem) {
        log.info("Entao exibe a nova senha {}", mensagem);

        senhaNovoUsuario = usuarioPage.getSenhaNova();

        assertTrue(econsigHelper.getMensagemSucesso(getWebDriver()).contains(mensagem));
    }

    @Entao("verifica que o novo usuario servidor autentica {string}")
    public void verificarNovoUsuarioServidor(String matricula) {
        log.info("Entao verifica que usuário servidor consegue autenticar");

        // autenticar
        loginPage.acessarTelaLoginServidor();
        loginPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47");
        loginPage.login(matricula, senhaNovoUsuario);

        econsigHelper.verificaTextoPagina(getWebDriver(), "Informações legais sobre o eConsig");
        assertEquals("Política de Privacidade", getWebDriver().getTitle());

        acoesUsuarioPage.clicarConfirmar();

        assertEquals("Confirma a aceitação da Política de Privacidade?", econsigHelper.getMensagemPopUp(getWebDriver()));
        assertEquals("Política de Privacidade marcada como aceito.", econsigHelper.getMensagemSucesso(getWebDriver()));
    }

    @Entao("verifica que o usuario servidor autentica com a nova senha")
    public void verificarServidorAutentica() {
        log.info("Entao verifica que usuário servidor consegue autenticar");

        // autenticar
        loginPage.acessarTelaLoginServidor();
        loginPage.selecionarOrgao("Carlota Joaquina 21.346.414/0001-47");
        loginPage.login(loginServidor2.getLogin(), senhaNovoUsuario);

        econsigHelper.verificaTextoPagina(getWebDriver(), "Página inicial");
        assertEquals("Alteração de senha de usuário", getWebDriver().getTitle());
    }

    @Entao("verifica que o usuario servidor nao consegue autenticar")
    public void verificarStatusBancoUsuarioServidorNaoAutentica() {
        log.info("Entao verifica que o usuário servidor não consegue autenticar");

        // verifica o status no banco
        assertEquals(CodedValues.STU_BLOQUEADO_POR_CSE, usuarioService.getUsuario("213464140-123456").getStuCodigo());

        // autenticar
        loginPage.loginServidor(loginServidor1);

        assertEquals("USUÁRIO BLOQUEADO NO SISTEMA", econsigHelper.getMensagemErro(getWebDriver()));
    }

    @Entao("verifica que usuario servidor consegue autenticar")
    public void verificarUsuarioServidorAutentica() {
        log.info("Entao verifica que usuário servidor consegue autenticar");

        // verifica o status no banco
        assertEquals(CodedValues.STU_ATIVO, usuarioService.getUsuario("213464140-123456").getStuCodigo());

        // autenticar
        loginPage.loginServidor(loginServidor1);

        econsigHelper.verificaTextoPagina(getWebDriver(), "Página inicial");
        assertEquals("eConsig - Principal", getWebDriver().getTitle());
    }
}
