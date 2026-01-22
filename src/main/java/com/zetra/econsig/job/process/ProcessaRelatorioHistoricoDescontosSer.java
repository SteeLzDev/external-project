package com.zetra.econsig.job.process;

import java.util.ArrayList;
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
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioHistoricoDescontosSer</p>
 * <p>Description: Classe para processamento do relatório de histórico de descontos do servidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioHistoricoDescontosSer extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioHistoricoDescontosSer.class);

    public ProcessaRelatorioHistoricoDescontosSer(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        String cpf = getParametro("CPF", parameterMap);
        String matricula = getParametro("RSE_MATRICULA", parameterMap);
        String adeNumero    = getParametro("ADE_NUMERO", parameterMap);
       // String[] adeNumeros = parameterMap.get("ADE_NUMERO_LIST");
        String[] ade = (parameterMap.get("ADE_NUMERO_LIST"));
        List<Long> adeNumeros = new ArrayList<>();
        String name = "";

        if(ade != null) {
            for(String contrato : ade) {
                adeNumeros.add(Long.parseLong(contrato));
            }
        }

        if (!TextHelper.isNull(adeNumero)) {
            adeNumeros.add(Long.parseLong(adeNumero));
        }

        if (!responsavel.isSer()) {
            if (TextHelper.isNull(matricula) && TextHelper.isNull(cpf) && TextHelper.isNull(ade) && TextHelper.isNull(adeNumero)) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("mensagem.pesquisa.informe.ade.matricula.ou.cpf", responsavel);
                return;
             }
        }

        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder("");
        StringBuilder nome = new StringBuilder("");

        if (adeNumeros.size() == 1) {
            nome = new StringBuilder((getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.ocorrencia.autorizacao", responsavel), responsavel, parameterMap, adeNumeros.get(0).toString())));
        } else {
            nome = new StringBuilder((getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.ocorrencia.autorizacao", responsavel), responsavel, parameterMap, name)));
        }

        // Consignatária
        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Correspondente
        String corCodigo = getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Verba
        String cnvCodVerba = getFiltroCnvCodVerba(parameterMap, subTitulo, nome, session, responsavel);

        // Matrícula
        if (!TextHelper.isNull(matricula)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.arg0", responsavel, matricula));
        }

        String[] sad = (parameterMap.get("SAD_CODIGO"));
        List<String> sadCodigos = sad != null ? Arrays.asList(sad) : null;
        List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // CPF
        if (!TextHelper.isNull(cpf)) {
            if (TextHelper.isNull(matricula)) {
                subTitulo.append(System.getProperty("line.separator"));
            } else {
                subTitulo.append(" - ");
            }
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.cpf.arg0", responsavel, cpf));
        }

        String reportName = null;
        try {
            criterio.setAttribute(Columns.SER_CPF, cpf);
            criterio.setAttribute(Columns.RSE_MATRICULA, matricula);
            criterio.setAttribute("ADE_NUMERO_LIST", adeNumeros);
            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            criterio.setAttribute(Columns.COR_CODIGO, corCodigo);
            criterio.setAttribute(Columns.CNV_COD_VERBA, cnvCodVerba);
            criterio.setAttribute("RESPONSAVEL", responsavel);
            criterio.setAttribute("SAD_CODIGO", sadCodigos);
            criterio.setAttribute("SVC_CODIGO", svcCodigos);


            String strFormato = getStrFormato();

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
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
