package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioConfCadMargem</p>
 * <p>Description: Classe para processamento de relatorio de conferência de cadastro de órgãos
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioConfCadOrg extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioConfCadOrg.class);

    public ProcessaRelatorioConfCadOrg(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        String titulo = relatorio.getTitulo();
        String estabelecimento = "";
        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.conf.cad.org", responsavel), responsavel, parameterMap, null);
        String estCodigo = responsavel.getEstCodigo();

        if (parameterMap.containsKey("estCodigo")) {
            String values[] = (parameterMap.get("estCodigo"));
            if (values.length == 0 || values[0].equals("")) {
                titulo += System.getProperty("line.separator") + ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase());
                estabelecimento = ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase();
            } else {
                values = values[0].split(";");
                estCodigo = values[0];
                titulo += System.getProperty("line.separator") + ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular.arg0", responsavel, values[2]);
                estabelecimento = values[2];
            }
        } else if (!TextHelper.isNull(estCodigo)) {
            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

            try {
                EstabelecimentoTransferObject cto = cseDelegate.findEstabelecimento(estCodigo, responsavel);
                if (cto != null) {
                    estabelecimento = cto.getEstNome();
                }
            } catch (ConsignanteControllerException ex) {
                LOG.error("Falha ao recuperar nome do estabelecimento.", ex);
            }
        }

        String reportName = null;
        try {
            criterio.setAttribute(Columns.EST_CODIGO, estCodigo);

            String strFormato = getStrFormato();

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put(ReportManager.PARAM_NAME_ESTABELECIMENTO, estabelecimento);
            parameters.put("RESPONSAVEL", responsavel);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

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
