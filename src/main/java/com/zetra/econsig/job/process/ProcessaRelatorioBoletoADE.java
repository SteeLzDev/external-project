package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioBoletoADE</p>
 * <p>Description: Versão em PDF de boletos de consignação</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioBoletoADE extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioBoletoADE.class);

    public ProcessaRelatorioBoletoADE(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, Boolean.FALSE, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }
    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        // seta o nome do arquivo
        String titulo = relatorio.getTitulo();

        titulo = "AUTORIZAÇÃO DESCONTO: " + getParametro("ADE_NUMERO", parameterMap);
        // criterio.setAttribute("RSE_CODIGO", getParametro("rseCodigo", parameterMap));

        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, getParametro(ReportManager.REPORT_FILE_NAME, parameterMap));
        parameters.put(ReportManager.REPORT_DIR_EXPORT, getParametro(ReportManager.REPORT_DIR_EXPORT, parameterMap));
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
//            parameters.put("MATRICULA", getParametro("MATRICULA", parameterMap));
//            parameters.put("NOME", getParametro("NOME", parameterMap));
        parameters.put("CAMINHO_BACKGROUND", getParametro("CAMINHO_BACKGROUND", parameterMap));
        parameters.put("SER_NOME", getParametro("SER_NOME", parameterMap));
        parameters.put("SER_CPF", getParametro("SER_CPF", parameterMap));
        parameters.put("SER_NRO_IDT", getParametro("SER_NRO_IDT", parameterMap));
        parameters.put("SER_DATA_NASC", getParametro("SER_DATA_NASC", parameterMap));
        parameters.put("SER_EST_CIVIL", getParametro("SER_EST_CIVIL", parameterMap));
        parameters.put("SER_END", getParametro("SER_END", parameterMap));
        parameters.put("SER_NRO", getParametro("SER_NRO", parameterMap));
        parameters.put("SER_COMPL", getParametro("SER_COMPL", parameterMap));
        parameters.put("SER_BAIRRO", getParametro("SER_BAIRRO", parameterMap));
        parameters.put("SER_UF", getParametro("SER_UF", parameterMap));
        parameters.put("SER_CIDADE", getParametro("SER_CIDADE", parameterMap));
        parameters.put("SER_CEP", getParametro("SER_CEP", parameterMap));
        parameters.put("SER_TEL", getParametro("SER_TEL", parameterMap));
        parameters.put("RSE_MATRICULA", getParametro("RSE_MATRICULA", parameterMap));
        parameters.put("RSE_DATA_ADMISSAO", getParametro("RSE_DATA_ADMISSAO", parameterMap));
        parameters.put("ORG_NOME", getParametro("ORG_NOME", parameterMap));
        parameters.put("USU_LOGIN", getParametro("USU_LOGIN", parameterMap));
        parameters.put("CDE_RANKING", getParametro("CDE_RANKING", parameterMap));
        parameters.put("SVC_DESCRICAO", getParametro("SVC_DESCRICAO", parameterMap));
        parameters.put("ADE_DATA", getParametro("ADE_DATA", parameterMap));
        parameters.put("ADE_NUMERO", getParametro("ADE_NUMERO", parameterMap));
        parameters.put("ADE_VLR", getParametro("ADE_VLR", parameterMap));
        parameters.put("ADE_PRAZO_REF", getParametro("ADE_PRAZO_REF", parameterMap));
        parameters.put("ADE_ANO_MES_INI", getParametro("ADE_ANO_MES_INI", parameterMap));
        parameters.put("ADE_ANO_MES_FIM", getParametro("ADE_ANO_MES_FIM", parameterMap));
        parameters.put("CNV_COD_VERBA", getParametro("CNV_COD_VERBA", parameterMap));
        parameters.put("CDE_VLR_LIBERADO", getParametro("CDE_VLR_LIBERADO", parameterMap));
        parameters.put("CSA_NOME", getParametro("CSA_NOME", parameterMap));
        parameters.put("MSG_BOLETO", getParametro("MSG_BOLETO", parameterMap));

        try {
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportController.makeReport("PDF", criterio, parameters, relatorio, responsavel);
        } catch (ReportControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
            LOG.error(mensagem, ex);
        } catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
            LOG.error(mensagem, ex);
        }
    }
}
