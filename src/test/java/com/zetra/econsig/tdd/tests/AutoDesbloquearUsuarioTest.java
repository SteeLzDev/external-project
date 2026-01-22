package com.zetra.econsig.tdd.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.EConsigInitializer;
import com.zetra.econsig.helper.EconsigHelper;
import com.zetra.econsig.helper.SeleniumHelper;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.ConsignatariaService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.RecuperarSenhaPage;
import com.zetra.econsig.values.CodedValues;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AutoDesbloquearUsuarioTest extends BaseTest {

    private static final String CSA_CODIGO = "CSA124";
    private static final String CSA_NOME = "CSA TESTE";
    private static final String USU_LOGIN = "csa124";
    private static final String USU_CPF = "768.998.435-52";
    private static final String USU_SENHA = "teste@124";
    private static final String USU_EMAIL = "testes.interno+124@nostrum.com.br";

    private LoginPage loginPage;
    private RecuperarSenhaPage recuperarSenhaPage;

    @Autowired
    private EconsigHelper econsigHelper;

    @Autowired
    private UsuarioServiceTest usuarioService;

    @Autowired
    private ConsignatariaService consignatariaService;

    @Autowired
    private ParametroSistemaService parametroSistemaService;

    @BeforeEach
    public void beforeEach() throws Exception {
        super.setUp();
        loginPage = new LoginPage(webDriver);
        recuperarSenhaPage = new RecuperarSenhaPage(webDriver);

        // Cria CSA e usuário para os testes
        consignatariaService.criarConsignatariaCasoNaoExista(CSA_CODIGO, CSA_NOME, CSA_CODIGO);
        usuarioService.criarUsuarioCsaNaConsignatariaCasoNaoExista(USU_LOGIN, USU_CPF, USU_EMAIL, CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE, CSA_CODIGO);

        // Habilita recuperação de senha de usuários
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_USU, "S");
        // Habilita auto desbloqueio de usuários de CSA/COR
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, "S");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_SUP, "N");
        EConsigInitializer.limparCache();
    }

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

    @Test
    @Order(1)
    public void autoDesbloquearErroLoginInexistente() throws Exception {
        final String login = "xyx";

        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(login);
        
        assertFalse(loginPage.verificarLinkAutoDesbloqueiPresente());
        
    }

    @Test
    @Order(2)
    public void autoDesbloquearErroCpfIncorreto() throws Exception {
        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        loginPage.clicarAutoDesbloquear();

        assertEquals("AUTO-DESBLOQUEIO DE USUÁRIO", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherCpf("111.111.111-11");
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();
        assertEquals("USUÁRIO NÃO ENCONTRADO. FAVOR TENTAR NOVAMENTE.", econsigHelper.getMensagemErro(webDriver));


        
    }

    @Test
    @Order(3)
    public void autoDesbloquearErroCodigoRecuperacaoInvalido() throws Exception {
        recuperarSenhaPage.acessarTelaAutoDesbloquearUsuPasso3("xyz");
        assertEquals("NÃO FOI POSSÍVEL LOCALIZAR O CÓDIGO PARA RECUPERAÇÃO DA SENHA DE ACESSO OU ESTE SE ENCONTRA EXPIRADO. POR FAVOR REINICIE A OPERAÇÃO.", econsigHelper.getMensagemErro(webDriver));
    }

    @Test
    @Order(4)
    public void autoDesbloquearSucessoPorLinkEmail() throws Exception {
        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        loginPage.clicarAutoDesbloquear();

        assertEquals("AUTO-DESBLOQUEIO DE USUÁRIO", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherCpf(USU_CPF);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá realizar o auto-desbloqueio.", econsigHelper.getMensagemSucesso(webDriver));

        // Busca código de recuperação de senha
        final Usuario usuario = usuarioService.getUsuario(USU_LOGIN);
        assertNotNull(usuario);
        assertNotNull(usuario.getUsuChaveRecuperarSenha());

        recuperarSenhaPage.acessarTelaAutoDesbloquearUsuPasso3(usuario.getUsuChaveRecuperarSenha());
        assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains("A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherSenha(USU_SENHA);
        recuperarSenhaPage.preencherConfirmarSenha(USU_SENHA);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("Senha alterada e usuário auto-desbloqueado com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.", econsigHelper.getMensagemSucesso(webDriver));

        // Testa o login com a nova senha
        loginPage.acessarTelaLogin();
        loginPage.login(USU_LOGIN, USU_SENHA);
        assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", econsigHelper.getMensagemAlerta(webDriver));
    }

    @Test
    @Order(5)
    public void autoDesbloquearErroUsuarioAtivo() throws Exception {
        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        loginPage.clicarAutoDesbloquear();

        assertEquals("AUTO-DESBLOQUEIO DE USUÁRIO", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherCpf(USU_CPF);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("USUÁRIO NÃO PODE SER AUTO-DESBLOQUEADO. FAVOR ENTRAR EM CONTATO COM O SUPORTE.", econsigHelper.getMensagemErro(webDriver));
    }

    @Test
    @Order(6)
    public void autoDesbloquearErroParametroDesabilitadoPapel() throws Exception {
        // Bloqueia novamente o usuário
        usuarioService.alterarStatusUsuario(USU_LOGIN, CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE);

        // Desabilita o auto-desbloqueio de usuário de CSA/COR
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, "N");

        // Habilita o auto-desbloqueio de usuário CSE/ORG para que o link apareça na página de login
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, "S");
        EConsigInitializer.limparCache();

        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        
        assertFalse(loginPage.verificarLinkAutoDesbloqueiPresente());

    }

    @Test
    @Order(7)
    public void autoDesbloquearErroOutroLogin() throws Exception {
        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        loginPage.clicarAutoDesbloquear();

        assertEquals("AUTO-DESBLOQUEIO DE USUÁRIO", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherCpf(USU_CPF);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá realizar o auto-desbloqueio.", econsigHelper.getMensagemSucesso(webDriver));

        // Busca código de recuperação de senha
        final Usuario usuario = usuarioService.getUsuario(USU_LOGIN);
        assertNotNull(usuario);
        assertNotNull(usuario.getUsuChaveRecuperarSenha());

        recuperarSenhaPage.acessarTelaAutoDesbloquearUsuPasso3(usuario.getUsuChaveRecuperarSenha());
        assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains("A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

        // Preenche com login de outro usuário
        recuperarSenhaPage.preencherUsuario("cse");
        recuperarSenhaPage.preencherSenha(USU_SENHA);
        recuperarSenhaPage.preencherConfirmarSenha(USU_SENHA);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("USUÁRIO NÃO ENCONTRADO. FAVOR TENTAR NOVAMENTE.", econsigHelper.getMensagemErro(webDriver));

        // Restaura os parâmetro de sistema
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, "N");
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_SUP, "N");
        EConsigInitializer.limparCache();

        // Remove usuário e consignatárias criados para o teste
        usuarioService.removerUsuario(usuario.getUsuCodigo());
        consignatariaService.removerConsignataria(CSA_CODIGO);
    }
}
