package com.zetra.econsig.web.controller.sistema;

import java.security.KeyPair;

import javax.crypto.BadPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.SenhaConfig;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AutoDesbloquearUsuarioWebController</p>
 * <p>Description: Controlador Web para o caso de uso Recuperar Senha.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/autoDesbloquearUsuario" })
public class AutoDesbloquearUsuarioWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutoDesbloquearUsuarioWebController.class);

    private static final String JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1 = "jsp/recuperarSenha/recuperarSenhaUsuPasso1";
    private static final String JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_2 = "jsp/recuperarSenha/recuperarSenhaUsuPasso2";
    private static final String JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_3 = "jsp/recuperarSenha/recuperarSenhaUsuPasso3";
    private static final String JSP_VISUALIZAR_MENSAGEM_ERRO                    = "jsp/visualizarPaginaErro/visualizarMensagem";

    @Autowired
    private UsuarioController usuarioController;

    private boolean autoDesbloqueioHabilitado(AcessoSistema responsavel) {
        return ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, CodedValues.TPC_SIM, responsavel) ||
               ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, CodedValues.TPC_SIM, responsavel) ||
               ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_SUP, CodedValues.TPC_SIM, responsavel);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.auto.desbloqueio.usuario.titulo", responsavel));
        model.addAttribute("autodesbloqueio", Boolean.TRUE);
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=iniciarUsuario" })
    public String iniciarUsuario(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!autoDesbloqueioHabilitado(responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        final String tipo = JspHelper.verificaVarQryStr(request, "tipo");

        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "enti"))) {
            responsavel.setTipoEntidade(JspHelper.verificaVarQryStr(request, "enti"));
        }

        // Usuário
        response.addCookie(new Cookie("LOGIN", ""));
        model.addAttribute("tipo", tipo);

        if ("".equals(tipo)) {
            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);

        } else if ("recuperar".equals(tipo)) {
            return redirecionarPasso3Usuario(request, response, session, model);

        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }
    }

    private String redirecionarPasso3Usuario(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final String codRecuperacaoEnviado = JspHelper.verificaVarQryStr(request, "cod_recuperar");
            final TransferObject usuario = (!TextHelper.isNull(codRecuperacaoEnviado) ? usuarioController.buscarUsuarioPorCodRecuperarSenha(codRecuperacaoEnviado, responsavel) : null);
            if (usuario == null) {
                // Retorno mensagem de erro se o codigo de recuperação de senha for invalido
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.recuperar.senha.codigo.nao.localizado", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
            }

            final String tipoEntidade = UsuarioHelper.obterTipoEntidade(usuario);
            final SenhaConfig senhaConfig = SenhaConfig.getSenhaUsuarioConfig(tipoEntidade, responsavel);
            model.addAttribute("intpwdStrength", senhaConfig.forcaSenha());
            model.addAttribute("pwdStrengthLevel", senhaConfig.nivelForcaSenha());
            model.addAttribute("strpwdStrengthLevel", senhaConfig.rotuloForcaSenha());
            model.addAttribute("ignoraSeveridade", senhaConfig.ignoraSeveridade());
            model.addAttribute("strMensagemSenha", senhaConfig.mensagemSenha());
            model.addAttribute("strMensagemSenha1", senhaConfig.mensagemSenha1());
            model.addAttribute("strMensagemSenha2", senhaConfig.mensagemSenha2());
            model.addAttribute("strMensagemSenha3", senhaConfig.mensagemSenha3());
            model.addAttribute("strMensagemErroSenha", senhaConfig.mensagemErroSenha());
            model.addAttribute("tamMinSenhaUsuario", senhaConfig.tamMinSenha());
            model.addAttribute("tamMaxSenhaUsuario", senhaConfig.tamMaxSenha());

            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_3, request, session, model, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }
    }

    @PostMapping( params = { "acao=concluirUsuario" })
    public String concluirUsuario(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!autoDesbloqueioHabilitado(responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        try {
            // Valida captcha
            if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                    && !ImageCaptchaServlet.validaCaptcha(session.getId(), request.getParameter("captcha"))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
            }
            session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);

            // Recupera dados usuario
            final String usuLogin = JspHelper.verificaVarQryStr(request, "matricula");

            // Seta responsavel
            try {
                responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(usuLogin, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
            }

            final TransferObject usuario = usuarioController.findTipoUsuarioByCodigo(responsavel.getUsuCodigo(), responsavel);
            if (usuario == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
            }

            final String usuCodigo = (String) usuario.getAttribute(Columns.USU_CODIGO);

            // Para autodesbloqueio de usuário este deve estar bloqueado automaticamente
            final String stuCodigo = (String) usuario.getAttribute(Columns.USU_STU_CODIGO);
            if (!CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE.equals(stuCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.usuario.nao.pode.desbloqueado", responsavel));
                return iniciarUsuario(request, response, session, model);
            }

            final String campoCpf = JspHelper.verificaVarQryStr(request, "USU_CPF");
            final String campoEmail = JspHelper.verificaVarQryStr(request, "USU_EMAIL");

            // Determina o tipo da entidade do usuário
            UsuarioHelper.obterTipoEntidade(usuario);
            final String tipoEntidade = usuario.getAttribute("TIPO_ENTIDADE").toString();
            final String codEntidade = usuario.getAttribute("COD_ENTIDADE").toString();
            responsavel.setTipoEntidade(tipoEntidade);
            responsavel.setFunCodigo(getFuncaoPorEtipoEntidade(tipoEntidade));

            // Valida o parâmetro de sistema de autodesbloqueio pelo tipo da entidade do usuário
            if (!usuarioController.usuarioPossuiPermissaoAutoDesbloqueio(usuario, tipoEntidade, responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.usuario.nao.pode.desbloqueado", responsavel));
                return "forward:" + forwardUrl(LoginHelper.getPaginaLogin());
            }

            final UsuarioTransferObject dadosUsuario = usuarioController.findUsuario(usuCodigo, responsavel);
            // Encontrou Usuário verifica se possui e-mail cadastrado
            final String usuEmail = dadosUsuario.getUsuEmail();
            final String usuCpf = dadosUsuario.getUsuCPF();

            if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                if (AcessoSistema.ENTIDADE_SER.equals(tipoEntidade)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                    return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);

                } else {
                    if (TextHelper.isNull(usuEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.email.nao.cadastrado", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
                    }

                    if (!usuEmail.equals(campoEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.dados.nao.conferem", responsavel));
                        return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
                    }
                }
            } else {
                if (TextHelper.isNull(usuEmail)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.email.nao.cadastrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
                }
                final boolean cpfObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_CPF_OBRIGATORIO_USUARIO, CodedValues.TPC_SIM, responsavel);
                if (cpfObrigatorio && TextHelper.isNull(usuCpf)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.cpf.nao.cadastrado", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
                }
                if (!TextHelper.isNull(usuCpf) && !TextHelper.isNull(campoCpf) && !usuCpf.equals(campoCpf)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
                }
            }

            if (!TextHelper.isNull(tipoEntidade) && !AcessoSistema.ENTIDADE_SER.equals(tipoEntidade) && !AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
                final boolean usuEmailRepeat;
                if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    usuEmailRepeat = usuarioController.findEmailExistenteCsaCseOrgCor(campoEmail, dadosUsuario.getUsuCodigo(), responsavel);
                } else {
                    usuEmailRepeat = usuarioController.findEmailExistenteCsaCseOrgCor(usuEmail, dadosUsuario.getUsuCodigo(), responsavel);
                }
                if (usuEmailRepeat) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.usuario.csa.cse.org.cor.repetido", responsavel));
                    return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
                }
            }

            try {
                // valida IP/DNS de acesso
                UsuarioHelper.verificarIpDDNSAcesso(tipoEntidade, codEntidade, JspHelper.getRemoteAddr(request), (String) usuario.getAttribute(Columns.USU_IP_ACESSO), (String) usuario.getAttribute(Columns.USU_DDNS_ACESSO), (String) usuario.getAttribute(Columns.USU_CODIGO), responsavel);

                // O usuário possui e-mail, então envia email com link para alterar senha
                String link = request.getRequestURL().toString();
                link += "?acao=iniciarUsuario&enti=" + responsavel.getTipoEntidade();

                // Gera uma nova codigo de recuparação de senha
                final String cod_Senha = SynchronizerToken.generateToken();

                // Atualiza o codigo de recuperação de senha do usuário
                usuarioController.alteraChaveRecupSenhaAutoDesbloqueio(usuCodigo, cod_Senha, responsavel);
                // Envia e-mail com link para recuperação de senha
                usuarioController.enviaLinkReinicializarSenhaUsuAutoDesbloqueio(usuLogin, link, cod_Senha, responsavel);
                // Retorna mensagem de sucesso para o usuário
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.usuario.sucesso", responsavel));
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_2, request, session, model, responsavel);

            } catch (ViewHelperException | UsuarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
            }

        } catch (UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_1, request, session, model, responsavel);
        }
    }

    @PostMapping( params = { "acao=recuperarUsuario" })
    public String recuperarUsuario(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!autoDesbloqueioHabilitado(responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }

        // Valida captcha
        if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                && !ImageCaptchaServlet.validaCaptcha(session.getId(), request.getParameter("captcha"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
            return redirecionarPasso3Usuario(request, response, session, model);
        }
        session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);

        // Recupera dados usuario
        final String usuLogin = JspHelper.verificaVarQryStr(request, "matricula");

        // Seta responsavel
        try {
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(usuLogin, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
            return redirecionarPasso3Usuario(request, response, session, model);
        }

        // Busca o usuário pelo login enviado
        final TransferObject usuario;
        try {
            usuario = usuarioController.findTipoUsuarioByCodigo(responsavel.getUsuCodigo(), responsavel);
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
            return redirecionarPasso3Usuario(request, response, session, model);
        }

        final String usuCodigo = (String) usuario.getAttribute(Columns.USU_CODIGO);

        // Pega a chave enviada no link
        final String codRecuperacaoEnviado = JspHelper.verificaVarQryStr(request, "cod_recuperar");
        final String usuChaveRecuperarSenhaSalva = (String) usuario.getAttribute(Columns.USU_CHAVE_RECUPERAR_SENHA);
        if (TextHelper.isNull(usuChaveRecuperarSenhaSalva) || TextHelper.isNull(codRecuperacaoEnviado) || !usuChaveRecuperarSenhaSalva.equals(codRecuperacaoEnviado)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.usuario.nao.encontrado.tente", responsavel));
            return redirecionarPasso3Usuario(request, response, session, model);
        }

        // Para autodesbloqueio de usuário este deve estar bloqueado automaticamente
        final String stuCodigo = (String) usuario.getAttribute(Columns.USU_STU_CODIGO);
        if (!CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE.equals(stuCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.usuario.nao.pode.desbloqueado", responsavel));
            return redirecionarPasso3Usuario(request, response, session, model);
        }

        // Determina o tipo da entidade do usuário
        UsuarioHelper.obterTipoEntidade(usuario);
        final String tipoEntidade = usuario.getAttribute("TIPO_ENTIDADE").toString();
        final String codEntidade = usuario.getAttribute("COD_ENTIDADE").toString();
        responsavel.setTipoEntidade(tipoEntidade);
        responsavel.setFunCodigo(getFuncaoPorEtipoEntidade(tipoEntidade));

        // Valida o parâmetro de sistema de autodesbloqueio pelo tipo da entidade do usuário
        if (!usuarioController.usuarioPossuiPermissaoAutoDesbloqueio(usuario, tipoEntidade, responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.usuario.nao.pode.desbloqueado", responsavel));
            return "forward:" + forwardUrl(LoginHelper.getPaginaLogin());
        }

        try {
            // valida IP/DNS de acesso
            UsuarioHelper.verificarIpDDNSAcesso(tipoEntidade, codEntidade, JspHelper.getRemoteAddr(request), (String) usuario.getAttribute(Columns.USU_IP_ACESSO), (String) usuario.getAttribute(Columns.USU_DDNS_ACESSO), (String) usuario.getAttribute(Columns.USU_CODIGO), responsavel);

            // Decriptografa a nova senha
            final String senhaNovaCriptografada = JspHelper.verificaVarQryStr(request, "senhaNovaRSA");
            final String dica = JspHelper.verificaVarQryStr(request, "dica");
            final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
            final String senhaNova = RSA.decrypt(senhaNovaCriptografada, keyPair.getPrivate());

            // Executa a recuperação de senha
            usuarioController.recuperarSenha(usuCodigo, tipoEntidade, codRecuperacaoEnviado, senhaNova, dica, true, responsavel);

            // Retorna mensagem de sucesso para o usuário
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.alterada.auto.desbloqueio.usuario.sucesso", responsavel));
            return viewRedirect(JSP_RECUPERAR_SENHA_RECUPERAR_SENHA_USU_PASSO_2, request, session, model, responsavel);

        } catch (ViewHelperException | UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return redirecionarPasso3Usuario(request, response, session, model);
        } catch (BadPaddingException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect(JSP_VISUALIZAR_MENSAGEM_ERRO, request, session, model, responsavel);
        }
    }

    private String getFuncaoPorEtipoEntidade(String tipoEntidade) {
        if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) {
            return CodedValues.FUN_AUTODESBLOQUEIO_CSE_ORG;
        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) || AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
            return CodedValues.FUN_AUTODESBLOQUEIO_CSA_COR;
        } else if (AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
            return CodedValues.FUN_AUTODESBLOQUEIO_SUP;
        }
        return null;
    }

}
