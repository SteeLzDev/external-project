package com.zetra.econsig.web.controller.ajuda;

import java.util.ArrayList;
import java.util.List;

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
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper.AcessoRecurso;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Ajuda;
import com.zetra.econsig.service.sistema.AjudaController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: VisualizarAjudaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Visualizar Ajuda.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class VisualizarAjudaWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VisualizarAjudaWebController.class);

    @Autowired
    private AjudaController ajudaController;

    @Autowired
    private SistemaController sistemaController;

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/visualizarAjuda" }, params = { "acao=visualizarAjuda" })
    public String visualizarAjuda(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        boolean mostraComboPreview = responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_EDT_MANUAL_AJUDA_SISTEMA);
        String filtro = null;
        if (mostraComboPreview) {
            try {
                filtro = JspHelper.verificaVarQryStr(request, "FILTRO_TIPO");
            } catch (Exception ex) {
            }
        }
        List<TransferObject> ajudas = null;
        try {
            ajudas = ajudaController.lstTopicosAjuda(filtro, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (mostraComboPreview && filtro != null && filtro.isEmpty()) {
            if (responsavel.isSup()) {
                filtro = "7";
            } else if (responsavel.isCse()) {
                filtro = "1";
            } else if (responsavel.isCsa()) {
                filtro = "2";
            } else if (responsavel.isOrg()) {
                filtro = "3";
            } else if (responsavel.isCor()) {
                filtro = "4";
            } else if (responsavel.isSer()) {
                filtro = "6";
            }
        }

        String emailSuporte = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel);
        if (TextHelper.isNull(emailSuporte)) {
            emailSuporte = "suporte@zetrasoft.com.br";
        }
        model.addAttribute("mostraComboPreview", mostraComboPreview);
        model.addAttribute("filtro", filtro);
        model.addAttribute("ajudas", ajudas);
        model.addAttribute("emailSuporte", emailSuporte);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.ajuda", responsavel));

        return viewRedirect("jsp/visualizarAjuda/listarAjuda", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/visualizarAjudaContexto" }, params = { "acao=visualizar" })
    public String listarAjuda(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            AcessoRecurso acesso = (AcessoRecurso) session.getAttribute("acesso_recurso");
            String rotuloBotaoEditar = ApplicationResourcesHelper.getMessage("rotulo.botao.editar.ajuda", responsavel);
            String acrCodigo = JspHelper.verificaVarQryStr(request, "acrCodigo") != null ? JspHelper.verificaVarQryStr(request, "acrCodigo") : null;

            String acrOperacao = "";
            String acrParametro = "";
            if (TextHelper.isNull(acrCodigo) && acesso != null) {
                acrCodigo = acesso.getAcrCodigo();
                acrParametro = acesso.getAcrParametro();
                acrOperacao = acesso.getAcrOperacao();
            } else {

                com.zetra.econsig.persistence.entity.AcessoRecurso recurso = sistemaController.findAcessoRecurso(acrCodigo, responsavel);
                acrParametro = recurso.getAcrParametro();
                acrOperacao = recurso.getAcrOperacao();
            }

            String funCodigo = "";
            if (!TextHelper.isNull(acrCodigo)) {
                List<String> acrCodigos = new ArrayList<>();
                acrCodigos.add(acrCodigo);
                List<TransferObject> retorno = null;
                try {
                    retorno = ajudaController.lstFuncoesPapeisAcessoRecurso(acrCodigos, null, null, null, null, responsavel);
                } catch (Exception ex) {
                }
                if (retorno == null || retorno.isEmpty()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.dados.ajuda", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem?tipo=popup", request, session, model, responsavel);
                }
                funCodigo = !TextHelper.isNull(((CustomTransferObject) retorno.get(0)).getAttribute(Columns.ACR_FUN_CODIGO)) ? ((CustomTransferObject) retorno.get(0)).getAttribute(Columns.ACR_FUN_CODIGO).toString() : "";
            }

            Ajuda ajuda = null;
            try {
                ajuda = ajudaController.findAjudaByPrimaryKey(acrCodigo, responsavel);

                if (!TextHelper.isNull(ajuda.getAjuHtml()) && ajuda.getAjuHtml().equals("N")) {
                    ajuda.setAjuTexto(new Markdown4jProcessorExtended().process(TextHelper.forHtmlContent(ajuda.getAjuTexto())).toString());
                }

            } catch (Exception ex) {

            }
            model.addAttribute("acrCodigo", acrCodigo);
            model.addAttribute("funCodigo", funCodigo);
            model.addAttribute("rotuloBotaoEditar", rotuloBotaoEditar);
            model.addAttribute("ajuda", ajuda);
            model.addAttribute("acrParametro", acrParametro);
            model.addAttribute("acrOperacao", acrOperacao);
            model.addAttribute("ajudaPopup", request.getParameter("ajudaPopup") != null ? (request.getParameter("ajudaPopup").equals(CodedValues.TPC_SIM) ? true : false) : false);

            if (ajuda == null) {
                return viewRedirect("jsp/visualizarAjuda/visualizarAjudaErro", request, session, model, responsavel);
            } else {
                return viewRedirect("jsp/visualizarAjuda/visualizarAjuda", request, session, model, responsavel);
            }
        } catch (ConsignanteControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
