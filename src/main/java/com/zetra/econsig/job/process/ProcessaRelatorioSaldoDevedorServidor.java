package com.zetra.econsig.job.process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ProcessaRelatorioSaldoDevedorServidor</p>
 * <p>Description: Classe de processamento do relatorio saldo devedor servidor
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ProcessaRelatorioSaldoDevedorServidor extends ProcessaRelatorio{

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSaldoDevedorServidor.class);

    public ProcessaRelatorioSaldoDevedorServidor(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        owner = responsavel.getUsuCodigo();

        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String strIniPeriodo = "";
        String strFimPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        final HashMap<String, Object> parameters = new HashMap<>();
        final StringBuilder subTitulo = new StringBuilder("");
        final StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.saldo.devedor.servidor", responsavel), responsavel, parameterMap, null));

        final List<String> serCpfMultiplo = parameterMap.containsKey("SER_CPF_MULTIPLO") && !TextHelper.isNull(parameterMap.get("SER_CPF_MULTIPLO")) ? Arrays.asList(parameterMap.get("SER_CPF_MULTIPLO")) : null;
        final List<String> rseMatriculaMultiplo = parameterMap.containsKey("RSE_MATRICULA_MULTIPLO") && !TextHelper.isNull(parameterMap.get("RSE_MATRICULA_MULTIPLO")) ? Arrays.asList(parameterMap.get("RSE_MATRICULA_MULTIPLO")) : null;
        final String csaCodigo = getFiltroCsaCodigoSaldoDevedorServidor(parameterMap, subTitulo, nome, session, responsavel);

        if (parameterMap.containsKey("periodoIni") && parameterMap.containsKey("periodoFim")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            paramIniPeriodo = !TextHelper.isNull(strIniPeriodo) ? reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00") : null;
            paramFimPeriodo = !TextHelper.isNull(strFimPeriodo) ? reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59") : null;
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        if(!TextHelper.isNull(strIniPeriodo)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.devedor.servidor.filtro.data.inicial", responsavel, strIniPeriodo));
        }

        if(!TextHelper.isNull(strFimPeriodo)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.devedor.servidor.filtro.data.final", responsavel, strFimPeriodo));
        }

        if(!TextHelper.isNull(serCpfMultiplo)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.devedor.servidor.filtro.cpf", responsavel, TextHelper.join(serCpfMultiplo, ", ")));
        }

        if(!TextHelper.isNull(rseMatriculaMultiplo)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.devedor.servidor.filtro.matricula", responsavel, TextHelper.join(rseMatriculaMultiplo, ", ")));
        }

        criterio.setAttribute("DATA_INI", paramIniPeriodo);
        criterio.setAttribute("DATA_FIM", paramFimPeriodo);

        criterio.setAttribute(Columns.SER_CPF, serCpfMultiplo);
        criterio.setAttribute(Columns.RSE_MATRICULA, rseMatriculaMultiplo);
        criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);

        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
        parameters.put(ReportManager.PARAM_NAME_TITULO, relatorio.getTitulo());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());

        try {
            String reportName = null;
            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

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
