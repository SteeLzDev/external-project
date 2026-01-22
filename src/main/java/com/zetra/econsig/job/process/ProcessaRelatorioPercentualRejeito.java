package com.zetra.econsig.job.process;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.jasper.dto.PercentualRejeitoBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioPercentualRejeito</p>
 * <p> Description: Classe para processamento de relatorio de percentual de rejeito</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioPercentualRejeito extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioPercentualRejeito.class);

    private String periodo;
    private List<String> orgCodigos;
    private List<String> estCodigos;
    private String agdCodigo;
    private boolean integrada = false;

    public ProcessaRelatorioPercentualRejeito(String agdCodigo, AcessoSistema responsavel) throws AgendamentoControllerException {
        super(ConfigRelatorio.getInstance().getRelatorio("percentual_rejeito"), new HashMap<String, String[]>(), null, false, responsavel);

        this.agdCodigo = agdCodigo;
        setParameterMap();

        if (parameterMap.containsKey("periodo")) {
            periodo = parameterMap.get("periodo")[0];
            if (TextHelper.isNull(periodo)) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.periodo.nao.informado", responsavel));
                throw new AgendamentoControllerException("mensagem.erro.periodo.nao.informado", responsavel);
            } else {
                try {
                    DateHelper.parse(periodo, "yyyy-MM-dd");
                } catch (ParseException e) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.formato.invalido.para.periodo", responsavel));
                    throw new AgendamentoControllerException("mensagem.erro.formato.invalido.para.periodo", responsavel);
                }
            }
        }

        if (parameterMap.containsKey("integrada")) {
            integrada = Boolean.valueOf(parameterMap.get("integrada")[0]);
        }

        if (parameterMap.containsKey(Columns.getColumnName(Columns.ORG_CODIGO))) {
            String[] orgs = parameterMap.get(Columns.getColumnName(Columns.ORG_CODIGO));
            if (orgs != null && orgs[0] != null && !orgs[0].equals("")) {
                orgCodigos = Arrays.asList(orgs);
            }
        }

        if (parameterMap.containsKey(Columns.getColumnName(Columns.EST_CODIGO))) {
            String[] ests = parameterMap.get(Columns.getColumnName(Columns.EST_CODIGO));
            if (ests != null && ests[0] != null && !ests[0].equals("")) {
                estCodigos = Arrays.asList(ests);
            }
        }

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        parameterMap.put("formato", new String[]{"PDF"});
    }

    public ProcessaRelatorioPercentualRejeito(String periodo, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) {
        super(ConfigRelatorio.getInstance().getRelatorio("percentual_rejeito"), new HashMap<String, String[]>(), null, false, responsavel);

        this.periodo = periodo;
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        parameterMap.put("formato", new String[]{"PDF"});
    }

    @Override
    protected void executar() {
        String hoje = getHoje("ddMMyyHHmmss");
        String titulo = relatorio.getTitulo();
        StringBuilder subtitulo = new StringBuilder();
        try {
            subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.periodo.singular.arg0", responsavel, DateHelper.toPeriodString(DateHelper.parse(periodo, "yyyy-MM-dd"))));
            subtitulo.append(System.getProperty("line.separator"));
        } catch (ParseException e) {
            LOG.error("Não foi possível realizar a formatação do período: " + periodo, e);
        }

        String nome = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.percentual.rejeito", responsavel) + "_" + hoje;

        //Lista as consignatárias que possuem contratos na carteira total
        List<TransferObject> consignatarias = null;
        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            consignatarias = relatorioController.lstPercentualRejeitoTotal(periodo, orgCodigos, estCodigos, integrada, responsavel);
        } catch (RelatorioControllerException e) {
            LOG.error("Não foi possível listar as consignatárias.");
            consignatarias = new ArrayList<>();
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            List<TransferObject> orgaos = new ArrayList<>();
            try {
                TransferObject criterioOrg = new CustomTransferObject();
                criterioOrg.setAttribute(Columns.ORG_CODIGO, orgCodigos);
                orgaos = new ConsignanteDelegate().lstOrgaos(criterioOrg, responsavel);
                Iterator<TransferObject> iteOrg = orgaos.iterator();
                if (iteOrg.hasNext()) {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ""));
                }
                while (iteOrg.hasNext()) {
                    TransferObject orgao = iteOrg.next();
                    String orgIdentificador = orgao.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                    subtitulo.append(orgIdentificador);
                    subtitulo.append(" - ").append(orgao.getAttribute(Columns.ORG_NOME).toString());
                    nome += "_" + orgIdentificador;
                    if (iteOrg.hasNext()) {
                        subtitulo.append(", ");
                    }
                }
                if (subtitulo.length() > 0) {
                    subtitulo.append(System.getProperty("line.separator"));
                }

            } catch (ConsignanteControllerException ex) {
                LOG.error("Não foi possível listar os órgaos.");
            }
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            List<TransferObject> estabelecimentos = new ArrayList<>();
            try {
                TransferObject criterioEst = new CustomTransferObject();
                criterioEst.setAttribute(Columns.EST_CODIGO, estCodigos);
                estabelecimentos = new ConsignanteDelegate().lstEstabelecimentos(criterioEst, responsavel);
                Iterator<TransferObject> iteEst = estabelecimentos.iterator();
                if (iteEst.hasNext()) {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.estabelecimento.singular.arg0", responsavel, ""));
                }
                while (iteEst.hasNext()) {
                    TransferObject estabelecimento = iteEst.next();
                    String estIdentificador = estabelecimento.getAttribute(Columns.EST_IDENTIFICADOR).toString();
                    subtitulo.append(estIdentificador);
                    subtitulo.append(" - ").append(estabelecimento.getAttribute(Columns.EST_NOME).toString());
                    nome += "_" + estIdentificador;
                    if (iteEst.hasNext()) {
                        subtitulo.append(", ");
                    }
                }
                if (subtitulo.length() > 0) {
                    subtitulo.append(System.getProperty("line.separator"));
                }
            } catch (ConsignanteControllerException ex) {
                LOG.error("Não foi possível listar os estabelecimentos.");
            }
        }

        try {
            String strFormato = getStrFormato();
            String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(true, session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

            try {
                RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
                List<TransferObject> percRejeitoTotal = relatorioController.lstPercentualRejeitoTotal(periodo, orgCodigos, estCodigos, integrada, responsavel);
                parameters.put("PERC_REJEITO_TOTAL", recuperaPercentualRejeitoBean(percRejeitoTotal));

                List<TransferObject> percRejeitoPeriodo = relatorioController.lstPercentualRejeitoPeriodo(periodo, orgCodigos, estCodigos, integrada, responsavel);
                parameters.put("PERC_REJEITO_PERIODO", recuperaPercentualRejeitoBean(percRejeitoPeriodo));
            } catch (RelatorioControllerException e) {
                LOG.error("Não foi possível recuperar o percentual de rejeito!");
                if (!TextHelper.isNull(periodo)) {
                    LOG.error("PERIODO: " + periodo);
                }
                if (orgCodigos != null && !orgCodigos.isEmpty()) {
                    LOG.error("ORGAOS: '" + TextHelper.join(orgCodigos, "', '") + "'");
                }
                if (estCodigos != null && !estCodigos.isEmpty()) {
                    LOG.error("ESTABELECIMENTOS: '" + TextHelper.join(estCodigos, "', '") + "'");
                }
            }

            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);
            String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

            setMensagem(reportNameZip, relatorio.getTipo(), relatorio.getTitulo(), session);

            LOG.debug("CONSIGNATARIAS: " + consignatarias.size());
            Iterator<TransferObject> iteCsa = consignatarias.iterator();

            while (iteCsa.hasNext()) {
                TransferObject csa = iteCsa.next();
                String csaCodigo = csa.getAttribute(Columns.CSA_CODIGO).toString();
                String csaIdentificador = csa.getAttribute(Columns.CSA_IDENTIFICADOR).toString();
                StringBuilder subtituloCsa = new StringBuilder(subtitulo);
                subtituloCsa.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, csaIdentificador));
                subtituloCsa.append(" - ").append(csa.getAttribute("CSA").toString());
                parameters.put("CSA_CODIGO", csaCodigo);
                parameters.put(ReportManager.REPORT_FILE_NAME, nome + "_" + csaIdentificador);
                String diretorioRelatorioCsa = geraDirExportRelatorioCsa(csaCodigo, "csa");
                parameters.put(ReportManager.REPORT_DIR_EXPORT, diretorioRelatorioCsa);
                parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtituloCsa.toString());

                String nomeRelConsig = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);
                nomeRelConsig = nomeRelConsig.substring(nomeRelConsig.lastIndexOf(File.separator) + 1, nomeRelConsig.length());
                LOG.info("Consignataria [" + csaCodigo + "]: " + nomeRelConsig);

                String arqRelatorioCsa = diretorioRelatorioCsa + File.separatorChar + nomeRelConsig;
                String fileZip = arqRelatorioCsa.substring(0, arqRelatorioCsa.lastIndexOf('.')) + ".zip";
                FileHelper.zip(arqRelatorioCsa, fileZip);
                FileHelper.delete(arqRelatorioCsa);
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

    private String geraDirExportRelatorioCsa(String codigoEntidade, String tipoEntidade) {
        String path = null;
        if (relatorio != null) {
            path = ParamSist.getDiretorioRaizArquivos();
            path += File.separatorChar + "relatorio" + File.separatorChar + tipoEntidade;
            path += File.separatorChar + relatorio.getTipo();
            path += File.separatorChar + codigoEntidade;

            // Garante que existirão os diretórios especificados pelo path.
            new File(path).mkdirs();
        }
        return path;
    }

    private void setParameterMap() throws AgendamentoControllerException {
        AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
        Map<String, List<String>> retorno = agendamentoController.lstParametrosAgendamento(agdCodigo, responsavel);

        Iterator<Map.Entry<String, List<String>>> iteKey = retorno.entrySet().iterator();
        while (iteKey.hasNext()) {
            Map.Entry<String, List<String>> entry = iteKey.next();
            String chave = entry.getKey();
            List<String> valores = entry.getValue();
            String[] strValores = valores.toArray(new String[0]);
            parameterMap.put(chave, strValores);
        }
    }

    public List<PercentualRejeitoBean> recuperaPercentualRejeitoBean(List<TransferObject> lista) {
        List<PercentualRejeitoBean> retorno = new ArrayList<>();

        if (lista != null) {
            Iterator<TransferObject> ite = lista.iterator();
            while (ite.hasNext()) {
                TransferObject to = ite.next();
                PercentualRejeitoBean bean = new PercentualRejeitoBean();
                if (to.getAttribute("PERIODO") != null) {
                    bean.setPeriodo(DateHelper.toPeriodString((java.util.Date) to.getAttribute("PERIODO")));
                }
                if (!TextHelper.isNull(to.getAttribute(Columns.CSA_CODIGO))) {
                    bean.setCsaCodigo(to.getAttribute(Columns.CSA_CODIGO).toString());
                }
                if (!TextHelper.isNull(to.getAttribute(Columns.CSA_IDENTIFICADOR))) {
                    bean.setCsaIdentificador(to.getAttribute(Columns.CSA_IDENTIFICADOR).toString());
                }
                if (!TextHelper.isNull(to.getAttribute("CSA_ID"))) {
                    bean.setCsaId(to.getAttribute("CSA_ID").toString());
                }
                if (!TextHelper.isNull(to.getAttribute("CSA"))) {
                    bean.setCsa(to.getAttribute("CSA").toString());
                }
                try {
                    bean.setLiquidadas(new BigDecimal(to.getAttribute("LIQUIDADAS").toString()));
                } catch (Exception e) {
                    LOG.warn(e.getMessage());
                }
                try {
                    bean.setRejeitadas(new BigDecimal(to.getAttribute("REJEITADAS").toString()));
                } catch (Exception e) {
                    LOG.warn(e.getMessage());
                }
                try {
                    bean.setPercRejeito(new BigDecimal(to.getAttribute("PERC_REJEITO").toString()));
                } catch (Exception e) {
                    LOG.warn(e.getMessage());
                }

                retorno.add(bean);
            }
        }

        return retorno;
    }
}
