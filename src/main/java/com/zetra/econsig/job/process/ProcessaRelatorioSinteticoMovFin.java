package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.Pair;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dinamico.RelatorioSinteticoMovFinInfo;
import com.zetra.econsig.report.jasper.dto.SinteticoDataSourceBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ProcessaRelatorioSinteticoMovFin</p>
 * <p>Description: Classe que dispara processo para montar relatï¿½rio sintï¿½tico de mov. financeira</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioSinteticoMovFin extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSinteticoMovFin.class);

    public ProcessaRelatorioSinteticoMovFin(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }
    @Override
    protected void executar() {
        final HashMap<String, Object> parameters = new HashMap<>();
        String strPeriodo = "";
        String paramPeriodo = "";

        String tipoEntidade = responsavel.getTipoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)
                && parameterMap.containsKey("orgCodigo")) {
            tipoEntidade = "EST";
        }
        if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            tipoEntidade = "CSA";
        }

        if (parameterMap.containsKey("periodo")) {
            strPeriodo = getParametro("periodo", parameterMap);
            paramPeriodo = formatarPeriodo(strPeriodo);
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        final String titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, getParametro("periodo", parameterMap));
        final StringBuilder subTitulo = new StringBuilder("");

        final StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.sintetico.mov.fin", responsavel), responsavel, parameterMap, null));

        final String order = parameterMap.get("ORDENACAO_AUX")[0];

        final Map<String,String> tipoOrdMap = new HashMap<>();
        tipoOrdMap.put("ORDEM_STATUS", parameterMap.get("ORDEM_STATUS")[0]);
        tipoOrdMap.put("ORDEM_PARCELAS", parameterMap.get("ORDEM_PARCELAS")[0]);
        tipoOrdMap.put("ORDEM_PRESTACAO", parameterMap.get("ORDEM_PRESTACAO")[0]);

        final String[] sad = (parameterMap.get("SAD_CODIGO"));
        final List<String> sadCodigos = sad != null ? Arrays.asList(sad) : null;
        final String[] spd = (parameterMap.get("SPD_CODIGO"));
        final List<String> spdCodigos = spd != null ? Arrays.asList(spd) : null;
        final String[] campo = parameterMap.get("chkCAMPOS");
        final List<String> campos = campo != null ? Arrays.asList(campo) : null;
        final List<String> dynList = new ArrayList<>();


        if (campos != null) {
            dynList.addAll(campos);

            if (dynList.contains(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA_ABREV.getCodigo())) {
                dynList.add(CamposRelatorioSinteticoEnum.CAMPO_CONSIGNATARIA.getCodigo());
            }
        }

        String periodoIni = "";
        String periodoFim = "";
        if (parameterMap.containsKey("periodoIni")) {
            periodoIni = getParametro("periodoIni", parameterMap);
        }
        if (parameterMap.containsKey("periodoFim")) {
            periodoFim = getParametro("periodoFim", parameterMap);
        }
        if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.data.de.inclusao.de.arg0.a.arg1", responsavel, periodoIni, periodoFim));
            periodoIni = reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd");
            periodoFim = reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd");

        } else if (!TextHelper.isNull(periodoIni)) {
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.data.de.inclusao.a.partir.de.arg0", responsavel, periodoIni));
            periodoIni = reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd");

        } else if (!TextHelper.isNull(periodoFim)) {
            subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.data.inclusao.ate.arg0", responsavel, periodoFim));
            periodoFim = reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd");
        }

        final String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> orgCodigo = getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String sboCodigo = getFiltroSboCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String uniCodigo = getFiltroUniCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final String matricula = getFiltroRseMatricula(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> marCodigos = getFiltroMarCodigo();
        final String cpf = getFiltroCpf(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        final List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);
        subTitulo.append(System.lineSeparator());

        // Correspondente
        final List<String> corCodigos = new ArrayList<>();
        if (responsavel.isCor()) {
            corCodigos.add(getFiltroCorCodigo(parameterMap, subTitulo, nome, session, responsavel));
        }

        criterio.setAttribute("PERIODO", paramPeriodo);
        criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_INICIO, periodoIni);
        criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO_FIM, periodoFim);
        if (parameterMap.containsKey("corCodigo")) {
            final String[] correspondentes = (parameterMap.get("corCodigo"));
            if(!"".equals(correspondentes[0])) {
                if ((correspondentes.length == 0) || "-1".equals(correspondentes[0].substring(0, 2))) {
                    corCodigos.add("-1");
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                } else {
                    subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)).append(": ");
                    for(final String cor : correspondentes){
                        final String [] correspondente = cor.split(";");
                        corCodigos.add(correspondente[0]);
                        subTitulo.append(correspondente[2]).append(", ");
                    }
                    subTitulo.deleteCharAt(subTitulo.length()-2);
                }
                subTitulo.append(System.lineSeparator());
                criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
            }
        }
        criterio.setAttribute("CSA_CODIGO", csaCodigo);
        criterio.setAttribute("ORG_CODIGO", orgCodigo);
        criterio.setAttribute("SBO_CODIGO", sboCodigo);
        criterio.setAttribute("UNI_CODIGO", uniCodigo);
        criterio.setAttribute("ORDER", order);
        criterio.setAttribute("TIPO_ENTIDADE", tipoEntidade);
        criterio.setAttribute("CAMPOS", dynList);
        criterio.setAttribute("SVC_CODIGO", svcCodigos);
        criterio.setAttribute("SAD_CODIGO", sadCodigos);
        criterio.setAttribute("SPD_CODIGO", spdCodigos);
        criterio.setAttribute("TIPO_ORD", tipoOrdMap);
        criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
        criterio.setAttribute("RSE_MATRICULA", matricula);
        criterio.setAttribute("CPF", cpf);
        if (responsavel.isCor() && TextHelper.isNull(parameterMap.get("corCodigo"))) {
            criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
        }
        if ((spdCodigos != null) && (spdCodigos.contains(CodedValues.SPD_LIQUIDADAFOLHA) || spdCodigos.contains(CodedValues.SPD_LIQUIDADAMANUAL))) {
            criterio.setAttribute("tarifacao", true);
        }
        if ((marCodigos != null) && !marCodigos.isEmpty()) {
            criterio.setAttribute("MAR_CODIGOS", marCodigos);
        }

        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());

        String reportName = null;
        try {
            final RelatorioSinteticoMovFinInfo movFinReportInfo = new RelatorioSinteticoMovFinInfo(relatorio);
            movFinReportInfo.setCriterios(criterio);
            movFinReportInfo.buildJRXML(parameters, responsavel);

            final String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);

            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final Pair<String[], List<TransferObject>> conteudo = relatorioController.geraRelatorioSinteticoMovFin(criterio, responsavel);
            final List<Object[]> conteudoList = DTOToList(conteudo.second, conteudo.first);
            Map<String, List<SinteticoDataSourceBean>> reportParameters = new HashMap<>();

            if(parameterMap.containsKey("chkGrafico")){
                reportParameters = preparaGrafico(conteudo.second, conteudo.first);
                parameters.putAll(reportParameters);
            }

            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            reportName = reportController.makeReport(getStrFormato(), criterio, parameters, relatorio, conteudoList, responsavel);

            final String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (final Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }

    public Map<String, List<SinteticoDataSourceBean>> preparaGrafico(List<TransferObject> conteudo, String[] fields ){
        final Map<String, List<SinteticoDataSourceBean>> parametros = new HashMap<>();
        final List<String> campos = Arrays.asList(fields);

        parametros.put("CSA", new ArrayList<>());
        parametros.put("CORRESPONDENTE", new ArrayList<>());
        parametros.put("SERVICO", new ArrayList<>());
        parametros.put("SITUACAO", new ArrayList<>());
        parametros.put("ORGAO", new ArrayList<>());
        parametros.put("ESTABELECIMENTO", new ArrayList<>());
        parametros.put("DATA_INICIO", new ArrayList<>());
        parametros.put("COD_VERBA", new ArrayList<>());

        parametros.put("CSA_DATA_INICIO_LIQ", new ArrayList<>());
        parametros.put("CSA_DATA_INICIO_REJ", new ArrayList<>());


        final List<SinteticoDataSourceBean> listaCsa = new ArrayList<>();
        final List<SinteticoDataSourceBean> listaCor = new ArrayList<>();
        final List<SinteticoDataSourceBean> listaEst = new ArrayList<>();
        final List<SinteticoDataSourceBean> listaSer = new ArrayList<>();
        final List<SinteticoDataSourceBean> listaSit = new ArrayList<>();
        final List<SinteticoDataSourceBean> listaOrg = new ArrayList<>();
        final List<SinteticoDataSourceBean> listaDataIni = new ArrayList<>();
        final List<SinteticoDataSourceBean> listaCodVerba = new ArrayList<>();

        //        List<SinteticoDataSourceBean> listaCsaDataInicioLiq = new ArrayList<SinteticoDataSourceBean>();
        //        List<SinteticoDataSourceBean> listaCsaDataInicioRej = new ArrayList<SinteticoDataSourceBean>();


        final Map<String, Long> csaMap = new HashMap<>();
        final Map<String, Long> corMap = new HashMap<>();
        final Map<String, Long> estMap = new HashMap<>();
        final Map<String, Long> serMap = new HashMap<>();
        final Map<String, Long> sitMap = new HashMap<>();
        final Map<String, Long> orgMap = new HashMap<>();
        final Map<String, Long> dataIniMap = new HashMap<>();
        final Map<String, Long> codVerbaMap = new HashMap<>();

        final Map<String, Long> csaDataIniLiqMap = new HashMap<>();
        final Map<String, Long> csaDataIniRejMap = new HashMap<>();


        for(final TransferObject linha: conteudo){
            final long numParcelasLong = Long.parseLong(linha.getAttribute("NUM_PARCELAS").toString());
            //Consignatária
            if(campos.contains(Columns.CSA_NOME)){
                if(csaMap.containsKey(linha.getAttribute(Columns.CSA_NOME))){
                    final String key = (String) linha.getAttribute(Columns.CSA_NOME);
                    final Long value = csaMap.get(key) + numParcelasLong;
                    if(key.length()>36){
                        csaMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        csaMap.put(key, value);
                    }

                } else{
                    final String key = (String) linha.getAttribute(Columns.CSA_NOME);
                    final Long value = numParcelasLong;
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
                    final String key = (String) linha.getAttribute(Columns.COR_NOME);
                    final Long value = corMap.get(key) + numParcelasLong;
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
                    final Long value = numParcelasLong;
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
                    final String key = (String) linha.getAttribute(Columns.SVC_DESCRICAO);
                    final Long value = serMap.get(key) + numParcelasLong;
                    if(key.length()>36){
                        serMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        serMap.put(key, value);
                    }
                } else{
                    final String key = (String) linha.getAttribute(Columns.SVC_DESCRICAO);
                    final Long value = numParcelasLong;
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
                    final String key = (String) linha.getAttribute(Columns.SAD_DESCRICAO);
                    final Long value = sitMap.get(key) + numParcelasLong;
                    if(key.length()>36){
                        sitMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        sitMap.put(key, value);
                    }
                } else{
                    final String key = (String) linha.getAttribute(Columns.SAD_DESCRICAO);
                    final Long value = numParcelasLong;
                    if(key.length()>36){
                        sitMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        sitMap.put(key, value);
                    }
                }
            }
            //            //Período
            //            if(campos.contains(Columns.ADE_DATA)){
            //                if(perMap.containsKey(linha.getAttribute(Columns.ADE_DATA))){
            //                    String key = (String) linha.getAttribute(Columns.ADE_DATA);
            //                    Long value = perMap.get(key) + (Long)linha.getAttribute("NUM_PARCELAS");
            //                    if(key.length()>36){
            //                        perMap.put(key.substring(0, 33)+"...", value);
            //                    }
            //                    else{
            //                        perMap.put(key, value);
            //                    }
            //
            //                } else{
            //                    String key = (String) linha.getAttribute(Columns.ADE_DATA);
            //                    Long value = (Long) linha.getAttribute("NUM_PARCELAS");
            //                    if(key.length()>36){
            //                        perMap.put(key.substring(0, 33)+"...", value);
            //                    }
            //                    else{
            //                        perMap.put(key, value);
            //                    }
            //                }
            //
            //                String dataBean = (String)linha.getAttribute(Columns.CSA_NOME) +"---"+ linha.getAttribute(Columns.ADE_DATA);
            //
            //                //para o gráfico de linhas
            //                if(csaPerMap.containsKey(dataBean)){
            //                    Long value = csaPerMap.get(dataBean) + (Long)linha.getAttribute("NUM_PARCELAS");
            //                    csaPerMap.put(dataBean, value);
            //                }
            //                else{
            //                    Long value = (Long)linha.getAttribute("NUM_PARCELAS");
            //                    csaPerMap.put(dataBean, value);
            //                }
            //            }
            //Orgï¿½o
            if(campos.contains(Columns.ORG_NOME)){
                if(orgMap.containsKey(linha.getAttribute(Columns.ORG_NOME))){
                    final String key = (String) linha.getAttribute(Columns.ORG_NOME);
                    final Long value = orgMap.get(key) + numParcelasLong;
                    if(key.length()>36){
                        orgMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        orgMap.put(key, value);
                    }
                } else{
                    final String key = (String) linha.getAttribute(Columns.ORG_NOME);
                    final Long value = numParcelasLong;
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
                    final String key = (String) linha.getAttribute(Columns.EST_NOME);
                    final Long value = estMap.get(key) + numParcelasLong;
                    if(key.length()>36){
                        estMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        estMap.put(key, value);
                    }
                } else{
                    final String key = (String) linha.getAttribute(Columns.EST_NOME);
                    final Long value = numParcelasLong;
                    if(key.length()>36){
                        estMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        estMap.put(key, value);
                    }
                }
            }

            //Data Inicio
            if(campos.contains(Columns.ADE_DATA)){
                if(dataIniMap.containsKey(linha.getAttribute(Columns.ADE_DATA))){
                    final String key = (String) linha.getAttribute(Columns.ADE_DATA);
                    final Long value = dataIniMap.get(key) + numParcelasLong;
                    if(key.length()>36){
                        dataIniMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        dataIniMap.put(key, value);
                    }

                } else{
                    final String key = (String) linha.getAttribute(Columns.ADE_DATA);
                    final Long value = numParcelasLong;
                    if(key.length()>36){
                        dataIniMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        dataIniMap.put(key, value);
                    }
                }


                //Gráficos de linha
                final String dataBean = (String)linha.getAttribute(Columns.CSA_NOME) +"---"+ linha.getAttribute(Columns.ADE_DATA);

                if(linha.getAttribute("STATUS").toString().contains("Liquidada")){
                    if(csaDataIniLiqMap.containsKey(dataBean)){
                        final Long value = csaDataIniLiqMap.get(dataBean) + numParcelasLong;
                        csaDataIniLiqMap.put(dataBean, value);
                    }
                    else{
                        final Long value = numParcelasLong;
                        csaDataIniLiqMap.put(dataBean, value);
                    }

                }else if(linha.getAttribute("STATUS").toString().contains("Rejeitada")){
                    if(csaDataIniRejMap.containsKey(dataBean)){
                        final Long value = csaDataIniRejMap.get(dataBean) + numParcelasLong;
                        csaDataIniRejMap.put(dataBean, value);
                    }
                    else{
                        final Long value = numParcelasLong;
                        csaDataIniRejMap.put(dataBean, value);
                    }
                }
            }

            //Cod Verba
            if(campos.contains(Columns.CNV_COD_VERBA)){
                if(codVerbaMap.containsKey(linha.getAttribute(Columns.CNV_COD_VERBA))){
                    final String key = (String) linha.getAttribute(Columns.CNV_COD_VERBA);
                    final Long value = codVerbaMap.get(key) + numParcelasLong;
                    if(key.length()>36){
                        codVerbaMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        codVerbaMap.put(key, value);
                    }

                } else{
                    final String key = (String) linha.getAttribute(Columns.CNV_COD_VERBA);
                    final Long value = numParcelasLong;
                    if(key.length()>36){
                        codVerbaMap.put(key.substring(0, 33)+"...", value);
                    }
                    else{
                        codVerbaMap.put(key, value);
                    }
                }
            }
        }


        //Consignatária
        if(campos.contains(Columns.CSA_NOME)){

            for(final String key: csaMap.keySet()){
                final SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(key, csaMap.get(key));
                listaCsa.add(elemento);
            }
            parametros.put("CSA", agrupador(listaCsa, null));
        }

        //Correspondente
        if(campos.contains(Columns.COR_NOME)){

            for(final String key: corMap.keySet()){
                final SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(key, corMap.get(key));
                listaCor.add(elemento);
            }
            parametros.put("CORRESPONDENTE", agrupador(listaCor, null));
        }

        //Serviço
        if(campos.contains(Columns.SVC_DESCRICAO)){

            for(final String key: serMap.keySet()){
                final SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(key, serMap.get(key));
                listaSer.add(elemento);
            }
            parametros.put("SERVICO", agrupador(listaSer, null));
        }
        //Situação
        if(campos.contains(Columns.SAD_DESCRICAO)){

            for(final String key: sitMap.keySet()){
                final SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(key, sitMap.get(key));
                listaSit.add(elemento);
            }
            parametros.put("SITUACAO", agrupador(listaSit, null));
        }
        //        //Período
        //        if(campos.contains(Columns.ADE_DATA)){
        //
        //            for(String key: perMap.keySet()){
        //                SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(key, perMap.get(key));
        //                listaPer.add(elemento);
        //            }
        //
        //            parametros.put("PERIODO", agrupador(listaPer, null));
        //
        //            //Para gráfico de linhas
        //            if(!listaCsa.isEmpty() && listaPer.size()>1){
        //                List<SinteticoDataSourceBean> listaGra = new ArrayList<SinteticoDataSourceBean>();
        //
        //                for(String key: csaPerMap.keySet()){
        //                    String[] dados = key.split("---");
        //                    SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(dados[0], csaPerMap.get(key), dados[1]);
        //                    listaGra.add(elemento);
        //                }
        //                parametros.put("CSA_PERIODO", agrupador(listaGra, parametros.get("CSA")));
        //            }
        //
        //        }
        //Orgão
        if(campos.contains(Columns.ORG_NOME)){

            for(final String key: orgMap.keySet()){
                final SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(key, orgMap.get(key));
                listaOrg.add(elemento);
            }
            parametros.put("ORGAO", agrupador(listaOrg, null));
        }
        //Estabelecimento
        if(campos.contains(Columns.EST_NOME)){

            for(final String key: estMap.keySet()){
                final SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(key, estMap.get(key));
                listaEst.add(elemento);
            }
            parametros.put("ESTABELECIMENTO", agrupador(listaEst, null));
        }

        //Data Inicio
        if(campos.contains(Columns.ADE_DATA)){

            for(final String key: dataIniMap.keySet()){
                final SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(key, dataIniMap.get(key));
                listaDataIni.add(elemento);
            }

            parametros.put("DATA_INICIO", agrupador(listaDataIni, null));

            if(!listaCsa.isEmpty() && (listaDataIni.size()>1)){
                List<SinteticoDataSourceBean> listaGra = new ArrayList<>();

                for(final String key: csaDataIniLiqMap.keySet()){
                    final String[] dados = key.split("---");
                    final SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(dados[0], csaDataIniLiqMap.get(key), dados[1]);
                    listaGra.add(elemento);
                }
                parametros.put("CSA_DATA_INICIO_LIQ", agrupador(listaGra, parametros.get("CSA")));


                listaGra = new ArrayList<>();
                for(final String key: csaDataIniRejMap.keySet()){
                    final String[] dados = key.split("---");
                    final SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(dados[0], csaDataIniRejMap.get(key), dados[1]);
                    listaGra.add(elemento);
                }
                parametros.put("CSA_DATA_INICIO_REJ", agrupador(listaGra, parametros.get("CSA")));
            }
        }


        //Cod Verba
        if(campos.contains(Columns.CNV_COD_VERBA)){

            for(final String key: codVerbaMap.keySet()){
                final SinteticoDataSourceBean elemento = new SinteticoDataSourceBean(key, codVerbaMap.get(key));
                listaCodVerba.add(elemento);
            }

            parametros.put("COD_VERBA", agrupador(listaCodVerba, null));
        }



        return parametros;
    }
}
