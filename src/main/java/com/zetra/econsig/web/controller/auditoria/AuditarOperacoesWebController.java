package com.zetra.econsig.web.controller.auditoria;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AuditoriaControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.auditoria.AuditoriaController;
import com.zetra.econsig.service.log.LogController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: AuditarOperacoesWebController</p>
 * <p>Description: Controlador Web para o caso de uso de auditar operações</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 **/
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/auditarOperacoes" })
public class AuditarOperacoesWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AuditarOperacoesWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private AuditoriaController auditoriaController;

    @Autowired
    private LogController logController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        String codigoEntidade = responsavel.getCodigoEntidade();
        String tipoEntidade = responsavel.getTipoEntidade();
        String diffDias = JspHelper.getParametroDifDatasRelatorio(null, responsavel);

        String exibeAuditado = JspHelper.verificaVarQryStr(request, "EXIBE_AUDITADO");
        boolean naoAuditado = !exibeAuditado.equals(CodedValues.TPC_SIM);

        TransferObject criterios = montarCriterioPesquisa(request);

        try {
            int total = auditoriaController.qtdeLogAuditoriaQuery(codigoEntidade, tipoEntidade, naoAuditado, criterios, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
            	offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            List<String> requestParams = new ArrayList<>(params);
            String linkAction = request.getRequestURI() + "?acao=iniciar";
            configurarPaginador(linkAction, "rotulo.paginacao.titulo.consignataria", total, size, requestParams, false, request, model);

            List<TransferObject> logsAuditoria = auditoriaController.lstLogAuditoriaQuery(codigoEntidade, tipoEntidade, naoAuditado, criterios, offset, size, responsavel);
            List<TransferObject> tiposEntidade = logController.lstTipoEntidade(null);
            List<TransferObject> funcoesAuditaveis = usuarioController.lstFuncoesAuditaveis(tipoEntidade, codigoEntidade, responsavel);

            model.addAttribute("diffDias", diffDias);
            model.addAttribute("logsAuditoria", logsAuditoria);
            model.addAttribute("tiposEntidade", tiposEntidade);
            model.addAttribute("funcoesAuditaveis", funcoesAuditaveis);

            return viewRedirect("jsp/auditarOperacoes/auditarOperacoes", request, session, model, responsavel);

        } catch (AuditoriaControllerException | LogControllerException | UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=auditar" })
    public String auditar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);
            TransferObject criterios = montarCriterioPesquisa(request);

            String codigoEntidade = responsavel.getCodigoEntidade();
            String tipoEntidade = responsavel.getTipoEntidade();

            String aplicarTodos = JspHelper.verificaVarQryStr(request, "APLICAR_TODOS");
            if (!aplicarTodos.equals(CodedValues.TPC_SIM)) {
                String[] aud = request.getParameterValues("AUD_CODIGO");
                if (aud == null || aud.length == 0) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.auditoria.selecionar.registro", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                // Audita os logs selecionados
                List<Integer> audCodigos = new ArrayList<>();
                for (String audCodigo : aud) {
                    audCodigos.add(Integer.valueOf(audCodigo));
                }

                auditoriaController.auditarLog(audCodigos, codigoEntidade, tipoEntidade, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.auditoria.sucesso", responsavel));

            } else {
                auditoriaController.auditarTodosLogs(codigoEntidade, tipoEntidade, criterios, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.auditoria.sucesso", responsavel));
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (AuditoriaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private TransferObject montarCriterioPesquisa(HttpServletRequest request) {
        String periodo_ini = JspHelper.verificaVarQryStr(request, "periodoIni");
        String periodo_fim = JspHelper.verificaVarQryStr(request, "periodoFim");
        String fun_codigo = JspHelper.verificaVarQryStr(request, "FUN_CODIGO");
        String ten_codigo = JspHelper.verificaVarQryStr(request, "TEN_CODIGO");
        String usu_login = JspHelper.verificaVarQryStr(request, "USU_LOGIN");
        String log_obs = JspHelper.verificaVarQryStr(request, "LOG_OBS");

        TransferObject criterios = new CustomTransferObject();
        if (!TextHelper.isNull(periodo_ini)) {
            criterios.setAttribute("PERIODO_INI", periodo_ini);
        }
        if (!TextHelper.isNull(periodo_fim)) {
            criterios.setAttribute("PERIODO_FIM", periodo_fim);
        }
        if (!TextHelper.isNull(fun_codigo)) {
            criterios.setAttribute("FUN_CODIGO", fun_codigo);
        }
        if (!TextHelper.isNull(ten_codigo)) {
            criterios.setAttribute("TEN_CODIGO", ten_codigo);
        }
        if (!TextHelper.isNull(usu_login)) {
            criterios.setAttribute("USU_LOGIN", CodedValues.LIKE_MULTIPLO + usu_login + CodedValues.LIKE_MULTIPLO);
        }
        if (!TextHelper.isNull(log_obs)) {
            criterios.setAttribute("LOG_OBS", CodedValues.LIKE_MULTIPLO + log_obs + CodedValues.LIKE_MULTIPLO);
        }

        return criterios;
    }
}
