package com.zetra.econsig.job.process;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.EstatisticoProcessamentoBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ProcessaRelatorioEstatisticoProcessamento extends ProcessaRelatorio {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioEstatistico.class);

    private final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);

    public ProcessaRelatorioEstatisticoProcessamento(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        owner = responsavel.getUsuCodigo();

        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder("");
        StringBuilder nome = new StringBuilder((getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.estatistico.processamento", responsavel), responsavel, parameterMap, null)));
        String reportName = null;
        String strFormato = getStrFormato();

        List<String> funCodigos = new ArrayList<>();
        funCodigos.add(CodedValues.FUN_IMP_RET_INTEGRACAO);
        funCodigos.add(CodedValues.FUN_IMP_CAD_MARGENS);
        funCodigos.add(CodedValues.FUN_EXP_MOV_FINANCEIRO);

        List<String> tarCodigos = new ArrayList<>();
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_MOVIMENTO_FINANCEIRO.getCodigo());

        criterio.setAttribute("funCodigos", funCodigos);
        criterio.setAttribute("tarCodigos", tarCodigos);
        criterio.setAttribute("RESPONSAVEL", responsavel);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
        parameters.put("RESPONSAVEL", responsavel);

        try {
            List<TransferObject> estatisticoProcessamentoTipoArquivos = relatorioController.listaEstatisticoProcessamentoTipoArquivos(tarCodigos, responsavel);

            List<Date> harPeriodos = new ArrayList<>();
            List<String> harPeriodosFormatados = new ArrayList<>();
            List<TransferObject> estatisticoProcessamentoPeriodos = relatorioController.listaEstatisticoProcessamentoPeriodos(funCodigos, tarCodigos, responsavel);
            for (TransferObject estatisticoProcessamentoPeriodo : estatisticoProcessamentoPeriodos) {
                String harPeriodo = estatisticoProcessamentoPeriodo.getAttribute(Columns.HAR_PERIODO).toString();
                harPeriodos.add(DateHelper.toSQLDate(DateHelper.objectToDate(harPeriodo)));
                harPeriodosFormatados.add(DateHelper.reformat(harPeriodo, "yyyy-MM-dd", "MM/yyyy"));
            }

            Collections.reverse(harPeriodosFormatados);
            parameters.put("HAR_PERIODOS", harPeriodosFormatados);

            JRDataSource estatisticoProcessamentoDataSource = getDataSource(harPeriodos, harPeriodosFormatados, estatisticoProcessamentoTipoArquivos);
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), parameters, relatorio, estatisticoProcessamentoDataSource, responsavel);

            String reportNameZip = geraZip(nome.toString(), reportName);
            enviaEmail(reportNameZip);
        } catch (RelatorioControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
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

    public JRDataSource getDataSource(List<Date> harPeriodos, List<String> harPeriodosFormatados, List<TransferObject> estatisticoProcessamentoTipoArquivos) throws ZetraException {
        List<EstatisticoProcessamentoBean> estatisticoProcessamentoBeans = relatorioController.listaEstatisticoProcessamento(harPeriodos, harPeriodosFormatados, estatisticoProcessamentoTipoArquivos, responsavel);
        return new JRBeanCollectionDataSource(estatisticoProcessamentoBeans);
    }
}