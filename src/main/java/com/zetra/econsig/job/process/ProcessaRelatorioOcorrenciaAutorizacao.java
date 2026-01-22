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
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioOcorrenciaAutorizacao</p>
 * <p>Description: Classe para processamento do relatório de ocorrência de consignação</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioOcorrenciaAutorizacao extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioOcorrenciaAutorizacao.class);

    public ProcessaRelatorioOcorrenciaAutorizacao(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
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
        String strPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        String cpf = "";
        String matricula = "";
        String usuLogin = "";
        List<String> tocCodigos = new ArrayList<>();
        List<String> tmoCodigos = new ArrayList<>();
        String[] papel = parameterMap.get("papel");
        boolean cse = false;
        boolean org = false;
        boolean csa = false;
        boolean cor = false;
        boolean ser = false;
        boolean sup = false;

        for (String i : papel) {
            cse = cse || i.equals("cse") ? true : false;
            org = org || i.equals("org") ? true : false;
            csa = csa || i.equals("csa") ? true : false;
            cor = cor || i.equals("cor") ? true : false;
            ser = ser || i.equals("ser") ? true : false;
            sup = sup || i.equals("sup") ? true : false;
        }        

        HashMap<String, Object> parameters = new HashMap<>();
        cpf = getParametro("CPF", parameterMap);
        matricula = getParametro("RSE_MATRICULA", parameterMap);
        boolean obrDataInclusaoPage = Boolean.parseBoolean(getParametro("obrDataInclusaoPage", parameterMap));
        if(obrDataInclusaoPage) {
        	if (parameterMap.containsKey("periodoIni")&& parameterMap.containsKey("periodoFim")) {
	            strIniPeriodo = getParametro("periodoIni", parameterMap);
	            strFimPeriodo = getParametro("periodoFim", parameterMap);
	            if (!TextHelper.isNull(strIniPeriodo) && !TextHelper.isNull(strFimPeriodo)) {
	            	paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
	                paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
	            }
        	} else {
        		session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
        		return;
        	}

        	strPeriodo = getParametro("periodo", parameterMap);

        	if (TextHelper.isNull(strPeriodo) && TextHelper.isNull(strIniPeriodo) && TextHelper.isNull(strFimPeriodo) && TextHelper.isNull(cpf) && TextHelper.isNull(matricula)) {
        		codigoRetorno = ERRO;
        		mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.periodo.ou.data.ini.data.fim.oca", responsavel);
        		return;
        	}
        }
        usuLogin = getParametro("OP_LOGIN", parameterMap);

        String complementoTitulo = !TextHelper.isNull(strPeriodo) ? ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strPeriodo) : (!TextHelper.isNull(strIniPeriodo) && !TextHelper.isNull(strFimPeriodo)) ? ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo) : ""; 
        String titulo = relatorio.getTitulo() + " - " + complementoTitulo;
        StringBuilder subTitulo = new StringBuilder("");

        StringBuilder nome = new StringBuilder((getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.ocorrencia.autorizacao", responsavel), responsavel, parameterMap, null)));
        String agrupamento = responsavel.isCsaCor()? "ORG" : "CSA";

        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String corCodigo = getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> sadCodigos = getFiltroSadCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> origensAdes  = getFiltroOrigemContrato(parameterMap, subTitulo, nome, session, responsavel);

        String [] motivoTerminoAde = parameterMap.containsKey("chkTermino") ? (String[]) parameterMap.get("chkTermino") : null;
        List<String> motivoTerminoAdes = motivoTerminoAde != null ? Arrays.asList(motivoTerminoAde) : null;
        if (motivoTerminoAdes != null && !motivoTerminoAdes.isEmpty()) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.termino.contratos.arg0", responsavel, ""));
            for (String termino : motivoTerminoAde) {
                if (termino.equals(CodedValues.TERMINO_ADE_CANCELADA)) {
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.cancelamento", responsavel).toUpperCase());
                } else if (termino.equals(CodedValues.TERMINO_ADE_CONCLUSAO)) {
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.conclusao", responsavel).toUpperCase());
                } else if (termino.equals(CodedValues.TERMINO_ADE_RENEGOCIADA)) {
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.renegociacao", responsavel).toUpperCase());
                } else if (termino.equals(CodedValues.TERMINO_ADE_LIQ_ANTECIPADA)) {
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.liquidacao.antecipada", responsavel).toUpperCase());
                } else if (termino.equals(CodedValues.TERMINO_ADE_VENDA)) {
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.relatorio.venda", responsavel).toUpperCase());
                }
                subTitulo.append(",");
            }
            subTitulo.deleteCharAt(subTitulo.length() - 1);
        }

        if (motivoTerminoAdes != null && origensAdes != null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.filtros.origem.termino.contrato.nao.podem.ser.aplicados.simultaneamente", responsavel));
            return;
        }

        if (!TextHelper.isNull(matricula)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.arg0", responsavel, matricula));
        }

        if (!TextHelper.isNull(cpf)) {
            if (TextHelper.isNull(matricula)) {
                subTitulo.append(System.getProperty("line.separator"));
            } else {
                subTitulo.append(" - ");
            }
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.cpf.arg0", responsavel, cpf));
        }

        if (!TextHelper.isNull(usuLogin)) {
            subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.login.responsavel.arg0", responsavel, usuLogin));
        }

        // tipo ocorrencia
        if (parameterMap.containsKey("tocCodigo")) {
            String tocs[] = (parameterMap.get("tocCodigo"));
            if (!tocs[0].equals("")) {
                String values[];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.ocorrencia.arg0", responsavel, ""));
                for (int i = 0; i < tocs.length; i++) {
                    if (!tocs[i].equals("")) {
                        values = tocs[i].split(";");
                        tocCodigos.add(values[0]);
                        if (i == (tocs.length - 1)) {
                            subTitulo.append(" ").append(values[1]);
                        } else {
                            subTitulo.append(" ").append(values[1]).append(",");
                        }
                    }
                }
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.ocorrencia.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                tocCodigos = CodedValues.TOC_CODIGOS_AUTORIZACAO;
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

        String reportName = null;
        try {
            criterio.setAttribute("DATA_INI", paramIniPeriodo);
            criterio.setAttribute("DATA_FIM", paramFimPeriodo);
            if (!TextHelper.isNull(strPeriodo)) {
            	criterio.setAttribute("DATA_PERIODO", DateHelper.parsePeriodString(strPeriodo));
            }
            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            criterio.setAttribute(Columns.COR_CODIGO, corCodigo);
            criterio.setAttribute(Columns.ORG_CODIGO, orgCodigo);
            criterio.setAttribute(Columns.SER_CPF, cpf);
            criterio.setAttribute(Columns.RSE_MATRICULA, matricula);
            criterio.setAttribute(Columns.USU_LOGIN, usuLogin);
            criterio.setAttribute(Columns.TOC_CODIGO, tocCodigos);
            criterio.setAttribute(Columns.TMO_CODIGO, tmoCodigos);
            criterio.setAttribute(Columns.SVC_CODIGO, svcCodigos);
            criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
            criterio.setAttribute(Columns.SAD_CODIGO, sadCodigos);
            criterio.setAttribute("ORIGEM_ADE", origensAdes);
            criterio.setAttribute("TERMINO_ADE", motivoTerminoAdes);
            criterio.setAttribute("AGRUPAMENTO", agrupamento);
            criterio.setAttribute("RESPONSAVEL", responsavel);
            criterio.setAttribute("cse", cse);
            criterio.setAttribute("org", org);
            criterio.setAttribute("csa", csa);
            criterio.setAttribute("cor", cor);
            criterio.setAttribute("ser", ser);
            criterio.setAttribute("sup", sup);

            String strFormato = getStrFormato();

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put(ReportManager.PARAM_NAME_TIPO_AGRUPAMENTO, agrupamento);
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
