package com.zetra.econsig.service.relatorio;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessaRelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;

import net.sf.jasperreports.engine.JRDataSource;

/**
 * <p> Title: ReportControllerBean</p>
 * <p> Description: </p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ReportControllerBean implements ReportController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReportControllerBean.class);

    @Override
    public String makeReport(String formato, CustomTransferObject criterio, Map<String, Object> parameters, Relatorio relatorio, AcessoSistema responsavel) throws ReportControllerException {
        return makeReport(formato, parameters, criterio, relatorio, null, null, responsavel);
    }

    @Override
    public String makeReport(String formato, CustomTransferObject criterio, Map<String, Object> parameters, Relatorio relatorio, List<Object[]> conteudo, AcessoSistema responsavel) throws ReportControllerException {
        return makeReport(formato, parameters, criterio, relatorio, conteudo, null, responsavel);
    }

    @Override
    public String makeReport(String formato, Map<String, Object> parameters, Relatorio relatorio, JRDataSource myDataSource, AcessoSistema responsavel) throws ReportControllerException {
        return makeReport(formato, parameters, null, relatorio, null, myDataSource, responsavel);
    }

    private String makeReport(String formato, Map<String, Object> parameters, CustomTransferObject criterio, Relatorio relatorio, List<Object[]> conteudo, JRDataSource myDataSource, AcessoSistema responsavel) throws ReportControllerException {
        try {
            // Executa a geração do relatório
            String reportName = ReportManager.getInstance().build(formato, parameters, criterio, relatorio, conteudo, myDataSource, responsavel);

            // Grava log da geração do relatório
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.GERAR_RELATORIO, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.tipo.relatorio.arg0", responsavel, relatorio.getTitulo()));
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.arg0", responsavel, reportName));
            if (criterio != null && !TextHelper.isNull(criterio.getAttribute(ProcessaRelatorio.LOG_OBSERVACAO))) {
                // Adiciona ao log os critérios utilizados
                log.add(criterio.getAttribute(ProcessaRelatorio.LOG_OBSERVACAO).toString());
            }
            log.write();

            return reportName;
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ReportControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
