package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioMarketShareCsa</p>
 * <p>Description: Classe para processamento de Relatório Market Share de consignatária de empréstimo consignado.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioMarketShareCsa extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioMarketShareCsa.class);

    public ProcessaRelatorioMarketShareCsa(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder();
        StringBuilder nomeRelatorio = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.market.share.csa", responsavel), responsavel, parameterMap, null));

        StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.market.share.csa", responsavel), responsavel, parameterMap, null));

        Map<String, String> periodo = getFiltroPeriodo(parameterMap, subTitulo, nomeRelatorio, session, responsavel);

        String strPeriodo = periodo.get("PERIODO");

        if (TextHelper.isNull(strPeriodo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        String estCodigo = getFiltroEstCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> nseCodigos = getFiltroNseCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> csaCodigos = getFiltroCsaCodigos(parameterMap, subTitulo, nome, session, responsavel);

        String strFormato = getStrFormato();

        try {
            criterio.setAttribute("PERIODO", strPeriodo);
            criterio.setAttribute("EST_CODIGO", estCodigo);
            criterio.setAttribute("ORG_CODIGO", orgCodigo);
            criterio.setAttribute("NSE_CODIGO", nseCodigos);
            criterio.setAttribute("SVC_CODIGO", svcCodigos);
            criterio.setAttribute("CSA_CODIGO", csaCodigos);

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nomeRelatorio.toString());
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (ReportControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }
}
