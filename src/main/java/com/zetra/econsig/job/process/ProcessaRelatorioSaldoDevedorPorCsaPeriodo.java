package com.zetra.econsig.job.process;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dinamico.RelatorioSaldoDevedorPorCsaInfo;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioSaldoDevedorPorCsaPeriodo</p>
 * <p> Description: Processa o Relatório Sintético de Saldo Devedor por Consignatária e período</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioSaldoDevedorPorCsaPeriodo extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSaldoDevedorPorCsaPeriodo.class);

    public ProcessaRelatorioSaldoDevedorPorCsaPeriodo(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();
        StringBuilder subTitulo = new StringBuilder("");
        StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.sdv.por.csa", responsavel), responsavel, parameterMap, null));

        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String estCodigo = getFiltroEstCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
        Boolean csaAtivo = null;
        if (parameterMap.containsKey("CSA_ATIVO")) {
            String values[] = (parameterMap.get("CSA_ATIVO"));
            String ativo = values[0];
            csaAtivo = (ativo.equals("1")) ? true : false;

            if (csaAtivo) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.status.arg0", responsavel, ""));
                subTitulo.append(" ").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.ativa", responsavel));
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.status.arg0", responsavel, ""));
                subTitulo.append(" ").append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.consignataria.bloqueada", responsavel));
            }
        } else {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.status.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
        }

        criterio.setAttribute(Columns.EST_CODIGO, estCodigo);
        criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
        criterio.setAttribute(Columns.ORG_CODIGO, orgCodigo);
        criterio.setAttribute(Columns.CSA_ATIVO, csaAtivo);

        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
        parameters.put(ReportManager.PARAM_NAME_TITULO, relatorio.getTitulo());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());

        try {
            String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);

            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            List<Object> conteudo = relatorioController.geraRelatorioSaldoDevedorPorCsaPeriodo(criterio, responsavel);
            List<Object[]> conteudoList = DTOToList((List<TransferObject>)conteudo.get(0),(String[])conteudo.get(1));

            RelatorioSaldoDevedorPorCsaInfo relInfo =  new RelatorioSaldoDevedorPorCsaInfo(relatorio);
            CustomTransferObject criterioInfo = new CustomTransferObject();
            criterioInfo.setAttribute("countMes", conteudo.get(2));
            criterioInfo.setAttribute("countEst", conteudo.get(3));
            criterioInfo.setAttribute("campos", conteudo.get(4));
            criterioInfo.setAttribute("countPeriodos", conteudo.get(5));
            criterioInfo.setAttribute("alias", conteudo.get(6));
            criterioInfo.setAttribute("camposTotal", conteudo.get(7));
            criterioInfo.setAttribute("camposSpan", conteudo.get(8));
            criterioInfo.setAttribute("formato", getStrFormato());

            relInfo.setCriterios(criterioInfo);
            relInfo.buildJRXML(parameters, responsavel);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, conteudoList, responsavel);

            String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);


        } catch (RelatorioControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        } catch (ReportControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        } catch (IOException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }

    }

}
