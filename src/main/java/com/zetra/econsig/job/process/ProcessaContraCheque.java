package com.zetra.econsig.job.process;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaContraCheque</p>
 * <p>Description: Classe que gera contra chaques em PDF</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaContraCheque extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaContraCheque.class);

    public ProcessaContraCheque(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, AcessoSistema responsavel) {
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

        try {
            titulo = "CONTRACHEQUE - " + DateHelper.reformat(getParametro("periodo", parameterMap), "yyyy-MM", "MMMMMMMMM/yyyy");
            criterio.setAttribute("PERIODO", getParametro("periodo", parameterMap));
            criterio.setAttribute("RSE_CODIGO", getParametro("rseCodigo", parameterMap));

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, getParametro(ReportManager.REPORT_FILE_NAME, parameterMap));
            parameters.put(ReportManager.REPORT_DIR_EXPORT, getParametro(ReportManager.REPORT_DIR_EXPORT, parameterMap));
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put("MATRICULA", getParametro("MATRICULA", parameterMap));
            parameters.put("NOME", getParametro("NOME", parameterMap));
            parameters.put("CAMINHO_BACKGROUND", getParametro("CAMINHO_BACKGROUND", parameterMap));

        } catch (ParseException e) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.folha.formato.periodo.mensal", responsavel);
            LOG.error(mensagem, e);
        }

        try {
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportController.makeReport("PDF", criterio, parameters, relatorio, responsavel);
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
