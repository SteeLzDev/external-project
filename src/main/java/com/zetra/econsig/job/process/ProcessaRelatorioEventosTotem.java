package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.service.totem.EventosTotemController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioEventosTotem</p>
 * <p>Description: Classe para processamento de relatorio de eventos Totem</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: igor.lucas $
 * $Revision: 30014 $
 * $Date: 2020-07-29 11:09:56 -0300 (qua, 29 jul 2020) $
 */
public final class ProcessaRelatorioEventosTotem extends ProcessaRelatorio {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioEventosTotem.class);

    public ProcessaRelatorioEventosTotem(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);
    }

    @Override
    protected void executar() {
        try {
            String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.eventos.totem", responsavel), responsavel, parameterMap, null);
            String strFormato = parameterMap.get("formato")[0];
            StringBuilder subTitulo = new StringBuilder();
            TransferObject criterioParamListaEventosTotem = new CustomTransferObject();

            if (parameterMap.containsKey("matricula")) {
                String values[] = (parameterMap.get("matricula"));
                if (values.length > 0 && !TextHelper.isNull(values[0])) {
                    criterioParamListaEventosTotem.setAttribute("matricula", values[0]);
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.matricula.singular", responsavel)).append(" ").append(values[0]);
                }
            }

            if (parameterMap.containsKey("cpf")) {
                String values[] = (parameterMap.get("cpf"));
                if (values.length > 0 && !TextHelper.isNull(values[0])) {
                    criterioParamListaEventosTotem.setAttribute("cpf", values[0]);
                    if (!subTitulo.toString().isEmpty()) {
                        subTitulo.append(", ");
                    }
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel)).append(" ").append(values[0]);
                }
            }

            if (parameterMap.containsKey("periodoIni") && parameterMap.containsKey("periodoFim")) {
                String valuesIni[] = (parameterMap.get("periodoIni"));
                String valuesFim[] = (parameterMap.get("periodoFim"));
                if (valuesIni.length > 0 && !TextHelper.isNull(valuesIni[0]) && valuesFim.length > 0 && !TextHelper.isNull(valuesFim[0])) {
                    criterioParamListaEventosTotem.setAttribute("periodoIni", valuesIni[0]);
                    criterioParamListaEventosTotem.setAttribute("periodoFim", valuesFim[0]);
                    if (!subTitulo.toString().isEmpty()) {
                        subTitulo.append(", ");
                    }
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, valuesIni[0], valuesFim[0]));
                }
            }

            if (parameterMap.containsKey("vlrPossuiFotoPesquisa")) {
                String values[] = (parameterMap.get("vlrPossuiFotoPesquisa"));
                if (values.length > 0 && !TextHelper.isNull(values[0])) {
                    criterioParamListaEventosTotem.setAttribute("vlrPossuiFotoPesquisa", values[0]);
                    if (!subTitulo.toString().isEmpty()) {
                        subTitulo.append(", ");
                    }
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.evento.totem.possui.foto", responsavel)).append(" ").append(values[0].equals("1") ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel));
                }
            }

            EventosTotemController eventosTotemController = ApplicationContextProvider.getApplicationContext().getBean(EventosTotemController.class);

            List<TransferObject> eventosTotem = eventosTotemController.listarEventosTotem(criterioParamListaEventosTotem, responsavel);
            criterio.setAttribute("eventosTotem", eventosTotem);

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_NAME_TITULO, relatorio.getTitulo());
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put("RESPONSAVEL", responsavel);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);

            List<Object[]> arrayEventos = DTOToList(eventosTotem, new String[] { "MATRICULA", "CPF", "DATA", "IP", "DESCRICAO", "FOTO" });

            String reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, arrayEventos, responsavel);
            session.setAttribute("RELATORIO_TOTEM", reportName);

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
