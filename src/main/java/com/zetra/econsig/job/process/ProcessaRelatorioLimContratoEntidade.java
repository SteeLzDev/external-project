package com.zetra.econsig.job.process;

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
 * <p>Title: ProcessaRelatorioLimContratoEntidade</p>
 * <p>Description: Classe para processamento de relatorio de limite de contrato por entidade
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioLimContratoEntidade extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioLimContratoEntidade.class);

    public ProcessaRelatorioLimContratoEntidade(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {

        String matricula = parameterMap.get("matricula")[0];
        String nomeServidor = parameterMap.get("nome")[0];
        String tipo = parameterMap.get("tipo")[0];
        String cargo = parameterMap.get("cargo")[0];
        String ordenacao = parameterMap.get("ORDENACAO_AUX")[0];

        String titulo = relatorio.getTitulo();
        StringBuilder subtitulo = new StringBuilder("");

        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.limite.contrato.entidade", responsavel), responsavel, parameterMap, null);

        if (matricula != null && !matricula.equals("")) {
            subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.arg0", responsavel, matricula));
        }
        if (nomeServidor != null && !nomeServidor.equals("")) {
            subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.nome.singular.arg0", responsavel, nomeServidor));
        }

        if (tipo != null && !tipo.equals("")) {
            subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.categoria.funcional.singular.arg0", responsavel, tipo));
        }
        if (cargo != null && !cargo.equals("")) {
            subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.cargo.arg0", responsavel, cargo));
        }

        criterio.setAttribute("CARGO", cargo);
        criterio.setAttribute("MATRICULA", matricula);
        criterio.setAttribute("NOME", nomeServidor);
        criterio.setAttribute("TIPO", tipo);
        criterio.setAttribute("ORDER", ordenacao);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
        parameters.put("RESPONSAVEL", responsavel);

        String reportName = null;
        try {
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
