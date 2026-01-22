package com.zetra.econsig.service.parcela;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.assembler.ParcelaDescontoDtoAssembler;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.AutorizacaoDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.HistoricoRetMovFinDAO;
import com.zetra.econsig.persistence.dao.ParcelaDescontoDAO;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaParcela;
import com.zetra.econsig.persistence.entity.OcorrenciaParcelaHome;
import com.zetra.econsig.persistence.entity.OcorrenciaParcelaPeriodo;
import com.zetra.econsig.persistence.entity.OcorrenciaParcelaPeriodoHome;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.ParcelaDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDescontoHome;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.StatusParcelaDesconto;
import com.zetra.econsig.persistence.entity.StatusParcelaDescontoHome;
import com.zetra.econsig.persistence.query.historico.HistoricoConsultaParcelaQuery;
import com.zetra.econsig.persistence.query.historico.HistoricoOcorrenciaParcelaQuery;
import com.zetra.econsig.persistence.query.parcela.ListaAdesPorPeriodoParcelasQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasIntegracaoQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasLiquidarParcialQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasPeriodoQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasReimplantadasManualQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasReimplanteManualQuery;
import com.zetra.econsig.persistence.query.parcela.ListaResumoParcelasPeriodoQuery;
import com.zetra.econsig.persistence.query.parcela.ListaStatusParcelaQuery;
import com.zetra.econsig.persistence.query.parcela.ListarParcelasPorCsaQuery;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.ReimplantarConsignacaoController;
import com.zetra.econsig.service.folha.ImpRetornoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ParcelaControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ParcelaControllerBean implements ParcelaController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ParcelaControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ReimplantarConsignacaoController reimplantarConsignacaoController;

    @Autowired
    private ImpRetornoController impRetornoController;

    @Autowired
    private ParametroController parametroController;

    @Override
    public ParcelaDescontoTO findParcelaByAdeCodigoPrdCodigo(String adeCodigo, Integer prdCodigo, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final ParcelaDescontoPeriodo pdp = ParcelaDescontoPeriodoHome.findByPrimaryKeyAndAutDesconto(prdCodigo, adeCodigo);
            return ParcelaDescontoDtoAssembler.createDto(pdp);
        } catch (final FindException ex) {
            try {
                final ParcelaDesconto prd = ParcelaDescontoHome.findByPrimaryKeyAndAutDesconto(prdCodigo, adeCodigo);
                return ParcelaDescontoDtoAssembler.createDto(prd);
            } catch (final FindException ex2) {
                LOG.error(ex2.getMessage(), ex2);
                throw new ParcelaControllerException(ex2);
            }
        }
    }

    @Override
    public void liquidarTodasParcelas(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ParcelaControllerException {
        integrarParcela(CodedValues.SPD_LIQUIDADAFOLHA, tipoEntidade, codigoEntidade, responsavel);
    }

    @Override
    public void rejeitarTodasParcelas(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ParcelaControllerException {
        integrarParcela(CodedValues.SPD_REJEITADAFOLHA, tipoEntidade, codigoEntidade, responsavel);
    }

    /**
     * Integra todas as parcelas em processamento definindo um novo status para o ajuste.
     * Como trabalha sobre as parcelas em processamento, somente a tabela do periodo é verificada.
     * @param spdCodigo
     * @param tipoEntidade
     * @param codigoEntidade
     * @param responsavel
     * @throws ParcelaControllerException
     */
    @Override
    public void integrarParcela(String spdCodigo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            List<String> orgCodigos = null;
            List<String> estCodigos = null;
            if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                orgCodigos = new ArrayList<>();
                orgCodigos.add(codigoEntidade);
            }
            if ("EST".equalsIgnoreCase(tipoEntidade)) {
                estCodigos = new ArrayList<>();
                estCodigos.add(codigoEntidade);
            }

            final ParcelaDescontoDAO prdDAO = DAOFactory.getDAOFactory().getParcelaDescontoDAO();
            final java.sql.Date hoje = new java.sql.Date(Calendar.getInstance().getTimeInMillis());

            // Cria a ocorrência para a integração manual
            prdDAO.criaOcorrenciaRetorno(CodedValues.TOC_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno.manual", responsavel), tipoEntidade, codigoEntidade, (responsavel != null ? responsavel.getUsuCodigo() : null));

            // Integra as parcelas que estão em processamento
            prdDAO.liquidaParcelas(hoje.toString(), spdCodigo, tipoEntidade, codigoEntidade);

            final LogDelegate log = new LogDelegate(responsavel, Log.PARCELA, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setStatusParcela(spdCodigo);
            log.write();

            // Atualizar aut_desconto (sad_codigo, pagas++)
            // Faz pagas++ e sad_codigo = 'Em Andamento' apenas se a parcela foi Liquidada
            if (CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) || CodedValues.SPD_LIQUIDADAMANUAL.equals(spdCodigo)) {
                final String periodoRetorno = impRetornoController.recuperaPeriodoRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, orgCodigos, estCodigos, responsavel);

                final AutorizacaoDAO adeDAO = DAOFactory.getDAOFactory().getAutorizacaoDAO();
                adeDAO.atualizaAdeExportadas(orgCodigos, estCodigos, null, false, responsavel);

                final HistoricoRetMovFinDAO hrmDAO = DAOFactory.getDAOFactory().getHistoricoRetMovFinDAO();
                hrmDAO.iniciarHistoricoConclusaoRetorno(orgCodigos, estCodigos, periodoRetorno, "");
            }
        } catch (DAOException | ImpRetornoControllerException | LogControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void integrarParcela(String adeCodigo, Short prdNumero, BigDecimal prdVlrRealizado, Date prdDataDesconto, String spdCodigo, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException {
        // Busca parcelas por ADE_CODIGO, PRD_NUMERO, e PRD_DATA_DESCONTO caso informado
        // para recuperar o PRD_CODIGO necessário no método principal
        Integer codigo;
        Date periodo;
        try {
            final List<ParcelaDesconto> prds = ParcelaDescontoHome.findByAutDescontoPrdNumeroPrdDataDesconto(adeCodigo, prdNumero, prdDataDesconto);
            if ((prds != null) && !prds.isEmpty()) {
                if (prds.size() > 1) {
                    if ((prdNumero == null) || (prdDataDesconto == null)) {
                        // Refinar busca
                        throw new ParcelaControllerException("mensagem.erro.parcelas.selecionadas.integracao.mais.de.uma.encontrada", responsavel);
                    } else {
                        // Pega a última
                        codigo = prds.get(prds.size() - 1).getPrdCodigo();
                        periodo = prds.get(prds.size() - 1).getPrdDataDesconto();
                    }
                } else {
                    // Pega a primeira
                    codigo = prds.get(0).getPrdCodigo();
                    periodo = prds.get(0).getPrdDataDesconto();
                }
            } else {
                final List<ParcelaDescontoPeriodo> pdps = ParcelaDescontoPeriodoHome.findByAutDescontoPrdNumeroPrdDataDesconto(adeCodigo, prdNumero, prdDataDesconto);
                if ((pdps != null) && !pdps.isEmpty()) {
                    if (pdps.size() > 1) {
                        if ((prdNumero == null) || (prdDataDesconto == null)) {
                            // Refinar busca
                            throw new ParcelaControllerException("mensagem.erro.parcelas.selecionadas.integracao.mais.de.uma.encontrada", responsavel);
                        } else {
                            // Pega a última
                            codigo = pdps.get(pdps.size() - 1).getPrdCodigo();
                            periodo = pdps.get(pdps.size() - 1).getPrdDataDesconto();
                        }
                    } else {
                        // Pega a primeira
                        codigo = pdps.get(0).getPrdCodigo();
                        periodo = pdps.get(0).getPrdDataDesconto();
                    }
                } else if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_FUTURA, CodedValues.TPC_SIM, responsavel) && prdDataDesconto != null) {
                    // Se permite liquidar parcela futura e não encontrou a parcela, então assume que o período é futuro
                    codigo = 0;
                    periodo = prdDataDesconto;

                } else {
                    // Parcela não encontrada
                    throw new ParcelaControllerException("mensagem.erro.parcelas.selecionadas.integracao.nao.encontradas", responsavel);
                }
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        final String chaveMapa = codigo + ";" + DateHelper.toPeriodString(periodo);
        final Map<String, BigDecimal> vlrRealizadoPorParcela = new HashMap<>();
        vlrRealizadoPorParcela.put(chaveMapa, prdVlrRealizado);
        integrarParcela(adeCodigo, vlrRealizadoPorParcela, spdCodigo, ocpMotivo, responsavel);
    }

    @Override
    public void integrarParcela(String adeCodigo, Integer prdCodigo, BigDecimal prdVlrRealizado, String spdCodigo, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException {
        final ParcelaDescontoTO parcela = findParcelaByAdeCodigoPrdCodigo(adeCodigo, prdCodigo, responsavel);
        final String chaveMapa = prdCodigo + ";" + DateHelper.toPeriodString(parcela.getPrdDataDesconto());
        final Map<String, BigDecimal> vlrRealizadoPorParcela = new HashMap<>();
        vlrRealizadoPorParcela.put(chaveMapa, prdVlrRealizado);
        integrarParcela(adeCodigo, vlrRealizadoPorParcela, spdCodigo, ocpMotivo, responsavel);
    }

    /**
     * Integra as parcelas informada por parâmetro, ajustando o valor realizado e o status para
     * os valores informados. Se a parcela estiver na tabela histórica (tb_parcela_desconto) a
     * mesma é alterada e uma ocorrência é criada. Se a parcela estiver na tabela do período
     * (tb_parcela_desconto_periodo), a mesma é movida para a tabela histórica e alterada.
     * @param adeCodigo
     * @param vlrRealizadoPorParcela
     * @param spdCodigo
     * @param ocpMotivo
     * @param responsavel
     * @throws ParcelaControllerException
     */
    @Override
    public void integrarParcela(String adeCodigo, Map<String, BigDecimal> vlrRealizadoPorParcela, String spdCodigo, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            // Verificar se o usuário pode modificar esta ADE
            if (autorizacaoController.usuarioPodeModificarAde(adeCodigo, responsavel)) {
                // Define a descrição da ocorrência e a data de realização da parcela
                ocpMotivo = TextHelper.isNull(ocpMotivo) ? ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno.manual", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informacao.retorno.arg0", responsavel, ocpMotivo);
                final Date dataRealizado = DateHelper.getSystemDatetime();

                // Busca a consignação pela chave primária
                AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                final String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo();

                // Consignação suspensa não pode ter liquidação manual de parcela
                if (CodedValues.SAD_SUSPENSA.equals(sadCodigo) || CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo)) {
                    throw new ParcelaControllerException("mensagem.erro.situacao.contrato.nao.permite.liquidacao.manual.parcela", responsavel);
                }

                List<String> chavesParcelas = new ArrayList<>(vlrRealizadoPorParcela.keySet());

                // Ordena as parcelas pelo período de desconto
                if (chavesParcelas.size() > 1) {
                    Collections.sort(chavesParcelas, (o1, o2) -> {
                        try {
                            final String[] chaves1 = o1.split(";");
                            final Date prdDataDesconto1 = (chaves1.length > 1 ? DateHelper.parsePeriodString(chaves1[1]) : null);
                            final String[] chaves2 = o2.split(";");
                            final Date prdDataDesconto2 = (chaves2.length > 1 ? DateHelper.parsePeriodString(chaves2[1]) : null);
                            return (prdDataDesconto1 != null) && (prdDataDesconto2 != null) ? prdDataDesconto1.compareTo(prdDataDesconto2) : 0;
                        } catch (final ParseException ex) {
                            LOG.error(ex.getMessage(), ex);
                            return 0;
                        }
                    });
                }

                boolean permiteLiquidarParcelaFutura = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_FUTURA, CodedValues.TPC_SIM, responsavel);
                boolean permiteLiquidarParcelaPgtoParcial = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_PGTO_PARCIAL, CodedValues.TPC_SIM, responsavel);
                boolean colocarEmCarenciaLiqUltParcela = ParamSist.paramEquals(CodedValues.TPC_COLOCAR_EM_CARENCIA_LIQUIDACAO_MANUAL_ULT_PARCELA, CodedValues.TPC_SIM, responsavel) && consignacaoAptaCarenciaConclusao(adeCodigo, responsavel);
                boolean desfezIntegracaoParcela = false;

                // DESENV-14403 - Caso o sistema permita liquidação parcial, é necessário desfazer a integração para que o sistema não faça o incremento de parcelas pagas
                if (permiteLiquidarParcelaPgtoParcial && (spdCodigo.equals(CodedValues.SPD_LIQUIDADAFOLHA) || spdCodigo.equals(CodedValues.SPD_LIQUIDADAMANUAL))) {
                    // Cria uma cópia da lista pois não é permitido iterar sobre a lista e modificá-la ao mesmo tempo
                    List<String> novasChavesParcelas = new ArrayList<>();
                    novasChavesParcelas.addAll(chavesParcelas);

                    for (String chave : chavesParcelas) {
                        String[] chaves = chave.split(";");
                        Integer prdCodigo = (chaves.length > 0 ? Integer.valueOf(chaves[0]) : null);
                        final List<ParcelaDescontoTO> parcelasLiquidarParciais = findParcelasLiquidarParcial(adeCodigo, false, null, prdCodigo, null, responsavel);
                        if ((parcelasLiquidarParciais != null) && !parcelasLiquidarParciais.isEmpty()) {
                            prdCodigo = desfazIntegracao(adeCodigo, prdCodigo, CodedValues.TOC_DESFEITO, ocpMotivo, responsavel);
                            if (!desfezIntegracaoParcela) {
                                desfezIntegracaoParcela = true;
                            }
                            if (chaves.length > 0) {
                                // Atualiza a chave na lista de chaves de parcelas para liquidação
                                chaves[0] = prdCodigo.toString();
                                String novaChave = TextHelper.join(chaves, ";");
                                novasChavesParcelas.remove(chave);
                                novasChavesParcelas.add(novaChave);
                            }
                        }
                    }

                    chavesParcelas = novasChavesParcelas;
                }

                // Ao desfazer a integração é feito o decremento da quantidade de parcelas pagas, porém o bean deste método não tem os valores corretos da quantidade pagas, é necessário
                // atualiza-lo para isso.
                if(desfezIntegracaoParcela) {
                    adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                }

                int pagas = adeBean.getAdePrdPagas() != null ? adeBean.getAdePrdPagas() : 0;
                int pagasTotal = adeBean.getAdePrdPagasTotal() != null ? adeBean.getAdePrdPagasTotal() : 0;
                int pagasAnteriorDtIni = 0;

                for (final String chave : chavesParcelas) {
                    final String[] chaves = chave.split(";");
                    final Integer prdCodigo = (chaves.length > 0 ? Integer.valueOf(chaves[0]) : null);
                    final Date prdDataDesconto = (chaves.length > 1 ? DateHelper.parsePeriodString(chaves[1]) : null);
                    final BigDecimal prdVlrRealizado = vlrRealizadoPorParcela.get(chave);
                    BigDecimal prdVlrRealizadoEfetivo = null;

                    ParcelaDesconto prdBean = null;

                    if ((prdCodigo == null) || (prdCodigo == 0)) {
                        if (permiteLiquidarParcelaFutura && !TextHelper.isNull(prdDataDesconto)) {
                            final RegistroServidor rseBean = RegistroServidorHome.findByPrimaryKey(adeBean.getRegistroServidor().getRseCodigo());
                            final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(rseBean.getOrgao().getOrgCodigo(), responsavel);

                            if (prdDataDesconto.compareTo(periodoAtual) < 0) {
                                // Período do contra informado para parcela é menor que o período atual
                                throw new ParcelaControllerException("mensagem.erro.data.desconto.maior.periodo.atual", responsavel);
                            } else if ((prdDataDesconto.compareTo(adeBean.getAdeAnoMesIni()) < 0) || (!TextHelper.isNull(adeBean.getAdeAnoMesFim()) && (prdDataDesconto.compareTo(adeBean.getAdeAnoMesFim()) > 0))) {
                                // Período informado para parcela é menor que a data início do contrato ou maior que a data fim do contrato
                                throw new ParcelaControllerException("mensagem.erro.data.desconto.entre.data.inicio.data.fim", responsavel);
                            }

                            // Se encontrou parcela para o período passado por parâmetro, altera esta parcela
                            final Collection<ParcelaDesconto> parcelasDoPeriodo = ParcelaDescontoHome.findByAutDescontoPeriodo(adeCodigo, prdDataDesconto);
                            if ((parcelasDoPeriodo != null) && (parcelasDoPeriodo.size() == 1)) {
                                prdBean = parcelasDoPeriodo.iterator().next();

                                // Altera o status, a data e o valor da integração
                                prdBean.setStatusParcelaDesconto(StatusParcelaDescontoHome.findByPrimaryKey(spdCodigo));
                                prdBean.setPrdDataRealizado(dataRealizado);

                                if (CodedValues.SPD_REJEITADAFOLHA.equals(spdCodigo)) {
                                    prdVlrRealizadoEfetivo = new BigDecimal("0.00");
                                } else if (prdVlrRealizado != null) {
                                    prdVlrRealizadoEfetivo = prdVlrRealizado;
                                } else {
                                    prdVlrRealizadoEfetivo = prdBean.getPrdVlrPrevisto();
                                }

                                prdBean.setPrdVlrRealizado(prdVlrRealizadoEfetivo);
                                AbstractEntityHome.update(prdBean);

                                // Cria a ocorrência para a integração manual
                                // A ocorrência de retorno parcial deve ser grava somente quando o tipo valor for fixo
                                final String tocCodigo = ((prdBean.getPrdVlrPrevisto().compareTo(prdBean.getPrdVlrRealizado()) > 0) && CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) && CodedValues.TIPO_VLR_FIXO.equals(adeBean.getAdeTipoVlr()) ? CodedValues.TOC_RETORNO_PARCIAL : CodedValues.TOC_RETORNO);
                                OcorrenciaParcelaHome.create(prdBean.getPrdCodigo(), tocCodigo, ocpMotivo, (responsavel != null ? responsavel.getUsuCodigo() : null));

                            } else {
                                final Integer maxPrdNumero = ParcelaDescontoHome.findMaxPrdNumero(adeCodigo);
                                final Integer maxPdpNumero = ParcelaDescontoPeriodoHome.findMaxPrdNumero(adeCodigo);
                                final Integer prdNumeroCalculado = Math.max(maxPrdNumero, maxPdpNumero) + 1;

                                final BigDecimal prdValorPrevisto = !TextHelper.isNull(adeBean.getAdeVlrFolha()) ? adeBean.getAdeVlrFolha() : adeBean.getAdeVlr();
                                final String tdeCodigo = null;

                                if (CodedValues.SPD_REJEITADAFOLHA.equals(spdCodigo)) {
                                    prdVlrRealizadoEfetivo = new BigDecimal("0.00");
                                } else if (prdVlrRealizado != null) {
                                    prdVlrRealizadoEfetivo = prdVlrRealizado;
                                } else {
                                    prdVlrRealizadoEfetivo = prdValorPrevisto;
                                }

                                // Se não encontrou a parcela e permite liquidar parcela futura, cria parcela na tabela de histórico
                                prdBean = ParcelaDescontoHome.create(adeCodigo, prdNumeroCalculado.shortValue(), tdeCodigo, spdCodigo, prdDataDesconto, dataRealizado, prdValorPrevisto, prdVlrRealizadoEfetivo);

                                // Cria a ocorrência para a integração manual
                                // A ocorrência de retorno parcial deve ser grava somente quando o tipo valor for fixo
                                final String tocCodigo = ((prdBean.getPrdVlrPrevisto().compareTo(prdBean.getPrdVlrRealizado()) > 0) && CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) && CodedValues.TIPO_VLR_FIXO.equals(adeBean.getAdeTipoVlr()) ? CodedValues.TOC_RETORNO_PARCIAL : CodedValues.TOC_RETORNO);
                                OcorrenciaParcelaHome.create(prdBean.getPrdCodigo(), tocCodigo, ocpMotivo, (responsavel != null ? responsavel.getUsuCodigo() : null));
                            }
                        } else {
                            throw new ParcelaControllerException("mensagem.erro.parcelas.selecionadas.integracao.nao.encontradas", responsavel);
                        }
                    } else {
                        try {
                            // Busca a parcela na tabela histórica, das parcelas já integradas, pois normalmente
                            // as parcelas integradas manualmente estarão nesta tabela pois são registros históricos
                            // OBS: busca pela PK e pelo ADE_CODIGO, pois como a PK é incremental, pode existir nas duas tabelas
                            prdBean = ParcelaDescontoHome.findByPrimaryKeyAndAutDesconto(prdCodigo, adeCodigo);

                            // Altera o status, a data e o valor da integração
                            prdBean.setStatusParcelaDesconto(StatusParcelaDescontoHome.findByPrimaryKey(spdCodigo));
                            prdBean.setPrdDataRealizado(dataRealizado);

                            if (CodedValues.SPD_REJEITADAFOLHA.equals(spdCodigo)) {
                                prdVlrRealizadoEfetivo = new BigDecimal("0.00");
                            } else if (prdVlrRealizado != null) {
                                prdVlrRealizadoEfetivo = prdVlrRealizado;
                            } else {
                                prdVlrRealizadoEfetivo = prdBean.getPrdVlrPrevisto();
                            }

                            prdBean.setPrdVlrRealizado(prdVlrRealizadoEfetivo);
                            AbstractEntityHome.update(prdBean);

                            // Cria a ocorrência para a integração manual
                            // A ocorrência de retorno parcial deve ser grava somente quando o tipo valor for fixo
                            final String tocCodigo = ((prdBean.getPrdVlrPrevisto().compareTo(prdBean.getPrdVlrRealizado()) > 0) && CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) && CodedValues.TIPO_VLR_FIXO.equals(adeBean.getAdeTipoVlr()) ? CodedValues.TOC_RETORNO_PARCIAL : CodedValues.TOC_RETORNO);
                            OcorrenciaParcelaHome.create(prdBean.getPrdCodigo(), tocCodigo, ocpMotivo, (responsavel != null ? responsavel.getUsuCodigo() : null));

                        } catch (final FindException ex) {
                            try {
                                // A parcela que está sendo integrada deve estar na tabela do periodo
                                // OBS: busca pela PK e pelo ADE_CODIGO, pois como a PK é incremental, pode existir nas duas tabelas
                                final ParcelaDescontoPeriodo pdpBean = ParcelaDescontoPeriodoHome.findByPrimaryKeyAndAutDesconto(prdCodigo, adeCodigo);

                                if (CodedValues.SPD_REJEITADAFOLHA.equals(spdCodigo)) {
                                    prdVlrRealizadoEfetivo = new BigDecimal("0.00");
                                } else if (prdVlrRealizado != null) {
                                    prdVlrRealizadoEfetivo = prdVlrRealizado;
                                } else {
                                    prdVlrRealizadoEfetivo = pdpBean.getPrdVlrPrevisto();
                                }

                                final String tdeCodigo = pdpBean.getTipoDesconto() != null ? pdpBean.getTipoDesconto().getTdeCodigo() : null;
                                // Se encontrou a parcela, então move a parcela para a tabela histórica já com o novo status
                                prdBean = ParcelaDescontoHome.create(adeCodigo, pdpBean.getPrdNumero(), tdeCodigo, spdCodigo, pdpBean.getPrdDataDesconto(), dataRealizado, pdpBean.getPrdVlrPrevisto(), prdVlrRealizadoEfetivo);

                                // Cria a ocorrência para a integração manual já na tabela histórica
                                // A ocorrência de retorno parcial deve ser grava somente quando o tipo valor for fixo
                                final String tocCodigo = ((prdBean.getPrdVlrPrevisto().compareTo(prdBean.getPrdVlrRealizado()) > 0) && CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) && CodedValues.TIPO_VLR_FIXO.equals(adeBean.getAdeTipoVlr()) ? CodedValues.TOC_RETORNO_PARCIAL : CodedValues.TOC_RETORNO);
                                OcorrenciaParcelaHome.create(prdBean.getPrdCodigo(), tocCodigo, ocpMotivo, (responsavel != null ? responsavel.getUsuCodigo() : null));

                                // Move eventuais ocorrências que possam existir para esta parcela na tabela de ocorrências do periodo
                                final List<OcorrenciaParcelaPeriodo> ocorrencias = OcorrenciaParcelaPeriodoHome.findByPrdCodigo(pdpBean.getPrdCodigo());
                                if ((ocorrencias != null) && (ocorrencias.size() > 0)) {
                                    for (final OcorrenciaParcelaPeriodo oppBean : ocorrencias) {
                                        // Cria a ocorrência na tabela histórica
                                        OcorrenciaParcelaHome.create(prdBean.getPrdCodigo(), oppBean.getTipoOcorrencia().getTocCodigo(), oppBean.getOcpObs(), oppBean.getUsuario().getUsuCodigo(), oppBean.getOcpData());

                                        // Apaga a ocorrência da tabela de período
                                        AbstractEntityHome.remove(oppBean);
                                    }
                                }

                                // Apaga a parcela da tabela do período
                                AbstractEntityHome.remove(pdpBean);

                            } catch (final FindException ex2) {
                                throw new ParcelaControllerException("mensagem.erro.parcelas.selecionadas.integracao.nao.encontradas", responsavel, ex2);
                            }
                        }
                    }

                    // O total de parcelas pagas é igual a soma das parcelas pagas da tabela de histórico
                    // adicionando a quantidade de parcelas pagas
                    if (!CodedValues.SPD_REJEITADAFOLHA.equals(spdCodigo)) {
                        pagas++;
                        pagasTotal++;
                        if (prdBean.getPrdDataDesconto().compareTo(adeBean.getAdeAnoMesIni()) < 0) {
                            pagasAnteriorDtIni++;
                        }
                    }

                    final LogDelegate log = new LogDelegate(responsavel, Log.PARCELA, Log.UPDATE, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(adeCodigo);
                    log.setParcelaDesconto(prdCodigo != null ? prdCodigo.toString() : null);
                    log.addChangedField(Columns.PRD_DATA_REALIZADO, dataRealizado);
                    log.addChangedField(Columns.PRD_VLR_REALIZADO, prdVlrRealizadoEfetivo);
                    log.write();
                }

                // DESENV-16079 - Ao liquidar uma parcela manual é necessário verificar se existe reimplante manual
                // existindo parcelas com reimplante manual deve se ir retirando as parcelas de acordo com a quantidade de liquidação manual
                final List<String> spdCodigos = new ArrayList<>();
                spdCodigos.add(CodedValues.SPD_EMABERTO);
                final List<ParcelaDescontoTO> parcelasReimplantadasManualmente = findParcelasReimpladasManual(adeCodigo, spdCodigos, responsavel);
                if (CodedValues.SPD_LIQUIDADAMANUAL.equals(spdCodigo) && (parcelasReimplantadasManualmente != null) && !parcelasReimplantadasManualmente.isEmpty()) {
                    int numeroParcelasLiquidadas = chavesParcelas.size();
                    int numeroParcelasReimplantadas = parcelasReimplantadasManualmente.size();

                    for (final ParcelaDescontoTO parcela : parcelasReimplantadasManualmente) {
                        if ((numeroParcelasReimplantadas == 0) || (numeroParcelasLiquidadas == 0)) {
                            break;
                        }

                        final List<ParcelaDescontoPeriodo> parcelasPeriodo = ParcelaDescontoPeriodoHome.findByAutDescontoPrdNumero(adeCodigo, parcela.getPrdNumero());
                        if ((parcelasPeriodo != null) && !parcelasPeriodo.isEmpty()) {
                            AbstractEntityHome.remove(parcelasPeriodo.get(0));
                        }

                        final List<OcorrenciaParcela> lstOcorrenciaParcela = OcorrenciaParcelaHome.findMaxPrdNumeroTocCodigo(adeCodigo, CodedValues.TOC_REIMPLANTE_PARCELA_MANUAL);
                        if ((lstOcorrenciaParcela != null) && !lstOcorrenciaParcela.isEmpty()) {
                            AbstractEntityHome.remove(lstOcorrenciaParcela.get(0));
                        }
                        numeroParcelasReimplantadas--;
                        numeroParcelasLiquidadas--;
                    }
                }

                // Atualizar aut_desconto (sad_codigo, pagas++)
                // Faz pagas++ e sad_codigo = 'Em Andamento' apenas se a parcela foi Liquidada
                if (CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) || CodedValues.SPD_LIQUIDADAMANUAL.equals(spdCodigo)) {
                    String sadCodigoNovo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo();
                    final String sadCodigoAtual = adeBean.getStatusAutorizacaoDesconto().getSadCodigo();
                    final Integer adePrazo = adeBean.getAdePrazo();
                    final Short adeIntFolha = (adeBean.getAdeIntFolha() != null ? adeBean.getAdeIntFolha() : CodedValues.INTEGRA_FOLHA_SIM);
                    final BigDecimal adeVlrSdoRet = adeBean.getAdeVlrSdoRet();

                    // Verifica se preserva parcela, e caso não preserve, as parcelas liquidadas que são anteriores
                    // à data inicial do contrato não devem influenciar na verificação para conclusão do contrato
                    if (pagasAnteriorDtIni > 0) {
                        final boolean preservaPrdRejeitada = reimplantarConsignacaoController.sistemaPreservaParcela(adeCodigo, responsavel);
                        if (!preservaPrdRejeitada) {
                            pagas = pagas - pagasAnteriorDtIni;
                            pagasAnteriorDtIni = 0;
                        }
                    }

                    // Verifica se deve atualizar o status do contrato
                    if ((adePrazo != null) && (adePrazo.intValue() <= pagas)) {
                        // Se pagas == prazo, verifica se o contrato pode ser concluido
                        if ((CodedValues.SAD_DEFERIDA.equals(sadCodigoAtual) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigoAtual)) && adeIntFolha.equals(CodedValues.INTEGRA_FOLHA_SIM) && (adeVlrSdoRet == null)) {
                            sadCodigoNovo = CodedValues.SAD_CONCLUIDO;
                        }
                    } else if (CodedValues.SAD_DEFERIDA.equals(sadCodigoAtual)) {
                        // Se Deferida, então atualiza para Em Andamento
                        sadCodigoNovo = CodedValues.SAD_EMANDAMENTO;
                    }

                    if (sadCodigoNovo.equals(CodedValues.SAD_CONCLUIDO) && !sadCodigoNovo.equals(sadCodigoAtual)) {
                        // Atualiza o número de parcelas pagas
                        adeBean.setAdePrdPagas(pagas);
                        adeBean.setAdePrdPagasTotal(pagasTotal);
                        AbstractEntityHome.update(adeBean);

                        if (colocarEmCarenciaLiqUltParcela) {
                            // Se deve colocar a consignação em carência, então altera o status para em carência ao invés de concluir a consignação
                            sadCodigoNovo = CodedValues.SAD_EMCARENCIA;
                            autorizacaoController.modificaSituacaoADE(adeBean, sadCodigoNovo, responsavel);

                        } else {
                            // Liberar a margem
                            boolean liberaMargemConclusaoContrato = ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                            autorizacaoController.modificaSituacaoADE(adeBean, sadCodigoNovo, responsavel, false, liberaMargemConclusaoContrato);

                            // Insere ocorrência de conclusão de contrato
                            autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_CONCLUSAO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel), responsavel);
                        }

                    } else {
                        if (pagasAnteriorDtIni > 0) {
                            pagas = pagas - pagasAnteriorDtIni;
                            final String orgCodigo = OrgaoHome.findByAdeCod(adeCodigo).getOrgCodigo();
                            String adeIndice = null;
                            final String adeIndiceOld = adeBean.getAdeIndice() != null ? adeBean.getAdeIndice() : null;
                            final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                            final java.sql.Date periodoAtualSql = DateHelper.toSQLDate(periodoAtual);

                            // Se a quantidade de parcelas pagas anterior à data inicial for maior que zero,
                            // e o prazo não for indeterminado o sistema deve reduzir o prazo do contrato
                            if (adePrazo != null) {
                                final int novoPrazo = adePrazo.intValue() - pagasAnteriorDtIni;
                                adeBean.setAdePrazo(novoPrazo);
                                adeBean.setAdeAnoMesFim(PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeBean.getAdeAnoMesIni(), novoPrazo, adeBean.getAdePeriodicidade(), responsavel));

                                // Define observação da ocorrência de alteração
                                final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.prazo.alterado.arg0.de.arg1.para.arg2", responsavel, String.valueOf(pagasAnteriorDtIni), String.valueOf(adePrazo), String.valueOf(novoPrazo));

                                // Insere ocorrência de alteração de contrato, somente se contrato estiver Deferido ou Em Andamento
                                if (CodedValues.SAD_DEFERIDA.equals(sadCodigoNovo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigoNovo)) {
                                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_ALTERACAO_CONTRATO, ocaObs, responsavel);
                                } else {
                                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ocaObs, responsavel);
                                }

                                // Insere ocorrência de informação com o motivo da alteração
                                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AVISO, ApplicationResourcesHelper.getMessage("mensagem.informacao.prazos.ajustado.funcao.liquidacao.parcelas.atrasadas", responsavel), responsavel);

                                // Se somente automático, um novo índice deve ser gerado
                                if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_INDICE, CodedValues.TPC_SIM, responsavel)) && ParamSist.paramEquals(CodedValues.TPC_INDICE_SOMENTE_AUTOMATICO, CodedValues.TPC_SIM, responsavel)) {
                                    final Collection<OcorrenciaAutorizacao> ocorrencia = autorizacaoController.findByAdeTocCodigoOcaPeriodo(adeCodigo, CodedValues.TOC_ALTERACAO_INDICE, periodoAtualSql, responsavel);
                                    if (ocorrencia.isEmpty() && (CodedValues.SAD_DEFERIDA.equals(adeBean.getStatusAutorizacaoDesconto().getSadCodigo()) || CodedValues.SAD_EMANDAMENTO.equals(adeBean.getStatusAutorizacaoDesconto().getSadCodigo()))) {
                                        adeIndice = autorizacaoController.verificaAdeIndice(adeCodigo, adeBean.getRegistroServidor().getRseCodigo(), adeBean.getVerbaConvenio().getConvenio().getCnvCodigo(), adeIndice, adeBean.getAdeCodReg(), null, false, responsavel);
                                    }
                                }
                            }

                            if (!TextHelper.isNull(adeIndice) && !adeIndice.equals(adeIndiceOld)) {
                                adeBean.setAdeIndice(adeIndice);
                                final String msgAlteracaoIndice = ApplicationResourcesHelper.getMessage("mensagem.informacao.consignacao.indice.alterado.de.arg0.para.arg1", responsavel, adeIndiceOld, adeIndice);
                                // Cria ocorrência de alteração do índice
                                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_ALTERACAO_INDICE, msgAlteracaoIndice, null, null, null, periodoAtual, null, responsavel);
                                // Salva dado autorização com o índice anterior
                                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_INDICE_ANTERIOR, adeIndiceOld, responsavel);
                            }
                        }

                        adeBean.setAdePrdPagas(pagas);
                        adeBean.setAdePrdPagasTotal(pagasTotal);
                        adeBean.setStatusAutorizacaoDesconto(StatusAutorizacaoDescontoHome.findByPrimaryKey(sadCodigoNovo));
                        AbstractEntityHome.update(adeBean);
                    }

                    final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(adeCodigo);
                    log.setStatusAutorizacao(sadCodigoNovo);
                    log.addChangedField(Columns.ADE_PRD_PAGAS, Integer.valueOf(pagas));
                    log.addChangedField(Columns.ADE_PRD_PAGAS_TOTAL, Integer.valueOf(pagasTotal));
                    log.addChangedField(Columns.ADE_PAGA, "S");
                    log.write();
                }
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex instanceof ParcelaControllerException) {
                throw (ParcelaControllerException) ex;
            } else {
                throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Desfaz a integração de uma determinada parcela. Como a parcela terá o status alterado
     * para em processamento, a mesma deve ser migrada para a tabela do período. Eventuais
     * ocorrências de retorno que já existam são removidas, e as demais migradas para a
     * tabela do periodo.
     * @param adeCodigo
     * @param prdCodigo
     * @param tocCodigo
     * @param ocpMotivo
     * @param responsavel
     * @throws ParcelaControllerException
     */
    @Override
    public Integer desfazIntegracao(String adeCodigo, Integer prdCodigo, String tocCodigo, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            String spdCodigoAnterior = null;
            Short prdNumero = null;
            Integer prdCodigoNovo = null;
            try {
                // Se a parcela existe e a integração será desfeita ...
                final ParcelaDesconto prdBean = ParcelaDescontoHome.findByPrimaryKeyAndAutDesconto(prdCodigo, adeCodigo);
                spdCodigoAnterior = prdBean.getStatusParcelaDesconto().getSpdCodigo();

                prdNumero = prdBean.getPrdNumero();

                // Verificar se o usuário pode modificar esta ADE
                if (autorizacaoController.usuarioPodeModificarAde(adeCodigo, responsavel)) {

                    // Move a parcela para a tabela do período já com o novo status
                    final ParcelaDescontoPeriodo pdpBean = ParcelaDescontoPeriodoHome.create(adeCodigo, prdNumero, CodedValues.SPD_EMPROCESSAMENTO, prdBean.getPrdDataDesconto(), prdBean.getPrdVlrPrevisto());
                    prdCodigoNovo = pdpBean.getPrdCodigo();

                    // Remove a ocorrência de Retorno e move as demais para a tabela do período
                    final Collection<OcorrenciaParcela> ocorrencias = OcorrenciaParcelaHome.findByPrdCodigo(prdCodigo);
                    for (final OcorrenciaParcela ocpBean : ocorrencias) {
                        final String ocpTocCodigo = ocpBean.getTipoOcorrencia().getTocCodigo();
                        if (!CodedValues.TOC_CODIGOS_RETORNO_PARCELA.contains(ocpTocCodigo)) {
                            // Se não for de retorno, move
                            OcorrenciaParcelaPeriodoHome.create(prdCodigoNovo, ocpTocCodigo, ocpBean.getOcpObs(), ocpBean.getUsuario().getUsuCodigo(), ocpBean.getOcpData());
                        }
                        // Exclui as ocorrências
                        AbstractEntityHome.remove(ocpBean);
                    }

                    // Exclui a parcela da tabela histórica
                    AbstractEntityHome.remove(prdBean);

                    // Cria ocorrencia para a alteração
                    OcorrenciaParcelaPeriodoHome.create(prdCodigoNovo, tocCodigo, ocpMotivo, (responsavel != null ? responsavel.getUsuCodigo() : null));
                }

            } catch (final FindException ex) {
                // Se a parcela não foi encontrada, não verifica se a mesma está na tabela do período
                // pois isso não será possível, uma vez que somente parcelas já integradas podem ser desfeitas
                // e a listagem de parcelas já integradas só pesquisa na tabela histórica
                throw new ParcelaControllerException("mensagem.erro.parcelas.selecionadas.operacao.nao.encontradas", responsavel, ex);
            }

            // Altera a autorização, altera ade_prd_pagas e sad_codigo, apenas se a parcela estava liquidada
            if ((spdCodigoAnterior != null) && (CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigoAnterior) || CodedValues.SPD_LIQUIDADAMANUAL.equals(spdCodigoAnterior))) {
                final AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                int pagas = adeBean.getAdePrdPagas() != null ? adeBean.getAdePrdPagas() : 0;
                int pagasTotal = adeBean.getAdePrdPagasTotal() != null ? adeBean.getAdePrdPagasTotal() : 0;

                if (pagas > 0) {
                    pagas--;
                    pagasTotal--;
                    adeBean.setAdePrdPagas(pagas);
                    adeBean.setAdePrdPagasTotal(pagasTotal);

                    if (CodedValues.SAD_EMANDAMENTO.equals(adeBean.getStatusAutorizacaoDesconto().getSadCodigo()) ||
                            CodedValues.SAD_CONCLUIDO.equals(adeBean.getStatusAutorizacaoDesconto().getSadCodigo())) {
                        final String sadCodigoNovo = pagas == 0 ? CodedValues.SAD_DEFERIDA : CodedValues.SAD_EMANDAMENTO;
                        adeBean.setStatusAutorizacaoDesconto(StatusAutorizacaoDescontoHome.findByPrimaryKey(sadCodigoNovo));
                    }
                } else {
                    adeBean.setStatusAutorizacaoDesconto(StatusAutorizacaoDescontoHome.findByPrimaryKey(CodedValues.SAD_DEFERIDA));
                }
                AbstractEntityHome.update(adeBean);
            }

            // Grava Log de Alteração
            final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setAutorizacaoDesconto(adeCodigo);
            log.setParcelaDesconto(prdCodigo.toString());
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.integracao.desfeita.para.parcela.arg0", responsavel, String.valueOf(prdNumero)));
            log.write();

            return prdCodigoNovo;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex instanceof ParcelaControllerException) {
                throw (ParcelaControllerException) ex;
            } else {
                throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public List<TransferObject> getParcelas(String tipo, String adeNumero, String rseMatricula, String serCpf, String orgCodigo, String csaCodigo, List<String> spdCodigos, int offset, int count, List<String> papCodigos, AcessoSistema responsavel) throws ParcelaControllerException {
        return getParcelas(tipo, adeNumero, rseMatricula, serCpf, orgCodigo, csaCodigo, spdCodigos, offset, count, papCodigos, null, responsavel);
    }

    @Override
    public List<TransferObject> getParcelas(String tipo, String adeNumero, String rseMatricula, String serCpf, String orgCodigo, String csaCodigo, List<String> spdCodigos, int offset, int count, List<String> papCodigos, TransferObject criterio ,AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final ListaParcelasIntegracaoQuery query = new ListaParcelasIntegracaoQuery();
            query.responsavel = responsavel;
            query.tipo = tipo;
            query.adeNumero = adeNumero;
            query.rseMatricula = rseMatricula;
            query.serCpf = serCpf;
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            query.spdCodigos = spdCodigos;
            query.papCodigos = papCodigos;
            if(!TextHelper.isNull(criterio)) {
                query.periodoIni = (Date) criterio.getAttribute("periodoIni");
                query.periodoFim = (Date) criterio.getAttribute("periodoFim");
                query.adeIdentificador = (String) criterio.getAttribute("adeIdentificador");
                query.tocCodigos = (List<String>) criterio.getAttribute("tocCodigos");
            }

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            // Adiciona cláusula de matricula e cpf
            if (CanalEnum.SOAP.equals(responsavel.getCanal()) && responsavel.isCsa()){
                final String param = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP, responsavel);
                query.matriculaExataSoap = (param != null) && CodedValues.TPA_SIM.equals(param);
            }

            return query.executarDTO();
        } catch (HQueryException | ParametroControllerException ex) {
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> getParcelasOrdenaDataDescontoDesc(String adeCodigo, List<String> spdCodigos, Date prdDataDesconto, Short prdNumero, AcessoSistema responsavel) throws ParcelaControllerException {
        try {

            final ListaParcelasQuery query = new ListaParcelasQuery();
            query.adeCodigo = adeCodigo;
            query.spdCodigos = spdCodigos;
            query.prdNumero = prdNumero;
            query.prdDataDesconto = prdDataDesconto;
            query.ordenaDataDescontoDesc = true;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstOcorrenciasParcela(Integer prdCodigo, AcessoSistema responsavel) throws ParcelaControllerException {
        final List<TransferObject> retorno = new ArrayList<>();

        try {
            final Collection<OcorrenciaParcela> ocpList = OcorrenciaParcelaHome.findByPrdCodigo(prdCodigo);

            for (final OcorrenciaParcela ocp : ocpList) {
                final TransferObject ocpTO = new CustomTransferObject();
                ocpTO.setAttribute(Columns.OCP_CODIGO, ocp.getOcpCodigo());
                ocpTO.setAttribute(Columns.OCP_DATA, ocp.getOcpData());
                ocpTO.setAttribute(Columns.OCP_OBS, ocp.getOcpObs());
                retorno.add(ocpTO);
            }
        } catch (final FindException ex) {
            throw new ParcelaControllerException("mensagem.informacao.ocorrencia.parcela.nao.encontrada", responsavel, ex);
        }

        return retorno;
    }

    @Override
    public int countParcelas(String tipo, String adeNumero, String rseMatricula, String serCpf, String orgCodigo, String csaCodigo, List<String> spdCodigos, List<String> papCodigos, AcessoSistema responsavel) throws ParcelaControllerException {
       return countParcelas(tipo, adeNumero, rseMatricula, serCpf, orgCodigo, csaCodigo, spdCodigos, papCodigos, null, responsavel);
    }

    @Override
    public int countParcelas(String tipo, String adeNumero, String rseMatricula, String serCpf, String orgCodigo, String csaCodigo, List<String> spdCodigos, List<String> papCodigos, TransferObject criterio, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final ListaParcelasIntegracaoQuery query = new ListaParcelasIntegracaoQuery();
            query.responsavel = responsavel;
            query.count = true;
            query.tipo = tipo;
            query.adeNumero = adeNumero;
            query.rseMatricula = rseMatricula;
            query.serCpf = serCpf;
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            query.spdCodigos = spdCodigos;
            query.papCodigos = papCodigos;
            if(!TextHelper.isNull(criterio)) {
                query.periodoIni = (Date) criterio.getAttribute("periodoIni");
                query.periodoFim = (Date) criterio.getAttribute("periodoFim");
                query.adeIdentificador = (String) criterio.getAttribute("adeIdentificador");
                query.tocCodigos = (List<String>) criterio.getAttribute("tocCodigos");
            }
            return query.executarContador();
        } catch (final HQueryException ex) {
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Obtem historico de parcela
    @Override
    public List<TransferObject> getHistoricoParcelas(Date prdDataDesconto, List<String> spdCodigos, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final HistoricoConsultaParcelaQuery query = new HistoricoConsultaParcelaQuery();
            query.csaCodigo = responsavel.getCsaCodigo();
            query.spdCodigos = spdCodigos;
            query.prdDataDesconto = prdDataDesconto;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Obtem historico de parcelas
    @Override
    public List<TransferObject> getHistoricoParcelas(String adeCodigo, List<String> spdCodigos, List<String> tocCodigos, boolean arquivado, int offset, int count, boolean exibeParcelaEmAberto, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final HistoricoOcorrenciaParcelaQuery query = new HistoricoOcorrenciaParcelaQuery();
            query.adeCodigo = adeCodigo;
            query.spdCodigos = spdCodigos;
            query.tocCodigos = tocCodigos;
            query.arquivado = arquivado;
            query.offset = offset;
            query.maxResults = count;
            final List<TransferObject> resultado = query.executarDTO();

            if (exibeParcelaEmAberto) {
                final List<String> spdCodigoAbertos = new ArrayList<>();
                spdCodigoAbertos.add(CodedValues.SPD_EMABERTO);
                spdCodigoAbertos.add(CodedValues.SPD_EMPROCESSAMENTO);

                final ListaParcelasPeriodoQuery listaParcelasPeriodoQuery = new ListaParcelasPeriodoQuery();
                listaParcelasPeriodoQuery.adeCodigo = adeCodigo;
                listaParcelasPeriodoQuery.spdCodigos = spdCodigoAbertos;
                final List<TransferObject> parcelasPeriodo = listaParcelasPeriodoQuery.executarDTO();
                resultado.addAll(parcelasPeriodo);
            }

            return resultado;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<ParcelaDescontoTO> findParcelas(String adeCodigo, List<String> spdCodigos, AcessoSistema responsavel) throws ParcelaControllerException {
        try {

            final ListaParcelasQuery listaParcelasQuery = new ListaParcelasQuery();
            listaParcelasQuery.adeCodigo = adeCodigo;
            listaParcelasQuery.spdCodigos = spdCodigos;
            final List<ParcelaDescontoTO> parcelas = listaParcelasQuery.executarDTO(ParcelaDescontoTO.class);

            final ListaParcelasPeriodoQuery listaParcelasPeriodoQuery = new ListaParcelasPeriodoQuery();
            listaParcelasPeriodoQuery.adeCodigo = adeCodigo;
            listaParcelasPeriodoQuery.spdCodigos = spdCodigos;
            final List<ParcelaDescontoTO> parcelasPeriodo = listaParcelasPeriodoQuery.executarDTO(ParcelaDescontoTO.class);

            final List<ParcelaDescontoTO> retorno = new ArrayList<>(parcelas);
            retorno.addAll(parcelasPeriodo);

            // Ordena o resultado pela data de desconto das parcelas
            Collections.sort(retorno, (o1, o2) -> {
                final Date d1 = o1.getPrdDataDesconto();
                final Date d2 = o2.getPrdDataDesconto();
                return d1.compareTo(d2);
            });

            return retorno;

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Map<String, String> selectStatusParcela(AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final ListaStatusParcelaQuery query = new ListaStatusParcelaQuery();
            return query.executarMapa();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<ParcelaDescontoPeriodo> findByAutDescontoStatus(String adeCodigo, String spdCodigo, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            return ParcelaDescontoPeriodoHome.findByAutDescontoStatus(adeCodigo, spdCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.informacao.parcelas.status.nao.encontradas", responsavel, ex);
        }
    }

    @Override
    public TransferObject findParcelaByAdePeriodo(String adeCodigo, Date prdDataDesconto, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final Collection<ParcelaDesconto> parcelas = ParcelaDescontoHome.findByAutDescontoPeriodo(adeCodigo, prdDataDesconto);
            if ((parcelas == null) || parcelas.isEmpty() || (parcelas.size() > 1)) {
                throw new ParcelaControllerException("mensagem.informacao.parcela.nao.encontrada", responsavel);
            }

            final ParcelaDesconto parcela = parcelas.iterator().next();
            final TransferObject retorno = new CustomTransferObject();
            retorno.setAttribute(Columns.PRD_ADE_CODIGO, adeCodigo);
            retorno.setAttribute(Columns.PRD_NUMERO, parcela.getPrdNumero());
            retorno.setAttribute(Columns.PRD_SPD_CODIGO, parcela.getStatusParcelaDesconto().getSpdCodigo());
            retorno.setAttribute(Columns.PRD_DATA_DESCONTO, parcela.getPrdDataDesconto());
            retorno.setAttribute(Columns.PRD_VLR_PREVISTO, parcela.getPrdVlrPrevisto());
            retorno.setAttribute(Columns.PRD_VLR_REALIZADO, parcela.getPrdVlrRealizado());
            retorno.setAttribute(Columns.PRD_DATA_REALIZADO, parcela.getPrdDataRealizado());
            if (!TextHelper.isNull(parcela.getTipoDesconto())) {
                retorno.setAttribute(Columns.PRD_TDE_CODIGO, parcela.getTipoDesconto().getTdeCodigo());
            }
            if (!TextHelper.isNull(parcela.getTipoMotivoNaoExportacao())) {
                retorno.setAttribute(Columns.PRD_MNE_CODIGO, parcela.getTipoMotivoNaoExportacao().getMneCodigo());
            }

            return retorno;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.informacao.parcela.nao.encontrada", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstResumoParcelasPerido(Date periodo, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final ListaResumoParcelasPeriodoQuery query = new ListaResumoParcelasPeriodoQuery();
            query.periodo = periodo;
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void criaParcelaDesconto(ParcelaDescontoTO parcela, String ocpObs, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final List<TransferObject> parcelas = getParcelasOrdenaDataDescontoDesc(parcela.getAdeCodigo(), Arrays.asList(CodedValues.SPD_EMABERTO), parcela.getPrdDataDesconto(), parcela.getPrdNumero(), responsavel);

            ParcelaDesconto prdBean = null;
            if (parcelas.size() > 0) {
                prdBean = ParcelaDescontoHome.findByPrimaryKeyAndAutDesconto((Integer) parcelas.get(0).getAttribute(Columns.PRD_CODIGO), parcela.getAdeCodigo());

                prdBean.setPrdDataDesconto(parcela.getPrdDataDesconto());
                prdBean.setPrdDataRealizado(parcela.getPrdDataRealizado());
                prdBean.setPrdVlrPrevisto(parcela.getPrdVlrPrevisto());
                prdBean.setPrdVlrRealizado(parcela.getPrdVlrRealizado());

                AbstractEntityHome.update(prdBean);

            } else {
                // Cria a parcela
                prdBean = ParcelaDescontoHome.create(parcela.getAdeCodigo(), parcela.getPrdNumero(), parcela.getTdeCodigo(), parcela.getSpdCodigo(),
                                           parcela.getPrdDataDesconto(), parcela.getPrdDataRealizado(), parcela.getPrdVlrPrevisto(),
                                           parcela.getPrdVlrRealizado());
            }

            //cria ocorrencia de modificação de valor da parcela
            OcorrenciaParcelaHome.create(prdBean.getPrdCodigo(), CodedValues.TOC_EDICAO_FLUXO_PARCELAS, ocpObs, (responsavel != null ? responsavel.getUsuCodigo() : null));

            final LogDelegate log = new LogDelegate(responsavel, Log.PARCELA, Log.CREATE, Log.LOG_INFORMACAO);
            log.setAutorizacaoDesconto(parcela.getAdeCodigo());
            log.addChangedField(Columns.PRD_NUMERO, parcela.getPrdNumero());
            log.setStatusParcela(CodedValues.SPD_EMABERTO);
            log.addChangedField(Columns.PRD_DATA_DESCONTO, parcela.getPrdDataDesconto());
            log.addChangedField(Columns.PRD_VLR_PREVISTO, parcela.getPrdVlrRealizado());
            log.write();


        } catch (com.zetra.econsig.exception.CreateException | FindException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject findStatusParcelaDesconto(String spdCodigo, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final TransferObject spd = new CustomTransferObject();
            final StatusParcelaDesconto statusPrd = StatusParcelaDescontoHome.findByPrimaryKey(spdCodigo);
            spd.setAttribute(Columns.SPD_CODIGO, statusPrd.getSpdCodigo());
            spd.setAttribute(Columns.SPD_DESCRICAO, statusPrd.getSpdDescricao());

            return spd;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<ParcelaDescontoTO> findParcelasLiquidarParcial(String adeCodigo, boolean ordenar, Date prdDataDesconto, Short prdNumero, AcessoSistema responsavel) throws ParcelaControllerException {
        return findParcelasLiquidarParcial(adeCodigo, ordenar, prdDataDesconto, null, prdNumero, responsavel);
    }

    private List<ParcelaDescontoTO> findParcelasLiquidarParcial(String adeCodigo, boolean ordenar, Date prdDataDesconto, Integer prdCodigo, Short prdNumero, AcessoSistema responsavel) throws ParcelaControllerException {
        try {

            final boolean permiteLiquidarParcelaParcial = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_PGTO_PARCIAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            final ListaParcelasLiquidarParcialQuery listaParcelasLiquidarParcialQuery = new ListaParcelasLiquidarParcialQuery();
            listaParcelasLiquidarParcialQuery.adeCodigo = adeCodigo;
            listaParcelasLiquidarParcialQuery.prdDataDesconto = prdDataDesconto;
            listaParcelasLiquidarParcialQuery.prdCodigo = prdCodigo;
            listaParcelasLiquidarParcialQuery.prdNumero = prdNumero;
            listaParcelasLiquidarParcialQuery.ordenaDataDescontoDesc = ordenar;
            listaParcelasLiquidarParcialQuery.permiteLiquidarParcelaParcial = permiteLiquidarParcelaParcial;
            return listaParcelasLiquidarParcialQuery.executarDTO(ParcelaDescontoTO.class);

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista as parcelas possíveis para reimplante
     * @param adeCodigo
     * @param spdCodigo
     * @param responsavel
     * @throws ParcelaControllerException
     */
    @Override
    public List<ParcelaDescontoTO> findParcelasReimplantarManual(String adeCodigo, List<String> spdCodigos, AcessoSistema responsavel) throws ParcelaControllerException {
        try {

            final ListaParcelasReimplanteManualQuery listaParcelasReimplanteManualQuery = new ListaParcelasReimplanteManualQuery();
            listaParcelasReimplanteManualQuery.adeCodigo = adeCodigo;
            listaParcelasReimplanteManualQuery.spdCodigos = spdCodigos;

            return listaParcelasReimplanteManualQuery.executarDTO(ParcelaDescontoTO.class);

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Reimplanta parcelas rejeitadas para depois do período de conclusão do contrato.
     * @param adeCodigo
     * @param vlrPrevistoPorParcela
     * @param spdCodigo
     * @param ocpMotivo
     * @param responsavel
     * @throws ParcelaControllerException
     */
    @Override
    public void reimplentarParcela(String adeCodigo, Map<String, BigDecimal> vlrPrevistoPorParcela, String spdCodigo, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            // Define a descrição da ocorrência e a data de realização da parcela
            ocpMotivo = ((ocpMotivo == null) || "".equals(ocpMotivo)) ? ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.reimplante.manual", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informacao.reimplante.arg0", responsavel, ocpMotivo);

            final StatusAutorizacaoDesconto sadBean = StatusAutorizacaoDescontoHome.findStatusAutorizacaoContrato(adeCodigo);
            final String sadCodigo = sadBean.getSadCodigo();
            if (CodedValues.SAD_INDEFERIDA.equals(sadCodigo) || CodedValues.SAD_CANCELADA.equals(sadCodigo)
                    || CodedValues.SAD_LIQUIDADA.equals(sadCodigo) || CodedValues.SAD_CONCLUIDO.equals(sadCodigo) || CodedValues.SAD_ENCERRADO.equals(sadCodigo)) {
                throw new ParcelaControllerException("mensagem.erro.situacao.contrato.nao.permite.reimplante.parcela", responsavel);
            }

            final List<String> chavesParcelas = new ArrayList<>(vlrPrevistoPorParcela.keySet());

            // Ordena numericamente os números de parcelas
            if (chavesParcelas.size() > 1) {
                Collections.sort(chavesParcelas, (o1, o2) -> {
                    final String[] chaves1 = o1.split(";");
                    final String prdNumero1 = (chaves1.length > 0 ? chaves1[0] : "0");
                    final String[] chaves2 = o2.split(";");
                    final String prdNumero2 = (chaves2.length > 0 ? chaves2[0] : "0");
                    return Integer.valueOf(prdNumero1).compareTo(Integer.valueOf(prdNumero2));
                });
            }

            final AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

            if(TextHelper.isNull(adeBean.getAdePrazo())){
                throw new ParcelaControllerException("mensagem.erro.contrato.prazo.indeterminado.nao.permite.reimplante.parcela",responsavel);
            }

            final Integer maxPrdNumero = ParcelaDescontoHome.findMaxPrdNumero(adeCodigo);
            final Integer maxPdpNumero = ParcelaDescontoPeriodoHome.findMaxPrdNumero(adeCodigo);
            Integer prdNumeroCalculado = Math.max(maxPrdNumero, maxPdpNumero) + 1;
            prdNumeroCalculado = prdNumeroCalculado <= adeBean.getAdePrazo() ? adeBean.getAdePrazo() + 1 : prdNumeroCalculado;
            final ParcelaDescontoPeriodo lastPdp = ParcelaDescontoPeriodoHome.findLastByAutDesconto(adeCodigo);
            Date lastPeriodoPdp = (lastPdp != null) && (lastPdp.getPrdDataDesconto().compareTo(adeBean.getAdeAnoMesFim()) > 0) ? lastPdp.getPrdDataDesconto() : (Date) adeBean.getAdeAnoMesFim();
            final String orgCodigo = adeBean.getRegistroServidor().getOrgao().getOrgCodigo();

            for (final String chave : chavesParcelas) {
                final String[] chaves = chave.split(";");
                final String prdNumero = (chaves.length > 0 ? chaves[0] : null);
                final Date prdDataDesconto = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, lastPeriodoPdp, 2, adeBean.getAdePeriodicidade(), responsavel);
                final BigDecimal prdVlrPrevisto = vlrPrevistoPorParcela.get(prdNumero + ";" + (chaves.length > 1 ? chaves[1] : null));

                // Criar a parcela desconto periodo
                final ParcelaDescontoPeriodo pdpBean = ParcelaDescontoPeriodoHome.create(adeCodigo, prdNumeroCalculado.shortValue(), spdCodigo, prdDataDesconto, prdVlrPrevisto);

                // Cria a ocorrência para a integração manual
                // A ocorrência de retorno parcial deve ser grava somente quando o tipo valor for fixo
                OcorrenciaParcelaHome.create(pdpBean.getPrdCodigo(), CodedValues.TOC_REIMPLANTE_PARCELA_MANUAL, ocpMotivo, (responsavel != null ? responsavel.getUsuCodigo() : null));

                //Cria ocorrência de consignação de reimplante para que seja reenviado no movimento após a data fim.
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_RELANCAMENTO, ApplicationResourcesHelper.getMessage("mensagem.informacao.reimplante.parcela.arg0", responsavel,prdNumero, DateHelper.toDateString(prdDataDesconto)), prdDataDesconto, responsavel);

                lastPeriodoPdp = prdDataDesconto;
                prdNumeroCalculado++;
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex instanceof ParcelaControllerException) {
                throw (ParcelaControllerException) ex;
            } else {
                throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Lista as parcelas reimplantadas manualmente
     * @param adeCodigo
     * @param spdCodigo
     * @param responsavel
     * @throws ParcelaControllerException
     */
    private List<ParcelaDescontoTO> findParcelasReimpladasManual(String adeCodigo, List<String> spdCodigos, AcessoSistema responsavel) throws ParcelaControllerException {
        try {

            final ListaParcelasReimplantadasManualQuery listaParcelasReimplantadasManualQuery = new ListaParcelasReimplantadasManualQuery();
            listaParcelasReimplantadasManualQuery.adeCodigo = adeCodigo;
            listaParcelasReimplantadasManualQuery.spdCodigos = spdCodigos;

            return listaParcelasReimplantadasManualQuery.executarDTO(ParcelaDescontoTO.class);

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista Parcelas somente da tb_parcela_desconto_periodo
     * @param adeCodigo
     * @param responsavel
     * @throws ParcelaControllerException
     */
    @Override
    public List<TransferObject> findParcelasPeriodo(String adeCodigo, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final List<String> spdCodigoAbertos = new ArrayList<>();
            spdCodigoAbertos.add(CodedValues.SPD_EMABERTO);
            spdCodigoAbertos.add(CodedValues.SPD_EMPROCESSAMENTO);

            final ListaParcelasPeriodoQuery listaParcelasPeriodoQuery = new ListaParcelasPeriodoQuery();
            listaParcelasPeriodoQuery.adeCodigo = adeCodigo;
            listaParcelasPeriodoQuery.spdCodigos = spdCodigoAbertos;
            return listaParcelasPeriodoQuery.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista ADE's de acordo com período das parcelas da ade
     * @param adeCodigo
     * @param responsavel
     * @throws ParcelaControllerException
     */
    @Override
    public List<TransferObject> adesParcelasFuturasByPeriodo(TransferObject criterio, AcessoSistema responsavel) throws ParcelaControllerException {
        try {
            final ListaAdesPorPeriodoParcelasQuery query = new ListaAdesPorPeriodoParcelasQuery();

            query.dataIni = (String) criterio.getAtributos().get("DATA_INI");
            query.dataFim = (String) criterio.getAtributos().get("DATA_FIM");
            query.estCodigo = (String) criterio.getAtributos().get("EST_CODIGO");
            query.orgCodigos = (List<String>) criterio.getAtributos().get("ORG_CODIGO");
            query.rseMatricula = (String) criterio.getAtributos().get("RSE_MATRICULA");
            query.serCpf = (String) criterio.getAtributos().get("SER_CPF");
            query.csaCodigo = (String) criterio.getAtributos().get("CSA_CODIGO");
            query.corCodigos = (List<String>) criterio.getAtributos().get(Columns.COR_CODIGO);
            query.sboCodigo = (String) criterio.getAtributos().get("SBO_CODIGO");
            query.uniCodigo = (String) criterio.getAtributos().get("UNI_CODIGO");
            query.svcCodigo = (List<String>) criterio.getAtributos().get("SVC_CODIGO");
            query.sadCodigos = (List<String>) criterio.getAtributos().get("SAD_CODIGO");
            query.tipoEntidade = (String) criterio.getAtributos().get("TIPO_ENTIDADE");
            query.origemAdes = (List<String>) criterio.getAttribute("ORIGEM_ADE");
            query.motivoTerminoAdes = (List<String>) criterio.getAttribute("TERMINO_ADE");
            query.srsCodigos = (List<String>) criterio.getAtributos().get(Columns.SRS_CODIGO);
            query.nseCodigos = (List<String>) criterio.getAttribute("NSE_CODIGO");
            query.tmoDecisaoJudicial = (Boolean) criterio.getAttribute("TMO_DECISAO_JUDICIAL");
            query.responsavel = responsavel;
            query.parcelaDescontoPeriodo = false;

            if ((responsavel == null) && (criterio.getAttribute("responsavel") != null)) {
                query.responsavel = (AcessoSistema) criterio.getAttribute("responsavel");
            }

            final List<TransferObject> parcelasDesconto = query.executarDTO();

            query.parcelaDescontoPeriodo = true;
            final List<TransferObject> parcelasPeriodo = query.executarDTO();

            return Stream.concat(parcelasDesconto.stream(), parcelasPeriodo.stream()).toList();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public Date dataLimiteOcorrencia(String adeCodigo, String tocCodigo) throws FindException {
        final List<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, tocCodigo);

        ocorrencias.sort(Comparator.comparing(OcorrenciaAutorizacao::getOcaPeriodo).reversed());
        Date dataOcorrencia = !TextHelper.isNull(ocorrencias) && ocorrencias.size() > 0 ? ocorrencias.get(0).getOcaPeriodo() : null;
        return dataOcorrencia;
    }

    @Override
    public boolean consignacaoAptaCarenciaConclusao(String adeCodigo, AcessoSistema responsavel) throws ParcelaControllerException {
        final String paramCarenciaConclusao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, responsavel);
        final String paramCarenciaConclusaoComSdv = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CARENCIA_CONCLUSAO_APENAS_COM_SDV, responsavel);
        final int carenciaFolha = TextHelper.isNum(paramCarenciaConclusao) ? Integer.valueOf(paramCarenciaConclusao) : 0;
        final int carenciaFolhaComSdv = TextHelper.isNum(paramCarenciaConclusaoComSdv) ? Integer.valueOf(paramCarenciaConclusaoComSdv) : 0;

        boolean aptaCarenciaConclusao = false;
        try {
            if (carenciaFolhaComSdv > 0) {
                // Verifica se é da natureza de empréstimo ou salaryPay, pois a carência com saldo só se aplica a serviços destas naturezas
                final Servico svc = ServicoHome.findByAdeCodigo(adeCodigo);
                if (svc.getNaturezaServico() != null && (CodedValues.NSE_EMPRESTIMO.equals(svc.getNaturezaServico().getNseCodigo()) || CodedValues.NSE_SALARYPAY.equals(svc.getNaturezaServico().getNseCodigo()))) {
                    // 947 = S deve considerar apenas parcelas rejeitadas ou liquidadas pela folha
                    final boolean saldoDevedorLiqFolha = ParamSist.paramEquals(CodedValues.TPC_VERIFICA_CARENCIA_CONCLUSAO_APENAS_COM_SDV_PARCELAS_PAGAS, CodedValues.TPC_SIM, responsavel);

                    // Verifica nas parcelas do contrato se tem saldo devedor aberto
                    final List<ParcelaDescontoTO> parcelas = findParcelas(adeCodigo, null, responsavel);
                    if (parcelas != null && !parcelas.isEmpty()) {
                        for (ParcelaDescontoTO parcela : parcelas) {
                            final String spdCodigo = parcela.getSpdCodigo();
                            if (spdCodigo.equals(CodedValues.SPD_REJEITADAFOLHA)) {
                                aptaCarenciaConclusao = true;
                                break;
                            } else {
                                final BigDecimal vlrPrevisto = parcela.getPrdVlrPrevisto();
                                final BigDecimal vlrRealizado = parcela.getPrdVlrRealizado();
                                // Se pagou menos que o previsto
                                if (vlrRealizado != null && vlrRealizado.compareTo(vlrPrevisto) < 0) {
                                    if (spdCodigo.equals(CodedValues.SPD_LIQUIDADAFOLHA) || (!saldoDevedorLiqFolha && spdCodigo.equals(CodedValues.SPD_LIQUIDADAMANUAL))) {
                                        aptaCarenciaConclusao = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (carenciaFolha > 0) {
                aptaCarenciaConclusao = true;
            }
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return aptaCarenciaConclusao;
    }

    /**
     * Lista todas as parcelas de acordo com a CSA e demais critérios
     * @param adeCodigo
     * @param responsavel
     * @throws ParcelaControllerException
     */
    @Override
    public List<TransferObject> listarParcelasPorCsa(TransferObject criterio, AcessoSistema responsavel) throws ParcelaControllerException{
        try {
            List<TransferObject> parcelas = new ArrayList<>();

            final ListarParcelasPorCsaQuery query = new ListarParcelasPorCsaQuery();
            query.responsavel = responsavel;
            query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
            query.adeNumero = (Long) criterio.getAttribute(Columns.ADE_NUMERO);
            query.adeIdentificador = (String) criterio.getAttribute(Columns.ADE_IDENTIFICADOR);
            query.spdCodigos = (List<String>) criterio.getAttribute("SITUACAO_PARCELA");
            query.serCpf = (String) criterio.getAttribute(Columns.SER_CPF);
            query.rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
            query.svcIdentificador = (String) criterio.getAttribute(Columns.SVC_IDENTIFICADOR);
            query.prdDataDesconto = (Date) criterio.getAttribute(Columns.PRD_DATA_DESCONTO);
            query.estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
            query.orgCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
            query.cnvCodVerba = (String) criterio.getAttribute(Columns.CNV_COD_VERBA);

            final List<TransferObject> parcelasDesconto = query.executarDTO();

            query.parcelaDescontoPeriodo = true;
            final List<TransferObject> parcelasDescontoPeriodo = query.executarDTO();

            parcelas.addAll(parcelasDesconto);
            parcelas.addAll(parcelasDescontoPeriodo);
            parcelas.sort(Comparator.comparing((TransferObject o) -> (Long) o.getAttribute(Columns.ADE_NUMERO), Comparator.reverseOrder()).thenComparing((TransferObject o) -> (Short) o.getAttribute(Columns.PRD_NUMERO)));

            return parcelas;
        }catch(final HQueryException ex) {
            throw new ParcelaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

}