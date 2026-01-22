package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dinamico.RelatorioIntegracaoSemProcessamentoInfo;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioIntegracaoSemProcessamento</p>
 * <p>Description: Classe que dispara processo para montar relatório integração sem processamento</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioIntegracaoSemProcessamento extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioIntegracaoSemProcessamento.class);

    private final List<String> campos;
    private final List<TransferObject> dataSetList;

    public ProcessaRelatorioIntegracaoSemProcessamento(Relatorio relatorio, Map<String, String[]> parameterMap, List<String> campos, List<TransferObject> dataSetList, HttpSession session, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, Boolean.FALSE, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());

        this.campos = campos;
        this.dataSetList = dataSetList;
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        // seta o nome do arquivo
        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder("");

        StringBuilder nome = new StringBuilder(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.integracao.sem.processamento", responsavel) + "_" + getHoje("dd-MM-yy-HHmmss"));

        criterio.setAttribute("CAMPOS", campos);

        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put(ReportManager.REPORT_DIR_EXPORT, getParametro(ReportManager.REPORT_DIR_EXPORT, parameterMap));

        try {
            RelatorioIntegracaoSemProcessamentoInfo integracaoSemProcessamento = new RelatorioIntegracaoSemProcessamentoInfo(relatorio);
            integracaoSemProcessamento.setCriterios(criterio);
            integracaoSemProcessamento.buildJRXML(parameters, responsavel);

            List<Object[]> conteudoList = DTOToList(dataSetList, campos.toArray(new String[campos.size()]));

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportController.makeReport("XLS", criterio, parameters, relatorio, conteudoList, responsavel);
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
