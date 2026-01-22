package com.zetra.econsig.web.controller.exportarmovimento;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.ExportaMovimentoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.folha.ExportaMovimentoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ProcessarExportacaoMovimentoWebController</p>
 * <p>Description: Controlador Web para o caso de uso de processar exportação de movimento financeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 29172 $
 * $Date: 2020-03-25 14:33:04 -0300 (qua, 25 mar 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/processarMovimento" })
public class ProcessarExportacaoMovimentoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessarExportacaoMovimentoWebController.class);

    @Autowired
    private ExportaMovimentoController exportaMovimentoController;

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean isEst = (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO));
        model.addAttribute("isEst", isEst);

        String rotuloEstabelecimento = ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular", responsavel);
        String rotuloOrgao = ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel);

        String acao = ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo();
        String reexportar = (!TextHelper.isNull(request.getParameter(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo())))
                          ? request.getParameter(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo())
                          : (String) request.getAttribute(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo());
        if (!TextHelper.isNull(reexportar)) {
            acao = !reexportar.toLowerCase().equals("true") ? acao : ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo();
        }
        model.addAttribute(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo(), (acao.equals(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo())) ? Boolean.FALSE : Boolean.TRUE);

        Object param = ParamSist.getInstance().getParam(CodedValues.TPC_EXP_MOV_POR_ESTABELECIMENTO, responsavel);
        boolean expPorEstab = param != null && param.equals(CodedValues.TPC_SIM);

        List<String> orgCodigos = request.getParameterValues("org_codigo") != null ? Arrays.asList(request.getParameterValues("org_codigo")) : null;
        List<String> estCodigos = request.getParameterValues("est_codigo") != null ? Arrays.asList(request.getParameterValues("est_codigo")) : null;

        // Traz parâmetros para conferência do usuário
        List<TransferObject> paramSist = null;
        try {
            paramSist = parametroController.selectParamSistCse(null, null, null, null, responsavel);

            // Define as datas de corte e o período atual
            String periodAnterior = DateHelper.toPeriodString(PeriodoHelper.getInstance().getPeriodoAnterior(null, responsavel));

            // Lista os códigos de verbas a serem exportados
            List<TransferObject> verbas = exportaMovimentoController.selectResumoExportacao(orgCodigos, estCodigos, null, acao.equals(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo()), responsavel);

            Iterator<TransferObject> itPar = paramSist.iterator();

            String parametros = "";

            while (itPar.hasNext()) {
                CustomTransferObject next = (CustomTransferObject) itPar.next();
                if (next.getAttribute(Columns.TPC_CODIGO).toString().equals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO) ||
                        next.getAttribute(Columns.TPC_CODIGO).toString().equals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL) ||
                        next.getAttribute(Columns.TPC_CODIGO).toString().equals(CodedValues.TPC_EXPORTA_LIQCANC_NAO_PAGAS) ||
                        next.getAttribute(Columns.TPC_CODIGO).toString().equals(CodedValues.TPC_FOLHA_ACEITA_ALTERACAO)) {

                    String valorParametro = (next.getAttribute(Columns.PSI_VLR) != null ? next.getAttribute(Columns.PSI_VLR).toString() : "");

                    if (next.getAttribute(Columns.TPC_DOMINIO) != null && next.getAttribute(Columns.TPC_DOMINIO).equals("SN")) {
                        if (valorParametro.equals(CodedValues.TPC_SIM)) {
                            valorParametro = ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);
                        } else if (valorParametro.equals(CodedValues.TPC_NAO)) {
                            valorParametro = ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
                        }
                    }

                    parametros += next.getAttribute(Columns.TPC_DESCRICAO) + " = " + valorParametro + "<BR>";
                }
            }

            // Imprime parâmetros para conferência do usuário
            if (parametros != "" && parametros.endsWith("<BR>")) {
                parametros = parametros.substring(0, parametros.length() - 4);
                session.setAttribute(CodedValues.MSG_ALERT, parametros);
            }

            model.addAttribute("verbas", verbas);
            model.addAttribute("periodAnterior", periodAnterior);
        } catch (ParametroControllerException | ConsignanteControllerException | PeriodoException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            paramSist = new ArrayList<>();
        }

        model.addAttribute("paramSist", paramSist);
        model.addAttribute("expPorEstab", expPorEstab);
        model.addAttribute("rotuloEstabelecimento", rotuloEstabelecimento);
        model.addAttribute("rotuloOrgao", rotuloOrgao);
        model.addAttribute("orgCodigos", orgCodigos);
        model.addAttribute("estCodigos", estCodigos);

        return viewRedirect("jsp/integrarFolha/processarExportacaoMovFinanceiro", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=processar" })
    public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        int timeout = session.getMaxInactiveInterval();
        try {
            List<String> orgCodigos = request.getParameterValues("org_codigo") != null ? Arrays.asList(request.getParameterValues("org_codigo")) : null;
            List<String> estCodigos = request.getParameterValues("est_codigo") != null ? Arrays.asList(request.getParameterValues("est_codigo")) : null;

            // Desabilita o timeout da sessão de usuário
            session.setMaxInactiveInterval(-1);
            // Pega quais verbas foram marcadas
            List<String> verbas = request.getParameterValues("verbas") != null ? Arrays.asList(request.getParameterValues("verbas")) : null;

            String acao = ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo();
            if (!TextHelper.isNull(request.getParameter(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo()))) {
                acao = !request.getParameter(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo()).toLowerCase().equals("true") ? acao : ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo();
            }
            model.addAttribute(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo(), (acao.equals(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo())) ? Boolean.FALSE : Boolean.TRUE);

            // Gera o arquivo de exportação de movimento
            ExportaMovimentoDelegate expDelegate = new ExportaMovimentoDelegate();
            ParametrosExportacao parametrosExportacao = new ParametrosExportacao();
            parametrosExportacao.setOrgCodigos(orgCodigos)
                                .setEstCodigos(estCodigos)
                                .setVerbas(verbas)
                                .setAcao(acao)
                                .setOpcao(JspHelper.verificaVarQryStr(request, "opcao"))
                                .setResponsavel(responsavel);

            String nomeArqSaida = expDelegate.exportaMovimentoFinanceiro(parametrosExportacao, responsavel);
            // Seta mensagem de sucesso na sessão do usuário
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.exportacao.concluida.sucesso", responsavel));
            // Envia nome do arquivo gerado para a interface criar opção para download
            model.addAttribute("nomeArqSaida", nomeArqSaida);
        } catch (ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.falha.exportacao", responsavel, ex.getMessage()));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }  finally {
            session.setMaxInactiveInterval(timeout);
        }

        return iniciar(request, response, session, model);
    }
}
