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
import com.zetra.econsig.report.jasper.dinamico.RelatorioSinteticoDecisaoJudicialInfo;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CamposRelatorioSinteticoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioSinteticoDecisaoJudicial</p>
 * <p>Description: Classe que dispara processo para montar relatório sintético de decisões judiciais</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioSinteticoDecisaoJudicial extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioSinteticoDecisaoJudicial.class);

    public ProcessaRelatorioSinteticoDecisaoJudicial(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
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

        if (parameterMap.containsKey("periodoIni")&& parameterMap.containsKey("periodoFim")) {
            strIniPeriodo = getParametro("periodoIni", parameterMap);
            strFimPeriodo = getParametro("periodoFim", parameterMap);
            paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
            paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
        } else {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            return;
        }

        String titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo);
        StringBuilder subTitulo = new StringBuilder();

        StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.sintetico.decisao.judicial", responsavel), responsavel, parameterMap, null));

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

        List<String> camposQuery = new ArrayList<>();
        List<String> camposRelatorio = new ArrayList<>();

        if (campos != null && campos.size() > 0) {
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
        List<String> svcCodigos = getFiltroSvcCodigo(parameterMap, subTitulo, nome, session, responsavel);
        List<String> srsCodigos = getFiltroSrsCodigo(parameterMap, subTitulo, nome, session, responsavel);
        String tjuCodigo = getFiltroTipoJustica(parameterMap, subTitulo, nome, session, responsavel);
        String cidCodigo = getFiltroComarcaJustica(parameterMap, subTitulo, nome, session, responsavel);
        String ufCodigo = "";
        if (TextHelper.isNull(cidCodigo)) {
            ufCodigo = getFiltroEstadoJustica(parameterMap, subTitulo, nome, session, responsavel);
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
            criterio.setAttribute("ORG_CODIGO", orgCodigo);
            criterio.setAttribute("CSA_CODIGO", csaCodigo);
            criterio.setAttribute("SVC_CODIGO", svcCodigos);
            criterio.setAttribute("TJU_CODIGO", tjuCodigo);
            criterio.setAttribute("CID_CODIGO", cidCodigo);
            criterio.setAttribute("UF_CODIGO", ufCodigo);
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
            criterio.setAttribute("TIPO_ORD", tipoOrdMap);
            criterio.setAttribute(Columns.SRS_CODIGO, srsCodigos);
            if (responsavel.isCor() && TextHelper.isNull(parameterMap.get("corCodigo"))) {
                subTitulo.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                criterio.setAttribute(Columns.COR_CODIGO, corCodigos);
            }

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

            RelatorioSinteticoDecisaoJudicialInfo sinteticoReportInfo = new RelatorioSinteticoDecisaoJudicialInfo(relatorio);
            sinteticoReportInfo.setCriterios(criterio);
            sinteticoReportInfo.buildJRXML(parameters, responsavel);

            String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);

            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            Pair<String[], List<TransferObject>> conteudo = relatorioController.geraRelatorioSinteticoDecisaoJudicial(criterio, responsavel);
            List<Object[]> conteudoList = DTOToList(conteudo.second, conteudo.first);

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
}
