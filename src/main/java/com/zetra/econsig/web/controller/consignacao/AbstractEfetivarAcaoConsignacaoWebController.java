package com.zetra.econsig.web.controller.consignacao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AbstractEfetivarAcaoConsignacaoWebController</p>
 * <p>Description: Controlador Web base para o casos de uso que necessitam informar o motivo de operação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractEfetivarAcaoConsignacaoWebController extends AbstractListarTodasConsignacoesWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractEfetivarAcaoConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private PeriodoController periodoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        // inicializa parâmetros com valores padrões
        model.addAttribute("temPermissaoAnexarReativar", false);
        model.addAttribute("isDestinoRenegociacao", false);
        model.addAttribute("deferirTodos", false);
        model.addAttribute("indeferirTodos", false);
        model.addAttribute("operacaoPermiteSelecionarPeriodo", false);
        model.addAttribute("nomeCampo", "");
    }

    @RequestMapping(params = { "acao=informarMotivoOperacao" })
    public String informarMotivoOperacao(@RequestParam(value = "FUN_CODIGO", required = true, defaultValue = "") String funCodigo, @RequestParam(value = "URL_DESTINO", required = true, defaultValue = "") String urlDestino, @RequestParam(value = "ADE_CODIGOS", required = true, defaultValue = "") String[] adeCodigos, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            String strAdeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");
            String strRseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            String obsOca = request.getParameter("obs");

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            if (adeCodigos == null) {
                adeCodigos = request.getParameterValues("chkADE");
            }

            if ((strAdeCodigo == null || strAdeCodigo.equals("")) && adeCodigos == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String tmo_codigo = null;
            String reqColumnsStr = "TMO_CODIGO";
            String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

            // Busca dados da ade
            List<TransferObject> autdesList = new ArrayList<>();
            Set<String> svcCodigoSet = new HashSet<>();
            String orgCodigo = null;
            java.util.Date adeAnoMesFim = null;
            try {
                if ((strAdeCodigo == null || strAdeCodigo.equals("")) && adeCodigos != null) {
                    CustomTransferObject autdes = null;
                    for (String adeCodigo : adeCodigos) {
                        autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                        autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                        autdesList.add(autdes);
                        svcCodigoSet.add(autdes.getAttribute(Columns.SVC_CODIGO).toString());
                        orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
                        adeAnoMesFim = DateHelper.leastDate((Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM), adeAnoMesFim);
                    }
                } else {
                    CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(strAdeCodigo, responsavel);
                    autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                    autdesList.add(autdes);
                    svcCodigoSet.add(autdes.getAttribute(Columns.SVC_CODIGO).toString());
                    orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
                    adeAnoMesFim = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM);
                }
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (strAdeCodigo != null) {
                urlDestino += "&ADE_CODIGO=" + strAdeCodigo;
            }

            if (obsOca != null) {
                urlDestino += "&obs=" + obsOca;
            }

            Iterator<String> it = svcCodigoSet.iterator();
            while (it.hasNext()) {
                String svcCodigo = it.next();
                if (!AcessoFuncaoServico.temAcessoFuncao(request, funCodigo, responsavel.getUsuCodigo(), svcCodigo)) {
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Repassa o per-page-token recebido nos parâmetros
            urlDestino = SynchronizerToken.updateTokenInURL(urlDestino, request);
            model.addAttribute("urlDestino", urlDestino);
            model.addAttribute("tmoCodigo", tmo_codigo);
            model.addAttribute("msgErro", msgErro);

            model.addAttribute("adeCodigos", adeCodigos);
            model.addAttribute("autdesList", autdesList);

            model.addAttribute("strAdeCodigo", strAdeCodigo);
            model.addAttribute("strRseMatricula", strRseMatricula);

            Set<java.util.Date> periodos = periodoController.listarPeriodosPermitidos(orgCodigo, adeAnoMesFim, responsavel);
            model.addAttribute("periodos", periodos);

            return viewRedirect("jsp/informarMotivoOperacao/informarMotivoOperacao", request, session, model, responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
