package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioDecisaoJudicial</p>
 * <p>Description: Classe para processamento do relatório de decisões judiciais</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioDecisaoJudicial extends ProcessaRelatorio {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioDecisaoJudicial.class);

    public ProcessaRelatorioDecisaoJudicial(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        String strIniPeriodo = "";
        String strFimPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        if (parameterMap.containsKey("periodoIni")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            if (!TextHelper.isNull(strIniPeriodo)) {
                paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            }
        }
        if (parameterMap.containsKey("periodoFim")) {
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            if (!TextHelper.isNull(strFimPeriodo)) {
                paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
            }
        }

        if (TextHelper.isNull(paramIniPeriodo) || TextHelper.isNull(paramFimPeriodo)) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel);
            return;
        }

        String cpf = getParametro("CPF", parameterMap);
        String matricula = getParametro("RSE_MATRICULA", parameterMap);

        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder("");

        StringBuilder nome = new StringBuilder((getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.decisao.judicial", responsavel), responsavel, parameterMap, null)));

        // Consignatária
        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Correspondente
        String corCodigo = getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Estabelecimento
        String estCodigo = getFiltroEstCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Órgão
        List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Serviço
        List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Serviço
        List<String> sadCodigos = getFiltroSadCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Serviço
        List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Matrícula
        if (!TextHelper.isNull(matricula)) {
            subTitulo.append(System.lineSeparator()).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.arg0", responsavel, matricula));
        }

        // CPF
        if (!TextHelper.isNull(cpf)) {
            if (TextHelper.isNull(matricula)) {
                subTitulo.append(System.lineSeparator());
            } else {
                subTitulo.append(" - ");
            }
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.cpf.arg0", responsavel, cpf));
        }

        String tipoJustica = getFiltroTipoJustica(parameterMap, subTitulo, nome, session, responsavel);
        String estadoJustica = getFiltroEstadoJustica(parameterMap, subTitulo, nome, session, responsavel);
        String comarcaJustica = getFiltroComarcaJustica(parameterMap, subTitulo, nome, session, responsavel);
        String numeroProcessoJustica = getFiltroNumeroProcessoJustica(parameterMap, subTitulo, nome, session, responsavel);

        String reportName = null;
        try {
            criterio.setAttribute("DATA_INI", paramIniPeriodo);
            criterio.setAttribute("DATA_FIM", paramFimPeriodo);
            criterio.setAttribute(Columns.SER_CPF, cpf);
            criterio.setAttribute(Columns.RSE_MATRICULA, matricula);
            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            criterio.setAttribute(Columns.COR_CODIGO, corCodigo);
            criterio.setAttribute(Columns.EST_CODIGO, estCodigo);
            criterio.setAttribute(Columns.ORG_CODIGO, orgCodigo);
            criterio.setAttribute(Columns.SVC_CODIGO, svcCodigos);
            criterio.setAttribute(Columns.SAD_CODIGO, sadCodigos);
            criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
            criterio.setAttribute(Columns.TJU_CODIGO, tipoJustica);
            criterio.setAttribute(Columns.UF_COD, estadoJustica);
            criterio.setAttribute(Columns.CID_CODIGO, comarcaJustica);
            criterio.setAttribute(Columns.DJU_NUM_PROCESSO, numeroProcessoJustica);
            criterio.setAttribute("RESPONSAVEL", responsavel);

            String strFormato = getStrFormato();

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put("RESPONSAVEL", responsavel);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }
}
