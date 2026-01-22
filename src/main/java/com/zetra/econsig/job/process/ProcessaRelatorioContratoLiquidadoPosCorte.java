package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioContratoLiquidadoPosCorte</p>
 * <p>Description: Classe para processamento de relatorio de contrato liquidado após o corte.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioContratoLiquidadoPosCorte extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioContratoLiquidadoPosCorte.class);

    public ProcessaRelatorioContratoLiquidadoPosCorte(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        String reportName = null;
        try {
            String strPeriodo = "";
            String paramPeriodo = "";

            if (parameterMap.containsKey("periodo")) {
                strPeriodo = getParametro("periodo", parameterMap);
                paramPeriodo = formatarPeriodo(strPeriodo);
            } else {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
                return;
            }

            String titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strPeriodo);
            StringBuilder subTitulo = new StringBuilder();
            StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.liquidado.pos.corte", responsavel), responsavel, parameterMap, null));
            String strFormato = getStrFormato();

            String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
            String corCodigo = getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel);
            List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
            List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
            Boolean ordenacaoPorCsa = responsavel.isCseSupOrg();

            criterio.setAttribute(Columns.ORG_CODIGO, orgCodigo);
            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            criterio.setAttribute(Columns.COR_CODIGO, corCodigo);
            criterio.setAttribute(Columns.SVC_CODIGO, svcCodigos);
            criterio.setAttribute("PERIODO", paramPeriodo);
            criterio.setAttribute("ORDEM", ordenacaoPorCsa);

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put("AGRUPAMENTO_CSA", ordenacaoPorCsa.toString());
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
