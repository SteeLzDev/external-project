package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * <p>Title: ProcessaRelatorioOcorrenciaServidor</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioOcorrenciaServidor extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioOcorrenciaServidor.class);


    public ProcessaRelatorioOcorrenciaServidor(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {

        String strIniPeriodo = "";
        String strFimPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        String cpf = "";
        String matricula = "";
        String opLogin = "";
        String estCodigo = "";
        List<String> tocCodigos = new ArrayList<>();
        List<String> tmoCodigos = new ArrayList<>();

        HashMap<String, Object> parameters = new HashMap<>();

        if (parameterMap.containsKey("periodoIni")&& parameterMap.containsKey("periodoFim")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        cpf = getParametro("CPF", parameterMap);
        matricula = getParametro("RSE_MATRICULA", parameterMap);
        opLogin = getParametro("OP_LOGIN", parameterMap);

        List<String> orgCodigos = null;

        String titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo);
        StringBuilder subTitulo = new StringBuilder("");

        String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.ocorrencia.usu.ser", responsavel), responsavel, parameterMap, null);

        if ((!responsavel.isOrg() || (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)))) {
            List<String> orgNames = null;
            if (parameterMap.containsKey("orgCodigo")) {
                String values[] = (parameterMap.get("orgCodigo"));
                if (values[0].equals("")) {
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                    estCodigo = responsavel.getEstCodigo();
                } else {
                    orgCodigos = new ArrayList<>();
                    orgNames = new ArrayList<>();
                    try {
                        for (final String value : values) {
                            String[] separ = value.split(";");
                            orgCodigos.add(separ[0]);
                            orgNames.add(separ[2] + " ");
                        }
                        subTitulo.append(" - ").append(ApplicationResourcesHelper.getMessage("rotulo.orgao.abreviado.upper.arg0", responsavel, String.valueOf(orgNames)));
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }

        // tipo ocorrencia
        if (parameterMap.containsKey("tocCodigo")) {
            String tocs[] = (parameterMap.get("tocCodigo"));
            if (!tocs[0].equals("")) {
                String values[];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.ocorrencia.arg0", responsavel, ""));
                for (int i = 0; i < tocs.length; i++) {
                    values = tocs[i].split(";");
                    tocCodigos.add(values[0]);
                    if (i == (tocs.length - 1)) {
                        subTitulo.append(" ").append(values[1]);
                    } else {
                        subTitulo.append(" ").append(values[1]).append(",");
                    }
                }
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.ocorrencia.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                tocCodigos = CodedValues.TOC_CODIGOS_USUARIO;
            }
        }

        // tipo motivo operacao
        if (parameterMap.containsKey("tmoCodigo")) {
            String tmos[] = (parameterMap.get("tmoCodigo"));
            if (!tmos[0].equals("")) {
                String values[];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.operacao.arg0", responsavel, ""));
                for (int i = 0; i < tmos.length; i++) {
                    values = tmos[i].split(";");
                    tmoCodigos.add(values[0]);
                    if (i == (tmos.length - 1)) {
                        subTitulo.append(" ").append(values[1]);
                    } else {
                        subTitulo.append(" ").append(values[1]).append(",");
                    }
                }
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.motivo.operacao.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }
        }

        // usuários org acessam ocorrência de usuário servidor
        if (responsavel.isOrg() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            orgCodigos = new ArrayList<>();
            orgCodigos.add(responsavel.getCodigoEntidade());
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, responsavel.getNomeEntidade()));
        }

        if (!TextHelper.isNull(matricula)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.arg0", responsavel, matricula));
        }

        if (!TextHelper.isNull(cpf)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.cpf.arg0", responsavel, cpf));
        }

        if (!TextHelper.isNull(opLogin)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.login.responsavel.arg0", responsavel, opLogin));
        }

        criterio.setAttribute("DATA_INI", paramIniPeriodo);
        criterio.setAttribute("DATA_FIM", paramFimPeriodo);
        criterio.setAttribute("ORG_CODIGO", orgCodigos);
        criterio.setAttribute("CPF", cpf);
        criterio.setAttribute("MATRICULA", matricula);
        criterio.setAttribute("OP_LOGIN", opLogin);
        criterio.setAttribute("EST_CODIGO", estCodigo);
        criterio.setAttribute(Columns.TOC_CODIGO, tocCodigos);
        criterio.setAttribute(Columns.TMO_CODIGO, tmoCodigos);

        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome);
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
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
