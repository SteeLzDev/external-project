package com.zetra.econsig.web.controller.usuario;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: BloquearUsuarioWebController.java</p>
 * <p>Description: Controlador Web para o caso de uso desbloqueio de usuário por número de tentativas de login.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/desbloquearNumTentativas" })
public class DesbloquearUsuarioNumTentativasWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DesbloquearUsuarioNumTentativasWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciarBloqueioServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        String retorno = iniciar(request, response, session, model);

        model.addAttribute("urlDestino", "../v3/desbloquearNumTentativas?acao=desbloquear");

        return retorno;
    }

    private String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        String funCodigo = "";
        String usuCodigo = "";
        String operacao = "";
        String status = "";
        UsuarioTransferObject usuario = null;
        String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        Map<String, String[]> parametros = new HashMap<>();

        try {
            usuCodigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
            operacao = JspHelper.verificaVarQryStr(request, "operacao");
            usuario = usuarioController.findUsuario(usuCodigo, responsavel);

            parametros.put("USU_CODIGO", new String[]{usuCodigo});
            parametros.put("operacao", new String[]{operacao});
            parametros.put("tipo", new String[]{tipo});
            parametros.put("_skip_history_", new String[]{"true"});

            status = JspHelper.verificaVarQryStr(request, "STATUS");
            funCodigo = CodedValues.FUN_DESBLOQUEAR_USUARIOS_SER;

            if (TextHelper.isNull(funCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.usuario.desbloquear", responsavel));
            model.addAttribute("msgConfirmacao", ApplicationResourcesHelper.getMessage("mensagem.confirmacao.desbloqueio.usuario", responsavel, usuario.getUsuLogin()));

            parametros.put("STATUS", new String[] { status });

        } catch (UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Busca atributos quanto a exigencia de Tipo de motivo da operacao
        Object objMtvOperacao = ParamSist.getInstance().getParam(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel);
        if (objMtvOperacao == null || objMtvOperacao.equals(CodedValues.TPC_NAO) || !FuncaoExigeMotivo.getInstance().exists(funCodigo, responsavel)) {
            return bloquear(usuCodigo, status, null, null, tipo, operacao, request, response, session, model);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("parametros", parametros);

        return viewRedirect("jsp/editarUsuarioServidor/efetivarAcaoUsuario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=desbloquear" })
    public String desbloquearServidor(@RequestParam(value = "USU_CODIGO", required = true, defaultValue = "") String usuCodigo,
                           @RequestParam(value = "STATUS", required = true, defaultValue = "") String status,
                           @RequestParam(value = "TMO_CODIGO", required = true, defaultValue = "") String tmoCodigo,
                           @RequestParam(value = "ADE_OBS", required = true, defaultValue = "") String ousObs,
                           @RequestParam(value = "tipo", required = true, defaultValue = "") String tipo,
                           @RequestParam(value = "operacao", required = true, defaultValue = "") String operacao,
            HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return bloquear(usuCodigo, status, tmoCodigo, ousObs, tipo, operacao, request, response, session, model);
    }

    private String bloquear(@RequestParam(value = "USU_CODIGO", required = true, defaultValue = "") String usuCodigo,
                           @RequestParam(value = "STATUS", required = true, defaultValue = "") String status,
                           @RequestParam(value = "TMO_CODIGO", required = true, defaultValue = "") String tmoCodigo,
                           @RequestParam(value = "ADE_OBS", required = true, defaultValue = "") String ousObs,
                           @RequestParam(value = "tipo", required = true, defaultValue = "") String tipo,
                           @RequestParam(value = "operacao", required = true, defaultValue = "") String operacao,
            HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Exige tipo de motivo da operacao
        boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel);
        boolean exibeMotivoOperacao = exigeMotivoOperacaoUsu && FuncaoExigeMotivo.getInstance().exists(responsavel.getFunCodigo(), responsavel) ? true : false;

        if(exigeMotivoOperacaoUsu && exibeMotivoOperacao && tmoCodigo.equals("")){
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        String link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
        String msgRet = ApplicationResourcesHelper.getMessage("mensagem.usuario.desbloqueado.sucesso", responsavel);
        status = CodedValues.STU_ATIVO;

        try {
            usuarioController.bloquearDesbloquearUsuario(usuCodigo, status, tipo, tmoCodigo, ousObs, responsavel, false);
            session.setAttribute(CodedValues.MSG_INFO,msgRet);
        } catch (UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        request.setAttribute("url64", link);
        return "jsp/redirecionador/redirecionar";
    }

}