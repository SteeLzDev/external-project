package com.zetra.econsig.job.process;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioComissionamentoAgenciamentoAnalitico</p>
 * <p>Description: Classe para processamento de Relatorio de Comissionamento e Agenciamento Analítico</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author:$
 * $Revision$
 * $Date:$
 */

public class ProcessaRelatorioComissionamentoAgenciamentoAnalitico extends ProcessaRelatorio {

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioComissionamentoAgenciamentoAnalitico.class);

	public ProcessaRelatorioComissionamentoAgenciamentoAnalitico(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
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
			String strFormato = getStrFormato();
			StringBuilder subTitulo = new StringBuilder();

			List<String> strOrgao = null;
			List<String> orgNames = null;
			String aux = "";
			String strbenCodigo = getParametro("BEN_CODIGO", parameterMap);

			String orgao = null;
			String benCodigo = null;

			String strPeriodo = "";
			String periodo = "";

            if (parameterMap.containsKey("periodo")) {
                strPeriodo = getParametro("periodo", parameterMap);
                periodo = formatarPeriodo(strPeriodo);
            } else {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
                return;
            }

			if (parameterMap.containsKey("orgCodigo")) {
				String[] org = parameterMap.get("orgCodigo");
				if (org[0].equals("")) {
					subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
				} else {
					strOrgao = new ArrayList<>();
					orgNames = new ArrayList<>();
					try {
						for (final String value : org) {
							String[] separ = value.split(";");
							strOrgao.add(separ[0]);
							orgNames.add(separ[2] + " ");
						}
						subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.abreviado.upper.arg0", responsavel, String.valueOf(orgNames).replace("[", "").replace("]", "")));
					} catch (final Exception ex) {
						LOG.error(ex.getMessage(), ex);
					}
				}
			}

            aux = getParametro("BEN_CODIGO", parameterMap);
            if (!TextHelper.isNull(aux)) {
                String helper[] = aux.split(";");
                strbenCodigo = helper[0];
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.abreviado.upper.arg0", responsavel, helper[1].toUpperCase()));
            } else {
                subTitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.beneficio.abreviado.upper.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
            }


            if(!TextHelper.isNull(strbenCodigo)) {
                benCodigo = strbenCodigo.split(";")[0];
            }

	        String path = getPath(responsavel);
	        if (path == null) {
	            codigoRetorno = ERRO;
	            mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel);
	            return;
	        } else {
	            String entidade = getEntidade(responsavel);

	            path += File.separatorChar + "relatorio" + File.separatorChar
	                    + entidade + File.separatorChar + relatorio.getTipo();

	            if (!responsavel.isCseSup()) {
	                path += File.separatorChar + responsavel.getCodigoEntidade();
	            }

	            // Cria a pasta de relatório caso não exista.
	            new File(path).mkdirs();

	            HashMap<String, String> tipoRelatorio = new HashMap<>();
	            tipoRelatorio.put("COMISSIONAMENTO_SAUDE", CodedValues.NSE_PLANO_DE_SAUDE);
	            tipoRelatorio.put("COMISSIONAMENTO_ODONTO", CodedValues.NSE_PLANO_ODONTOLOGICO);
	            tipoRelatorio.put("AGENCIAMENTO_SAUDE", CodedValues.NSE_PLANO_DE_SAUDE);
	            tipoRelatorio.put("AGENCIAMENTO_ODONTO", CodedValues.NSE_PLANO_ODONTOLOGICO);

	            List<String> listArquiv = new ArrayList<>();

	            Iterator<Map.Entry<String, String>> it = tipoRelatorio.entrySet().iterator();


	            String fileZip = "";
	            String hoje = getHoje("ddMMyyHHmmss");

	            HashMap<String, Object> parameters = new HashMap<>();

	            // Analisando o parametro
	            String percentualAgenciamento = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PERCENTUAL_AGENCIAMENTO_CONTRATOS_BENEFICIO, responsavel);
                percentualAgenciamento = percentualAgenciamento == null ? "0" : percentualAgenciamento;

	            while (it.hasNext()) {
	                Boolean agenciamento = false;
	                Map.Entry<String, String> entry = it.next();

	                String nomeRelatorio = entry.getKey();

	                String titulo = null;

	            	if (nomeRelatorio.startsWith("AGENCIAMENTO")) {
	            		agenciamento = true;
	            		titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strPeriodo) + "AGENCIAMENTO";
	            	} else {
	            		titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strPeriodo) + "COMISSIONAMENTO";
	            	}

	                String nseCodigo = entry.getValue();

	    			criterio.setAttribute("agenciamento", agenciamento);
	    			criterio.setAttribute("periodo", periodo);
	    			criterio.setAttribute("orgao", orgao);
	    			criterio.setAttribute("percentual", percentualAgenciamento);
	    			criterio.setAttribute("nseCodigo", nseCodigo);
	    			criterio.setAttribute("benCodigo", benCodigo);

	    			parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
	    			parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
	    			parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
	                parameters.put(ReportManager.REPORT_FILE_NAME, nomeRelatorio);
	                parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
	                parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
	                parameters.put("PERCENTUAL_MODULO", percentualAgenciamento);
	                parameters.put("TIPO_RELATORIO", (agenciamento ? "AGENCIAMENTO" : "COMISSIONAMENTO") );
	                parameters.put("RESPONSAVEL", responsavel);

	                ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
		            reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

		            listArquiv.add(reportName);
	            }
	            String nomeZip = (ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.comissionamento.agenciamento.analitico", responsavel) + "_"  + hoje).replace('/', '-');
                fileZip = path + File.separatorChar + nomeZip + ".zip";
                FileHelper.zip(listArquiv, fileZip);

                for(String arqDelete: listArquiv){
                    FileHelper.delete(arqDelete);
                }

                setMensagem(fileZip, relatorio.getTipo(), relatorio.getTitulo(), session);

                enviaEmail(fileZip);
	        }
		} catch(Exception ex) {
			codigoRetorno = ERRO;
			mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
		}
	}

}
