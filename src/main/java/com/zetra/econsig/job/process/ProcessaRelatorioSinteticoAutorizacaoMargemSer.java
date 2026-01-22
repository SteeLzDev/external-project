package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

public class ProcessaRelatorioSinteticoAutorizacaoMargemSer extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSinteticoAutorizacaoMargemSer.class);

    public ProcessaRelatorioSinteticoAutorizacaoMargemSer(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);
    }

    @Override
    protected void executar() {
        final StringBuilder subTitulo = new StringBuilder();
        final StringBuilder nomeRelatorio = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.sintetico.autorizacao.margem.ser", responsavel), responsavel, parameterMap, null));
        final HashMap<String, Object> parameters = new HashMap<>();
        final List<String> csaCodigos = getFiltroCsaCodigos(parameterMap, new StringBuilder(), nomeRelatorio, session, responsavel);

        subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.empresa.singular.arg0", responsavel, getCseNome(responsavel)));

        criterio.setAttribute("CSA_CODIGO", csaCodigos);

        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nomeRelatorio.toString());
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, getStrFormato());
        parameters.put(ReportManager.PARAM_NAME_TITULO, relatorio.getTitulo());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put("RESPONSAVEL", responsavel);

        String reportName = null;
        try {
            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

            final String reportNameZip = geraZip(nomeRelatorio.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (final Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }

    }

}
