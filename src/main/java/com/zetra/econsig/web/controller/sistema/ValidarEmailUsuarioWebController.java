package com.zetra.econsig.web.controller.sistema;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ValidarEmailUsuarioWebController</p>
 * <p>Description: Controlador Web para o caso de uso Validar Email Usuario.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision:  $
 * $Date: 2019-12-16 13:57:41 -0300 (ter, 16 dez 2019) $
 */
@Controller
public class ValidarEmailUsuarioWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarEmailUsuarioWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(value = { "/v3/validarEmailUsuario" }, params = { "acao=enviarVerificacao" })
    public String enviarVerificacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        String usuCodigo = JspHelper.verificaVarQryStr(request, "usuCodigo");
        String usuEmail = JspHelper.verificaVarQryStr(request, "USU_EMAIL");
        boolean editarEmail = JspHelper.verificaVarQryStr(request, "editarEmail").equals("true");

        String codValidacaoEmail = SynchronizerToken.generateToken();

        try {

        UsuarioTransferObject dadosUsuario = usuarioController.findUsuario(usuCodigo, responsavel);
        String emailUsuario = dadosUsuario.getUsuEmail();

        //Modifico o email do usuário se o usuário pode digitar o email e o alterou
        if (!editarEmail && usuEmail != emailUsuario) {
            dadosUsuario.setUsuEmail(usuEmail);
        }

        //Insiro a chave de validação do email
        dadosUsuario.setUsuChaveValidacaoEmail(codValidacaoEmail);

        String link = request.getRequestURL().toString();
        link += "?acao=confirmarEmail&enti=" + responsavel.getTipoEntidade();

        usuarioController.alteraChaveValidacaoEmail(dadosUsuario, link, responsavel);

        model.addAttribute("retornoEnvio", ApplicationResourcesHelper.getMessage("mensagem.envio.email.confirmacao", responsavel, dadosUsuario.getUsuNome(), dadosUsuario.getUsuEmail()));
        model.addAttribute("editarEmail", editarEmail);
        model.addAttribute("retornoErro", Boolean.FALSE);

        } catch (UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            model.addAttribute("retornoErro", Boolean.TRUE);
            model.addAttribute("editarEmail", Boolean.FALSE);
            return viewRedirect("jsp/validarEmailUsuario/validarEmailUsuario", request, session, model, responsavel);
        }

        return viewRedirect("jsp/validarEmailUsuario/validarEmailUsuario", request, session, model, responsavel);
    }

    @RequestMapping(value = { "/v3/validarEmailUsuario" }, params = { "acao=confirmarEmail" })
    public String confirmarEmail(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);
        String codValidacao = JspHelper.verificaVarQryStr(request, "codValidacao");
        
        if (TextHelper.isNull(codValidacao)) {
        	session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "enti"))) {
            responsavel.setTipoEntidade(JspHelper.verificaVarQryStr(request, "enti"));
        }

        try {

        TransferObject usuario = usuarioController.buscarUsuarioPorCodValidaEmail(codValidacao, responsavel);
        if (usuario == null) {
            // Retorno mensagem de erro se o codigo de recuperação de senha for invalido
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.validaemail.link.confirmacao", responsavel));
            model.addAttribute("retornoErro", Boolean.TRUE);
            model.addAttribute("editarEmail", Boolean.FALSE);
            return viewRedirect("jsp/validarEmailUsuario/validarEmailUsuario", request, session, model, responsavel);
        }

        model.addAttribute("retornoEnvio", ApplicationResourcesHelper.getMessage("mensagem.validacaoemail.sucesso.usuario", responsavel));
        model.addAttribute("editarEmail", Boolean.FALSE);
        model.addAttribute("retornoErro", Boolean.FALSE);

        } catch (UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/validarEmailUsuario/validarEmailUsuario", request, session, model, responsavel);
    }
}
