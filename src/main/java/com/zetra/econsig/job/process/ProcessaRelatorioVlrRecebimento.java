package com.zetra.econsig.job.process;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.NaturezaServicoControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.relatorio.RelatorioHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.service.servico.NaturezaServicoController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioVlrRecebimento</p>
 * <p>Description: Classe para processamento de relatorio valor recebimento
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioVlrRecebimento extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioVlrRecebimento.class);

    public ProcessaRelatorioVlrRecebimento(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
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
        String paramIniPeriodo = "";

        if (parameterMap.containsKey("periodo")) {
            strIniPeriodo = getParametro("periodo", parameterMap);
            paramIniPeriodo = formatarPeriodo(strIniPeriodo);
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        String titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strIniPeriodo);
        StringBuilder subtitulo = new StringBuilder();

        StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.vlr.recebimento", responsavel), responsavel, parameterMap, null));

        List<String> orgNames = null;
        List<String> orgCodigos = null;
        String csaCodigo = "";
        Boolean tarifacaoPorModalidade = false;
        Boolean tarifacaoPorCorrespondente = false;
        String cnvCodVerba = getFiltroCnvCodVerba(parameterMap, subtitulo, nome, session, responsavel);
        List<String> svcCodigos = new ArrayList<>();
        Boolean servicoSemTarifacao = false;
        Boolean tarifacaoPorNatureza = false;

        String order = "TIPO";

        if ((!responsavel.isOrg() || (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)))) {
            if (parameterMap.containsKey("orgCodigo")) {
                String[] org = parameterMap.get("orgCodigo");
                if (org[0].equals("")) {
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    orgCodigos = new ArrayList<>();
                    orgNames = new ArrayList<>();
                    try {
                        for (final String value : org) {
                            String[] separ = value.split(";");
                            orgCodigos.add(separ[0]);
                            orgNames.add(separ[2] + " ");
                        }
                        subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.abreviado.upper.arg0", responsavel, String.valueOf(orgNames).replace("[", "").replace("]", "")));
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        } else if (responsavel.isOrg()) {
            orgCodigos = new ArrayList<>();
            subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, responsavel.getNomeEntidade()));
            orgCodigos.add(responsavel.getOrgCodigo());
        }
        if (responsavel.isCseSupOrg()) {
            if (parameterMap.containsKey("csaCodigo")) {
                String[] values = (parameterMap.get("csaCodigo"));
                if (values.length == 0 || values[0].equals("")) {
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
                } else {
                    values = values[0].split(";");
                    csaCodigo = values[0];
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, values[2]));
                }
            }
        } else if (responsavel.isCsa()) {
            subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, responsavel.getNomeEntidade()));
            csaCodigo = responsavel.getCsaCodigo();
        }

        if  (parameterMap.containsKey("tarifacaoPorModalidade")) {
            tarifacaoPorModalidade = Boolean.valueOf(getParametro("tarifacaoPorModalidade", parameterMap));
        }
        if  (parameterMap.containsKey("tarifacaoPorCorrespondente")) {
            tarifacaoPorCorrespondente = Boolean.valueOf(getParametro("tarifacaoPorCorrespondente", parameterMap));
        }
        if  (parameterMap.containsKey("servicoSemTarifacao")) {
        	servicoSemTarifacao = Boolean.valueOf(getParametro("servicoSemTarifacao", parameterMap));
        }
        if  (parameterMap.containsKey("tarifacaoPorNatureza")) {
        	tarifacaoPorNatureza = Boolean.valueOf(getParametro("tarifacaoPorNatureza", parameterMap));
        }

        String strFormato = getStrFormato();

        boolean exibeColunasAdicionais = false;
        // inclusão de colunas extras para usuários de suporte e relatórios nos formatos TXT e CSV
        if (responsavel.isSup() && (strFormato.equals("TEXT") || strFormato.equals("CSV"))) {
            exibeColunasAdicionais = true;
        }

        criterio.setAttribute("PERIODO", paramIniPeriodo);
        criterio.setAttribute("ORG_CODIGO", orgCodigos);
        criterio.setAttribute("CSA_CODIGO", csaCodigo);
        criterio.setAttribute("CNV_COD_VERBA", cnvCodVerba);
        criterio.setAttribute("SVC_CODIGO", svcCodigos);
        criterio.setAttribute("ORDER", order);
        criterio.setAttribute("POR_MODALIDADE", tarifacaoPorModalidade);
        criterio.setAttribute("POR_CORRESPONDENTE", tarifacaoPorCorrespondente);
        criterio.setAttribute("COLUNAS_ADICIONAIS", exibeColunasAdicionais);
        criterio.setAttribute("SERVICO_SEM_TARIFACAO", servicoSemTarifacao);
        criterio.setAttribute("TARIFACAO_POR_NATUREZA", tarifacaoPorNatureza);

        ServicoController servicoController = ApplicationContextProvider.getApplicationContext().getBean(ServicoController.class);
        NaturezaServicoController naturezaServicoController = ApplicationContextProvider.getApplicationContext().getBean(NaturezaServicoController.class);
        List<String> nseListCodigos = new ArrayList<>();

        if (parameterMap.get("nseCodigo") != null && !parameterMap.get("nseCodigo")[0].equals("")) {
        	String[] nseListCodigosParameter = (parameterMap.get("nseCodigo"));
        	String[] valuesSvc;

        	for(String nse : nseListCodigosParameter) {
        		valuesSvc = nse.split(";");
        		nseListCodigos.add(valuesSvc[0]);
        	}

        } else if (parameterMap.get("svcCodigo") != null && !parameterMap.get("svcCodigo")[0].equals("")) {
        	String[] svcs = (parameterMap.get("svcCodigo"));
        	String[] valuesSvc;

        	for(String svc : svcs) {
        		valuesSvc = svc.split(";");

        		try {
					CustomTransferObject nseCodigoSvc = servicoController.findNaturezaServico(valuesSvc[1], responsavel);
					if(nseCodigoSvc != null) {
						nseListCodigos.add((String) nseCodigoSvc.getAttribute(Columns.NSE_CODIGO));
					}
				} catch (ServicoControllerException e) {
					e.printStackTrace();
				}
        	}
        } else {
        	try {
				List<NaturezaServico> nseTodosCodigos = naturezaServicoController.listaNaturezas(responsavel);
				for (NaturezaServico nse : nseTodosCodigos){
					nseListCodigos.add(nse.getNseCodigo());
				}

			} catch (NaturezaServicoControllerException e) {
				e.printStackTrace();
			}
        }

        String path = RelatorioHelper.getCaminhoRelatorio(relatorio.getTipo(), csaCodigo, responsavel);
        String nomeRelConsig = "";
        List<String> listArquiv = new ArrayList<>();
        String fileZip = "";
        String fileName = path + File.separatorChar + nome.toString();
        String strExtFormato = getExtFormato();

        try {

        	if (Boolean.TRUE.equals(tarifacaoPorNatureza)) {

            	for (String nseCodigo : nseListCodigos){

            		 NaturezaServico chave = naturezaServicoController.buscaNaturezaServico(nseCodigo, responsavel);
	        		 String prefixo = nome.toString();
	                 String nomeRelatorio = Normalizer.normalize( prefixo.replace(" ", "_") + "_" + chave.getNseDescricao(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	                 nomeRelConsig = path + File.separatorChar + nomeRelatorio + strExtFormato.toLowerCase();
	                 List<String> nseCodigosList = new ArrayList<>();
	                 nseCodigosList.add(nseCodigo);
	                 int labelSubtilo = subtitulo.length();

	                 StringBuilder subtituloNse = new StringBuilder();
	                 subtituloNse.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.natureza.servico.arg0", responsavel, ""));
	                 subtituloNse.append(" ").append(chave.getNseDescricao());
	                 Boolean validacaoSvc  = true;

                    if  (parameterMap.containsKey("svcCodigo")) {
                        String[] svcs = (parameterMap.get("svcCodigo"));

                        if (!svcs[0].equals("")) {
                            String[] values;
                            subtituloNse.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ""));

                            for (int i = 0; i < svcs.length; i++) {
                                values = svcs[i].split(";");
                                svcCodigos.add(values[0]);
            					CustomTransferObject nseCodigoSvc = servicoController.findNaturezaServico(values[0], responsavel);
            					List<String> nseSvcCodigos = new ArrayList<>();
            					nseSvcCodigos.add((String) nseCodigoSvc.getAttribute(Columns.NSE_CODIGO));

                                if (nseSvcCodigos.contains(chave.getNseCodigo()))  {
	                                if (i == (svcs.length - 1)) {
	                                	subtituloNse.append(" ").append(values[2]);
	                                } else {
	                                	subtituloNse.append(" ").append(values[2]).append(",");
	                                }
                                } else {
                                	subtituloNse.append(" ").append("-");
                                	validacaoSvc = false;
                                }
                            }
                        } else {
                        	subtituloNse.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                        }
                    }

                    if	(Boolean.TRUE.equals(validacaoSvc)) {
	                     subtitulo.append(subtituloNse);

		                 criterio.setAttribute("NSE_CODIGO", nseCodigosList);
		                 parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
		                 parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
		                 parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
		                 parameters.put(ReportManager.REPORT_FILE_NAME, nomeRelatorio);
		                 parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
		                 parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
		                 parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
		                 parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);
		                 parameters.put("POR_MODALIDADE", tarifacaoPorModalidade);
		                 parameters.put("POR_CORRESPONDENTE", tarifacaoPorCorrespondente);
		                 parameters.put("COLUNAS_ADICIONAIS", exibeColunasAdicionais);
		                 parameters.put("SERVICO_SEM_TARIFACAO", servicoSemTarifacao);
		                 parameters.put("TARIFACAO_POR_NATUREZA", tarifacaoPorNatureza);

		                 ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
		                 reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

		                 nseCodigosList.remove(0);
		                 listArquiv.add(nomeRelConsig);
		                 subtitulo = new StringBuilder(subtitulo.substring(0, labelSubtilo));
                    }
	        	}

		        fileZip = fileName + ".zip";
	        	FileHelper.zip(listArquiv, fileZip);
        		for (String arqDelete : listArquiv) {
				      FileHelper.delete(arqDelete);
				}

	        	// Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
	        	enviaEmail(fileZip);

	        	setMensagem(fileZip, relatorio.getTipo(), relatorio.getTitulo(), session);

        	} else {

        		List<String> nseCodigos = getFiltroNseCodigo(parameterMap, subtitulo, nome, session, responsavel);
                if  (parameterMap.containsKey("svcCodigo")) {
                    String[] svcs = (parameterMap.get("svcCodigo"));
                    if (!svcs[0].equals("")) {
                        String[] values;
                        subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ""));
                        for (int i = 0; i < svcs.length; i++) {
                            values = svcs[i].split(";");
                            svcCodigos.add(values[0]);
                            if (i == (svcs.length - 1)) {
                                subtitulo.append(" ").append(values[2]);
                            } else {
                                subtitulo.append(" ").append(values[2]).append(",");
                            }
                        }
                    } else {
                        subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                    }
                }

        		criterio.setAttribute("NSE_CODIGO", nseCodigos);
                parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
                parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
                parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
                parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
                parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
                parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
                parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
                parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);
                parameters.put("POR_MODALIDADE", tarifacaoPorModalidade);
                parameters.put("POR_CORRESPONDENTE", tarifacaoPorCorrespondente);
                parameters.put("COLUNAS_ADICIONAIS", exibeColunasAdicionais);
                parameters.put("SERVICO_SEM_TARIFACAO", servicoSemTarifacao);
                parameters.put("TARIFACAO_POR_NATUREZA", tarifacaoPorNatureza);

                ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

		        fileZip = fileName + ".zip";

		        fileName = fileName + strExtFormato.toLowerCase();
	        	FileHelper.zip(fileName, fileZip);
				FileHelper.delete(fileName);

	        	// Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
	        	enviaEmail(fileZip);

	        	setMensagem(fileZip, relatorio.getTipo(), relatorio.getTitulo(), session);
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