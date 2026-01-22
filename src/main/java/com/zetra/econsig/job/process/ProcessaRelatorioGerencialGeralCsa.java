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
import com.zetra.econsig.exception.ReportControllerException;
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
import com.zetra.econsig.report.jasper.dto.ContratosPorCsaBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorSvcBean;
import com.zetra.econsig.report.jasper.dto.GerencialEstatiscoMargemBean;
import com.zetra.econsig.report.jasper.dto.GerencialInadimplenciaBean;
import com.zetra.econsig.report.jasper.dto.GerencialInadimplenciaCsaBean;
import com.zetra.econsig.report.jasper.dto.GerencialQtdeSerPorFaixaMargemBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorOrgBean;
import com.zetra.econsig.report.reports.HeadingsScriptlet;
import com.zetra.econsig.report.reports.ReportManager;
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
public class ProcessaRelatorioGerencialGeralCsa extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioGerencialGeralCsa.class);

    private static final int LIMITE_CONTRATO_POR_CSA = 10;
    private static final int LIMITE_CONTRATOS_POR_SVC = 10;

    private List<TransferObject> servicos;

    public ProcessaRelatorioGerencialGeralCsa(Relatorio relatorio, Map<String, String[]> parameterMap, AcessoSistema responsavel) throws AgendamentoControllerException {
        super(relatorio, parameterMap, null, true, responsavel);

        // Seta serviços
        setServicos();
    }

    @Override
    protected void executar() {
        StringBuilder titulo = new StringBuilder(ApplicationResourcesHelper.getMessage("rotulo.relatorio.gerencial.csa.titulo", responsavel));
        StringBuilder subtitulo = new StringBuilder("");
        String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;

        String strPeriodo = "";
        String paramPeriodo = "";

        // CONSTROI NOME DO ARQUIVO NO FORMATO: relatorio_dataHora
        StringBuilder nome = new StringBuilder(getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.gerencial.csa", responsavel), responsavel, parameterMap, null));

        //Pega parametro periodo
        if (parameterMap.containsKey("periodo") && !TextHelper.isNull(getParametro("periodo", parameterMap))) {
            strPeriodo = getParametro("periodo", parameterMap);
            paramPeriodo = formatarPeriodo(strPeriodo);
        }

        List<String> csaCodigo = getFiltroCsaCodigos(parameterMap, subtitulo, nome, session, responsavel);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO_ENTIDADE, getCaminhoLogoEntidade(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO_ECONSIG, getCaminhoLogoEConsig(responsavel));
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(true,null,responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
        parameters.put(ReportManager.PARAM_CSE_NOME, getCseNome(responsavel));
        parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
        parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
        parameters.put(ReportManager.REPORT_SCRIPTLET, new HeadingsScriptlet());
        parameters.put(ReportManager.PARAM_NAME_PERIODO, paramPeriodo);
        parameters.put(ReportManager.PARAM_NAME_CSA, csaCodigo);
        parameters.put(ReportManager.PARAM_NAME_MES_ANO, DateHelper.format(Calendar.getInstance().getTime(), "MMM/yyyy"));
        parameters.put(ReportManager.PARAM_NAME_ANO_ATUAL, DateHelper.format(Calendar.getInstance().getTime(), "yyyy"));
        parameters.put(ReportManager.PARAM_NAME_DATA_ATUAL, DateHelper.format(Calendar.getInstance().getTime(), LocaleHelper.getDatePattern()));
        parameters.put("RESPONSAVEL", responsavel);

        boolean quinzenal = !PeriodoHelper.folhaMensal(responsavel);
        parameters.put("QUINZENAL", quinzenal);

        geraInformacoesCse(parameters);
        geraInformacoesCsa(parameters);
        geraInformacoesMargens(parameters);
        geraEstatisticoServidores(parameters);

        // Uso os dados de MARGEM_USADA e MARGEM_TOTAL do Comprometimento de Margens no Estatistico de Margens
        geraComprometimentoMargens(parameters);
        geraEstatisticoMargens(parameters);

        geraInformacoesContratos(parameters);
        geraInformacoesInadimplencia(parameters);

        geraRankingTaxas(parameters);
        geraTaxasEfetivas(parameters);

        String reportName = null;
        try {
            ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
            String strFormato = getStrFormato();
            reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (ReportControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
        catch (Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }

    private void geraEstatisticoMargens(HashMap<String, Object> parameters) {
        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            List<GerencialEstatiscoMargemBean> estatisticoMargem = relatorioController.lstEstatiscoMargem(responsavel);
            Iterator<GerencialEstatiscoMargemBean> ite = estatisticoMargem.iterator();

            BigDecimal margemMedia1 = BigDecimal.ZERO;
            BigDecimal desvioMargem1 = BigDecimal.ZERO;
            BigDecimal margemMedia2 = BigDecimal.ZERO;
            BigDecimal desvioMargem2 = BigDecimal.ZERO;
            BigDecimal margemMedia3 = BigDecimal.ZERO;
            BigDecimal desvioMargem3 = BigDecimal.ZERO;
            while (ite.hasNext()) {
                GerencialEstatiscoMargemBean cto = ite.next();
                if (cto.getMargemRestMedia().compareTo(BigDecimal.ZERO) != 0) {
                    margemMedia1 = cto.getMargemRestMedia();
                }
                if (cto.getMargemRestDesvio().compareTo(BigDecimal.ZERO) != 0) {
                    desvioMargem1 = cto.getMargemRestDesvio();
                }
                if (cto.getMargemRestMedia2().compareTo(BigDecimal.ZERO) != 0) {
                    margemMedia2 = cto.getMargemRestMedia2();
                }
                if (cto.getMargemRestDesvio2().compareTo(BigDecimal.ZERO) != 0) {
                    desvioMargem2 = cto.getMargemRestDesvio2();
                }
                if (cto.getMargemRestMedia3().compareTo(BigDecimal.ZERO) != 0) {
                    margemMedia3 = cto.getMargemRestMedia3();
                }
                if (cto.getMargemRestDesvio3().compareTo(BigDecimal.ZERO) != 0) {
                    desvioMargem3 = cto.getMargemRestDesvio3();
                }
            }

            List<GerencialQtdeSerPorFaixaMargemBean> qtdeSerPorFaixaMargem1 = relatorioController.lstQtdeSerPorFaixaMargem(margemMedia1, desvioMargem1, CodedValues.INCIDE_MARGEM_SIM, responsavel);
            List<GerencialQtdeSerPorFaixaMargemBean> qtdeSerPorFaixaMargem2 = relatorioController.lstQtdeSerPorFaixaMargem(margemMedia2, desvioMargem2, CodedValues.INCIDE_MARGEM_SIM_2, responsavel);
            List<GerencialQtdeSerPorFaixaMargemBean> qtdeSerPorFaixaMargem3 = relatorioController.lstQtdeSerPorFaixaMargem(margemMedia3, desvioMargem3, CodedValues.INCIDE_MARGEM_SIM_3, responsavel);

            parameters.put("ESTATISTICO_MARGEM", estatisticoMargem);
            parameters.put("QTDE_SER_POR_FAIXA_MARGEM_1", qtdeSerPorFaixaMargem1);
            parameters.put("QTDE_SER_POR_FAIXA_MARGEM_2", qtdeSerPorFaixaMargem2);
            parameters.put("QTDE_SER_POR_FAIXA_MARGEM_3", qtdeSerPorFaixaMargem3);

        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }

    private void geraInformacoesCse(HashMap<String, Object> parameters) {
        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        List<TransferObject> orgaos = null;
        try {
            orgaos = cseDelegate.lstOrgaos(null, responsavel);
        } catch (ConsignanteControllerException e) {
            LOG.error("Não foi possível listar os órgãos.", e);
            orgaos = new ArrayList<>();
        }

        parameters.put("ORGAOS", orgaos);

        try {
            ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            List<ConsignanteTransferObject> consignante = new ArrayList<>();
            consignante.add(cse);
            parameters.put("CONSIGNANTE", consignante);
        } catch (ConsignanteControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        String linkAcesso = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel).toString() : "";
        String dataImplantacao = "";
        if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_DATA_IMPLANTACAO_SISTEMA, responsavel))) {
            try {
                dataImplantacao = DateHelper.reformat(ParamSist.getInstance().getParam(CodedValues.TPC_DATA_IMPLANTACAO_SISTEMA, responsavel).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
            } catch (ParseException e) {
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
        String versaoAtual = ApplicationResourcesHelper.getMessage("release.tag", responsavel);
        Boolean moduloCompra = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, responsavel);
        String emailSuporte = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel).toString() : "";
        String telefoneSuporte = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TELEFONE_SUPORTE_ZETRASOFT, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_TELEFONE_SUPORTE_ZETRASOFT, responsavel).toString() : "";

        parameters.put("LINK_ACESSO_SISTEMA", linkAcesso);
        parameters.put("DATA_IMPLANTACAO", dataImplantacao);
        parameters.put("DIA_CORTE", diaCorte);
        parameters.put("DIA_REPASSE", diaRepasse);
        parameters.put("VERSAO_ATUAL_SISTEMA", versaoAtual);
        parameters.put("MODULO_COMPRA", moduloCompra);
        parameters.put("EMAIL_SUPORTE", emailSuporte);
        parameters.put("TELEFONE_SUPORTE", telefoneSuporte);
    }

    private void geraInformacoesCsa(HashMap<String, Object> parameters) {
        try {
            ConsignatariaDelegate cseDelegate = new ConsignatariaDelegate();

            TransferObject criterioCsa = new CustomTransferObject();
            TransferObject criterioCor = new CustomTransferObject();

            criterioCsa.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);
            criterioCor.setAttribute(Columns.COR_ATIVO, CodedValues.STS_ATIVO);
            int qtdeCsaAtivas = cseDelegate.countConsignatarias(criterioCsa, responsavel);
            int qtdeCorAtivos = cseDelegate.countCorrespondentes(criterioCor, responsavel);

            List<Short> statusBloq = new ArrayList<>();
            statusBloq.add(CodedValues.STS_INATIVO);
            statusBloq.add(CodedValues.STS_INATIVO_CSE);
            statusBloq.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);

            criterioCsa.setAttribute(Columns.CSA_ATIVO, statusBloq);
            criterioCor.setAttribute(Columns.COR_ATIVO, statusBloq);
            int qtdeCsaInativas = cseDelegate.countConsignatarias(criterioCsa, responsavel);
            int qtdeCorInativos = cseDelegate.countCorrespondentes(criterioCor, responsavel);

            parameters.put("QTDE_CSA_TOTAL", qtdeCsaAtivas + qtdeCsaInativas);
            parameters.put("QTDE_CSA_ATIVOS_TOTAL", qtdeCsaAtivas);
            parameters.put("QTDE_CSA_INATIVOS_TOTAL", qtdeCsaInativas);
            parameters.put("QTDE_COR_TOTAL", qtdeCorAtivos + qtdeCorInativos);
            parameters.put("QTDE_COR_ATIVOS_TOTAL", qtdeCorAtivos);
            parameters.put("QTDE_COR_INATIVOS_TOTAL", qtdeCorInativos);
        } catch (ConsignatariaControllerException e) {
            LOG.error("Não foi possível recuperar a quantidade de consignatárias.", e);
        }

        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            String periodo = parameters.get(ReportManager.PARAM_NAME_PERIODO) != null ? parameters.get(ReportManager.PARAM_NAME_PERIODO).toString() : null;
            @SuppressWarnings("unchecked")
            List<String> csaCodigo = parameters.get(ReportManager.PARAM_NAME_CSA) != null ? (List<String>) parameters.get(ReportManager.PARAM_NAME_CSA) : null;
            List<ContratosPorCsaBean> qtdeContratosPorCsa = relatorioController.lstContratosPorCsa(LIMITE_CONTRATO_POR_CSA, periodo, csaCodigo, responsavel);

            parameters.put("QTDE_CONTRATOS_POR_CSA", relatorioController.lstContratosPorCsa(0, periodo, csaCodigo, responsavel).size());
            parameters.put("LISTA_QTDE_CONTRATOS_POR_CSA", qtdeContratosPorCsa);
        } catch (RelatorioControllerException ex) {
            LOG.error("ERROR: " + ex.getMessage());
        }

    }

    private void geraInformacoesContratos(HashMap<String, Object> parameters) {
        String periodo = parameters.get(ReportManager.PARAM_NAME_PERIODO) != null ? parameters.get(ReportManager.PARAM_NAME_PERIODO).toString() : null;
        @SuppressWarnings("unchecked")
        List<String> csaCodigo = parameters.get(ReportManager.PARAM_NAME_CSA) != null ? (List<String>) parameters.get(ReportManager.PARAM_NAME_CSA) : null;

        try {
            ConvenioDelegate cnvDelegate = new ConvenioDelegate();
            parameters.put("QTDE_SVC_TOTAL", cnvDelegate.lstServicos(null, responsavel).size());

            TransferObject criterioInfo = new CustomTransferObject();
            criterioInfo.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_ATIVO);
            parameters.put("QTDE_SVC_ATIVOS_TOTAL", cnvDelegate.lstServicos(criterioInfo, responsavel).size());

            criterioInfo.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_INATIVO);
            parameters.put("QTDE_SVC_INATIVOS_TOTAL", cnvDelegate.lstServicos(criterioInfo, responsavel).size());

        } catch (ConvenioControllerException e) {
            LOG.error("Não foi possível recuperar a quantidade de serviços.", e);
        }

        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);

            int qtdeContratosSerComCsa = relatorioController.qtdeContratosPorCategoria(null, csaCodigo, responsavel);
            int qtdeContratosSerComCsaPeriodo = relatorioController.qtdeContratosPorCategoria(periodo, csaCodigo, responsavel);
            int qtdeTotalContratosSer = relatorioController.qtdeContratosPorCategoria(null, null, responsavel);

            double percentualContratosSerComCsa = 0;
            if (qtdeTotalContratosSer > 0) {
                percentualContratosSerComCsa = (qtdeContratosSerComCsa * 100d)/qtdeTotalContratosSer;
            }

            parameters.put("QTDE_CONTRATOS_SER_ATIVOS", qtdeContratosSerComCsa);
            parameters.put("QTDE_CONTRATOS_ATIVOS", qtdeContratosSerComCsaPeriodo);
            parameters.put("QTDE_TOTAL_CONTRATOS_ATIVOS", qtdeTotalContratosSer);
            parameters.put("PERC_CONTRATOS_SER_ATIVOS", percentualContratosSerComCsa);

        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            List<ContratosPorSvcBean> qtdeContratosPorCategoria = relatorioController.lstContratosPorSvcRelGerencialCsa(LIMITE_CONTRATOS_POR_SVC, periodo, responsavel);

            parameters.put("QTDE_CONTRATOS_POR_SVC", relatorioController.lstContratosPorSvcRelGerencialCsa(0, periodo, responsavel).size());
            parameters.put("LISTA_QTDE_CONTRATOS_POR_SVC", qtdeContratosPorCategoria);

        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            parameters.put("TOTAL_PRESTACAO_EMPRESTIMO", relatorioController.getTotalPrestacaoEmprestimo(responsavel));
            parameters.put("SALDO_DEVEDOR_EMPRESTIMO", relatorioController.getSaldoDevedorEmprestimo(responsavel));
        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }

    private void geraInformacoesInadimplencia(HashMap<String, Object> parameters) {
        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            Calendar calendar = Calendar.getInstance();
            calendar.getTime();

            String periodo = parameters.get(ReportManager.PARAM_NAME_PERIODO) != null ? parameters.get(ReportManager.PARAM_NAME_PERIODO).toString() : null;

            // Obtenho a lista dos últimos períodos exportados que já possuem retorno
            List<TransferObject> lstHistoricoRetorno = new ImpRetornoDelegate().lstHistoricoConclusaoRetorno(null, 12, periodo, responsavel);

            // Seleciona da lista o maior período porque este é o último período que possui retorno
            List<Date> lstPeriodos = new ArrayList<>();
            Date periodoAtual = null;
            if (lstHistoricoRetorno != null && !lstHistoricoRetorno.isEmpty()) {
                Iterator<TransferObject> it = lstHistoricoRetorno.iterator();
                TransferObject historico = null;

                while (it.hasNext()) {
                    historico = it.next();
                    Date periodoTemp = (Date) historico.getAttribute(Columns.HCR_PERIODO);
                    lstPeriodos.add(periodoTemp);
                    if (periodoAtual == null || periodoTemp.after(periodoAtual)) {
                        periodoAtual = periodoTemp;
                    }
                }

                // Recupera relatório de inadimplência
                @SuppressWarnings("unchecked")
                List<String> csaCodigo = parameters.get(ReportManager.PARAM_NAME_CSA) != null ? (List<String>) parameters.get(ReportManager.PARAM_NAME_CSA) : null;
                GerencialInadimplenciaBean bean = relatorioController.geraRelatorioInadimplenciaConsignataria(periodoAtual, csaCodigo, responsavel);

                parameters.put("COUNT_TOTAL_CARTEIRA", bean.getQtdeTotalCarteira());
                parameters.put("COUNT_INADIMPLENCIA_TOTAL",  bean.getQtdeInadimplenciaTotal());
                parameters.put("COUNT_TOTAL_CARTEIRA_EMPRESTIMO", bean.getQtdeTotalCarteiraEmprestimo());
                parameters.put("COUNT_INADIMPLENCIA_EMPRESTIMO", bean.getQtdeInadimplenciaEmprestimo());
                if (bean.getQtdeInadimplenciaCsa().isEmpty()) {
                    GerencialInadimplenciaCsaBean gerencialInadimplenciaCsaBean = new GerencialInadimplenciaCsaBean();
                    gerencialInadimplenciaCsaBean.setCsaNome("");
                    gerencialInadimplenciaCsaBean.setCsaQtdeInadimplencia(Long.valueOf(0));
                    bean.getQtdeInadimplenciaCsa().add(gerencialInadimplenciaCsaBean);
                }
                parameters.put("CSA_INADIMPLENCIA", bean.getQtdeInadimplenciaCsa());
                parameters.put("PERIODO_INADIMPLENCIA", periodoAtual);

            }

        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        } catch (ImpRetornoControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

    }

    private void geraEstatisticoServidores(HashMap<String, Object> parameters) {
        try {
            ServidorDelegate serDelegate = new ServidorDelegate();
            parameters.put("QTDE_TOTAL_SER", serDelegate.countRegistroServidor(null, null, null, responsavel));

            List<String> srsCodigo = new ArrayList<>();
            srsCodigo.add(CodedValues.SRS_ATIVO);
            parameters.put("QTDE_SER_ATIVOS", serDelegate.countRegistroServidor(srsCodigo, null, null, responsavel));

            srsCodigo.clear();
            srsCodigo.addAll(CodedValues.SRS_INATIVOS);
            parameters.put("QTDE_SER_EXCLUIDOS", serDelegate.countRegistroServidor(srsCodigo, null, null, responsavel));

        } catch (ServidorControllerException e1) {
            LOG.error("Não foi possível localizar as informações dos servidores.");
        }

        try {
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            List<ServidorPorOrgBean> serPorOrg = relatorioController.lstServidorPorOrg(false,responsavel);

            parameters.put("SER_POR_ORG", serPorOrg);
            parameters.put("QTDE_CATEGORIAS_SER", relatorioController.lstServidorPorTipo(0, responsavel).size());

        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }

    private void geraInformacoesMargens(HashMap<String, Object> parameters) {
        parameters.put("MARGEM_1", MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM, responsavel));
        parameters.put("MARGEM_2", MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM_2, responsavel));
        parameters.put("MARGEM_3", MargemHelper.getInstance().getMarDescricao(CodedValues.INCIDE_MARGEM_SIM_3, responsavel));

        boolean margem1Casada3 = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, responsavel);
        boolean margem123Casadas = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS, responsavel);
        boolean margem1Casada3Esquerda = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, responsavel);
        boolean margem123CasadasEsquerda = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, responsavel);

        parameters.put("MARGEM_1_CASADA_3", margem1Casada3 || margem1Casada3Esquerda);
        parameters.put("MARGEM_123_CASADA", margem123Casadas || margem123CasadasEsquerda);

        MargemTO margemTO = null;
        ExibeMargem exibeMargem = null;

        try {
            ParametroDelegate parDelegate = new ParametroDelegate();
            List<TransferObject> incidencias = parDelegate.recuperaIncidenciasMargem(responsavel);
            Iterator<TransferObject> ite = incidencias.iterator();
            while (ite.hasNext()) {
                TransferObject incidencia = ite.next();
                try {
                    Short pseVlr = Short.valueOf(incidencia.getAttribute(Columns.PSE_VLR).toString());
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
                } catch (NumberFormatException e) {
                    LOG.debug("Não foi possível fazer o parser da incidência de margem.", e);
                } catch (NullPointerException e) {
                    LOG.debug("Incidência de margem informada nula.", e);
                }
            }
        } catch (ParametroControllerException e2) {
            LOG.error("Não foi possível recuperar as incidências de margem do sistema.", e2);
        }
    }

    private void geraComprometimentoMargens(HashMap<String, Object> parameters) {
        boolean margem1Casada3 = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, responsavel);
        boolean margem123Casadas = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS, responsavel);
        boolean margem1Casada3Esquerda = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, responsavel);
        boolean margem123CasadasEsquerda = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, responsavel);

        Map<Short, List<TransferObject>> svcsPorIncidencia = retornaServicosSeparadosPorIncidencia();
        if (svcsPorIncidencia.isEmpty()) {
            LOG.error("Não foi possível gerar o relatório de comprometimento de margem porque não existem serviços separados por incidência de margem.");
        } else {
            try {
                RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
                Iterator<Map.Entry<Short, List<TransferObject>>> iterator = svcsPorIncidencia.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Short, List<TransferObject>> entry = iterator.next();
                    //Critérios utilizados para a geração da SQL que irá recuperar os dados do relatório
                    Short incidencia = entry.getKey();
                    // Lista de serviços que serão utilizados para gerar o relatorio separado por incidência de margem
                    List<TransferObject> svcs = new ArrayList<>();
                    // TODO verificar se este tratamento pode ser feito em comum para este relatório e para o de comprometimento de margem.
                    // O trecho abaixo inclui na lista todos os serviços considenrando as incidencias de margem
                    // de acordo com o casamento de margem, caso exista.
                    if (margem1Casada3) {
                        if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM));
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2));
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                        }
                    } else if (margem123Casadas) {
                        if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM));
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2));
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2));
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                        }
                    } else if (margem1Casada3Esquerda) {
                        if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM));
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2));
                        } else if (incidencia.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM));
                            svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                        }
                    } else if (margem123CasadasEsquerda) {
                        svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM));
                        svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_2));
                        svcs.addAll(svcsPorIncidencia.get(CodedValues.INCIDE_MARGEM_SIM_3));
                    } else {
                        svcs.addAll(svcsPorIncidencia.get(incidencia));
                    }

                    TransferObject criterioCompMargem = new CustomTransferObject();
                    criterioCompMargem.setAttribute(Columns.CNV_SVC_CODIGO, svcs);
                    criterioCompMargem.setAttribute(CodedValues.TPS_INCIDE_MARGEM, incidencia);

                    List<ComprometimentoBean> comprometimento = relatorioController.lstComprometimento(criterioCompMargem, responsavel);
                    parameters.put("COMPROMETIMENTO_MARGEM_" + incidencia, comprometimento);
                }
            } catch (RelatorioControllerException e) {
                LOG.error("ERROR: " + e.getMessage());
            }
        }
    }

    /**
     * Busca todos os serviços ativos para que sejam setados para gerar o relatório.
     *
     */
    private void setServicos() {
        servicos = new ArrayList<>();
        //Busca todos os serviços para serem usados para gerar o relatório
        try {
            ConvenioDelegate convenioDelegate = new ConvenioDelegate();
            TransferObject criterioSvc = new CustomTransferObject();
            criterioSvc.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_ATIVO);
            List<TransferObject> svc = convenioDelegate.lstServicos(criterioSvc, AcessoSistema.getAcessoUsuarioSistema());
            Iterator<TransferObject> ite = svc.iterator();
            while (ite.hasNext()) {
                servicos.add(ite.next());
            }
        } catch (ConvenioControllerException ex) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage()), ex);
        }
    }

    /**
     * Retorna os serviços que serão utilizados para gerar o relatório separados pela incidência na margem.
     * Somente os serviços que incidirem nas margens 1, 2 e 3 serão retornados.
     *
     * @return Mapeamento de serviços que incidem na margem por incidência.
     */
    private Map<Short, List<TransferObject>> retornaServicosSeparadosPorIncidencia() {
        Map<Short, List<TransferObject>> retorno = new HashMap<>();

        try {
            List<String> tpsCodigo = new ArrayList<>();
            tpsCodigo.add(CodedValues.TPS_INCIDE_MARGEM);

            ParametroDelegate parametroDelegate = new ParametroDelegate();
            Iterator<TransferObject> ite = servicos.iterator();

            while (ite.hasNext()) {
                TransferObject servico = ite.next();
                String svcCodigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
                ParamSvcTO paramTO = parametroDelegate.selectParamSvcCse(svcCodigo, tpsCodigo, AcessoSistema.getAcessoUsuarioSistema());

                Short incideMargem = paramTO.getTpsIncideMargem();
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

        } catch (ParametroControllerException ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(ex.getMessage(), ex);
        }

        return retorno;
    }

    private void geraRankingTaxas(HashMap<String, Object> parameters){
        try{
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);

            TransferObject to = relatorioController.buscaSvcTaxasQuery(Boolean.FALSE, responsavel);
            String svcCodigo = to != null && !TextHelper.isNull(to.getAttribute(Columns.SVC_CODIGO)) ? to.getAttribute(Columns.SVC_CODIGO).toString() : null;
            String svcDescricao = to != null && !TextHelper.isNull(to.getAttribute(Columns.SVC_DESCRICAO)) ? to.getAttribute(Columns.SVC_DESCRICAO).toString() : null;
            TransferObject prazoMax = relatorioController.buscaMaxPrazo(responsavel);
            criterio.setAttribute("maxPrazo", Integer.valueOf((Short) prazoMax.getAttribute("prazoMax")));
            // Se não existe taxa ativa cadastrada, não gera relatório de taxas
            if (!TextHelper.isNull(svcCodigo)) {
                parameters.put("SVC_RANKING_TAXAS", svcDescricao);
                parameters.put("LISTA_RANKING_TAXAS", relatorioController.lstRakingTaxas(svcCodigo, criterio,responsavel));

            }
        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }

    private void geraTaxasEfetivas(HashMap<String, Object> parameters){
        try{
            RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            TransferObject to = relatorioController.buscaSvcTaxasQuery(Boolean.FALSE, responsavel);
            String svcCodigo = to != null && !TextHelper.isNull(to.getAttribute(Columns.SVC_CODIGO)) ? to.getAttribute(Columns.SVC_CODIGO).toString() : null;
            String svcDescricao = to != null && !TextHelper.isNull(to.getAttribute(Columns.SVC_DESCRICAO)) ? to.getAttribute(Columns.SVC_DESCRICAO).toString() : null;

            if (!TextHelper.isNull(svcCodigo)) {
                // taxas praticadas apenas para os prazos abaixo
                List<Integer> prazos = new ArrayList<>();
                prazos.add(12);
                prazos.add(24);
                prazos.add(36);
                prazos.add(48);
                prazos.add(60);
                prazos.add(72);

                Calendar calendar = Calendar.getInstance();
                Date currentDate = calendar.getTime();

                // Obtenho a lista dos últimos períodos exportados que já possuem retorno
                PeriodoDelegate perDelegate = new PeriodoDelegate();
                List<TransferObject> periodoExportacao = perDelegate.obtemPeriodoCalculoMargem(null, null, false, responsavel);

                // Seleciona da lista o período de menor data pois os demais períodos são contemplados nesta faixa de data
                // Isto trata o caso de se não ocorreu o processamento de algum orgão ou estabelecimento.
                Iterator<TransferObject> it = periodoExportacao.iterator();
                TransferObject orgao = null;
                Date periodo = null;
                if (periodoExportacao != null && !periodoExportacao.isEmpty()) {
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

        } catch (RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        } catch (PeriodoException e) {
            LOG.error("ERROR: " + e.getMessage());
        }
    }
}
