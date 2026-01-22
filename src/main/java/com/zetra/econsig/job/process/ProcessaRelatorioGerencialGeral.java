package com.zetra.econsig.job.process;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ImpRetornoDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.periodo.RepasseHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.ComprometimentoBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCategoriaBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCrsBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCsaBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorSvcBean;
import com.zetra.econsig.report.jasper.dto.CorPorCsaBean;
import com.zetra.econsig.report.jasper.dto.GerencialEstatiscoMargemBean;
import com.zetra.econsig.report.jasper.dto.GerencialInadimplenciaBean;
import com.zetra.econsig.report.jasper.dto.GerencialInadimplenciaCsaBean;
import com.zetra.econsig.report.jasper.dto.GerencialInadimplenciaEvolucaoBean;
import com.zetra.econsig.report.jasper.dto.GerencialQtdeSerPorFaixaMargemBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorCrsBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorEstBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorOrgBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorTipoBean;
import com.zetra.econsig.report.reports.HeadingsScriptlet;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioGerencialGeral</p>
 * <p> Description: Processa o Relatório de Gerencial Geral</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioGerencialGeral extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioGerencialGeral.class);

    private static final int LIMITE_SERVIDOR_POR_CRS = 10;
    private static final int LIMITE_TIPO_POR_SERVIDOR = 10;
    private static final int LIMITE_CONTRATO_POR_CSA = 10;
    private static final int LIMITE_CORRESPONDENTE_POR_CSA = 10;
    private static final int LIMITE_CONTRATOS_POR_CRS = 10;
    private static final int LIMITE_CONTRATOS_POR_CATEGORIA = 10;
    private static final int LIMITE_CONTRATOS_POR_SVC = 10;

    private List<TransferObject> servicos;

    public ProcessaRelatorioGerencialGeral(Relatorio relatorio, Map<String, String[]> parameterMap, AcessoSistema responsavel) throws AgendamentoControllerException {
        super(relatorio, parameterMap, null, true, responsavel);

        // Seta serviços
        setServicos();
    }

    @Override
    protected void executar() {
        final StringBuilder titulo = new StringBuilder(ApplicationResourcesHelper.getMessage("rotulo.relatorio.gerencial.titulo", responsavel));
        final StringBuilder subtitulo = new StringBuilder("");
        final String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;

        String strPeriodo = "";
        String paramPeriodo = "";

        //Pega parametro periodo
        if (parameterMap.containsKey("periodo") && !TextHelper.isNull(getParametro("periodo", parameterMap))) {
            strPeriodo = getParametro("periodo", parameterMap);
            paramPeriodo = formatarPeriodo(strPeriodo);
        }

        // CONSTROI NOME DO ARQUIVO NO FORMATO: relatorio_dataHora
        final String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.gerencial", responsavel), responsavel, parameterMap, null);

        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.REPORT_FILE_NAME, ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.gerencial", responsavel));
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO_ENTIDADE, getCaminhoLogoEntidade(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO_ECONSIG, getCaminhoLogoEConsig(responsavel));
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(true,null,responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome);
        parameters.put(ReportManager.PARAM_CSE_NOME, getCseNome(responsavel));
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
        parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
        parameters.put(ReportManager.REPORT_SCRIPTLET, new HeadingsScriptlet());
        parameters.put(ReportManager.PARAM_NAME_PERIODO, paramPeriodo);
        parameters.put(ReportManager.PARAM_NAME_MES_ANO, DateHelper.format(Calendar.getInstance().getTime(), "MMM/yyyy"));
        parameters.put(ReportManager.PARAM_NAME_ANO_ATUAL, DateHelper.format(Calendar.getInstance().getTime(), "yyyy"));
        parameters.put(ReportManager.PARAM_NAME_DATA_ATUAL, DateHelper.format(Calendar.getInstance().getTime(), LocaleHelper.getDatePattern()));
        parameters.put("RESPONSAVEL", responsavel);

        final boolean quinzenal = !PeriodoHelper.folhaMensal(responsavel);
        parameters.put("QUINZENAL", quinzenal);

        geraInformacoesCse(parameters);
        geraInformacoesCsa(parameters);
        geraInformacoesMargens(parameters);
        geraEstatisticoServidores(parameters);
        geraQuantitativaOrgaoServidores(parameters);
        geraQuantitativaCsaContratos(parameters);

        // Uso os dados de MARGEM_USADA e MARGEM_TOTAL do Comprometimento de Margens no Estatistico de Margens
        geraComprometimentoMargens(parameters);
        geraEstatisticoMargens(parameters);

        geraInformacoesContratos(parameters);
        geraInformacoesInadimplencia(parameters);

        geraRankingTaxas(parameters);
        geraTaxasEfetivas(parameters);
        geraListaSituacaoConsignataria(parameters);

        final String margensPossiveisExibicao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QUAIS_MARGENS_SERAO_EXIBIDAS_RELATORIO_GER_GERAL, responsavel);
        if(!TextHelper.isNull(margensPossiveisExibicao)) {
            final String[] margensRelatorio = margensPossiveisExibicao.split("[\\p{Space},;]");
            for(final String margem : margensRelatorio) {
                if(margem.equals(String.valueOf(CodedValues.INCIDE_MARGEM_NAO))) {
                    parameters.put("PARAM_EXIBE_MARGEM_NAO", "true");
                } else if(margem.equals(String.valueOf(CodedValues.INCIDE_MARGEM_SIM))) {
                    parameters.put("PARAM_EXIBE_MARGEM_1", "true");
                } else if(margem.equals(String.valueOf(CodedValues.INCIDE_MARGEM_SIM_2))) {
                    parameters.put("PARAM_EXIBE_MARGEM_2", "true");
                } else if(margem.equals(String.valueOf(CodedValues.INCIDE_MARGEM_SIM_3))) {
                    parameters.put("PARAM_EXIBE_MARGEM_3", "true");
                } else {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.relatorio.gerencialgeral.margens", responsavel));
                }
            }
        } else {
            parameters.put("PARAM_EXIBE_TODAS_MARGENS", "true");
        }

        String reportName = null;
        try {
            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            final String strFormato = getStrFormato();
            reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            final String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (final Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }

    protected void geraEstatisticoMargens(HashMap<String, Object> parameters) {
        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final List<GerencialEstatiscoMargemBean> estatisticoMargem = relatorioController.lstEstatiscoMargem(responsavel);
            BigDecimal margemMedia1 = BigDecimal.ZERO;
            BigDecimal desvioMargem1 = BigDecimal.ZERO;
            BigDecimal margemMedia2 = BigDecimal.ZERO;
            BigDecimal desvioMargem2 = BigDecimal.ZERO;
            BigDecimal margemMedia3 = BigDecimal.ZERO;
            BigDecimal desvioMargem3 = BigDecimal.ZERO;
            for (final GerencialEstatiscoMargemBean cto : estatisticoMargem) {
                if ((cto.getMargemRestMedia() != null) && (cto.getMargemRestMedia().compareTo(BigDecimal.ZERO) != 0)) {
                    margemMedia1 = cto.getMargemRestMedia();
                }
                if ((cto.getMargemRestDesvio() != null) && (cto.getMargemRestDesvio().compareTo(BigDecimal.ZERO) != 0)) {
                    desvioMargem1 = cto.getMargemRestDesvio();
                }
                if ((cto.getMargemRestMedia2() != null) && (cto.getMargemRestMedia2().compareTo(BigDecimal.ZERO) != 0)) {
                    margemMedia2 = cto.getMargemRestMedia2();
                }
                if ((cto.getMargemRestDesvio2() != null) && (cto.getMargemRestDesvio2().compareTo(BigDecimal.ZERO) != 0)) {
                    desvioMargem2 = cto.getMargemRestDesvio2();
                }
                if ((cto.getMargemRestMedia3() != null) && (cto.getMargemRestMedia3().compareTo(BigDecimal.ZERO) != 0)) {
                    margemMedia3 = cto.getMargemRestMedia3();
                }
                if ((cto.getMargemRestDesvio3() != null) && (cto.getMargemRestDesvio3().compareTo(BigDecimal.ZERO) != 0)) {
                    desvioMargem3 = cto.getMargemRestDesvio3();
                }
            }

            final List<GerencialQtdeSerPorFaixaMargemBean> qtdeSerPorFaixaMargem1 = relatorioController.lstQtdeSerPorFaixaMargem(margemMedia1, desvioMargem1, CodedValues.INCIDE_MARGEM_SIM, responsavel);
            final List<GerencialQtdeSerPorFaixaMargemBean> qtdeSerPorFaixaMargem2 = relatorioController.lstQtdeSerPorFaixaMargem(margemMedia2, desvioMargem2, CodedValues.INCIDE_MARGEM_SIM_2, responsavel);
            final List<GerencialQtdeSerPorFaixaMargemBean> qtdeSerPorFaixaMargem3 = relatorioController.lstQtdeSerPorFaixaMargem(margemMedia3, desvioMargem3, CodedValues.INCIDE_MARGEM_SIM_3, responsavel);

            parameters.put("ESTATISTICO_MARGEM", estatisticoMargem);
            parameters.put("QTDE_SER_POR_FAIXA_MARGEM_1", qtdeSerPorFaixaMargem1);
            parameters.put("QTDE_SER_POR_FAIXA_MARGEM_2", qtdeSerPorFaixaMargem2);
            parameters.put("QTDE_SER_POR_FAIXA_MARGEM_3", qtdeSerPorFaixaMargem3);

        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }

    protected void geraInformacoesCse(HashMap<String, Object> parameters) {
        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        List<TransferObject> orgaos = null;
        try {
            orgaos = cseDelegate.lstOrgaos(null, responsavel);
        } catch (final ConsignanteControllerException e) {
            LOG.error("Não foi possível listar os órgãos.", e);
            orgaos = new ArrayList<>();
        }

        List<TransferObject> estabelecimentos = null;
        try {
            estabelecimentos = cseDelegate.lstEstabelecimentos(null, responsavel);
        } catch (final ConsignanteControllerException e) {
            LOG.error("Não foi possível listar os estabelecimentos.", e);
            estabelecimentos = new ArrayList<>();
        }

        parameters.put("ORGAOS", orgaos);
        parameters.put("ESTABELECIMENTOS", estabelecimentos);


        try {
            final ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            final List<ConsignanteTransferObject> consignante = new ArrayList<>();
            consignante.add(cse);
            parameters.put("CONSIGNANTE", consignante);
        } catch (final ConsignanteControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        final String linkAcesso = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel).toString() : "";
        String dataImplantacao = "";
        if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_DATA_IMPLANTACAO_SISTEMA, responsavel))) {
            try {
                dataImplantacao = DateHelper.reformat(ParamSist.getInstance().getParam(CodedValues.TPC_DATA_IMPLANTACAO_SISTEMA, responsavel).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
            } catch (final ParseException e) {
                LOG.error("Não foi possível realizar a conversão da data de implantação do sistema.", e);
            }
        }
        String diaCorte = "";
        String diaRepasse = "";
        try {
            diaCorte = String.valueOf(PeriodoHelper.getInstance().getProximoDiaCorte(null, responsavel));
            diaRepasse = String.valueOf(RepasseHelper.getDiaRepasse(null, PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel), responsavel));
        } catch (PeriodoException|ViewHelperException e) {
            LOG.error(e.getMessage(), e);
        }
        final String versaoAtual = ApplicationResourcesHelper.getMessage("release.tag", responsavel);
        final Boolean moduloCompra = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, responsavel);
        final String emailSuporte = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel).toString() : "";
        final String telefoneSuporte = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TELEFONE_SUPORTE_ZETRASOFT, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_TELEFONE_SUPORTE_ZETRASOFT, responsavel).toString() : "";

        parameters.put("LINK_ACESSO_SISTEMA", linkAcesso);
        parameters.put("DATA_IMPLANTACAO", dataImplantacao);
        parameters.put("DIA_CORTE", diaCorte);
        parameters.put("DIA_REPASSE", diaRepasse);
        parameters.put("VERSAO_ATUAL_SISTEMA", versaoAtual);
        parameters.put("MODULO_COMPRA", moduloCompra);
        parameters.put("EMAIL_SUPORTE", emailSuporte);
        parameters.put("TELEFONE_SUPORTE", telefoneSuporte);
    }

    protected void geraInformacoesCsa(HashMap<String, Object> parameters) {
        try {
            final ConsignatariaDelegate cseDelegate = new ConsignatariaDelegate();

            final TransferObject criterioCsa = new CustomTransferObject();
            final TransferObject criterioCor = new CustomTransferObject();

            criterioCsa.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);
            criterioCor.setAttribute(Columns.COR_ATIVO, CodedValues.STS_ATIVO);
            final int qtdeCsaAtivas = cseDelegate.countConsignatarias(criterioCsa, responsavel);

            final List<Short> statusBloq = new ArrayList<>();
            statusBloq.add(CodedValues.STS_INATIVO);
            statusBloq.add(CodedValues.STS_INATIVO_CSE);
            statusBloq.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);

            criterioCsa.setAttribute(Columns.CSA_ATIVO, statusBloq);
            criterioCor.setAttribute(Columns.COR_ATIVO, statusBloq);
            final int qtdeCsaInativas = cseDelegate.countConsignatarias(criterioCsa, responsavel);
            int qtdeCorAtivos = 0;
            int qtdeCorInativos = 0;

            if (!ParamSist.getBoolParamSist(CodedValues.TPC_OCULTAR_INFO_COR_RELATORIO_GERENCIAL_GERAL, responsavel)) {
                qtdeCorAtivos = cseDelegate.countCorrespondentes(criterioCor, responsavel);
                qtdeCorInativos = cseDelegate.countCorrespondentes(criterioCor, responsavel);
            }

            parameters.put("QTDE_CSA_TOTAL", qtdeCsaAtivas + qtdeCsaInativas);
            parameters.put("QTDE_CSA_ATIVOS_TOTAL", qtdeCsaAtivas);
            parameters.put("QTDE_CSA_INATIVOS_TOTAL", qtdeCsaInativas);
            parameters.put("QTDE_COR_TOTAL", qtdeCorAtivos + qtdeCorInativos);
            parameters.put("QTDE_COR_ATIVOS_TOTAL", qtdeCorAtivos);
            parameters.put("QTDE_COR_INATIVOS_TOTAL", qtdeCorInativos);
        } catch (final ConsignatariaControllerException e) {
            LOG.error("Não foi possível recuperar a quantidade de consignatárias.", e);
        }

        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final String periodo = parameters.get(ReportManager.PARAM_NAME_PERIODO) != null ? parameters.get(ReportManager.PARAM_NAME_PERIODO).toString() : null;
            final List<ContratosPorCsaBean> qtdeContratosPorCsa = relatorioController.lstContratosPorCsa(LIMITE_CONTRATO_POR_CSA, periodo, false, responsavel);
            final List<CorPorCsaBean> qtdeCorrespondentesPorCsa = relatorioController.lstCorPorCsa(LIMITE_CORRESPONDENTE_POR_CSA, responsavel);

            parameters.put("QTDE_CONTRATOS_POR_CSA", relatorioController.lstContratosPorCsa(0, periodo, false, responsavel).size());
            parameters.put("LISTA_QTDE_CONTRATOS_POR_CSA", qtdeContratosPorCsa);
            parameters.put("LISTA_QTDE_COR_POR_CSA", qtdeCorrespondentesPorCsa);
        } catch (final RelatorioControllerException ex) {
            LOG.error("ERROR: " + ex.getMessage());
        }
    }

    protected void geraInformacoesContratos(HashMap<String, Object> parameters) {
        final String periodo = parameters.get(ReportManager.PARAM_NAME_PERIODO) != null ? parameters.get(ReportManager.PARAM_NAME_PERIODO).toString() : null;

        try {
            final ConvenioDelegate cnvDelegate = new ConvenioDelegate();
            parameters.put("QTDE_SVC_TOTAL", cnvDelegate.lstServicos(null, responsavel).size());

            final TransferObject criterioInfo = new CustomTransferObject();
            criterioInfo.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_ATIVO);
            parameters.put("QTDE_SVC_ATIVOS_TOTAL", cnvDelegate.lstServicos(criterioInfo, responsavel).size());

            criterioInfo.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_INATIVO);
            parameters.put("QTDE_SVC_INATIVOS_TOTAL", cnvDelegate.lstServicos(criterioInfo, responsavel).size());

        } catch (final ConvenioControllerException e) {
            LOG.error("Não foi possível recuperar a quantidade de serviços.", e);
        }

        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);

            parameters.put("QTDE_CONTRATOS_ATIVOS", relatorioController.qtdeContratosPorCategoria(periodo, responsavel));

            if (ParamSist.paramEquals(CodedValues.TPC_CATEGORIA_RELATORIO_GERENCIAL_GERAL_CORRESPONDE_CARGO_RSE, CodedValues.TPC_SIM, responsavel)) {
                final List<ContratosPorCrsBean> qtdeContratosPorCrs = relatorioController.lstContratosPorCrs(LIMITE_CONTRATOS_POR_CRS, periodo, responsavel);
                parameters.put("QTDE_CONTRATOS_POR_CRS", relatorioController.qtdeContratosPorCrs(periodo, responsavel));
                parameters.put("LISTA_QTDE_CONTRATOS_POR_CRS", qtdeContratosPorCrs);
            } else {
                final List<ContratosPorCategoriaBean> qtdeContratosPorCategoria = relatorioController.lstContratosPorCategoria(LIMITE_CONTRATOS_POR_CATEGORIA, periodo, responsavel);
                parameters.put("QTDE_CONTRATOS_POR_CATEGORIA", relatorioController.qtdeContratosPorCategoria(periodo, responsavel));
                parameters.put("LISTA_QTDE_CONTRATOS_POR_CATEGORIA", qtdeContratosPorCategoria);
            }

        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final List<ContratosPorSvcBean> qtdeContratosPorCategoria = relatorioController.lstContratosPorSvc(LIMITE_CONTRATOS_POR_SVC, periodo, false, responsavel);

            parameters.put("QTDE_CONTRATOS_POR_SVC", relatorioController.lstContratosPorSvc(0, periodo, false, responsavel).size());
            parameters.put("LISTA_QTDE_CONTRATOS_POR_SVC", qtdeContratosPorCategoria);

        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            parameters.put("TOTAL_PRESTACAO_EMPRESTIMO", relatorioController.getTotalPrestacaoEmprestimo(responsavel));
            parameters.put("SALDO_DEVEDOR_EMPRESTIMO", relatorioController.getSaldoDevedorEmprestimo(responsavel));
        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }

    protected void geraInformacoesInadimplencia(HashMap<String, Object> parameters) {
        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final Calendar calendar = Calendar.getInstance();
            calendar.getTime();

            final String periodo = parameters.get(ReportManager.PARAM_NAME_PERIODO) != null ? parameters.get(ReportManager.PARAM_NAME_PERIODO).toString() : null;

            // Obtenho a lista dos últimos períodos exportados que já possuem retorno
            final List<TransferObject> lstHistoricoRetorno = new ImpRetornoDelegate().lstHistoricoConclusaoRetorno(null, 12, periodo, responsavel);

            // Seleciona da lista o maior período porque este é o último período que possui retorno
            final List<Date> lstPeriodos = new ArrayList<>();
            Date periodoAtual = null;
            if ((lstHistoricoRetorno != null) && !lstHistoricoRetorno.isEmpty()) {
                final Iterator<TransferObject> it = lstHistoricoRetorno.iterator();
                TransferObject historico = null;

                while (it.hasNext()) {
                    historico = it.next();
                    final Date periodoTemp = (Date) historico.getAttribute(Columns.HCR_PERIODO);
                    lstPeriodos.add(periodoTemp);
                    if ((periodoAtual == null) || periodoTemp.after(periodoAtual)) {
                        periodoAtual = periodoTemp;
                    }
                }

                // Recupera relatório de inadimplência
                final GerencialInadimplenciaBean bean = relatorioController.geraRelatorioInadimplencia(periodoAtual, responsavel);

                parameters.put("COUNT_TOTAL_CARTEIRA", bean.getQtdeTotalCarteira());
                parameters.put("COUNT_TOTAL_CARTEIRA_EMPRESTIMO", bean.getQtdeTotalCarteiraEmprestimo());

                Float porcentagemTotalContratosInadimplencia = 0.0f;
                if ((!TextHelper.isNull(bean.getQtdeTotalCarteira()) && (bean.getQtdeTotalCarteira() > 0))) {
                    porcentagemTotalContratosInadimplencia = ((bean.getQtdeInadimplenciaTotal() / bean.getQtdeTotalCarteira().floatValue()) * 100.0f);
                }

                Float porcentagemEmprestimoContratosInadimplencia = 0.0f;
                if ((!TextHelper.isNull(bean.getQtdeTotalCarteiraEmprestimo()) && (bean.getQtdeTotalCarteiraEmprestimo() > 0))) {
                    porcentagemEmprestimoContratosInadimplencia = ((bean.getQtdeInadimplenciaEmprestimo() / bean.getQtdeTotalCarteiraEmprestimo().floatValue()) * 100.0f);
                }

                parameters.put("PORC_TOTAL_CONTRATOS_INADIMPLENCIA", porcentagemTotalContratosInadimplencia);
                parameters.put("PORC_EMPRESTIMO_CONTRATOS_INADIMPLENCIA", porcentagemEmprestimoContratosInadimplencia);

                if (bean.getQtdeInadimplenciaCsa().isEmpty()) {
                    final GerencialInadimplenciaCsaBean gerencialInadimplenciaCsaBean = new GerencialInadimplenciaCsaBean();
                    gerencialInadimplenciaCsaBean.setCsaNome("");
                    gerencialInadimplenciaCsaBean.setCsaQtdeInadimplencia((long) 0);
                    bean.getQtdeInadimplenciaCsa().add(gerencialInadimplenciaCsaBean);
                }
                parameters.put("CSA_INADIMPLENCIA", bean.getQtdeInadimplenciaCsa());
                parameters.put("PERIODO_INADIMPLENCIA", periodoAtual);

                // Recupera relatório de evolução da inadimplência
                final List<GerencialInadimplenciaEvolucaoBean> evolucao = relatorioController.geraRelatorioInadimplenciaEvolucao(lstPeriodos, responsavel);
                parameters.put("EVOLUCAO", evolucao);
            }

        } catch (final RelatorioControllerException | ImpRetornoControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

    }

    protected void geraEstatisticoServidores(HashMap<String, Object> parameters) {
        try {
            final ServidorDelegate serDelegate = new ServidorDelegate();
            parameters.put("QTDE_TOTAL_SER", serDelegate.countRegistroServidor(null, null, null, responsavel));

            final List<String> srsCodigo = new ArrayList<>();
            srsCodigo.add(CodedValues.SRS_ATIVO);
            parameters.put("QTDE_SER_ATIVOS", serDelegate.countRegistroServidor(srsCodigo, null, null, responsavel));

            parameters.put("QTDE_SER_EXCLUIDOS", serDelegate.countRegistroServidorExcluidos(responsavel));

            parameters.put("QTDE_SER_TRANSFERIDOS", serDelegate.countRegistroServidorTransferidos(responsavel));

        } catch (final ServidorControllerException e1) {
            LOG.error("Não foi possível localizar as informações dos servidores.", e1);
        }

        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);

            final List<ServidorPorEstBean> serPorEst = relatorioController.lstServidorPorEst(responsavel);
            parameters.put("SER_POR_EST", serPorEst);

            final List<ServidorPorOrgBean> serPorOrg = relatorioController.lstServidorPorOrg(false,responsavel);
            parameters.put("SER_POR_ORG", serPorOrg);

            if (ParamSist.paramEquals(CodedValues.TPC_CATEGORIA_RELATORIO_GERENCIAL_GERAL_CORRESPONDE_CARGO_RSE, CodedValues.TPC_SIM, responsavel)) {
                final List<ServidorPorCrsBean> qtdeSerPorCrs = relatorioController.lstServidorPorCrs(LIMITE_SERVIDOR_POR_CRS, responsavel);
                parameters.put("QTDE_SER_POR_CRS", qtdeSerPorCrs);
                parameters.put("QTDE_CARGOS_SER", relatorioController.lstServidorPorCrs(0, responsavel).size());
            } else {
                final List<ServidorPorTipoBean> qtdeSerPorTipo = relatorioController.lstServidorPorTipo(LIMITE_TIPO_POR_SERVIDOR, responsavel);
                parameters.put("QTDE_SER_POR_TIPO", qtdeSerPorTipo);
                parameters.put("QTDE_CATEGORIAS_SER", relatorioController.lstServidorPorTipo(0, responsavel).size());
            }

        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage(), e);
        }
    }

    protected void geraInformacoesMargens(HashMap<String, Object> parameters) {
        parameters.put("MARGEM_1", MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM, responsavel));
        parameters.put("MARGEM_2", MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM_2, responsavel));
        parameters.put("MARGEM_3", MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM_3, responsavel));

        final boolean margem1Casada3 = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, responsavel);
        final boolean margem123Casadas = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS, responsavel);
        final boolean margem1Casada3Esquerda = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, responsavel);
        final boolean margem123CasadasEsquerda = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, responsavel);

        parameters.put("MARGEM_1_CASADA_3", margem1Casada3 || margem1Casada3Esquerda);
        parameters.put("MARGEM_123_CASADA", margem123Casadas || margem123CasadasEsquerda);

        MargemTO margemTO = null;
        ExibeMargem exibeMargem = null;

        try {
            final ParametroDelegate parDelegate = new ParametroDelegate();
            final List<TransferObject> incidencias = parDelegate.recuperaIncidenciasMargem(responsavel);
            for (final TransferObject incidencia : incidencias) {
                try {
                    final Short pseVlr = Short.valueOf(incidencia.getAttribute(Columns.PSE_VLR).toString());
                    margemTO = MargemHelper.getInstance().getMargem(pseVlr, responsavel);
                    exibeMargem = new ExibeMargem(margemTO, responsavel);

                    if (pseVlr.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                        parameters.put("INCIDE_MARGEM", true);
                        if (exibeMargem.isExibeValor()) {
                            parameters.put("EXIBE_MARGEM_1", true);
                        }
                    } else if (pseVlr.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                        parameters.put("INCIDE_MARGEM_2", true);
                        if (exibeMargem.isExibeValor()) {
                            parameters.put("EXIBE_MARGEM_2", true);
                        }
                    } else if (pseVlr.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                        parameters.put("INCIDE_MARGEM_3", true);
                        if (exibeMargem.isExibeValor()) {
                            parameters.put("EXIBE_MARGEM_3", true);
                        }
                    }
                } catch (final NumberFormatException e) {
                    LOG.debug("Não foi possível fazer o parser da incidência de margem.", e);
                } catch (final NullPointerException e) {
                    LOG.debug("Incidência de margem informada nula.", e);
                }
            }
        } catch (final ParametroControllerException e2) {
            LOG.error("Não foi possível recuperar as incidências de margem do sistema.", e2);
        }
    }

    protected void geraComprometimentoMargens(HashMap<String, Object> parameters) {
        final boolean margem1Casada3 = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, responsavel);
        final boolean margem123Casadas = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS, responsavel);
        final boolean margem1Casada3Esquerda = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, responsavel);
        final boolean margem123CasadasEsquerda = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, responsavel);

        final Map<Short, List<TransferObject>> svcsPorIncidencia = retornaServicosSeparadosPorIncidencia();
        if (svcsPorIncidencia.isEmpty()) {
            LOG.error("Não foi possível gerar o relatório de comprometimento de margem porque não existem serviços separados por incidência de margem.");
        } else {
            try {
                final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
                for (final Entry<Short, List<TransferObject>> entry : svcsPorIncidencia.entrySet()) {
                    //Critérios utilizados para a geração da SQL que irá recuperar os dados do relatório
                    final Short incidencia = entry.getKey();
                    // Lista de serviços que serão utilizados para gerar o relatorio separado por incidência de margem
                    final List<TransferObject> svcs = new ArrayList<>();
                    // TODO verificar se este tratamento pode ser feito em comum para este relatório e para o de comprometimento de margem.
                    // O trecho abaixo inclui na lista todos os serviços considenrando as incidencias de margem
                    // de acordo com o casamento de margem, caso exista.
                    if (margem1Casada3) {
                        if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM));
                            }
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                            }
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2));
                            }
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_3) && (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3) != null)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                        }
                    } else if (margem123Casadas) {
                        if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM));
                            }
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2));
                            }
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                            }
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2));
                            }
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                            }
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_3) && (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3) != null)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                        }
                    } else if (margem1Casada3Esquerda) {
                        if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM));
                            }
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                            }
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2));
                            }
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM));
                            }
                            if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3) != null) {
                                svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                            }
                        }
                    } else if (margem123CasadasEsquerda) {
                        if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM) != null) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM));
                        }
                        if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2) != null) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2));
                        }
                        if (svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3) != null) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                        }
                    } else {
                        svcs.addAll(svcsPorIncidencia.get(incidencia));
                    }

                    final TransferObject criterioCompMargem = new CustomTransferObject();
                    criterioCompMargem.setAttribute(Columns.CNV_SVC_CODIGO, svcs);
                    criterioCompMargem.setAttribute(CodedValues.TPS_INCIDE_MARGEM, incidencia);

                    final List<ComprometimentoBean> comprometimento = relatorioController.lstComprometimento(criterioCompMargem, responsavel);
                    parameters.put("COMPROMETIMENTO_MARGEM_" + incidencia, comprometimento);
                }
            } catch (final RelatorioControllerException e) {
                LOG.error("ERROR: " + e.getMessage());
            }
        }
    }

    /**
     * Busca todos os serviços ativos para que sejam setados para gerar o relatório.
     *
     */
    protected void setServicos() {
        servicos = new ArrayList<>();
        //Busca todos os serviços para serem usados para gerar o relatório
        try {
            final ConvenioDelegate convenioDelegate = new ConvenioDelegate();
            final TransferObject criterioSvc = new CustomTransferObject();
            criterioSvc.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_ATIVO);
            final List<TransferObject> svc = convenioDelegate.lstServicos(criterioSvc, AcessoSistema.getAcessoUsuarioSistema());
            final Iterator<TransferObject> ite = svc.iterator();
            while (ite.hasNext()) {
                servicos.add(ite.next());
            }
        } catch (final ConvenioControllerException ex) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage()), ex);
        }
    }

    /**
     * Retorna os serviços que serão utilizados para gerar o relatório separados pela incidência na margem.
     * Somente os serviços que incidirem nas margens 1, 2 e 3 serão retornados.
     *
     * @return Mapeamento de serviços que incidem na margem por incidência.
     */
    protected Map<Short, List<TransferObject>> retornaServicosSeparadosPorIncidencia() {
        final Map<Short, List<TransferObject>> retorno = new HashMap<>();

        try {
            final List<String> tpsCodigo = new ArrayList<>();
            tpsCodigo.add(CodedValues.TPS_INCIDE_MARGEM);

            final ParametroDelegate parametroDelegate = new ParametroDelegate();
            for (final TransferObject servico : servicos) {
                final String svcCodigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
                final ParamSvcTO paramTO = parametroDelegate.selectParamSvcCse(svcCodigo, tpsCodigo, AcessoSistema.getAcessoUsuarioSistema());

                final Short incideMargem = paramTO.getTpsIncideMargem();
                if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM) ||
                        incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2) ||
                        incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    //Somente os serviços que incidirem na margens 1, 2 e 3
                    List<TransferObject> svcs = retorno.get(incideMargem);
                    if (svcs == null) {
                        svcs = new ArrayList<>();
                        retorno.put(incideMargem, svcs);
                    }
                    svcs.add(servico);
                }
            }

        } catch (final ParametroControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(ex.getMessage(), ex);
        }

        return retorno;
    }

    protected void geraRankingTaxas(HashMap<String, Object> parameters){
        try{
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);

            final TransferObject to = relatorioController.buscaSvcTaxasQuery(false, responsavel);
            final String svcCodigo = (to != null) && !TextHelper.isNull(to.getAttribute(Columns.SVC_CODIGO)) ? to.getAttribute(Columns.SVC_CODIGO).toString() : null;
            final String svcDescricao = (to != null) && !TextHelper.isNull(to.getAttribute(Columns.SVC_DESCRICAO)) ? to.getAttribute(Columns.SVC_DESCRICAO).toString() : null;
            final TransferObject prazoMax = relatorioController.buscaMaxPrazo(responsavel);
            criterio.setAttribute("maxPrazo", Integer.valueOf((Short) prazoMax.getAttribute("prazoMax")));

            // Se não existe taxa ativa cadastrada, não gera relatório de taxas
            if (!TextHelper.isNull(svcCodigo)) {
                parameters.put("SVC_RANKING_TAXAS", svcDescricao);
                parameters.put("LISTA_RANKING_TAXAS", relatorioController.lstRakingTaxas(svcCodigo, criterio, responsavel));
            }
        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }

    protected void geraTaxasEfetivas(HashMap<String, Object> parameters){
        try{
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final TransferObject to = relatorioController.buscaSvcTaxasQuery(false, responsavel);
            final String svcCodigo = (to != null) && !TextHelper.isNull(to.getAttribute(Columns.SVC_CODIGO)) ? to.getAttribute(Columns.SVC_CODIGO).toString() : null;
            final String svcDescricao = (to != null) && !TextHelper.isNull(to.getAttribute(Columns.SVC_DESCRICAO)) ? to.getAttribute(Columns.SVC_DESCRICAO).toString() : null;

            if (!TextHelper.isNull(svcCodigo)) {
                // taxas praticadas apenas para os prazos abaixo
                final List<Integer> prazos = new ArrayList<>();
                prazos.add(12);
                prazos.add(24);
                prazos.add(36);
                prazos.add(48);
                prazos.add(60);
                prazos.add(72);
                prazos.add(84);
                prazos.add(96);

                final Calendar calendar = Calendar.getInstance();
                final Date currentDate = calendar.getTime();

                // Obtenho a lista dos últimos períodos exportados que já possuem retorno
                final PeriodoDelegate perDelegate = new PeriodoDelegate();
                final List<TransferObject> periodoExportacao = perDelegate.obtemPeriodoCalculoMargem(null, null, false, responsavel);

                // Seleciona da lista o período de menor data pois os demais períodos são contemplados nesta faixa de data
                // Isto trata o caso de se não ocorreu o processamento de algum orgão ou estabelecimento.
                final Iterator<TransferObject> it = periodoExportacao.iterator();
                TransferObject orgao = null;
                Date periodo = null;
                if ((periodoExportacao != null) && !periodoExportacao.isEmpty()) {
                    periodo = (Date) periodoExportacao.get(0).getAttribute(Columns.PEX_PERIODO);

                    while (it.hasNext()) {
                        orgao = it.next();
                        if (periodo.after((Date) orgao.getAttribute(Columns.PEX_PERIODO))) {
                            periodo = (Date) orgao.getAttribute(Columns.PEX_PERIODO);
                        }
                    }
                } else {
                    periodo = currentDate;
                }

                parameters.put("SVC_TAXAS_EFETIVAS", svcDescricao);
                parameters.put("LISTA_TAXAS_EFETIVAS", relatorioController.lstTaxasEfetivas(svcCodigo, periodo, prazos, responsavel));
            }

        } catch (RelatorioControllerException | PeriodoException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }

    private void geraQuantitativaOrgaoServidores(HashMap<String, Object> parameters) {
        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.ORG_ATIVO, CodedValues.STS_ATIVO);

            final List<ServidorPorOrgBean> serPorOrgAtivo = relatorioController.lstServidorPorOrg(true,responsavel);
            parameters.put("SER_POR_ORG_ATIVO", serPorOrgAtivo);

            final int qtdeOrgaosAtivos = consignanteController.countOrgaos(criterio, responsavel);
            parameters.put("QTDE_ORGAOS_ATIVOS", qtdeOrgaosAtivos);

        } catch (RelatorioControllerException | ConsignanteControllerException e) {
            LOG.error("ERROR: " + e.getMessage(), e);
        }
    }

    private void geraQuantitativaCsaContratos(HashMap<String, Object> parameters) {
        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final String periodo = parameters.get(ReportManager.PARAM_NAME_PERIODO) != null ? parameters.get(ReportManager.PARAM_NAME_PERIODO).toString() : null;
            final List<ContratosPorCsaBean> qtdeContratosPorCsa = relatorioController.lstContratosPorCsa(0, periodo, true, responsavel);

            parameters.put("LISTA_QTDE_CONTRATOS_POR_CSA_ATIVA", qtdeContratosPorCsa);
        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage(), e);
        }
    }

    private void geraListaSituacaoConsignataria(HashMap<String, Object> parameters) {
        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final List<ContratosPorCsaBean> consignatariasSituacao = relatorioController.lstConsignatariaSituacao(responsavel);

            parameters.put("LISTA_CONSIGNATARIA_SITUACAO", consignatariasSituacao);
        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage(), e);
        }
    }
}
