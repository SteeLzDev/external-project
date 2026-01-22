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
 * <p>Title: ProcessaPoliticaPrivacidade</p>
 * <p>Description: Classe que gera politica de privacidade em PDF</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaPoliticaPrivacidade extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaPoliticaPrivacidade.class);

    public ProcessaPoliticaPrivacidade(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, AcessoSistema responsavel) {
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

        titulo = "POLÍTICA DE PRIVACIDADE";
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, getParametro(ReportManager.REPORT_FILE_NAME, parameterMap));
        parameters.put(ReportManager.REPORT_DIR_EXPORT, getParametro(ReportManager.REPORT_DIR_EXPORT, parameterMap));
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put("CAMINHO_BACKGROUND", getParametro("CAMINHO_BACKGROUND", parameterMap));
        parameters.put("CORPO", getParametro("CORPO", parameterMap));

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
