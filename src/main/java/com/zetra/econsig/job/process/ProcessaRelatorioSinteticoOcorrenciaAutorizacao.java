package com.zetra.econsig.job.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dinamico.RelatorioSinteticoOcorrenciaAutorizacaoInfo;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioSintetico</p>
 * <p>Description: Classe para processamento de relatorio sintetico
 * de ocorrência de consignação</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioSinteticoOcorrenciaAutorizacao extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSinteticoOcorrenciaAutorizacao.class);

    public ProcessaRelatorioSinteticoOcorrenciaAutorizacao(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        String strIniPeriodo = "";
        String strFimPeriodo = "";
        String strPeriodo = "";
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";
        String usuLogin = "";
        List<String> tocCodigos = new ArrayList<>();
        List<String> tmoCodigos = new ArrayList<>();

        String tipoEntidade = responsavel.getTipoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO) && parameterMap.containsKey("orgCodigo")) {
            tipoEntidade = "EST";
        }
        if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            tipoEntidade = "CSA";
        }

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

	        if (TextHelper.isNull(strPeriodo) && TextHelper.isNull(strIniPeriodo) && TextHelper.isNull(strFimPeriodo)) {
	            codigoRetorno = ERRO;
	            mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.ou.data.ini.data.fim.oca", responsavel);
	            return;
	        }
        }

        usuLogin = getParametro("OP_LOGIN", parameterMap);

        String complementoTitulo = !TextHelper.isNull(strPeriodo) ? ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strPeriodo) : (!TextHelper.isNull(strIniPeriodo) && !TextHelper.isNull(strFimPeriodo)) ? ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo) : "";
        String titulo = relatorio.getTitulo() + " - " + complementoTitulo;
        StringBuilder subTitulo = new StringBuilder();

        StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.sintetico.ocorrencia.autorizacao", responsavel), responsavel, parameterMap, null));

        String order = !TextHelper.isNull(parameterMap.get("ORDENACAO_AUX")) ? parameterMap.get("ORDENACAO_AUX")[0] : "";

        Map<String,String> tipoOrdMap = new HashMap<>();
        tipoOrdMap.put("ORDEM_QTD", parameterMap.get("ORDEM_QTD")[0]);
        tipoOrdMap.put("ORDEM_TOTAL", parameterMap.get("ORDEM_TOTAL")[0]);
        tipoOrdMap.put("ORDEM_PRESTACAO", parameterMap.get("ORDEM_PRESTACAO")[0]);
        tipoOrdMap.put("ORDEM_CAPITALDEVIDO", parameterMap.get("ORDEM_CAPITALDEVIDO")[0]);
        boolean agruparServicoAnalitico = getParametro("agruparServicoAnalitico", parameterMap) != null && getParametro("agruparServicoAnalitico", parameterMap).equals("true");

        if(agruparServicoAnalitico) {
            titulo = titulo.replace("Sintético ", "");
        }
        String[] campo = parameterMap.get("chkCAMPOS");
        List<String> campos = campo != null ? Arrays.asList(campo) : new ArrayList<>();

        List<String> camposQuery = new ArrayList<>();
        List<String> camposRelatorio = new ArrayList<>();

        if (campos != null && !campos.isEmpty()) {
            camposQuery.addAll(campos);
            camposRelatorio.addAll(campos);
            if (campos.contains(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo())) {
                camposQuery.add(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA.getCodigo());
                camposRelatorio.add(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA.getCodigo());
            }
            camposRelatorio.add(CamposRelatorioSinteticoEnum.CAMPO_CONTRATOS.getCodigo());
            camposRelatorio.add(CamposRelatorioSinteticoEnum.CAMPO_PRESTACAO.getCodigo());
            camposRelatorio.add(CamposRelatorioSinteticoEnum.CAMPO_VALOR.getCodigo());
            camposRelatorio.add(CamposRelatorioSinteticoEnum.CAMPO_CAPITAL_DEVIDO.getCodigo());
            camposRelatorio.add(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_NOME.getCodigo());
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.pelo.menos.uma.informacao.ser.exibida.relatorio", responsavel));
            return;
        }

        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String sboCodigo = getFiltroSboCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String uniCodigo = getFiltroUniCodigo(parameterMap, subTitulo, nome, session, responsavel);
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

        subTitulo.append(System.getProperty("line.separator"));
        // Correspondente
        List<String> corCodigos = new ArrayList<>();
        if (responsavel.isCor()) {
            corCodigos.add(getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel));
        }

        String reportName = null;
        try {
            criterio.setAttribute("CAMPOS", camposQuery);
            criterio.setAttribute("CAMPOS_RELATORIO", camposRelatorio);
            criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
            criterio.setAttribute("DATA_INI", paramIniPeriodo);
            criterio.setAttribute("DATA_FIM", paramFimPeriodo);
            if (!TextHelper.isNull(strPeriodo)) {
            	criterio.setAttribute("DATA_PERIODO", DateHelper.parsePeriodString(strPeriodo));
            }
            criterio.setAttribute("ORG_CODIGO", orgCodigo);
            criterio.setAttribute("SBO_CODIGO", sboCodigo);
            criterio.setAttribute("UNI_CODIGO", uniCodigo);
            criterio.setAttribute("CSA_CODIGO", csaCodigo);
            criterio.setAttribute("SVC_CODIGO", svcCodigos);
            if (parameterMap.containsKey("corCodigo")) {
                String correspondentes[] = (parameterMap.get("corCodigo"));
                if(!correspondentes[0].equals("")) {
                    if (correspondentes.length == 0 || correspondentes[0].substring(0, 2).equals("-1")) {
                        corCodigos.add("-1");
                        subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                    } else {
                        subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)).append(": ");
                        for(String cor : correspondentes){
                            String [] correspondente = cor.split(";");
                            corCodigos.add(correspondente[0]);
                            subTitulo.append(correspondente[2]).append(", ");
                        }
                        subTitulo.deleteCharAt(subTitulo.length()-2);
                    }
                    subTitulo.append(System.getProperty("line.separator"));
                    criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
                }
            }
            criterio.setAttribute("SAD_CODIGO", sadCodigos);
            criterio.setAttribute("ORDER", order);
            criterio.setAttribute("ORIGEM_ADE", origensAdes);
            criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
            if (responsavel.isCor() && TextHelper.isNull(parameterMap.get("corCodigo"))) {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
            }

            criterio.setAttribute(Columns.USU_LOGIN, usuLogin);
            criterio.setAttribute(Columns.TOC_CODIGO, tocCodigos);
            criterio.setAttribute(Columns.TMO_CODIGO, tmoCodigos);
            criterio.setAttribute("TIPO_ORD", tipoOrdMap);
            criterio.setAttribute("TERMINO_ADE", motivoTerminoAdes);
            criterio.setAttribute("RESPONSAVEL", responsavel);

            String strFormato = getStrFormato();

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);

            RelatorioSinteticoOcorrenciaAutorizacaoInfo sinteticoReportInfo = new RelatorioSinteticoOcorrenciaAutorizacaoInfo(relatorio);
            sinteticoReportInfo.setCriterios(criterio);
            sinteticoReportInfo.buildJRXML(parameters, responsavel);

            String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

            // DESENV-17457 - Quando o relatório gerado é através do analitico e não sintético precisamos colocar o arquivo de relatório na pasta analitica e não na pasta sintetica
            if(agruparServicoAnalitico) {
                File reportIn = new File(reportNameZip);
                File reportOut = new File(reportNameZip.replace("sint_", ""));
                FileHelper.copyFile(reportIn, reportOut);
                FileHelper.delete(reportNameZip);
            }

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
