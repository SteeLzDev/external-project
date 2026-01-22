package com.zetra.econsig.service.financiamentodivida;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PropostaPagamentoDividaTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.FinanciamentoDividaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.PropostaPagamentoDivida;
import com.zetra.econsig.persistence.entity.PropostaPagamentoDividaHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoId;
import com.zetra.econsig.persistence.entity.SaldoDevedor;
import com.zetra.econsig.persistence.entity.SaldoDevedorHome;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusCompra;
import com.zetra.econsig.persistence.entity.StatusProposta;
import com.zetra.econsig.persistence.entity.StatusSolicitacao;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.calendario.ObtemProximoDiaUtilQuery;
import com.zetra.econsig.persistence.query.proposta.ListaAcompanhamentoFinancDividaQuery;
import com.zetra.econsig.persistence.query.proposta.ListaPropostaPagamentoDividaExpiradaQuery;
import com.zetra.econsig.persistence.query.proposta.ListaPropostaPagamentoDividaQuery;
import com.zetra.econsig.persistence.query.proposta.ListaSolicitacaoPropostaExpiradaQuery;
import com.zetra.econsig.persistence.query.proposta.VerificaAusenciaPropostasPgtDivQuery;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusCompraEnum;
import com.zetra.econsig.values.StatusPropostaEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: FinanciamentoDividaControllerBean</p>
 * <p>Description: Session Façade para operações do módulo de financiamento
 * de dívida de cartão.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class FinanciamentoDividaControllerBean implements FinanciamentoDividaController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FinanciamentoDividaControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Autowired
    private ServicoController servicoController;

    /**
     * Valida as propostas de pagamento de dívida informadas pela consignatária
     * @param saldoDevedorTO
     * @param propostasPgtSaldo
     * @param responsavel
     * @return
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public List<PropostaPagamentoDividaTO> validarPropostasPgtSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, List<PropostaPagamentoDividaTO> propostasPgtSaldo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        List<PropostaPagamentoDividaTO> propostasValidas = new ArrayList<>();
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor()) {
            try {
                String adeCodigo = saldoDevedorTO.getAdeCodigo();
                // Consignatária deverá sempre ser a do usuário, e não a do contrato
                String csaCodigo = responsavel.getCsaCodigo();
                // Obtém os entity beans necessários
                AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
                VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
                Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());
                String svcCodigo = cnvBean.getServico().getSvcCodigo();
                String orgCodigo = cnvBean.getOrgao().getOrgCodigo();
                String rseCodigo = adeBean.getRegistroServidor().getRseCodigo();

                // Se está informando proposta para contrato que não pertence à consignatária, é COMPRA
                boolean compra = !csaCodigo.equals(cnvBean.getConsignataria().getCsaCodigo());

                // Busca os parâmetros de serviço para verificar se há limitação de saldo devedor
                ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                int qtdMinPropostas = 0;
                int qtdMaxPropostas = 9;
                if (!TextHelper.isNull(paramSvc.getTpsQtdPropostasPagamentoParcelSaldo())) {
                    try {
                        qtdMinPropostas = Integer.valueOf(paramSvc.getTpsQtdPropostasPagamentoParcelSaldo());
                    } catch (NumberFormatException ex) {
                        LOG.error("Valor incorreto para o parâmetro de serviço '" + CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO + "' e serviço '" + svcCodigo + "'.");
                        throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel);
                    }
                    if (!TextHelper.isNull(paramSvc.getTpsQtdPropostasPagamentoParcelSaldoRef())) {
                        try {
                            qtdMaxPropostas = Integer.valueOf(paramSvc.getTpsQtdPropostasPagamentoParcelSaldoRef());
                        } catch (NumberFormatException ex) {
                            LOG.error("Valor de referência incorreto para o parâmetro de serviço '" + CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO + "' e serviço '" + svcCodigo + "'.");
                            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel);
                        }
                    }
                }

                if (qtdMinPropostas > 0) {
                    // Verifica se a CSA tem convênio com serviço de financiamento de dívida
                    List<String> svcCodigosDestino = servicoController.obtemServicoRelacionadoComConvenioAtivo(svcCodigo, csaCodigo, orgCodigo, CodedValues.TNT_FINANCIAMENTO_DIVIDA, responsavel);
                    if (svcCodigosDestino != null && svcCodigosDestino.size() > 0) {
                        String svcCodigoDestino = svcCodigosDestino.get(0);

                        if (propostasPgtSaldo.size() < qtdMinPropostas) {
                            throw new FinanciamentoDividaControllerException("mensagem.erro.financiamento.divida.qtd.minima.propostas", responsavel, String.valueOf(qtdMinPropostas));
                        } else if (propostasPgtSaldo.size() > qtdMaxPropostas) {
                            throw new FinanciamentoDividaControllerException("mensagem.erro.financiamento.divida.qtd.maxima.propostas", responsavel, String.valueOf(qtdMaxPropostas));
                        }

                        Date prazoIni = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                        Date dataAtual = DateHelper.getSystemDatetime();

                        // Obtém a margem disponível para financiamento (margem + soma dos contratos),
                        // para validação do valor de parcela da proposta
                        BigDecimal rseMargemRest = obtemMargemFinanciamento(svcCodigoDestino, rseCodigo, adeCodigo, compra, responsavel);

                        for (PropostaPagamentoDividaTO propostaPgtSaldo : propostasPgtSaldo) {
                            Integer przProposta = propostaPgtSaldo.getPpdPrazo();
                            BigDecimal vlrProposta = propostaPgtSaldo.getPpdValorParcela();
                            BigDecimal valorDivida = propostaPgtSaldo.getPpdValorDivida();

                            if (valorDivida == null || valorDivida.signum() <= 0) {
                                throw new FinanciamentoDividaControllerException("mensagem.erro.valor.saldo.devedor.com.desconto.deve.ser.maior.zero", responsavel);
                            }

                            if (vlrProposta.compareTo(rseMargemRest) > 0) {
                                throw new FinanciamentoDividaControllerException("mensagem.margemInsuficiente", responsavel);
                            }

                            // Valida o CET da proposta
                            BigDecimal[] valores = null;
                            try {
                                valores = autorizacaoController.validarTaxaJuros(vlrProposta, valorDivida, null,
                                        null, null, przProposta,
                                        dataAtual, prazoIni, svcCodigoDestino, csaCodigo,
                                        orgCodigo, false, null, adeBean.getAdePeriodicidade(),
                                        rseCodigo, responsavel);
                            } catch (AutorizacaoControllerException ex) {
                                String errorKey = ex.getMessageKey();
                                if (errorKey != null) {
                                    if (errorKey.equals("mensagem.aviso.sem.cet.prazo.csa") || errorKey.equals("mensagem.aviso.sem.taxa.prazo.csa")) {
                                        throw new FinanciamentoDividaControllerException("mensagem.erro.cet.inexistente.prazo", responsavel, ex);
                                    } else if (errorKey.equals("mensagem.erro.cet.calculado.maior.anunciado") || errorKey.equals("mensagem.erro.taxa.calculada.maior.anunciado")) {
                                        ////DESENV-19717: Não colocamos aqui o valor do cet e máximo de parcela por lógica não ser aplicada aqui, mas nas reservas de margem.
                                        throw new FinanciamentoDividaControllerException("mensagem.erro.cet.superior.anunciado.simples", responsavel, ex);
                                    }
                                }
                                throw new FinanciamentoDividaControllerException(ex);
                            }
                            BigDecimal taxaCadastrada = valores[5];

                            // Calcula o CET efetivo da proposta
                            BigDecimal taxaJurosEfetiva = SimulacaoHelper.calcularTaxaJuros(valorDivida, vlrProposta, przProposta, dataAtual, prazoIni, orgCodigo, responsavel);
                            if (taxaJurosEfetiva.compareTo(taxaCadastrada) == 1) {
                                // Caso a taxa efetiva seja maior que a cadastrada, por causa da
                                // margem de erro, registra que a taxa foi a cadastrada
                                taxaJurosEfetiva = taxaCadastrada;
                            }
                            propostaPgtSaldo.setPpdTaxaJuros(taxaJurosEfetiva.setScale(2, java.math.RoundingMode.HALF_UP));

                            // Adiciona a proposta a lista de propostas válidas
                            propostasValidas.add(propostaPgtSaldo);
                        }
                    }
                }
            } catch (FinanciamentoDividaControllerException ex) {
                throw ex;
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        return propostasValidas;
    }

    /**
     * Salva as informações sobre as propostas de pagamento do saldo devedor.
     * @param adeCodigo
     * @param propostasPgtSaldo
     * @param responsavel
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public void atualizaPropostasPagamentoSaldo(String adeCodigo, List<PropostaPagamentoDividaTO> propostasPgtSaldo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
            try {
                // Delegate de parâmetros necessário para recuperação dos parâmetros de serviço
                // Serviço do contrato a ser atualizado
                String svcCodigo = ServicoHome.findByAdeCodigo(adeCodigo).getSvcCodigo();

                // Calcular data de validade das propostas de acordo com o parâmetro de serviço
                Date dataValidade = null;
                Date dataAtual = DateHelper.getSystemDatetime();

                if (propostasPgtSaldo != null && propostasPgtSaldo.size() > 0) {
                    // Busca parâmetro de serviço que determina a validade em dias das propostas de pagamento do saldo
                    ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                    if (!TextHelper.isNull(paramSvc.getTpsDiasValidadePropostasPgtoSaldo())) {
                        try {
                            Integer diasValidade = Integer.valueOf(paramSvc.getTpsDiasValidadePropostasPgtoSaldo());
                            dataValidade = obtemProximoDiaUtil(dataAtual, diasValidade);
                        } catch (NumberFormatException ex) {
                            LOG.error("Valor incorreto para o parâmetro de serviço '" + CodedValues.TPS_DIAS_VALIDADE_PROPOSTAS_PGTO_SALDO + "' e serviço '" + svcCodigo + "'.");
                            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel);
                        }
                    }

                    // Propostas criadas com status de agurad. aprovação
                    String stpCodigo = StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo();

                    // Mantém uma lista das propostas que foram salvas, para excluir as demais
                    List<String> ppdCodigoSalvo = new ArrayList<>();

                    for (PropostaPagamentoDividaTO proposta : propostasPgtSaldo) {
                        // Seta as informações no TO para gerar Log
                        proposta.setPpdDataCadastro(dataAtual);
                        proposta.setPpdDataValidade(dataValidade);
                        proposta.setStpCodigo(stpCodigo);

                        // Verifica se a proposta existe e deve ser atualizada
                        if (!TextHelper.isNull(proposta.getPpdCodigo())) {
                            PropostaPagamentoDivida ppdBean = PropostaPagamentoDividaHome.findByPrimaryKey(proposta.getPpdCodigo());

                            // Verifica se os valores foram alterados
                            if (!ppdBean.getPpdPrazo().equals(proposta.getPpdPrazo()) ||
                                    !ppdBean.getPpdValorDivida().equals(proposta.getPpdValorDivida()) ||
                                    !ppdBean.getPpdValorParcela().equals(proposta.getPpdValorParcela())) {
                                // Proposta alterada, verifica se o status permite a alteração
                                StatusPropostaEnum statusAtual = StatusPropostaEnum.recuperaStatusProposta(ppdBean.getStatusProposta().getStpCodigo());
                                if (statusAtual.equals(StatusPropostaEnum.APROVADA) || statusAtual.equals(StatusPropostaEnum.FINALIZADA)) {
                                    throw new FinanciamentoDividaControllerException("mensagem.erro.situacao.proposta.nao.permite.alteracao", responsavel);
                                }

                                // Grava log da operação
                                LogDelegate logDelegate = new LogDelegate(responsavel, Log.PROPOSTA_PAGAMENTO_DIVIDA, Log.UPDATE, Log.LOG_INFORMACAO);
                                logDelegate.setPropostaPagamentoDivida(ppdBean.getPpdCodigo());
                                logDelegate.setAutorizacaoDesconto(proposta.getAdeCodigo());
                                logDelegate.setUsuario(proposta.getUsuCodigo());
                                logDelegate.setConsignataria(proposta.getCsaCodigo());
                                logDelegate.setStatusProposta(proposta.getStpCodigo());

                                if (!ppdBean.getPpdPrazo().equals(proposta.getPpdPrazo())) {
                                    logDelegate.addChangedField(Columns.PPD_PRAZO, proposta.getPpdPrazo(), ppdBean.getPpdPrazo());
                                    ppdBean.setPpdPrazo(proposta.getPpdPrazo());
                                }
                                if (!ppdBean.getPpdValorDivida().equals(proposta.getPpdValorDivida())) {
                                    logDelegate.addChangedField(Columns.PPD_VALOR_DIVIDA, proposta.getPpdValorDivida(), ppdBean.getPpdValorDivida());
                                    ppdBean.setPpdValorDivida(proposta.getPpdValorDivida());
                                }
                                if (!ppdBean.getPpdValorParcela().equals(proposta.getPpdValorParcela())) {
                                    logDelegate.addChangedField(Columns.PPD_VALOR_PARCELA, proposta.getPpdValorParcela(), ppdBean.getPpdValorParcela());
                                    ppdBean.setPpdValorParcela(proposta.getPpdValorParcela());
                                }
                                if (!ppdBean.getPpdTaxaJuros().equals(proposta.getPpdTaxaJuros())) {
                                    logDelegate.addChangedField(Columns.PPD_TAXA_JUROS, proposta.getPpdTaxaJuros(), ppdBean.getPpdTaxaJuros());
                                    ppdBean.setPpdTaxaJuros(proposta.getPpdTaxaJuros());
                                }
                                if (!ppdBean.getStatusProposta().getStpCodigo().equals(proposta.getStpCodigo())) {
                                    ppdBean.setStatusProposta(new StatusProposta(proposta.getStpCodigo()));
                                }
                                if (!ppdBean.getPpdDataCadastro().equals(proposta.getPpdDataCadastro())) {
                                    logDelegate.addChangedField(Columns.PPD_DATA_CADASTRO, proposta.getPpdDataCadastro(), ppdBean.getPpdDataCadastro());
                                    ppdBean.setPpdDataCadastro(proposta.getPpdDataCadastro());
                                }
                                if (!ppdBean.getPpdDataValidade().equals(proposta.getPpdDataValidade())) {
                                    logDelegate.addChangedField(Columns.PPD_DATA_VALIDADE, proposta.getPpdDataValidade(), ppdBean.getPpdDataValidade());
                                    ppdBean.setPpdDataValidade(proposta.getPpdDataValidade());
                                }

                                // Efetua a atualização e gravação de log das alterações
                                PropostaPagamentoDividaHome.update(ppdBean);
                                logDelegate.write();
                            }

                            ppdCodigoSalvo.add(ppdBean.getPpdCodigo());

                        } else {
                            // Executa a criação da proposta
                            PropostaPagamentoDivida ppdBean = PropostaPagamentoDividaHome.create(proposta.getAdeCodigo(), proposta.getCsaCodigo(), proposta.getUsuCodigo(),
                                    proposta.getStpCodigo(), proposta.getPpdNumero(), proposta.getPpdValorDivida(), proposta.getPpdValorParcela(),
                                    proposta.getPpdPrazo(), proposta.getPpdTaxaJuros(), proposta.getPpdDataCadastro(), proposta.getPpdDataValidade());

                            // Grava log da operação
                            LogDelegate logDelegate = new LogDelegate(responsavel, Log.PROPOSTA_PAGAMENTO_DIVIDA, Log.CREATE, Log.LOG_INFORMACAO);
                            logDelegate.setPropostaPagamentoDivida(ppdBean.getPpdCodigo());
                            logDelegate.setAutorizacaoDesconto(proposta.getAdeCodigo());
                            logDelegate.setUsuario(proposta.getUsuCodigo());
                            logDelegate.setConsignataria(proposta.getCsaCodigo());
                            logDelegate.setStatusProposta(proposta.getStpCodigo());
                            logDelegate.getUpdatedFields(proposta.getAtributos(), null);
                            logDelegate.write();

                            ppdCodigoSalvo.add(ppdBean.getPpdCodigo());
                        }
                    }

                    // Remove propostas que não estejam na lista "ppdCodigoSalvo",
                    // pois o usuário pode ter removido as informações
                    Collection<PropostaPagamentoDivida> lstPropostaRemocao = PropostaPagamentoDividaHome.findByAdeCsaAusentes(adeCodigo, responsavel.getCsaCodigo(), ppdCodigoSalvo);
                    if (lstPropostaRemocao != null && lstPropostaRemocao.size() > 0) {
                        for (PropostaPagamentoDivida ppdBean : lstPropostaRemocao) {
                            // Grava log de remoção
                            LogDelegate logDelegate = new LogDelegate(responsavel, Log.PROPOSTA_PAGAMENTO_DIVIDA, Log.DELETE, Log.LOG_INFORMACAO);
                            logDelegate.setPropostaPagamentoDivida(ppdBean.getPpdCodigo());
                            logDelegate.setAutorizacaoDesconto(ppdBean.getAutDesconto().getAdeCodigo());
                            logDelegate.setUsuario(ppdBean.getUsuario().getUsuCodigo());
                            logDelegate.setConsignataria(ppdBean.getConsignataria().getCsaCodigo());
                            logDelegate.write();

                            // Remove o registro
                            PropostaPagamentoDividaHome.remove(ppdBean);
                        }
                    }

                } else {
                    // Se não foram informadas propostas, ou não é financiamento de dívida ou é, porém
                    // a consignatária não tem convênio com financiamento. Verifica parâmetro para determinar
                    // validade do saldo, ou seja o prazo que o servidor tem para solicitar propostas.
                    List<TransferObject> servicos = parametroController.getRelacionamentoSvc(CodedValues.TNT_FINANCIAMENTO_DIVIDA, svcCodigo, null, responsavel);
                    if (servicos != null && !servicos.isEmpty()) {
                        // Se o serviço é origem de relacionamento de financiamento, busca parâmetro
                        // de serviço que determina a validade em dias para solicitação de propostas
                        ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                        if (!TextHelper.isNull(paramSvc.getTpsDiasSolicitarPropostasPgtoSaldo())) {
                            try {
                                Integer diasValidade = Integer.valueOf(paramSvc.getTpsDiasSolicitarPropostasPgtoSaldo());
                                dataValidade = obtemProximoDiaUtil(dataAtual, diasValidade);
                            } catch (NumberFormatException ex) {
                                LOG.error("Valor incorreto para o parâmetro de serviço '" + CodedValues.TPS_DIAS_SOLICITAR_PROPOSTAS_PGTO_SALDO + "' e serviço '" + svcCodigo + "'.");
                                throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel);
                            }

                            // Atualiza o registro do saldo devedor com a data de validade
                            SaldoDevedor sdv = SaldoDevedorHome.findByPrimaryKey(adeCodigo);
                            sdv.setSdvDataValidade(dataValidade);
                            SaldoDevedorHome.update(sdv);
                        }
                    }
                }

            } catch (FinanciamentoDividaControllerException ex) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                throw ex;
            } catch (ZetraException ex) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                throw new FinanciamentoDividaControllerException(ex);
            } catch (Exception ex) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Retorna o próximo dia útil à partir da data atual, contando
     * o "prazo" em dias úteis.
     * @param prazo
     * @return
     * @throws HQueryException
     */
    private Date obtemProximoDiaUtil(Date dataInicio, Integer prazo) throws HQueryException {
        ObtemProximoDiaUtilQuery proxDiaUtilQuery = new ObtemProximoDiaUtilQuery(dataInicio, prazo);
        List<Date> proxDiaUtil = proxDiaUtilQuery.executarLista();
        if (proxDiaUtil != null && proxDiaUtil.size() > 0) {
            return DateHelper.getEndOfDay(proxDiaUtil.get(0));
        } else {
            LOG.warn("Não foram encontrados dias úteis no calendário do sistema.");
            return dataInicio;
        }
    }

    /**
     * Realiza a gravação das propostas de pagamento de saldo para contratos
     * de terceiros.
     * @param adeCodigo
     * @param propostasPgtSaldo
     * @param responsavel
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public void informarPropostasPgtSdvTerceiros(String adeCodigo, List<PropostaPagamentoDividaTO> propostasPgtSaldo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                SaldoDevedorTransferObject saldoDevedorTO = saldoDevedorController.getSaldoDevedor(adeCodigo, responsavel);
                propostasPgtSaldo = validarPropostasPgtSaldoDevedor(saldoDevedorTO, propostasPgtSaldo, responsavel);
                atualizaPropostasPagamentoSaldo(adeCodigo, propostasPgtSaldo, responsavel);

                // Cria ocorrência de propostas aprovada pelo servidor
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.proposta.pagamento.informada.entidade", responsavel, responsavel.getNomeEntidade()), responsavel);
            }
        } catch (AutorizacaoControllerException | SaldoDevedorControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista as propostas para pagamento da dívida do saldo devedor.
     * @param adeCodigo
     * @param csaCodigo
     * @param stpCodigo
     * @param responsavel
     * @return
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public List<TransferObject> lstPropostaPagamentoDivida(String adeCodigo, String csaCodigo, String stpCodigo, boolean arquivado, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        try {
            ListaPropostaPagamentoDividaQuery query = new ListaPropostaPagamentoDividaQuery();
            query.adeCodigo = adeCodigo;
            query.csaCodigo = csaCodigo;
            query.stpCodigo = stpCodigo;
            query.arquivado = arquivado;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstPropostaPagamentoDivida(String adeCodigo, String csaCodigo, String stpCodigo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        return lstPropostaPagamentoDivida(adeCodigo, csaCodigo, stpCodigo, false, responsavel);
    }

    /**
     * Verifica se a proposta passada por parâmetro existe e está aprovada
     * @param ppdCodigo
     * @param responsavel
     * @return
     */
    @Override
    public boolean propostaAprovada(String ppdCodigo, AcessoSistema responsavel) {
        try {
            // Valida o status da proposta
            PropostaPagamentoDivida ppdBean = PropostaPagamentoDividaHome.findByPrimaryKey(ppdCodigo);
            StatusPropostaEnum statusAtual = StatusPropostaEnum.recuperaStatusProposta(ppdBean.getStatusProposta().getStpCodigo());
            return (statusAtual.equals(StatusPropostaEnum.APROVADA));
        } catch (FindException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
    }

    /**
     * Verifica se deve exibir ícone de ação para solicitação de propostas de
     * pagamento de saldo para outras entidades consignatárias.
     * @param adeCodigo
     * @param responsavel
     * @return
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public boolean exibeLinkSolicitacaoProposta(String adeCodigo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        try {
            boolean exibirLinkPropostas = false;
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel) &&
                    responsavel.isSer()) {
                VerificaAusenciaPropostasPgtDivQuery query = new VerificaAusenciaPropostasPgtDivQuery();
                query.adeCodigo = adeCodigo;
                return (query.executarContador() > 0);
            }
            return exibirLinkPropostas;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Determina se o contrato informado por parâmetro possui solicitação de propostas de
     * pagamento, solicitada pelo servidor a terceiros, pendente ou não.
     * @param adeCodigo   : Contrato a ser verificado se existe solicitação
     * @param pendente    : Determina se pesquisa apenas por solicitações pendentes
     * @param responsavel : Responsável pela operação
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public boolean temSolicitacaoProposta(String adeCodigo, boolean pendente, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        try {
            String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_PARCELAMENTO_DIVIDA.getCodigo()};
            String[] ssoCodigos = {StatusSolicitacaoEnum.PENDENTE.getCodigo()};
            if (!pendente) {
                ssoCodigos = new String[]{StatusSolicitacaoEnum.PENDENTE.getCodigo(), StatusSolicitacaoEnum.FINALIZADA.getCodigo()};
            }
            Collection<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            return (soaList != null && !soaList.isEmpty());
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Realiza a solicitação de propostas de pagamento da dívida do saldo
     * para as demais entidades consignatárias que tenham convênio com
     * serviço de financiamento de dívida.
     * @param adeCodigo
     * @param responsavel
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public void solicitarPropostaPagamento(String adeCodigo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                if (autorizacaoController.usuarioPodeModificarAdeCompra(adeCodigo, responsavel)) {
                    Date dataAtual = DateHelper.getSystemDatetime();

                    // Obtém os entity beans necessários
                    AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
                    SaldoDevedor sdvBean = SaldoDevedorHome.findByPrimaryKey(adeCodigo);

                    // Verifica se é permitido a solicitação de saldo deste contrato
                    String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo().trim();
                    if (!sadCodigo.equals(CodedValues.SAD_DEFERIDA) &&
                            !sadCodigo.equals(CodedValues.SAD_EMANDAMENTO)) {
                        throw new FinanciamentoDividaControllerException("mensagem.erro.proposta.nao.realizada.operacao.nao.permitida", responsavel);
                    }
                    if (!exibeLinkSolicitacaoProposta(adeCodigo, responsavel)) {
                        throw new FinanciamentoDividaControllerException("mensagem.erro.solicitacao.proposta.nao.realizada", responsavel);
                    }

                    // Verifica a validade do saldo
                    if (sdvBean.getSdvDataValidade() != null &&
                            dataAtual.compareTo(sdvBean.getSdvDataValidade()) > 0) {
                        throw new FinanciamentoDividaControllerException("mensagem.erro.proposta.nao.realizada.validade.saldo.devedor", responsavel);
                    }

                    VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
                    Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());
                    String svcCodigo = cnvBean.getServico().getSvcCodigo();

                    // Calcular data de validade da solicitação de propostas de acordo com o parâmetro de serviço
                    Date dataValidade = null;

                    // Verifica parâmetro de serviço para determinar a validade da solicitação,
                    // ou seja, o prazo que as entidades tem para informarem propostas
                    ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                    if (!TextHelper.isNull(paramSvc.getTpsDiasInfPropostasPgtoSaldoTercei())) {
                        try {
                            Integer diasValidade = Integer.valueOf(paramSvc.getTpsDiasInfPropostasPgtoSaldoTercei());
                            ObtemProximoDiaUtilQuery proxDiaUtilQuery = new ObtemProximoDiaUtilQuery(dataAtual, diasValidade);
                            List<Date> proxDiaUtil = proxDiaUtilQuery.executarLista();
                            if (proxDiaUtil != null && proxDiaUtil.size() > 0) {
                                dataValidade = DateHelper.getEndOfDay(proxDiaUtil.get(0));
                            } else {
                                LOG.warn("Não foram encontrados dias úteis no calendário do sistema.");
                            }
                        } catch (NumberFormatException ex) {
                            LOG.error("Valor incorreto para o parâmetro de serviço '" + CodedValues.TPS_DIAS_INF_PROPOSTAS_PGTO_SALDO_TERCEI + "' e serviço '" + svcCodigo + "'.");
                            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel);
                        }
                    }

                    // Cria ocorrência de solicitação de propostas efetuada pelo servidor
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.solicitacao.proposta.pagamento.parcelado", responsavel), responsavel);
                    // Cria registro de solicitação de propostas para a autorização
                    SolicitacaoAutorizacaoHome.create(adeCodigo, responsavel.getUsuCodigo(), TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_PARCELAMENTO_DIVIDA.getCodigo(), StatusSolicitacaoEnum.PENDENTE.getCodigo(), dataValidade);
                }
            }
        } catch (FinanciamentoDividaControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Aprova uma proposta de pagamento de saldo, alterando o status da
     * proposta para que fique apta a conclusão do processo.
     * @param adeCodigo
     * @param ppdCodigo
     * @param responsavel
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public void aprovarPropostaPagamento(String adeCodigo, String ppdCodigo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                if (autorizacaoController.usuarioPodeModificarAdeCompra(adeCodigo, responsavel)) {
                    // Obtém os entity beans necessários
                    PropostaPagamentoDivida ppdBean = PropostaPagamentoDividaHome.findByPrimaryKey(ppdCodigo);

                    // Valida o status da proposta
                    StatusPropostaEnum statusAtual = StatusPropostaEnum.recuperaStatusProposta(ppdBean.getStatusProposta().getStpCodigo());
                    if (statusAtual.equals(StatusPropostaEnum.APROVADA) || statusAtual.equals(StatusPropostaEnum.FINALIZADA)) {
                        throw new FinanciamentoDividaControllerException("mensagem.erro.situacao.proposta.nao.permite.alteracao", responsavel);
                    }

                    // Atualiza o status da proposta seleciona
                    StatusPropostaEnum statusNovo = StatusPropostaEnum.APROVADA;
                    alterarStatusProposta(ppdBean, statusNovo, responsavel);

                    // Rejeita as demais propostas pendentes de aprovação
                    rejeitarPropostasPendentes(adeCodigo, responsavel);

                    // Finaliza o status da solicitação de proposta a terceiros
                    finalizarSolicitacaoProposta(adeCodigo, responsavel);

                    // Cria ocorrência de propostas aprovada pelo servidor
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.proposta.pagamento.aprovada.prazo.parcela", responsavel, ppdBean.getPpdPrazo().toString(), NumberHelper.format(ppdBean.getPpdValorParcela().doubleValue(), NumberHelper.getLang())), responsavel);
                }
            }
        } catch (FinanciamentoDividaControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Altera o status da proposta "ppdBean" para o novo status e grava log
     * da alteração.
     * @param ppdBean
     * @param statusNovo
     * @param responsavel
     * @throws UpdateException
     * @throws LogControllerException
     */
    private void alterarStatusProposta(PropostaPagamentoDivida ppdBean, StatusPropostaEnum statusNovo, AcessoSistema responsavel) throws UpdateException, LogControllerException {
        // Atualiza o status da proposta seleciona
        ppdBean.setStatusProposta(new StatusProposta(statusNovo.getCodigo()));
        PropostaPagamentoDividaHome.update(ppdBean);

        // Grava log da operação
        LogDelegate logDelegate = new LogDelegate(responsavel, Log.PROPOSTA_PAGAMENTO_DIVIDA, Log.UPDATE, Log.LOG_INFORMACAO);
        logDelegate.setPropostaPagamentoDivida(ppdBean.getPpdCodigo());
        logDelegate.setAutorizacaoDesconto(ppdBean.getAutDesconto().getAdeCodigo());
        logDelegate.setStatusProposta(statusNovo.getCodigo());
        logDelegate.write();
    }

    /**
     * Rejeita (status = REJEITADA) as propostas de pagamento da consignação
     * representada pelo parâmetro "adeCodigo" que ainda estão pendentes de aprovação
     * (status = AGUARDANDO_APROVACAO).
     * @param adeCodigo
     * @param responsavel
     * @throws FindException
     * @throws UpdateException
     * @throws LogControllerException
     */
    private void rejeitarPropostasPendentes(String adeCodigo, AcessoSistema responsavel) throws FindException, UpdateException, LogControllerException {
        Collection<PropostaPagamentoDivida> lstPropostaPendente = PropostaPagamentoDividaHome.findPendentesByAde(adeCodigo);
        if (lstPropostaPendente != null && lstPropostaPendente.size() > 0) {
            StatusPropostaEnum statusNovo = StatusPropostaEnum.REJEITADA;
            for (PropostaPagamentoDivida ppdPendente : lstPropostaPendente) {
                // Rejeita a proposta
                ppdPendente.setStatusProposta(new StatusProposta(statusNovo.getCodigo()));
                PropostaPagamentoDividaHome.update(ppdPendente);

                // Grava log de remoção
                LogDelegate logDelegate = new LogDelegate(responsavel, Log.PROPOSTA_PAGAMENTO_DIVIDA, Log.UPDATE, Log.LOG_INFORMACAO);
                logDelegate.setPropostaPagamentoDivida(ppdPendente.getPpdCodigo());
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.setStatusProposta(statusNovo.getCodigo());
                logDelegate.write();
            }
        }
    }

    /**
     * Finaliza (status = FINALIZADA) as solicitações de propostas de pagamento feita
     * pelo servidor às demais entidades consignatárias.
     * @param adeCodigo
     * @param responsavel
     * @throws FindException
     * @throws UpdateException
     */
    private void finalizarSolicitacaoProposta(String adeCodigo, AcessoSistema responsavel) throws FindException, UpdateException {
        // Finalizar status da solicitação de proposta de pagamento
        Collection<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, new String[]{TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_PARCELAMENTO_DIVIDA.getCodigo()}, StatusSolicitacaoEnum.PENDENTE.getCodigo());
        if (soaList != null && !soaList.isEmpty()) {
            for (SolicitacaoAutorizacao solicitacaoAutorizacao : soaList) {
                solicitacaoAutorizacao.setStatusSolicitacao(new StatusSolicitacao(StatusSolicitacaoEnum.FINALIZADA.getCodigo()));
                SolicitacaoAutorizacaoHome.update(solicitacaoAutorizacao);
            }
        }
    }

    /**
     * Calcula a margem disponível para a operação de financiamento de dívida, seja compra ou
     * renegociação. A margem será acrescida do valor do contrato que está envolvido no
     * processo, até o limite permitido.
     * @param svcCodigo
     * @param rseCodigo
     * @param adeCodigo
     * @param compra
     * @param responsavel
     * @return
     * @throws ViewHelperException
     * @throws AutorizacaoControllerException
     * @throws ParametroControllerException
     */
    private BigDecimal obtemMargemFinanciamento(String svcCodigo, String rseCodigo, String adeCodigo, boolean compra, AcessoSistema responsavel) throws ViewHelperException, AutorizacaoControllerException, ParametroControllerException {
        // Parâmetros de serviços necessários
        ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        Short svcIncMargem = paramSvc.getTpsIncideMargem();

        List<String> adeCodigosRenegociacao = new ArrayList<>();
        adeCodigosRenegociacao.add(adeCodigo);

        // Busca a margem do servidor do serviço de financiamento
        MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, responsavel.getCsaCodigo(), svcCodigo, svcIncMargem, adeCodigosRenegociacao, responsavel);
        BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();

        // Busca os dados do contrato a ser financiado
        TransferObject ade = pesquisarConsignacaoController.findAutDesconto(adeCodigo, responsavel);
        Short adeIncMargem = (Short) ade.getAttribute(Columns.ADE_INC_MARGEM);
        BigDecimal adeVlr = (BigDecimal) ade.getAttribute(Columns.ADE_VLR);

        // Se o valor da margem pode ser usado para renegociação, então adiciona o valor do contrato à margem
        if (AutorizacaoHelper.valorMargemDisponivelRenegociacao(svcIncMargem, adeIncMargem, responsavel)) {
            rseMargemRest = rseMargemRest.add(AutorizacaoHelper.restringirValorDisponivelRenegociacao(adeVlr, svcCodigo, compra, responsavel));
        }

        return rseMargemRest;
    }

    /**
     * Calcula os prazos obrigatórios para as propostas, sendo estes os prazos: mínimo, médio e máximo.
     * O máximo é o maior prazo permitido no serviço. O mínimo é o menor prazo calculado pelo valor
     * da dívida com desconto, CET cadastrado no serviço e menor que a margem disponível para a operação.
     * O prazo médio é a média entre o mínimo e máximo. É feita uma simulação pelo valor da dívida,
     * sem restrição de prazo e depois os valores são comparados com a margem, e o menor prazo possível
     * é obtido.
     * @param svcCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param adeCodigo
     * @param compra
     * @param responsavel
     * @return
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public List<Integer> calcularPrazosObrigProposta(String svcCodigo, String rseCodigo, String orgCodigo, String adeCodigo, boolean compra, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        try {
            List<Integer> prazos = null;
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                ConsignatariaTransferObject csa = consignatariaController.findConsignataria(responsavel.getCsaCodigo(), responsavel);
                if (!csa.getCsaAtivo().equals(CodedValues.STS_ATIVO)) {
                    throw new FinanciamentoDividaControllerException("rotulo.consignataria.bloqueada", responsavel);
                }

                // Parâmetros de serviços necessários
                ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                Integer prazoMaximo = (!TextHelper.isNull(paramSvc.getTpsMaxPrazo()) ? Integer.valueOf(paramSvc.getTpsMaxPrazo()) : 120);

                // Obtém a margem disponível para financiamento (margem + soma dos contratos)
                BigDecimal rseMargemRest = obtemMargemFinanciamento(svcCodigo, rseCodigo, adeCodigo, compra, responsavel);

                // Busca os dados do saldo devedor
                SaldoDevedorTransferObject saldoDevedorTO = saldoDevedorController.getSaldoDevedor(adeCodigo, responsavel);
                BigDecimal sdvValorComDesconto = saldoDevedorTO.getSdvValorComDesconto();

                // Calcula o menor prazo possível, de acordo com a margem disponível para a operação e
                // o valor da dívida com desconto.
                Integer prazoMinimo = null;
                List<TransferObject> simulacao = simulacaoController.simularConsignacao(responsavel.getCsaCodigo(), svcCodigo, orgCodigo, rseCodigo, null, sdvValorComDesconto, (short) 0, null, true, true, CodedValues.PERIODICIDADE_FOLHA_MENSAL, responsavel);
                simulacao = simulacaoController.selecionarLinhasSimulacao(simulacao, rseCodigo, rseMargemRest, 1, false, false, responsavel);
                for (TransferObject resultadoPrazo : simulacao) {
                    if ((Boolean) resultadoPrazo.getAttribute("OK")) {
                        prazoMinimo = Integer.valueOf(resultadoPrazo.getAttribute(Columns.PRZ_VLR).toString());
                        break;
                    }
                }

                if (prazoMinimo == null) {
                    throw new FinanciamentoDividaControllerException("mensagem.erro.margem.insuficiente.calculo.prazo.minimo", responsavel);
                }

                // Calcula o prazo médio
                Integer prazoMedio = (prazoMaximo + prazoMinimo) / 2;

                prazos = new ArrayList<>();
                prazos.add(prazoMinimo);
                prazos.add(prazoMedio);
                prazos.add(prazoMaximo);
            }
            return prazos;
        } catch (FinanciamentoDividaControllerException ex) {
            throw ex;
        } catch (ZetraException ex) {
            throw new FinanciamentoDividaControllerException(ex);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Realiza a pesquisa de acompanhamento das propostas de financiamento de dívida
     * de cartão, seja da própria consignatária, quanto de terceiros.
     * @param criteriosPesquisa
     * @param responsavel
     * @return
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public List<TransferObject> acompanharFinanciamentoDivida(TransferObject criteriosPesquisa,  int offset, int count, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
            try {
                ListaAcompanhamentoFinancDividaQuery query = new ListaAcompanhamentoFinancDividaQuery();
                query.tipoFiltro = (String) criteriosPesquisa.getAttribute("filtro");
                query.periodoIni = (String) criteriosPesquisa.getAttribute("periodoIni");
                query.periodoFim = (String) criteriosPesquisa.getAttribute("periodoFim");
                query.csaCodigo = (String) criteriosPesquisa.getAttribute("CSA_CODIGO");
                query.rseMatricula = (String) criteriosPesquisa.getAttribute("RSE_MATRICULA");
                query.serCpf = (String) criteriosPesquisa.getAttribute("SER_CPF");
                query.adeNumero = (Long) criteriosPesquisa.getAttribute("ADE_NUMERO");

                if (count != -1) {
                    query.maxResults = count;
                    query.firstResult = offset;
                }

                return query.executarDTO();
            } catch (HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        return null;
    }

    /**
     * Realiza a contagem de registros da pesquisa de acompanhamento das propostas de
     * financiamento de dívida de cartão, seja da própria consignatária, quanto de terceiros.
     * @param criteriosPesquisa
     * @param responsavel
     * @return
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public int contarFinanciamentoDivida(TransferObject criteriosPesquisa,  AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
            try {
                ListaAcompanhamentoFinancDividaQuery query = new ListaAcompanhamentoFinancDividaQuery();
                query.count = true;
                query.tipoFiltro = (String) criteriosPesquisa.getAttribute("filtro");
                query.periodoIni = (String) criteriosPesquisa.getAttribute("periodoIni");
                query.periodoFim = (String) criteriosPesquisa.getAttribute("periodoFim");
                query.csaCodigo = (String) criteriosPesquisa.getAttribute("CSA_CODIGO");
                query.rseMatricula = (String) criteriosPesquisa.getAttribute("RSE_MATRICULA");
                query.serCpf = (String) criteriosPesquisa.getAttribute("SER_CPF");
                query.adeNumero = (Long) criteriosPesquisa.getAttribute("ADE_NUMERO");

                return query.executarContador();
            } catch (HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        return 0;
    }

    /**
     * Realiza a validação do processo de conclusão do financiamento de dívida, seja através de uma
     * renegociação ou compra de contrato. Verifica se a proposta foi informada, e se o status
     * da proposta aceita a operação. Valida ainda os valores da renegociação se são os mesmos da proposta.
     * @param renegociarParam
     * @param svcCodigo
     * @param responsavel
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public void validarConclusaoFinanciamento(RenegociarConsignacaoParametros renegociarParam, String svcCodigo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                List<TransferObject> servicos = parametroController.getRelacionamentoSvc(CodedValues.TNT_FINANCIAMENTO_DIVIDA, null, svcCodigo, responsavel);
                if (servicos != null && !servicos.isEmpty()) {
                    // Se o serviço da nova reserva é destino de um relacionamento de financiamento de dívida,
                    // então o processo de renegociação/compra só deve ser realizado mediante a informação
                    // de um código de proposta.
                    if (TextHelper.isNull(renegociarParam.getPpdCodigo())) {
                        throw new FinanciamentoDividaControllerException("mensagem.erro.proposta.pagamento.obrigatoria.operacao", responsavel);
                    } else {
                        // Somente um contrato de cartão pode ser renegociado/comprado por vez, verifica
                        // esta restrição e obtém o código deste contrato
                        if (renegociarParam.getAdeCodigosRenegociacao().size() != 1) {
                            throw new FinanciamentoDividaControllerException("mensagem.erro.somente.uma.consignacao.permitida", responsavel);
                        }

                        String ppdCodigo = renegociarParam.getPpdCodigo();
                        String adeCodigo = renegociarParam.getAdeCodigosRenegociacao().get(0);

                        // Se a proposta foi informada, verifica se ela está aprovada, ou se nenhuma das
                        // propostas da consignatária está aprovada, estando todas pendentes.
                        Collection<PropostaPagamentoDivida> lstPropostas = PropostaPagamentoDividaHome.findByAdeCsa(adeCodigo, responsavel.getCsaCodigo());
                        if (lstPropostas == null || lstPropostas.isEmpty()) {
                            throw new FinanciamentoDividaControllerException("mensagem.erro.nenhuma.proposta.encontrada", responsavel);
                        } else {
                            PropostaPagamentoDivida propostaEscolhida = null;
                            // Verifica se existem propostas aprovadas, ou aguardando aprovação
                            // OBS: Não pode incluir "break" no for para que todas as propostas sejam validadas
                            for (PropostaPagamentoDivida proposta : lstPropostas) {
                                StatusPropostaEnum status = StatusPropostaEnum.recuperaStatusProposta(proposta.getStatusProposta().getStpCodigo());
                                if (status.equals(StatusPropostaEnum.APROVADA)) {
                                    if (!proposta.getPpdCodigo().equals(ppdCodigo)) {
                                        // Se existe proposta aprovada e não foi a escolhida, retorna erro
                                        throw new FinanciamentoDividaControllerException("mensagem.erro.proposta.informada.diferente.proposta.aprovada", responsavel);
                                    }
                                }
                                if (proposta.getPpdCodigo().equals(ppdCodigo) &&
                                        (status.equals(StatusPropostaEnum.APROVADA) || status.equals(StatusPropostaEnum.AGUARDANDO_APROVACAO))) {
                                    propostaEscolhida = proposta;
                                }
                            }

                            // Se a proposta informada não foi localizada ou não está no status correto, retorna erro
                            if (propostaEscolhida == null) {
                                throw new FinanciamentoDividaControllerException("mensagem.erro.situacao.nao.permite.operacao", responsavel);
                            } else {
                                // Verifica se os valores passados para a renegociação são os mesmos da proposta
                                BigDecimal adeVlrLiquido = renegociarParam.getAdeVlrLiquido();
                                BigDecimal adeVlr = renegociarParam.getAdeVlr();
                                Integer adePrazo = renegociarParam.getAdePrazo();

                                if (adeVlrLiquido == null || !adeVlrLiquido.equals(propostaEscolhida.getPpdValorDivida()) ||
                                        adeVlr == null || adeVlr.compareTo(propostaEscolhida.getPpdValorParcela()) > 0 ||
                                        adePrazo == null || adePrazo.compareTo(propostaEscolhida.getPpdPrazo()) > 0) {
                                    throw new FinanciamentoDividaControllerException("mensagem.erro.valores.informados.maiores.proposta", responsavel);
                                }
                            }
                        }
                    }
                }
            }
        } catch (FindException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Realiza a conclusão da operação de financiamento de dívida de cartão, realizada
     * pela renegociação ou compra do contrato antigo.
     * @param adeCodigoNovo
     * @param adeCodigosRenegociacao
     * @param ppdCodigo
     * @param svcCodigo
     * @param compra
     * @param responsavel
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public void concluirFinanciamento(String adeCodigoNovo, List<String> adeCodigosRenegociacao, String ppdCodigo, String svcCodigo, boolean compra,
            AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                List<TransferObject> servicos = parametroController.getRelacionamentoSvc(CodedValues.TNT_FINANCIAMENTO_DIVIDA, null, svcCodigo, responsavel);
                if (servicos != null && !servicos.isEmpty()) {
                    // Se o serviço da nova reserva é destino de um relacionamento de financiamento de dívida,
                    // realiza os procedimentos de finalização do processo de renegociação/compra
                    if (!TextHelper.isNull(ppdCodigo) && adeCodigosRenegociacao.size() == 1) {
                        String adeCodigo = adeCodigosRenegociacao.get(0);

                        if (compra) {
                            // Obtém as informações do saldo devedor
                            SaldoDevedor sdv = SaldoDevedorHome.findByPrimaryKey(adeCodigo);

                            // Na operação de compra, alterar status da compra para aguard. pagamento, alterando as datas de inf. saldo
                            RelacionamentoAutorizacaoId radId = new RelacionamentoAutorizacaoId(adeCodigo, adeCodigoNovo, CodedValues.TNT_CONTROLE_COMPRA);
                            RelacionamentoAutorizacao rad = RelacionamentoAutorizacaoHome.findByPrimaryKey(radId);

                            // Data de informação do saldo = data cadastrada no saldo devedor
                            rad.setRadDataInfSaldo(sdv.getSdvDataMod());
                            // Data de referência para pagamento do saldo = data atual
                            rad.setRadDataRefPgtSaldo(DateHelper.getSystemDatetime());

                            // Se tem aprovação de saldo, seta as datas de aprovação
                            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                                rad.setRadDataRefAprSaldo(sdv.getSdvDataMod());
                                rad.setRadDataAprSaldo(DateHelper.getSystemDatetime());
                            }

                            // Status da compra = AGUARDANDO_PAG_SALDO
                            rad.setStatusCompra(new StatusCompra(StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo()));

                            // Atualiza o relacionamento
                            RelacionamentoAutorizacaoHome.update(rad);

                            // Finaliza o status da solicitação de proposta a terceiros
                            finalizarSolicitacaoProposta(adeCodigo, responsavel);
                        }

                        // Finalizar status da proposta escolhida
                        PropostaPagamentoDivida ppdBean = PropostaPagamentoDividaHome.findByPrimaryKey(ppdCodigo);
                        StatusPropostaEnum statusNovo = StatusPropostaEnum.FINALIZADA;
                        alterarStatusProposta(ppdBean, statusNovo, responsavel);

                        // Rejeita as demais propostas pendentes de aprovação
                        rejeitarPropostasPendentes(adeCodigo, responsavel);

                    } else {
                        throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel);
                    }
                }
            }
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException | ParametroControllerException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Utilizado por processo automático para validar os prazos de expiração das solicitações de propostas,
     * das propostas de pagamento, e outras entidades envolvidas no processo de financiamento de dívida.
     * @param responsavel
     * @throws FinanciamentoDividaControllerException
     */
    @Override
    public void processarPrazoExpiracaoFinancDivida(AcessoSistema responsavel) throws FinanciamentoDividaControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                // Finaliza as solicitações de proposta de pagamento parcelado que já estão com data de validade passada
                ListaSolicitacaoPropostaExpiradaQuery soaQuery = new ListaSolicitacaoPropostaExpiradaQuery();
                List<String> soaCodigos = soaQuery.executarLista();
                for (String soaCodigo : soaCodigos) {
                    SolicitacaoAutorizacao solicitacaoAutorizacao = SolicitacaoAutorizacaoHome.findByPrimaryKey(soaCodigo);
                    solicitacaoAutorizacao.setStatusSolicitacao(new StatusSolicitacao(StatusSolicitacaoEnum.FINALIZADA.getCodigo()));
                    SolicitacaoAutorizacaoHome.update(solicitacaoAutorizacao);
                }

                // Expira as propostas de pagamento parcelado que já estão com data de validade passada
                ListaPropostaPagamentoDividaExpiradaQuery ppdQuery = new ListaPropostaPagamentoDividaExpiradaQuery();
                List<String> ppdCodigos = ppdQuery.executarLista();
                for (String ppdCodigo : ppdCodigos) {
                    PropostaPagamentoDivida ppdBean = PropostaPagamentoDividaHome.findByPrimaryKey(ppdCodigo);
                    StatusPropostaEnum statusNovo = StatusPropostaEnum.EXPIRADA;
                    alterarStatusProposta(ppdBean, statusNovo, responsavel);
                }
            }
        } catch (ZetraException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new FinanciamentoDividaControllerException(ex);
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new FinanciamentoDividaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
