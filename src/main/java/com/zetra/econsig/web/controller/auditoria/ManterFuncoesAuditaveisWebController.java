package com.zetra.econsig.web.controller.auditoria;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.PeriodicidadeEmailAuditoriaEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterFuncoesAuditaveisWebController</p>
 * <p>Description: Controlador Web para o caso de uso de manutenção de funções auditáveis</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 **/
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterFuncoesAuditaveis" })
public class ManterFuncoesAuditaveisWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterFuncoesAuditaveisWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String codigo = (responsavel.isCsa() ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "codigo"));
        String tipo = JspHelper.verificaVarQryStr (request, "tipo");
        String descricao = JspHelper.verificaVarQryStr (request, "descricao");

        if (!tipo.equals(AcessoSistema.ENTIDADE_CSE) && !tipo.equals(AcessoSistema.ENTIDADE_ORG) &&
            !tipo.equals(AcessoSistema.ENTIDADE_CSA) && !tipo.equals(AcessoSistema.ENTIDADE_COR) &&
            !tipo.equals(AcessoSistema.ENTIDADE_SUP)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.auditoria.uso.incorreto", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            List<TransferObject> funcoesAuditaveis = usuarioController.lstFuncoesAuditaveis(tipo, codigo, responsavel);

            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.FUN_GRF_CODIGO, "");
            funcoesAuditaveis.add(cto);

            String periodoEnvioEmailAudit = CodedValues.PER_ENV_EMAIL_AUDIT_DESABILITADO;

            if (responsavel.isCseSupOrg()) {
                periodoEnvioEmailAudit = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSE_ORG, responsavel);
            } else {
                periodoEnvioEmailAudit = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSA_COR, responsavel);
            }

            int total = usuarioController.countUsuariosAuditores(tipo, codigo, CodedValues.STU_ATIVO, responsavel);
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

            List<UsuarioTransferObject> usuariosAuditores = usuarioController.lstUsuariosAuditores(tipo, codigo, CodedValues.STU_ATIVO, offset, size, responsavel);

            String tituloPagina = ApplicationResourcesHelper.getMessage("rotulo.auditoria.titulo.pagina", responsavel, descricao);

            String msgAnterior = (session.getAttribute("mantem_auditoria") != null) ? (String) session.getAttribute(CodedValues.MSG_INFO) : null;
            if (session.getAttribute("mantem_auditoria") != null) {
                session.removeAttribute("mantem_auditoria");
            }
            session.setAttribute(CodedValues.MSG_INFO, (!TextHelper.isNull(msgAnterior)) ? msgAnterior + "<br>" :"" + ApplicationResourcesHelper.getMessage("mensagem.auditoria.periodicidade.emails", responsavel, PeriodicidadeEmailAuditoriaEnum.recuperaPeriodicidadeEmailAuditoria(periodoEnvioEmailAudit).descricao()));

            model.addAttribute("tipo", tipo);
            model.addAttribute("codigo", codigo);
            model.addAttribute("tituloPagina", tituloPagina);
            model.addAttribute("funcoesAuditaveis", funcoesAuditaveis);
            model.addAttribute("usuariosAuditores", usuariosAuditores);

            return viewRedirect("jsp/manterFuncoesAuditaveis/manterFuncoesAuditaveis", request, session, model, responsavel);

        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);

            String tipo = JspHelper.verificaVarQryStr(request, "tipo").toUpperCase();
            String codigo = (responsavel.isCsa() ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "codigo"));
            String[] funcoes = request.getParameterValues("funcao");
            List<String> funcoesArr = (funcoes != null ? Arrays.asList(funcoes) : null);

            usuarioController.updateFuncoesAuditaveis(funcoesArr, tipo, codigo, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.auditoria.salvas.sucesso", responsavel));
            session.setAttribute("mantem_auditoria", "true");

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
