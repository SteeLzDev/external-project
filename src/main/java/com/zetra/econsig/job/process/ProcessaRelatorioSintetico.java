package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.Pair;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dinamico.RelatorioSinteticoInfo;
import com.zetra.econsig.report.jasper.dto.SinteticoDataSourceBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioSintetico</p>
 * <p>Description: Classe para processamento de relatorio sintetico
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioSintetico extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSintetico.class);

    public ProcessaRelatorioSintetico(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
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
        String paramIniPeriodo = "";
        String paramFimPeriodo = "";

        String tipoEntidade = responsavel.getTipoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)
                && parameterMap.containsKey("orgCodigo")) {
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
	            paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
	            paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
	        } else {
	            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
	            return;
	        }
        }

        String complementoTitulo = (!TextHelper.isNull(strIniPeriodo) && !TextHelper.isNull(strFimPeriodo)) ? ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo) : "";
        String titulo = relatorio.getTitulo() + " - " + complementoTitulo;
        StringBuilder subTitulo = new StringBuilder();

        StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.sintetico", responsavel), responsavel, parameterMap, null));

        String order = parameterMap.get("ORDENACAO_AUX")[0];

        Map<String,String> tipoOrdMap = new HashMap<>();
        tipoOrdMap.put("ORDEM_QTD", parameterMap.get("ORDEM_QTD")[0]);
        tipoOrdMap.put("ORDEM_TOTAL", parameterMap.get("ORDEM_TOTAL")[0]);
        tipoOrdMap.put("ORDEM_PRESTACAO", parameterMap.get("ORDEM_PRESTACAO")[0]);
        tipoOrdMap.put("ORDEM_CAPITALDEVIDO", parameterMap.get("ORDEM_CAPITALDEVIDO")[0]);

        String[] sad = (parameterMap.get("SAD_CODIGO"));
        List<String> sadCodigos = sad != null ? Arrays.asList(sad) : null;
        String[] campo = parameterMap.get("chkCAMPOS");
        List<String> campos = campo != null ? Arrays.asList(campo) : null;

        String[] origemAde = parameterMap.get("chkOrigem");
        List<String> origensAdes = origemAde != null ? Arrays.asList(origemAde) : null;
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
        List<String> nseCodigos = getFiltroNseCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Correspondente
        List<String> corCodigos = new ArrayList<>();
        if (responsavel.isCor() || parameterMap.containsKey("corCodigo")) {
            corCodigos.add(getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel));
        }

        subTitulo.append(System.getProperty("line.separator"));

        String reportName = null;
        try {
            criterio.setAttribute("CAMPOS", camposQuery);
            criterio.setAttribute("CAMPOS_RELATORIO", camposRelatorio);
            criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
            criterio.setAttribute("DATA_INI", paramIniPeriodo);
            criterio.setAttribute("DATA_FIM", paramFimPeriodo);
            criterio.setAttribute("ORG_CODIGO", orgCodigo);
            criterio.setAttribute("SBO_CODIGO", sboCodigo);
            criterio.setAttribute("UNI_CODIGO", uniCodigo);
            criterio.setAttribute("CSA_CODIGO", csaCodigo);
            criterio.setAttribute("SVC_CODIGO", svcCodigos);
            criterio.setAttribute("NSE_CODIGO", nseCodigos);
            criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
            criterio.setAttribute("SAD_CODIGO", sadCodigos);
            criterio.setAttribute("ORDER", order);
            criterio.setAttribute("ORIGEM_ADE", origensAdes);
            criterio.setAttribute("TIPO_ORD", tipoOrdMap);
            criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

            RelatorioSinteticoInfo sinteticoReportInfo = new RelatorioSinteticoInfo(relatorio);
            sinteticoReportInfo.setCriterios(criterio);
            sinteticoReportInfo.buildJRXML(parameters, responsavel);

            String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);

            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            Pair<String[], List<TransferObject>> conteudo = relatorioController.geraRelatorioSinteticoConsignacoes(criterio, responsavel);
            List<Object[]> conteudoList = DTOToList(conteudo.second, conteudo.first);
            Map<String, List<SinteticoDataSourceBean>> reportParameters;

            if(parameterMap.containsKey("chkGrafico")){
                reportParameters = preparaGrafico(conteudo.second, conteudo.first);
                parameters.putAll(reportParameters);
            }

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, conteudoList, responsavel);

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

    public Map<String, List<SinteticoDataSourceBean>> preparaGrafico(List<TransferObject> conteudo, String[] fields ){
        Map<String, List<SinteticoDataSourceBean>> parametros = new HashMap<>();
        List<String> campos = Arrays.asList(fields);

        parametros.put("CSA", new ArrayList<>());
        parametros.put("CORRESPONDENTE", new ArrayList<>());
        parametros.put("SERVICO", new ArrayList<>());
        parametros.put("SITUACAO", new ArrayList<>());
        parametros.put("PERIODO", new ArrayList<>());
        parametros.put("ORGAO", new ArrayList<>());
        parametros.put("ESTABELECIMENTO", new ArrayList<>());
        parametros.put("CSA_PERIODO", new ArrayList<>());
        parametros.put("DATA_INICIO", new ArrayList<>());
        parametros.put("DATA_FIM", new ArrayList<>());

        List<SinteticoDataSourceBean> listaCsa = new ArrayList<>();
        List<SinteticoDataSourceBean> listaCor = new ArrayList<>();
        List<SinteticoDataSourceBean> listaEst = new ArrayList<>();
        List<SinteticoDataSourceBean> listaSer = new ArrayList<>();
        List<SinteticoDataSourceBean> listaSit = new ArrayList<>();
        List<SinteticoDataSourceBean> listaPer = new ArrayList<>();
        List<SinteticoDataSourceBean> listaOrg = new ArrayList<>();
        List<SinteticoDataSourceBean> listaDataIni = new ArrayList<>();
        List<SinteticoDataSourceBean> listaDataFim = new ArrayList<>();

        Map<String, Long> csaMap = new HashMap<>();
        Map<String, Long> corMap = new HashMap<>();
        Map<String, Long> estMap = new HashMap<>();
        Map<String, Long> serMap = new HashMap<>();
        Map<String, Long> sitMap = new HashMap<>();
        Map<String, Long> perMap = new HashMap<>();
        Map<String, Long> orgMap = new HashMap<>();
        Map<String, Long> csaPerMap = new HashMap<>();
        Map<String, Long> dataIniMap = new HashMap<>();
        Map<String, Long> dataFimMap = new HashMap<>();


        for(TransferObject linha: conteudo){
            //Consignatária
            if(campos.contains(Columns.CSA_NOME)){
                if(csaMap.containsKey(linha.getAttribute(Columns.CSA_NOME))){
                    String key = (String) linha.getAttribute(Columns.CSA_NOME);
                    Long value = csaMap.get(key) + (Long)linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        csaMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        csaMap.put(key, value);
                    }

                } else{
                    String key = (String) linha.getAttribute(Columns.CSA_NOME);
                    Long value = (Long) linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        csaMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        csaMap.put(key, value);
                    }
                }
            }
            //Correspondente
            if(campos.contains(Columns.COR_NOME)){
                if(corMap.containsKey(linha.getAttribute(Columns.COR_NOME))){
                    String key = (String) linha.getAttribute(Columns.COR_NOME);
                    Long value = corMap.get(key) + (Long)linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        corMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        corMap.put(key, value);
                    }

                } else{
                    String key = (String) linha.getAttribute(Columns.COR_NOME);
                    if(key==null){
                        key = "-";
                    }
                    Long value = (Long) linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        corMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        corMap.put(key, value);
                    }
                }
            }
            //Serviço
            if(campos.contains(Columns.SVC_DESCRICAO)){
                if(serMap.containsKey(linha.getAttribute(Columns.SVC_DESCRICAO))){
                    String key = (String) linha.getAttribute(Columns.SVC_DESCRICAO);
                    Long value = serMap.get(key) + (Long)linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        serMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        serMap.put(key, value);
                    }
                } else{
                    String key = (String) linha.getAttribute(Columns.SVC_DESCRICAO);
                    Long value = (Long) linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        serMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        serMap.put(key, value);
                    }
                }
            }
            //Situação
            if(campos.contains(Columns.SAD_DESCRICAO)){
                if(sitMap.containsKey(linha.getAttribute(Columns.SAD_DESCRICAO))){
                    String key = (String) linha.getAttribute(Columns.SAD_DESCRICAO);
                    Long value = sitMap.get(key) + (Long)linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        sitMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        sitMap.put(key, value);
                    }
                } else{
                    String key = (String) linha.getAttribute(Columns.SAD_DESCRICAO);
                    Long value = (Long) linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        sitMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        sitMap.put(key, value);
                    }
                }
            }
            //Período
            if(campos.contains(Columns.ADE_DATA)){
                if(perMap.containsKey(linha.getAttribute(Columns.ADE_DATA))){
                    String key = (String) linha.getAttribute(Columns.ADE_DATA);
                    Long value = perMap.get(key) + (Long)linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        perMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        perMap.put(key, value);
                    }

                } else{
                    String key = (String) linha.getAttribute(Columns.ADE_DATA);
                    Long value = (Long) linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        perMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        perMap.put(key, value);
                    }
                }

                String dataBean = (String)linha.getAttribute(Columns.CSA_NOME) +"---"+ linha.getAttribute(Columns.ADE_DATA);

                //para o gráfico de linhas
                if(csaPerMap.containsKey(dataBean)){
                    Long value = csaPerMap.get(dataBean) + (Long)linha.getAttribute("CONTRATOS");
                    csaPerMap.put(dataBean, value);
                }
                else{
                    Long value = (Long)linha.getAttribute("CONTRATOS");
                    csaPerMap.put(dataBean, value);
                }
            }
            //Orgão
            if(campos.contains(Columns.ORG_NOME)){
                if(orgMap.containsKey(linha.getAttribute(Columns.ORG_NOME))){
                    String key = (String) linha.getAttribute(Columns.ORG_NOME);
                    Long value = orgMap.get(key) + (Long)linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        orgMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        orgMap.put(key, value);
                    }
                } else{
                    String key = (String) linha.getAttribute(Columns.ORG_NOME);
                    Long value = (Long) linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        orgMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        orgMap.put(key, value);
                    }
                }
            }
            //Estabelecimento
            if(campos.contains(Columns.EST_NOME)){
                if(estMap.containsKey(linha.getAttribute(Columns.EST_NOME))){
                    String key = (String) linha.getAttribute(Columns.EST_NOME);
                    Long value = estMap.get(key) + (Long)linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        estMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        estMap.put(key, value);
                    }
                } else{
                    String key = (String) linha.getAttribute(Columns.EST_NOME);
                    Long value = (Long) linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        estMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        estMap.put(key, value);
                    }
                }
            }

            //Data Inicio
            if(campos.contains(Columns.ADE_ANO_MES_INI)){
                if(dataIniMap.containsKey(linha.getAttribute(Columns.ADE_ANO_MES_INI))){
                    String key = (String) linha.getAttribute(Columns.ADE_ANO_MES_INI);
                    Long value = dataIniMap.get(key) + (Long)linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        dataIniMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        dataIniMap.put(key, value);
                    }

                } else{
                    String key = (String) linha.getAttribute(Columns.ADE_ANO_MES_INI);
                    Long value = (Long) linha.getAttribute("CONTRATOS");
                    if(key.length()>36){
                        dataIniMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        dataIniMap.put(key, value);
                    }
                }
            }

            //Data Fim
            if(campos.contains(Columns.ADE_ANO_MES_FIM)){
                if(dataFimMap.containsKey(linha.getAttribute(Columns.ADE_ANO_MES_FIM))){
                    String key = (String) linha.getAttribute(Columns.ADE_ANO_MES_FIM);
                    Long value = dataFimMap.get(key) + (Long)linha.getAttribute("CONTRATOS");

                    dataFimMap.put(key, value);

                } else if(dataFimMap.containsKey("Indet.") && linha.getAttribute(Columns.ADE_ANO_MES_FIM)==null){
                    String key = "Indet.";
                    Long value = dataFimMap.get(key) + (Long)linha.getAttribute("CONTRATOS");
                    dataFimMap.put(key, value);

                } else {
                    String key = (String) linha.getAttribute(Columns.ADE_ANO_MES_FIM);
                    Long value = (Long) linha.getAttribute("CONTRATOS");
                    if(key==null){
                        dataFimMap.put("Indet.", value);
                    }else{
                        dataFimMap.put(key, value);
                    }
                }
            }
        }


        //Consignatária
        if(campos.contains(Columns.CSA_NOME)){

            for(Map.Entry<String,Long> entry: csaMap.entrySet()){
            	SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(entry.getKey(), entry.getValue());
            	listaCsa.add(elemento);
            }


            parametros.put("CSA", agrupador(listaCsa, null));
        }

        //Correspondente
        if(campos.contains(Columns.COR_NOME)){

            for(Map.Entry<String,Long> entry: corMap.entrySet()){
            	SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(entry.getKey(), entry.getValue());
            	listaCor.add(elemento);
            }

            parametros.put("CORRESPONDENTE", agrupador(listaCor, null));
        }

        //Serviço
        if(campos.contains(Columns.SVC_DESCRICAO)){

            for(Map.Entry<String,Long> entry: serMap.entrySet()){
            	SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(entry.getKey(), entry.getValue());
            	listaSer.add(elemento);
            }
            parametros.put("SERVICO", agrupador(listaSer, null));
        }

        //Situação
        if(campos.contains(Columns.SAD_DESCRICAO)){

            for(Map.Entry<String,Long> entry: sitMap.entrySet()){
            	SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(entry.getKey(), entry.getValue());
            	listaSit.add(elemento);
            }
            parametros.put("SITUACAO", agrupador(listaSit, null));
        }

        //Período
        if(campos.contains(Columns.ADE_DATA)){

            for(Map.Entry<String,Long> entry: perMap.entrySet()){
            	SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(entry.getKey(), entry.getValue());
            	listaPer.add(elemento);
            }
            parametros.put("PERIODO", agrupador(listaPer, null));

            //Para gráfico de linhas
            if(!listaCsa.isEmpty() && listaPer.size()>1){
                List<SinteticoDataSourceBean> listaGra = new ArrayList<>();

                for(Map.Entry<String,Long> entry: csaPerMap.entrySet()){
                	String[] dados = entry.getKey().split("---");
                	SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(dados[0], entry.getValue(), dados[1]);
                	listaGra.add(elemento);
                }
                parametros.put("CSA_PERIODO", agrupador(listaGra, parametros.get("CSA")));
            }
        }

        //Orgão
        if(campos.contains(Columns.ORG_NOME)){

            for(Map.Entry<String,Long> entry: orgMap.entrySet()){
            	SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(entry.getKey(), entry.getValue());
            	listaOrg.add(elemento);
            }
            parametros.put("ORGAO", agrupador(listaOrg, null));
        }

        //Estabelecimento
        if(campos.contains(Columns.EST_NOME)){

            for(Map.Entry<String,Long> entry: estMap.entrySet()){
            	SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(entry.getKey(), entry.getValue());
            	listaEst.add(elemento);
            }
            parametros.put("ESTABELECIMENTO", agrupador(listaEst, null));
        }

        //Data Inicio
        if(campos.contains(Columns.ADE_ANO_MES_INI)){

            for(Map.Entry<String,Long> entry: dataIniMap.entrySet()){
            	SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(entry.getKey(), entry.getValue());
            	listaDataIni.add(elemento);
            }
            parametros.put("DATA_INICIO", agrupador(listaDataIni, null));
        }

        //Data Fim
        if(campos.contains(Columns.ADE_ANO_MES_FIM)){

            for(Map.Entry<String,Long> entry: dataFimMap.entrySet()){
            	SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(entry.getKey(), entry.getValue());
            	listaDataFim.add(elemento);
            }
            parametros.put("DATA_FIM", agrupador(listaDataFim, null));
        }

        return parametros;
    }

}