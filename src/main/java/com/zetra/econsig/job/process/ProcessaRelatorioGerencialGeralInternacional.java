package com.zetra.econsig.job.process;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.ContratosPorCategoriaBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCrsBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCsaBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorSvcBean;
import com.zetra.econsig.report.jasper.dto.CorPorCsaBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p> Title: ProcessaRelatorioGerencialGeralInternacional</p>
 * <p> Description: Processa o Relatório de Gerencial Geral Internacional</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioGerencialGeralInternacional extends ProcessaRelatorioGerencialGeral {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioGerencialGeralInternacional.class);

    private static final int LIMITE_CONTRATO_POR_CSA = 10;
    private static final int LIMITE_CORRESPONDENTE_POR_CSA = 10;
    private static final int LIMITE_CONTRATOS_POR_CRS = 10;
    private static final int LIMITE_CONTRATOS_POR_CATEGORIA = 10;
    private static final int LIMITE_CONTRATOS_POR_SVC = 10;

    public ProcessaRelatorioGerencialGeralInternacional(Relatorio relatorio, Map<String, String[]> parameterMap, AcessoSistema responsavel) throws AgendamentoControllerException{
        super(relatorio, parameterMap, responsavel);

        // Seta serviços
        setServicos();
    }

    @Override
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
            final List<ContratosPorCsaBean> qtdeContratosPorCsa = relatorioController.lstContratosPorCsa(0, periodo, false, responsavel);
            final List<CorPorCsaBean> qtdeCorrespondentesPorCsa = relatorioController.lstCorPorCsa(LIMITE_CORRESPONDENTE_POR_CSA, responsavel);

            final List<ContratosPorCsaBean> qtdeContratosPorCsaGrafico = relatorioController.lstContratosPorCsa(LIMITE_CONTRATO_POR_CSA, periodo, false, responsavel);
            final List<CorPorCsaBean> qtdeCorrespondentesPorCsaGrafico = relatorioController.lstCorPorCsa(LIMITE_CORRESPONDENTE_POR_CSA, responsavel);

            parameters.put("QTDE_CONTRATOS_POR_CSA", relatorioController.lstContratosPorCsa(0, periodo, false, responsavel).size());
            parameters.put("LISTA_QTDE_CONTRATOS_POR_CSA", qtdeContratosPorCsa);
            parameters.put("LISTA_QTDE_COR_POR_CSA", qtdeCorrespondentesPorCsa);
            parameters.put("LISTA_QTDE_CONTRATOS_POR_CSA_GRAFICO", qtdeContratosPorCsaGrafico);
            parameters.put("LISTA_QTDE_COR_POR_CSA_GRAFICO", qtdeCorrespondentesPorCsaGrafico);
        } catch (final RelatorioControllerException ex) {
            LOG.error("ERROR: " + ex.getMessage());
        }
    }

    @Override
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
            final List<ContratosPorSvcBean> qtdeContratosPorCategoria = relatorioController.lstContratosPorSvc(LIMITE_CONTRATOS_POR_SVC, periodo, true, responsavel);

            parameters.put("QTDE_CONTRATOS_POR_SVC", relatorioController.lstContratosPorSvc(0, periodo, true, responsavel).size());
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

    @Override
    protected void geraTaxasEfetivas(HashMap<String, Object> parameters){
        try{
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final TransferObject to = relatorioController.buscaSvcTaxasQuery(true, responsavel);
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
}
