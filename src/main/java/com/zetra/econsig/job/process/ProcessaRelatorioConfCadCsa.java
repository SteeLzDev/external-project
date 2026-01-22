package com.zetra.econsig.job.process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioConfCadMargem</p>
 * <p>Description: Classe para processamento de relatorio de conferência de cadastro de consignatária</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioConfCadCsa extends ProcessaRelatorio {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioConfCadCsa.class);

    public ProcessaRelatorioConfCadCsa(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);
    }

    @Override
    protected void executar() {
        String reportName = null;
        try {
            String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.conf.cad.csa", responsavel), responsavel, parameterMap, null);
            String strFormato = getStrFormato();
            StringBuilder subTitulo = new StringBuilder();

            List<String> nseCodigos = getFiltroNseCodigo(parameterMap, subTitulo, new StringBuilder(nome), session, responsavel);
            criterio.setAttribute("NSE_CODIGO", nseCodigos);

            String[] csaAtivo = (parameterMap.get("CSA_ATIVO"));
            if (csaAtivo != null && csaAtivo.length > 0) {
                Short[] status = Arrays.stream(csaAtivo).map(Short::valueOf).toArray(Short[]::new);
                criterio.setAttribute("CSA_ATIVO", status);
            }

            String possuiAdeAtiva = getParametro("possuiAdeAtiva", parameterMap);
            if (!TextHelper.isNull(possuiAdeAtiva)) {
                criterio.setAttribute("POSSUI_ADE_ATIVA", possuiAdeAtiva.equals("true"));
            }

            String permiteIncluirAde = getParametro("permiteIncluirAde", parameterMap);
            if (!TextHelper.isNull(permiteIncluirAde)) {
                criterio.setAttribute("PERMITE_INCLUIR_ADE", permiteIncluirAde.equals("true"));
            }

            String titulo = relatorio.getTitulo();

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toUpperCase());
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put("RESPONSAVEL", responsavel);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

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
