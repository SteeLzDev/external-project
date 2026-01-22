package com.zetra.econsig.job.process;

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
 * <p>Title: ProcessaRelatorioComunicacoes</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioComunicacoes extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioComunicacoes.class);


    public ProcessaRelatorioComunicacoes(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        StringBuilder nomeArquivo = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.comunicacoes", responsavel), responsavel, parameterMap, null));
        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder();

        boolean exibeSomenteCse = false;
        String cmnPendente = null;
        String cmnLida = null;
        String ascCodigo = null;
        String matricula = null;
        String cpf = null;
        String cmnNumero = null;
        String periodoIni = null;
        String periodoFim = null;
        String strFormato = getStrFormato();

        if (parameterMap.containsKey("comunicacaoApenasGestor") && !TextHelper.isNull(getParametro("comunicacaoApenasGestor", parameterMap))) {
            exibeSomenteCse = Boolean.valueOf(getParametro("comunicacaoApenasGestor", parameterMap));
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.exibe.comunicacao.apenas.gestor.arg0", responsavel, ApplicationResourcesHelper.getMessage(exibeSomenteCse?"rotulo.sim":"rotulo.nao", responsavel)));
        }
        List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nomeArquivo, session, responsavel);
        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nomeArquivo, session, responsavel);

        if (parameterMap.containsKey("periodoIni") && !TextHelper.isNull(getParametro("periodoIni", parameterMap)) && parameterMap.containsKey("periodoFim") && !TextHelper.isNull(getParametro("periodoFim", parameterMap))) {
            periodoIni = getParametro("periodoIni", parameterMap);
            periodoFim = getParametro("periodoFim", parameterMap);
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.data.comunicacao.de.arg0.ate.arg1", responsavel, periodoIni, periodoFim));
        }

        if (parameterMap.containsKey("ASC_CODIGO") && !TextHelper.isNull(getParametro("ASC_CODIGO", parameterMap))) {
            ascCodigo = getParametro("ASC_CODIGO", parameterMap);
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.categoria.assunto.arg0", responsavel, ascCodigo));
        }
        if (parameterMap.containsKey("RSE_MATRICULA") && !TextHelper.isNull(getParametro("RSE_MATRICULA", parameterMap))) {
            matricula = getParametro("RSE_MATRICULA", parameterMap);
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.arg0", responsavel, matricula));
        }
        if (parameterMap.containsKey("CPF") && !TextHelper.isNull(getParametro("CPF", parameterMap))) {
            cpf = getParametro("CPF", parameterMap);
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.cpf.arg0", responsavel, cpf));
        }
        if (parameterMap.containsKey("CMN_NUMERO") && !TextHelper.isNull(getParametro("CMN_NUMERO", parameterMap))) {
            cmnNumero = getParametro("CMN_NUMERO", parameterMap);
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.identificador.comunicacao.arg0", responsavel, cmnNumero));
        }
        if (parameterMap.containsKey("comunicacaoPendente") && !TextHelper.isNull(getParametro("comunicacaoPendente", parameterMap))) {
            cmnPendente = getParametro("comunicacaoPendente", parameterMap);
            if (cmnPendente.equals("0")) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.comunicacoes.pendentes.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel)));
            } else if (cmnPendente.equals("1")) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.comunicacoes.pendentes.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel)));
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.comunicacoes.pendentes.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel)));
            }
        }
        if (parameterMap.containsKey("comunicacaoLida") && !TextHelper.isNull(getParametro("comunicacaoLida", parameterMap))) {
            cmnLida = getParametro("comunicacaoLida", parameterMap);
            if (cmnLida.equals("0")) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.comunicacoes.lidas.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel)));
            } else if (cmnLida.equals("1")) {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.comunicacoes.lidas.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel)));
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.comunicacoes.lidas.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel)));
            }
        }

        criterio.setAttribute("exibeSomenteCse", exibeSomenteCse);
        criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
        criterio.setAttribute(Columns.ORG_CODIGO, orgCodigo);
        criterio.setAttribute(Columns.CMN_ASC_CODIGO, ascCodigo);
        criterio.setAttribute("periodoIni", periodoIni);
        criterio.setAttribute("periodoFim", periodoFim);
        criterio.setAttribute(Columns.RSE_MATRICULA, matricula);
        criterio.setAttribute(Columns.SER_CPF, cpf);
        criterio.setAttribute(Columns.CMN_NUMERO, cmnNumero);
        criterio.setAttribute(Columns.CMN_PENDENCIA, cmnPendente);
        criterio.setAttribute("CMN_LIDA", cmnLida);
        criterio.setAttribute("APENAS_CMN_PAI", Boolean.TRUE);
        criterio.setAttribute("responsavel", responsavel);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nomeArquivo.toString());
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
        parameters.put("RESPONSAVEL", responsavel);

        try {
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            // Gera Zip
            String reportNameZip = geraZip(nomeArquivo.toString(), reportName);

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
