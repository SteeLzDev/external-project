package com.zetra.econsig.job.process;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.HeadingsScriptlet;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.folha.ImpRetornoController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioSinteticoGerencialGeralCsa</p>
 * <p> Description: Processa o Relatório Sintético Gerencial Geral Csa</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioSinteticoGerencialGeralCsa extends ProcessaRelatorio {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSinteticoGerencialGeralCsa.class);

    public ProcessaRelatorioSinteticoGerencialGeralCsa(Relatorio relatorio, Map<String, String[]> parameterMap, AcessoSistema responsavel) throws AgendamentoControllerException {
        super(relatorio, parameterMap, null, true, responsavel);
    }

    @Override
    protected void executar() {
        final StringBuilder titulo = new StringBuilder(ApplicationResourcesHelper.getMessage("rotulo.relatorio.sintetico.gerencial.csa.titulo", responsavel));
        final StringBuilder subtitulo = new StringBuilder("");
        final String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;

        // CONSTROI NOME DO ARQUIVO NO FORMATO: relatorio_dataHora
        final StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.sintetico.gerencial.csa", responsavel), responsavel, parameterMap, null));

        final String csaCodigo = getFiltroCsaCodigo(parameterMap, subtitulo, nome, session, responsavel);
        final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
        String csaNome = "";
        String ultimoPeriodoProcessado = "";
        try {
            final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);
            csaNome = csa.getCsaNome();

            final ImpRetornoController impRetornoController = ApplicationContextProvider.getApplicationContext().getBean(ImpRetornoController.class);
            final List<TransferObject> lstHistoricoRetorno = impRetornoController.lstHistoricoConclusaoRetorno(null, 1, null, true, responsavel);

            if((lstHistoricoRetorno != null) && !lstHistoricoRetorno.isEmpty()) {
                ultimoPeriodoProcessado = DateHelper.format((Date) lstHistoricoRetorno.get(0).getAttribute(Columns.HCR_PERIODO), "yyyy-MM-dd");
            }

        } catch (final ConsignatariaControllerException | ImpRetornoControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_LEGENDA_SETA_CIMA, getCaminhoSetaCimaLegenda(responsavel));
        parameters.put(ReportManager.PARAM_LEGENDA_SETA_BAIXO, getCaminhoSetaBaixoLegenda(responsavel));
        parameters.put(ReportManager.PARAM_LEGENDA_RETANGULO, getCaminhoRetanguloLegenda(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO_ENTIDADE, getCaminhoLogoEntidade(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO_ECONSIG, getCaminhoLogoEConsig(responsavel));
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(true,null,responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
        parameters.put(ReportManager.PARAM_CSE_NOME, getCseNome(responsavel));
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
        parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
        parameters.put(ReportManager.REPORT_SCRIPTLET, new HeadingsScriptlet());
        parameters.put(ReportManager.PARAM_NAME_CSA, csaNome);
        parameters.put(ReportManager.PARAM_NAME_MES_ANO, DateHelper.format(Calendar.getInstance().getTime(), "MMM/yyyy"));
        parameters.put(ReportManager.PARAM_NAME_ANO_ATUAL, DateHelper.format(Calendar.getInstance().getTime(), "yyyy"));
        parameters.put(ReportManager.PARAM_NAME_DATA_ATUAL, DateHelper.format(Calendar.getInstance().getTime(), LocaleHelper.getDatePattern()));
        parameters.put("RESPONSAVEL", responsavel);

        final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
        try {
            parameters = relatorioController.geraInformacoesSinteticoGerencialGeralCsa(parameters, ultimoPeriodoProcessado, csaCodigo, responsavel);
        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        String reportName = null;
        try {
            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            final String strFormato = getStrFormato();
            reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            final String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (final Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }
}