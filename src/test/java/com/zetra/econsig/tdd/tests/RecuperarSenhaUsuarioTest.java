package com.zetra.econsig.tdd.tests;

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
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senha.GeradorSenhaUtil;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.service.ConsignatariaService;
import com.zetra.econsig.service.ParametroSistemaService;
import com.zetra.econsig.service.UsuarioServiceTest;
import com.zetra.econsig.tdd.tests.pages.LoginPage;
import com.zetra.econsig.tdd.tests.pages.RecuperarSenhaPage;
import com.zetra.econsig.values.CodedValues;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecuperarSenhaUsuarioTest extends BaseTest {

    private static final String CSA_CODIGO = "CSA123";
    private static final String CSA_NOME = "CSA TESTE";
    private static final String USU_LOGIN = "csa123";
    private static final String USU_CPF = "164.910.214-32";
    private static final String USU_SENHA = "teste@123";
    private static final String USU_EMAIL = "testes.interno+123@nostrum.com.br";

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
        usuarioService.criarUsuarioCsaNaConsignatariaCasoNaoExista(USU_LOGIN, USU_CPF, USU_EMAIL, CodedValues.STU_ATIVO, CSA_CODIGO);

        // Habilita recuperação de senha de usuários
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_USU, "S");
        EConsigInitializer.limparCache();
    }

    @AfterEach
    public void afterEach() throws Exception {
        super.tearDown();
    }

    @Test
    @Order(1)
    public void recuperarSenhaErroLoginInexistente() throws Exception {
        final String login = "xyx";

        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(login);
        loginPage.clicarRecuperarSenha();

        assertEquals("RECUPERAR SENHA", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(login);
        recuperarSenhaPage.preencherCpf("111.111.111-11");
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("USUÁRIO NÃO ENCONTRADO. FAVOR TENTAR NOVAMENTE.", econsigHelper.getMensagemErro(webDriver));
    }

    @Test
    @Order(2)
    public void recuperarSenhaErroCpfIncorreto() throws Exception {
        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        loginPage.clicarRecuperarSenha();

        assertEquals("RECUPERAR SENHA", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherCpf("111.111.111-11");
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("USUÁRIO NÃO ENCONTRADO. FAVOR TENTAR NOVAMENTE.", econsigHelper.getMensagemErro(webDriver));
    }

    @Test
    @Order(3)
    public void recuperarSenhaErroCodigoRecuperacaoInvalido() throws Exception {
        recuperarSenhaPage.acessarTelaRecuperarSenhaUsuCsaPasso3("xyz");
        assertEquals("NÃO FOI POSSÍVEL LOCALIZAR O CÓDIGO PARA RECUPERAÇÃO DA SENHA DE ACESSO OU ESTE SE ENCONTRA EXPIRADO. POR FAVOR REINICIE A OPERAÇÃO.", econsigHelper.getMensagemErro(webDriver));
    }

    @Test
    @Order(4)
    public void recuperarSenhaSucessoPorLinkEmail() throws Exception {
        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        loginPage.clicarRecuperarSenha();

        assertEquals("RECUPERAR SENHA", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherCpf(USU_CPF);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha.", econsigHelper.getMensagemSucesso(webDriver));

        // Busca código de recuperação de senha
        final Usuario usuario = usuarioService.getUsuario(USU_LOGIN);
        assertNotNull(usuario);
        assertNotNull(usuario.getUsuChaveRecuperarSenha());

        recuperarSenhaPage.acessarTelaRecuperarSenhaUsuCsaPasso3(usuario.getUsuChaveRecuperarSenha());
        assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains("A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherSenha(USU_SENHA);
        recuperarSenhaPage.preencherConfirmarSenha(USU_SENHA);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.", econsigHelper.getMensagemSucesso(webDriver));

        // Testa o login com a nova senha
        loginPage.acessarTelaLogin();
        loginPage.login(USU_LOGIN, USU_SENHA);
        assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", econsigHelper.getMensagemAlerta(webDriver));
    }

    @Test
    @Order(5)
    public void recuperarSenhaErroOutroLogin() throws Exception {
        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        loginPage.clicarRecuperarSenha();

        assertEquals("RECUPERAR SENHA", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherCpf(USU_CPF);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("Operação concluída com sucesso. O usuário receberá um e-mail com um link de acesso ao sistema onde poderá redefinir sua senha.", econsigHelper.getMensagemSucesso(webDriver));

        // Busca código de recuperação de senha
        final Usuario usuario = usuarioService.getUsuario(USU_LOGIN);
        assertNotNull(usuario);
        assertNotNull(usuario.getUsuChaveRecuperarSenha());

        recuperarSenhaPage.acessarTelaRecuperarSenhaUsuCsaPasso3(usuario.getUsuChaveRecuperarSenha());
        assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains("A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

        // Preenche login com outro usuário tentando trocar a senha deste
        recuperarSenhaPage.preencherUsuario("cse");
        recuperarSenhaPage.preencherSenha(USU_SENHA);
        recuperarSenhaPage.preencherConfirmarSenha(USU_SENHA);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("USUÁRIO NÃO ENCONTRADO. FAVOR TENTAR NOVAMENTE.", econsigHelper.getMensagemErro(webDriver));
    }

    @Test
    @Order(6)
    public void recuperarSenhaErroOtpInvalido() throws Exception {
        // Habilita uso de OTP na recuperação de senha
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_USU, CodedValues.ENVIA_OTP_EMAIL);
        EConsigInitializer.limparCache();

        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        loginPage.clicarRecuperarSenha();

        assertEquals("RECUPERAR SENHA", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherCpf(USU_CPF);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains("A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

        // Verifica se um OTP foi gerado
        final Usuario usuario = usuarioService.getUsuario(USU_LOGIN);
        assertNotNull(usuario);
        assertNotNull(usuario.getUsuOtpCodigo());

        // Informa um OTP inválido
        recuperarSenhaPage.preencherOtp("111111");
        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherSenha(USU_SENHA);
        recuperarSenhaPage.preencherConfirmarSenha(USU_SENHA);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("TOKEN DE ACESSO INVÁLIDO", econsigHelper.getMensagemErro(webDriver));
    }

    @Test
    @Order(7)
    public void recuperarSenhaErroOtpOutroLogin() throws Exception {
        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        loginPage.clicarRecuperarSenha();

        assertEquals("RECUPERAR SENHA", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherCpf(USU_CPF);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains("A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

        // Verifica se um OTP foi gerado
        final Usuario usuario = usuarioService.getUsuario(USU_LOGIN);
        assertNotNull(usuario);
        assertNotNull(usuario.getUsuOtpCodigo());

        // Gera novo otp e salva no banco, pois o gerado pelo sistema estará criptografado e não é possível recuperá-lo
        final String otpGerado = GeradorSenhaUtil.getPasswordNumber(CodedValues.TAM_OTP, AcessoSistema.getAcessoUsuarioSistema());
        final String otpCrypt = SenhaHelper.criptografarSenha(usuario.getUsuLogin(), otpGerado, false, AcessoSistema.getAcessoUsuarioSistema());
        usuario.setUsuOtpCodigo(otpCrypt);
        usuarioService.alterarUsuario(usuario);

        recuperarSenhaPage.preencherOtp(otpGerado);
        // Preenche com login de outro usuário
        recuperarSenhaPage.preencherUsuario("cse");
        recuperarSenhaPage.preencherSenha(USU_SENHA);
        recuperarSenhaPage.preencherConfirmarSenha(USU_SENHA);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("TOKEN DE ACESSO INVÁLIDO", econsigHelper.getMensagemErro(webDriver));
    }

    @Test
    @Order(8)
    public void recuperarSenhaSucessoPorOtp() throws Exception {
        loginPage.acessarTelaLogin();
        loginPage.preencherUsuario(USU_LOGIN);
        loginPage.clicarRecuperarSenha();

        assertEquals("RECUPERAR SENHA", econsigHelper.getMensagemAlerta(webDriver));

        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherCpf(USU_CPF);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertTrue(econsigHelper.getMensagemAlerta(webDriver).contains("A NOVA SENHA INFORMADA DEVE ATENDER A REQUISITOS MÍNIMOS DE SEGURANÇA DETERMINADOS PELO SISTEMA."));

        // Verifica se um OTP foi gerado
        final Usuario usuario = usuarioService.getUsuario(USU_LOGIN);
        assertNotNull(usuario);
        assertNotNull(usuario.getUsuOtpCodigo());

        // Gera novo otp e salva no banco, pois o gerado pelo sistema estará criptografado e não é possível recuperá-lo
        final String otpGerado = GeradorSenhaUtil.getPasswordNumber(CodedValues.TAM_OTP, AcessoSistema.getAcessoUsuarioSistema());
        final String otpCrypt = SenhaHelper.criptografarSenha(usuario.getUsuLogin(), otpGerado, false, AcessoSistema.getAcessoUsuarioSistema());
        usuario.setUsuOtpCodigo(otpCrypt);
        usuarioService.alterarUsuario(usuario);

        recuperarSenhaPage.preencherOtp(otpGerado);
        recuperarSenhaPage.preencherUsuario(USU_LOGIN);
        recuperarSenhaPage.preencherSenha(USU_SENHA);
        recuperarSenhaPage.preencherConfirmarSenha(USU_SENHA);
        recuperarSenhaPage.preencherCaptcha();
        recuperarSenhaPage.clicarBotaoConfirmar();

        assertEquals("Senha alterada com sucesso. Clique no botão voltar para acessar a página de entrada no sistema.", econsigHelper.getMensagemSucesso(webDriver));

        // Testa o login com a nova senha
        loginPage.acessarTelaLogin();
        loginPage.login(USU_LOGIN, USU_SENHA);
        assertEquals("PARA CONTINUAR É NECESSÁRIO VALIDAR O EMAIL", econsigHelper.getMensagemAlerta(webDriver));

        // Restaura o parâmetro de uso de OTP
        parametroSistemaService.configurarParametroSistemaCse(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_USU, CodedValues.ENVIA_OTP_DESABILITADO);
        EConsigInitializer.limparCache();

        // Remove usuário e consignatárias criados para o teste
        usuarioService.removerUsuario(usuario.getUsuCodigo());
        consignatariaService.removerConsignataria(CSA_CODIGO);
    }
}
