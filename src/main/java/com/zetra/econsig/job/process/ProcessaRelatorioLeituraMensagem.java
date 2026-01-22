package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;


/**
 * <p> Title: ProcessaRelatorioLeituraMensagem</p>
 * <p> Description: Classe para processamento de relatorios de leitura de mensagem</p>
 * <p> Copyright: Copyright (c) 2015 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioLeituraMensagem extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioLeituraMensagem.class);

    public ProcessaRelatorioLeituraMensagem(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);
    }

    @Override
    protected void executar() {

        String strFormato = getStrFormato();
        String strIniPeriodo = getParametro("periodoIni", parameterMap);
        String strFimPeriodo = getParametro("periodoFim", parameterMap);
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";

        if (!TextHelper.isNull(strIniPeriodo) && !TextHelper.isNull(strFimPeriodo)) {
            paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        //getNomeArquivo(relatorio.getTitulo(), responsavel, parameterMap, null)
        String titulo = relatorio.getTitulo();
        StringBuilder subTitulo = new StringBuilder();
        StringBuilder nomeArquivo = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.confirmacoes.leituras.mensagens", responsavel), responsavel, parameterMap, null));

        String menCodigo = getFiltroMenCodigo(parameterMap, subTitulo, nomeArquivo, session, responsavel);

        String entidade = getParametro("entidadeCombo", parameterMap);

        if(!TextHelper.isNull(entidade)){
            if(entidade.equals(AcessoSistema.ENTIDADE_CSE)){
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.entidade.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel)));
            } else if(entidade.equals(AcessoSistema.ENTIDADE_CSA)){
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.entidade.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)));
            } else if(entidade.equals(AcessoSistema.ENTIDADE_ORG)){
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.entidade.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel)));
            } else if(entidade.equals(AcessoSistema.ENTIDADE_COR)){
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.entidade.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)));
            } else if(entidade.equals(AcessoSistema.ENTIDADE_SER)){
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.entidade.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)));
            } else if(entidade.equals(AcessoSistema.ENTIDADE_SUP)){
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.entidade.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel)));
            }
        }

        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nomeArquivo, session, responsavel);

        if(!entidade.equals("CSA")){
            csaCodigo = null;
        }

        String nomeUsuario = getParametro("nome", parameterMap);
        if(!TextHelper.isNull(nomeUsuario)){
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.nome.singular.arg0", responsavel, nomeUsuario));
        }

        String loginUsuario = getParametro("OP_LOGIN", parameterMap);
        if(!TextHelper.isNull(loginUsuario)){
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.login.arg0", responsavel, loginUsuario));
        }

        String cpfUsuario = null;
        if (parameterMap.containsKey("CPF") && !TextHelper.isNull(getParametro("CPF", parameterMap))) {
            cpfUsuario = getParametro("CPF", parameterMap);
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.cpf.arg0", responsavel, cpfUsuario));
        }

        criterio.setAttribute("DATA_INI", paramIniPeriodo);
        criterio.setAttribute("DATA_FIM", paramFimPeriodo);
        criterio.setAttribute(Columns.MEN_CODIGO, menCodigo);
        criterio.setAttribute("ENTIDADE", entidade);
        criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
        criterio.setAttribute(Columns.USU_NOME, nomeUsuario);
        criterio.setAttribute(Columns.USU_LOGIN, loginUsuario);
        criterio.setAttribute(Columns.USU_CPF, cpfUsuario);

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
            geraZip(nomeArquivo.toString(), reportName);

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
