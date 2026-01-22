package com.zetra.econsig.web.controller.autenticacao;

import java.security.KeyPair;

import javax.crypto.BadPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GoogleAuthenticatorHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.OperacaoValidacaoTotpEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: CadastrarValidacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso CadastrarValidacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/cadastrarValidacaoTotp" })
public class CadastrarValidacaoTotpWebController extends AbstractWebController {

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        final String mensagemCadastroTotp = ApplicationResourcesHelper.getMessage("mensagem.cadastro.totp", responsavel);
        final String mensagemCadastroTotpPopUp = ApplicationResourcesHelper.getMessage("mensagem.cadastro.totp.popup", responsavel);
        final String mensagemTotpCadastrado = ApplicationResourcesHelper.getMessage("mensagem.totp.cadastrado", responsavel);
        final String mensagemTotpRemoverCliqueAqui = ApplicationResourcesHelper.getMessage("mensagem.totp.remover.clique.aqui", responsavel);
        final String mensagemTotpCadastrarCliqueAqui = ApplicationResourcesHelper.getMessage("mensagem.totp.cadastrar.clique.aqui", responsavel);
        final String linkRet = "../v3/cadastrarValidacaoTotp?acao=iniciar";

        // Verifica se a sessão do usuário está ativa
        if (responsavel.getUsuNome() == null) {
            return "forward:/v3/expirarSistema?acao=iniciar";
        }

        if (responsavel.isSup() && !responsavel.isPermiteTotp()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.totp.usuario.nao.tem.permissao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final UsuarioTransferObject usuario;
        try {
            usuario = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);
        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        if (usuario == null) {
            session.setAttribute(CodedValues.MSG_ERRO, LoginHelper.getMensagemErroLogin());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String usuChaveValidacaoTotp = usuario.getUsuChaveValidacaoTotp();
        final boolean possuiChaveCadastrada = !TextHelper.isNull(usuario.getUsuChaveValidacaoTotp());
        final OperacaoValidacaoTotpEnum operacoesValidacaoTotp = OperacaoValidacaoTotpEnum.get(usuario.getUsuOperacoesValidacaoTotp());

        model.addAttribute("mensagemCadastroTotp", mensagemCadastroTotp);
        model.addAttribute("mensagemCadastroTotpPopUp", mensagemCadastroTotpPopUp);
        model.addAttribute("mensagemTotpCadastrado", mensagemTotpCadastrado);
        model.addAttribute("mensagemTotpRemoverCliqueAqui", mensagemTotpRemoverCliqueAqui);
        model.addAttribute("mensagemTotpCadastrarCliqueAqui", mensagemTotpCadastrarCliqueAqui);
        model.addAttribute("mensagemCadastroTotp", mensagemCadastroTotp);
        model.addAttribute("usuChaveValidacaoTotp", usuChaveValidacaoTotp);
        model.addAttribute("possuiChaveCadastrada", possuiChaveCadastrada);
        model.addAttribute("operacoesValidacaoTotp", operacoesValidacaoTotp);
        model.addAttribute("usuario", usuario);
        model.addAttribute("linkRet", linkRet);

        return viewRedirect("jsp/autenticacao/cadastrarValidacaoTotp", request, session, model, responsavel);
    }

    private String validarCodigoSeguranca(AcessoSistema responsavel, HttpServletRequest request, HttpSession session, Model model) {
        final String mensagemTotpCodigoInvalido = ApplicationResourcesHelper.getMessage("mensagem.totp.codigo.invalido", responsavel);

        try {
            // Decriptografa a senha informada
            final String usu_chave_validacao_totp = !TextHelper.isNull(responsavel.getUsuChaveValidacaoTotp()) ? responsavel.getUsuChaveValidacaoTotp() : (String) session.getAttribute(GoogleAuthenticatorHelper.CHAVE_VALIDACAO);
            final String senhaCriptografada = JspHelper.verificaVarQryStr(request, JspHelper.verificaVarQryStr(request, "segundaSenha"));

            final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
            final String senhaAberta;
            try {
                senhaAberta = RSA.decrypt(senhaCriptografada, keyPair.getPrivate());
            } catch (BadPaddingException e) {
                // Corresponde a tentativa de decriptografia com chave errada. A sessão pode ter expirado. Tentar novamente.
                session.setAttribute(CodedValues.MSG_ERRO, mensagemTotpCodigoInvalido);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            try {
                final GoogleAuthenticatorHelper authenticator = new GoogleAuthenticatorHelper();
                long timeInMilliseconds = 0;
                try {
                    final String strTimeInMilliseconds = JspHelper.verificaVarQryStr(request, "timeInMilliseconds");
                    timeInMilliseconds = !TextHelper.isNull(strTimeInMilliseconds) ? Long.parseLong(strTimeInMilliseconds) : 0;
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, mensagemTotpCodigoInvalido);
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                // Verificar código de segurança informado
                if (!authenticator.checkCode(usu_chave_validacao_totp, Long.valueOf(senhaAberta), timeInMilliseconds)) {
                    session.setAttribute(CodedValues.MSG_ERRO, mensagemTotpCodigoInvalido);
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, mensagemTotpCodigoInvalido);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return "";
    }

    @RequestMapping(params = { "acao=gerar" })
    public String gerar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Recupera chave de validação totp da sessão do usuário
            session.setAttribute(GoogleAuthenticatorHelper.CHAVE_VALIDACAO, GoogleAuthenticatorHelper.generateSecretKey());
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.totp.leia.codigo", responsavel));
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=cadastrar" })
    public String cadastrar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String erroCodigoSeguranca = validarCodigoSeguranca(responsavel, request, session, model);
        if (!erroCodigoSeguranca.equals("")) {
            return erroCodigoSeguranca;
        }

        try {
            // Recupera chave de validação totp da sessão do usuário
            final String usuChaveValidacaoTotp = (String) session.getAttribute(GoogleAuthenticatorHelper.CHAVE_VALIDACAO);
            final String usuOperacoesValidacaoTotp = !TextHelper.isNull(request.getParameter("operacoesValidacaoTotp")) ? request.getParameter("operacoesValidacaoTotp") : OperacaoValidacaoTotpEnum.AUTORIZACAO_OPERACAO_SENSIVEL.getCodigo();
            usuarioController.cadastrarChaveValidacaoTotp(usuChaveValidacaoTotp, usuOperacoesValidacaoTotp, responsavel);
            responsavel.setUsuChaveValidacaoTotp(usuChaveValidacaoTotp);
            responsavel.setUsuOperacoesValidacaoTotp(OperacaoValidacaoTotpEnum.get(usuOperacoesValidacaoTotp));
        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Armazena na sessão o objeto AcessoSistema para este usuário incluindo a chave de validação TOTP
        session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, responsavel);
        // Remove chave de validação armazenada da sessão do usuário
        session.removeAttribute(GoogleAuthenticatorHelper.CHAVE_VALIDACAO);
        // Seta mensagem de informação de chave cadastrada
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.totp.cadastrado.sucesso", responsavel));

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=remover" })
    public String remover(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String erroCodigoSeguranca = validarCodigoSeguranca(responsavel, request, session, model);
        if (!erroCodigoSeguranca.equals("")) {
            return erroCodigoSeguranca;
        }

        try {
            // Remove chave de validação TOTP
            usuarioController.removerChaveValidacaoTotp(responsavel);
        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Armazena na sessão o objeto AcessoSistema para este usuário excluindo a chave de validação TOTP
        responsavel.setUsuChaveValidacaoTotp(null);
        session.setAttribute(AcessoSistema.SESSION_ATTR_NAME, responsavel);
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.totp.removido.sucesso", responsavel));

        return iniciar(request, response, session, model);
    }
}
