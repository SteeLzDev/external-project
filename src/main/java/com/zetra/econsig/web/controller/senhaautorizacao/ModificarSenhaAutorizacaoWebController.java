package com.zetra.econsig.web.controller.senhaautorizacao;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ModificarSenhaAutorizacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Modificar Senha de Autorização.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/modificarSenhaAutorizacao" })
public class ModificarSenhaAutorizacaoWebController extends AbstractWebController {

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=alterar" })
    public String alterar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);

        try {
            //Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String usuCodigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
            final String serCodigo = JspHelper.verificaVarQryStr(request, "SER_CODIGO");
            if (!responsavel.isSer() && !responsavel.isCseSupOrg()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                model.addAttribute("tipo", "principal");
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verifica o modo de entrega da senha de autorização: ESCOLHA[1=E-mail;2=Tela;3=E-mail/Tela;4=SMS;5=SMS/E-mail;6=Tela/E-mail]
            final String modoEntrega = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel).toString() : CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL;

            String serEmail = null;
            if (CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL.equals(modoEntrega) || CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA.equals(modoEntrega) || CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega) || CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA.equals(modoEntrega)) {
                // Busca os dados do servidor para obter o endereço de e-mail, caso exista
                ServidorTransferObject servidor = null;
                if (responsavel.isSer()) {
                    servidor = servidorController.findServidor(responsavel.getSerCodigo(), responsavel);
                } else {
                    servidor = servidorController.findServidor(serCodigo, responsavel);
                }

                serEmail = usuarioController.consultarEmailServidor(true, servidor.getSerCpf(), servidor.getSerEmail(), modoEntrega, responsavel);
                if (responsavel.isSer() && TextHelper.isNull(serEmail) && CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL.equals(modoEntrega)) {
                    // Se só entrega as senhas de autorização por e-mail, e o servidor
                    // não possui endereço de e-mail cadastrado, então não deixa gerar
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.erro.email.invalido", responsavel));
                    model.addAttribute("tipo", "principal");
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            String serCel = null;
            if (CodedValues.ALTERACAO_SENHA_AUT_SER_SMS.equals(modoEntrega) || CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega)) {
                // Busca os dados do servidor para obter o telefone celular, caso exista
                ServidorTransferObject servidor = null;
                if (responsavel.isSer()) {
                    servidor = servidorController.findServidor(responsavel.getSerCodigo(), responsavel);
                } else {
                    servidor = servidorController.findServidor(serCodigo, responsavel);
                }

                serCel = servidor.getSerCelular();
                if (responsavel.isSer() && TextHelper.isNull(serCel) && CodedValues.ALTERACAO_SENHA_AUT_SER_SMS.equals(modoEntrega)) {
                    // Se só entrega as senhas de autorização por SMS, e o servidor
                    // não possui celular, então não deixa gerar
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.erro.celular.invalido", responsavel));
                    model.addAttribute("tipo", "principal");
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                serEmail = usuarioController.consultarEmailServidor(true, servidor.getSerCpf(), servidor.getSerEmail(), modoEntrega, responsavel);
                if (responsavel.isSer() && TextHelper.isNull(serEmail) && TextHelper.isNull(serCel) && CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega)) {
                    // Se só entrega as senhas de autorização por e-mail, e o servidor
                    // não possui endereço de e-mail cadastrado, então não deixa gerar
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.erro.email.celular.invalido", responsavel));
                    model.addAttribute("tipo", "principal");
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

            }

            // Gera nova senha de autorização do servidor
            String novaSenhaPlana = null;
            if (responsavel.isSer()) {
                novaSenhaPlana = usuarioController.gerarSenhaAutorizacao(responsavel.getUsuCodigo(), false, responsavel);
            } else {
                novaSenhaPlana = usuarioController.gerarSenhaAutorizacao(usuCodigo, false, responsavel);
            }

            if (responsavel.isSer()) {
                // Verifica se a senha pode ser exibida na tela para o usuário, por e-mail ou sms
                if (CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL.equals(modoEntrega) || (CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA.equals(modoEntrega) && !TextHelper.isNull(serEmail)) || (CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega) && TextHelper.isNull(serCel) && !TextHelper.isNull(serEmail))) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.email", responsavel));
                } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_SMS.equals(modoEntrega) || (CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega) && TextHelper.isNull(serEmail) && !TextHelper.isNull(serCel))) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.sms", responsavel));
                } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_EXIBE_TELA.equals(modoEntrega) || (CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA.equals(modoEntrega) && TextHelper.isNull(serEmail))) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.exibe.tela", responsavel) + " <font class=\"novaSenha\">" + novaSenhaPlana + "</font>");
                } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega) && !TextHelper.isNull(serCel) && !TextHelper.isNull(serEmail)) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.email.sms", responsavel));
                } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA.equals(modoEntrega)) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.email", responsavel) + " <font class=\"novaSenha\">" + novaSenhaPlana + "</font>");
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                }
            } else {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.exibe.tela", responsavel) + " <font class=\"novaSenha\">" + novaSenhaPlana + "</font>");
            }
        } catch (ServidorControllerException | UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        if (responsavel.isSer()) {
            return "forward:/v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";
        } else {
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

    }

    @RequestMapping(params = { "acao=cancelar" })
    public String cancelar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);

        try {
            //Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String usuCodigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
            if (!responsavel.isSer() && !responsavel.isCseSupOrg()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                model.addAttribute("tipo", "principal");
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String senhaNaoUtilizada = request.getParameter("SENHA");

            // Altera a senha de autorização do servidor
            if (responsavel.isSer()) {
                usuarioController.cancelaSenhaAutorizacao(responsavel.getUsuCodigo(), senhaNaoUtilizada, responsavel);
            } else {
                usuarioController.cancelaSenhaAutorizacao(usuCodigo, senhaNaoUtilizada, responsavel);
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.cancela", responsavel));

        } catch (final UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        if (responsavel.isSer()) {
            return "forward:/v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";
        } else {
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }
    }
}
