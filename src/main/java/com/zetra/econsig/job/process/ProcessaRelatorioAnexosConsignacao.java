package com.zetra.econsig.job.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioAnexosConsignacao</p>
 * <p>Description: Classe para processamento de relatorio de Anexos de Consignacao</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioAnexosConsignacao extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioAnexosConsignacao.class);

    public ProcessaRelatorioAnexosConsignacao(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        HashMap<String, Object> parameters = new HashMap<>();

        String tipoEntidade = responsavel.getTipoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)
                && parameterMap.containsKey("orgCodigo")) {
            tipoEntidade = "EST";
        }
        if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            tipoEntidade = "CSA";
        }

        StringBuilder subTitulo = new StringBuilder();

        StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.anexos.consignacao", responsavel), responsavel, parameterMap, null));
        String fileZip = "";
        
        String tipoPeriodo = getFiltroTipoPeriodo(parameterMap, subTitulo, nome, session, responsavel);
        final Map<String, String> datas = getFiltroPeriodo(parameterMap, subTitulo, nome, session, responsavel);
        
        final String strPeriodo = getParametro("periodo", parameterMap);
        final String strIniPeriodo = getParametro("periodoIni", parameterMap);
        final String strFimPeriodo = getParametro("periodoFim", parameterMap);
        
        final String paramIniPeriodo = datas.get("PERIODO_INICIAL");
        final String paramFimPeriodo = datas.get("PERIODO_FINAL");      
        
		boolean hasPeriodo = !TextHelper.isNull(strPeriodo);
		boolean hasTipoPeriodo = !TextHelper.isNull(tipoPeriodo);
		boolean hasDataIni = !TextHelper.isNull(paramIniPeriodo);
		boolean hasDataFim = !TextHelper.isNull(paramFimPeriodo);
		
		boolean periodoCompleto = hasPeriodo && hasTipoPeriodo;
		boolean dataCompleta = hasDataIni && hasDataFim;

		if (!periodoCompleto && !dataCompleta) {
		    codigoRetorno = ERRO;
		    mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.data.para.geracao.relatorio", responsavel);
		    return;
		}

        String[] sad = (parameterMap.get("SAD_CODIGO"));
        List<String> sadCodigos = sad != null ? Arrays.asList(sad) : null;

        String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String corCodigo = getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String estCodigo = (tipoEntidade.equals("EST")) ? responsavel.getEstCodigo() : getFiltroEstCodigo(parameterMap, subTitulo, nome, session, responsavel);
        
        String titulo = relatorio.getTitulo() + " - ";
        if (periodoCompleto) {
            titulo += ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, strPeriodo);
        } else if (!periodoCompleto && dataCompleta) {
            titulo += ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo);
        }
        
        Boolean temAnexo = getFiltroTemAnexo(parameterMap, subTitulo, nome, session, responsavel);

        String path = getPath(responsavel);
        String strFormato = getStrFormato();

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

            String fileName = path + File.separatorChar + nome.toString();

            try {
                List<String> listArquiv = new ArrayList<>();
                String nomeRelConsig = "";

                nomeRelConsig = path + File.separatorChar + nome;

                if (strFormato.equals("TEXT")) {
                    nomeRelConsig += ".txt";
                } else {
                    nomeRelConsig += "." + strFormato.toLowerCase();
                }
                
                if (dataCompleta && !periodoCompleto) {
	                HashMap<String, List<String>> ocorrencia = new HashMap<>();
	                List<String> toc = new ArrayList<>();
	                toc.add(CodedValues.TOC_TARIF_RESERVA);
	                ocorrencia.put("INCLUSAO", toc);
	
	                toc = new ArrayList<>();
	                toc.add(CodedValues.TOC_ALTERACAO_CONTRATO);
	                ocorrencia.put("ALTERACAO", toc);
	                
	                nomeRelConsig = "";
	                for (final Entry<String, List<String>> entry : ocorrencia.entrySet()) {	                    
	                    final String chave = entry.getKey();
	                    final String prefixo = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.anexos.consignacao.prefixo." + chave.toLowerCase().trim(), responsavel);
	                    final String nomeRelatorio = prefixo.replace(" ", "_") + "_" + nome.toString();
	                    nomeRelConsig = path + File.separatorChar + nomeRelatorio;

	                    if ("TEXT".equals(strFormato)) {
	                        nomeRelConsig += ".txt";
	                    } else {
	                        nomeRelConsig += "." + strFormato.toLowerCase();
	                    }

	                    final List<String> tocTemp = entry.getValue();

	                    //busca data de inclusão/alteração
                        criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
                        criterio.setAttribute("DATA_INI", paramIniPeriodo);
                        criterio.setAttribute("DATA_FIM", paramFimPeriodo);
                        criterio.setAttribute("EST_CODIGO", estCodigo);
                        criterio.setAttribute("ORG_CODIGO", orgCodigo);
                        criterio.setAttribute("CSA_CODIGO", csaCodigo);
                        criterio.setAttribute("COR_CODIGO", corCodigo);
                        criterio.setAttribute("SVC_CODIGO", svcCodigos);
                        criterio.setAttribute("SAD_CODIGO", sadCodigos);
                        criterio.setAttribute("temAnexo", temAnexo);
                        criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
                        criterio.setAttribute("TOC_CODIGO", tocTemp);
	                   	                    
	                    parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
	                    parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
	                    parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
	                    parameters.put(ReportManager.REPORT_FILE_NAME, nomeRelatorio);
	                    parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
	                    parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
	                    parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
	                    parameters.put("RESPONSAVEL", responsavel);

	                    ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
	                    reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

	                    listArquiv.add(nomeRelConsig);
	                }	                    
                } else {
                	//busca data inicial e final do periodo
                    criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
                    criterio.setAttribute("DATA_PERIODO", DateHelper.parsePeriodString(strPeriodo));
                    criterio.setAttribute("EST_CODIGO", estCodigo);
                    criterio.setAttribute("ORG_CODIGO", orgCodigo);
                    criterio.setAttribute("CSA_CODIGO", csaCodigo);
                    criterio.setAttribute("COR_CODIGO", corCodigo);
                    criterio.setAttribute("SVC_CODIGO", svcCodigos);
                    criterio.setAttribute("SAD_CODIGO", sadCodigos);
                    criterio.setAttribute("tipoPeriodo", tipoPeriodo);
                    criterio.setAttribute("temAnexo", temAnexo);
                    criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);     
                    
                    parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
                    parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
                    parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
                    parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
                    parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
                    parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
                    parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
                    parameters.put("RESPONSAVEL", responsavel);

                    ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                    reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

                    listArquiv.add(nomeRelConsig);
                }                                

                fileZip = fileName + ".zip";
                FileHelper.zip(listArquiv, fileZip);

                for(String arqDelete: listArquiv){
                    FileHelper.delete(arqDelete);
                }

                setMensagem(fileZip, relatorio.getTipo(), relatorio.getTitulo(), session);

            } catch (Exception ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            }
        }
    }
}
