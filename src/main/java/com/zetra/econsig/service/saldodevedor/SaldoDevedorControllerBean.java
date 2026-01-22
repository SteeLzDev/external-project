package com.zetra.econsig.service.saldodevedor;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.assembler.SaldoDevedorDtoAssembler;
import com.zetra.econsig.dto.entidade.CoeficienteCorrecaoTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PropostaPagamentoDividaTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FinanciamentoDividaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.BancoHome;
import com.zetra.econsig.persistence.entity.Coeficiente;
import com.zetra.econsig.persistence.entity.CoeficienteDesconto;
import com.zetra.econsig.persistence.entity.CoeficienteDescontoHome;
import com.zetra.econsig.persistence.entity.CoeficienteHome;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodoHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.SaldoDevedor;
import com.zetra.econsig.persistence.entity.SaldoDevedorHome;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusSolicitacao;
import com.zetra.econsig.persistence.entity.TipoOcorrenciaHome;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.calendario.ObtemProximoDiaUtilQuery;
import com.zetra.econsig.persistence.query.compra.ObtemCsaRelacionamentoCompraQuery;
import com.zetra.econsig.persistence.query.correcao.ListaCoeficienteCorrecaoMaisProximoQuery;
import com.zetra.econsig.persistence.query.parametro.ObtemParamSvcAdeQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasStatusQuery;
import com.zetra.econsig.persistence.query.saldodevedor.ListaSolicitacaoSaldoDevedorPrazoQuery;
import com.zetra.econsig.persistence.query.saldodevedor.ListaSolicitacaoSaldoDevedorSemRespostaQuery;
import com.zetra.econsig.persistence.query.saldodevedor.ListaSolicitacaoSaldoPagoComAnexoNaoLiquidadoQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoNaturezaServicoQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemServidorProprietarioAdeQuery;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.financiamentodivida.FinanciamentoDividaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.rescisao.VerbaRescisoriaController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoCompraEnum;
import com.zetra.econsig.values.StatusCompraEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: SaldoDevedorControllerException</p>
 * <p>Description: Session Façade para cadastro de saldo devedor.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class SaldoDevedorControllerBean implements SaldoDevedorController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SaldoDevedorControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private CompraContratoController compraContratoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private FinanciamentoDividaController financiamentoDividaController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ComunicacaoController comunicacaoController;

    @Autowired
    private VerbaRescisoriaController verbaRescisoriaController;

    // Cadastro de Saldo Devedor
    @Override
    public SaldoDevedorTransferObject getSaldoDevedor(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return getSaldoDevedor(adeCodigo, false, responsavel);
    }

    @Override
    public SaldoDevedorTransferObject getSaldoDevedor(String adeCodigo, boolean arquivado, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            // Obtém o Home do entity bean e localiza o saldo devedor
            SaldoDevedor sdvBean = null;
            if (arquivado) {
                sdvBean = SaldoDevedorHome.findArquivadoByPrimaryKey(adeCodigo);
            } else {
                sdvBean = SaldoDevedorHome.findByPrimaryKey(adeCodigo);
            }
            return SaldoDevedorDtoAssembler.createDto(sdvBean);
        } catch (final FindException ex) {
            return null;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private boolean possuiInformacaoSaldoDevedor(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final Collection<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA, CodedValues.SAD_AGUARD_CONF);
            if ((adesCompra != null) && !adesCompra.isEmpty()) {
                final RelacionamentoAutorizacao radBean = adesCompra.iterator().next();
                return (radBean.getRadDataInfSaldo() != null);
            }
            return false;
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    private boolean isAguardandoPagamentoSaldo(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final Collection<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA, CodedValues.SAD_AGUARD_CONF);
            if ((adesCompra != null) && !adesCompra.isEmpty()) {
                final RelacionamentoAutorizacao radBean = adesCompra.iterator().next();
                return radBean.getStatusCompra().getStcCodigo().equals(StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo());
            }
            return false;
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    private boolean possuiAprovacaoSaldoDevedor(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final Collection<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA, CodedValues.SAD_AGUARD_CONF);
            if ((adesCompra != null) && !adesCompra.isEmpty()) {
                final RelacionamentoAutorizacao radBean = adesCompra.iterator().next();
                return (radBean.getRadDataAprSaldo() != null);
            }
            return false;
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public boolean existeSaldoDevedorPago(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final Collection<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA, CodedValues.SAD_AGUARD_CONF);
            if ((adesCompra != null) && !adesCompra.isEmpty()) {
                final RelacionamentoAutorizacao radBean = adesCompra.iterator().next();
                return (radBean.getRadDataPgtSaldo() != null);
            }
            return false;
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void createSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, CustomTransferObject comunicacao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        createSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, isCompra, comunicacao, false, responsavel);
    }

    @Override
    public void createSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, CustomTransferObject comunicacao, boolean importacao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            if (autorizacaoController.usuarioPodeModificarAde(saldoDevedorTO.getAdeCodigo(), responsavel)) {
                // Se não é cadastro de saldo devedor executado pelo sistema, então valida o saldo devedor
                if (!CodedValues.USU_CODIGO_SISTEMA.equals(responsavel.getUsuCodigo())) {
                    validarSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, responsavel);
                }

                // Data de Criação - Data atual
                final java.sql.Timestamp now = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());

                // Obtém o Home do entity bean e cria o registro de saldo devedor
                SaldoDevedorHome.create(saldoDevedorTO.getAdeCodigo(), saldoDevedorTO.getBcoCodigo(), saldoDevedorTO.getUsuCodigo(),
                        saldoDevedorTO.getSdvValor(), saldoDevedorTO.getSdvValorComDesconto(), saldoDevedorTO.getSdvAgencia(), saldoDevedorTO.getSdvConta(), now,
                        saldoDevedorTO.getSdvNomeFavorecido(), saldoDevedorTO.getSdvCnpj(), saldoDevedorTO.getSdvNumeroContrato(), saldoDevedorTO.getSdvLinkBoletoQuitacao());

                // Executa demais processos da atualização do saldo devedor
                processaAtualizacaoSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, isCompra, importacao, responsavel);

                // Grava log da operação
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.SALDO_DEVEDOR, Log.CREATE, Log.LOG_INFORMACAO);
                logDelegate.setAutorizacaoDesconto(saldoDevedorTO.getAdeCodigo());
                logDelegate.getUpdatedFields(saldoDevedorTO.getAtributos(), null);
                logDelegate.write();

                if ((comunicacao != null) && !TextHelper.isNull(comunicacao.getAttribute(Columns.ADE_CODIGO))) {
                    criarComunicacaoEmail(comunicacao, responsavel);
                }
            }
        } catch (final AutorizacaoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException(ex);
        } catch (final SaldoDevedorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException("mensagem.erro.informacoes.saldo.devedor.contrato.ja.cadastradas.ou.dados.bancarios.favorecido.invalidos", responsavel, ex);
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updateSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, CustomTransferObject comunicacao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        updateSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, isCompra, comunicacao, false, responsavel);
    }

    @Override
    public void updateSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, CustomTransferObject comunicacao, boolean importacao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            if (autorizacaoController.usuarioPodeModificarAde(saldoDevedorTO.getAdeCodigo(), responsavel)) {
                // Se o sistema exige o ciclo de vida fixo no processo de compra
                if (ParamSist.getBoolParamSist(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, responsavel)) {
                    // Se já foi realizada a aprovação do saldo pelo servidor, o mesmo não pode ser alterado no ciclo de vida fixo
                    if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel) &&
                            possuiAprovacaoSaldoDevedor(saldoDevedorTO.getAdeCodigo(), responsavel)) {
                        throw new SaldoDevedorControllerException("mensagem.erro.informacoes.saldo.devedor.consignacao.nao.podem.ser.alteradas.saldo.devedor.ja.aprovado", responsavel);
                    }
                    // Se já foi realizado o pagamento do saldo, o mesmo não pode ser alterado no ciclo de vida fixo
                    if (existeSaldoDevedorPago(saldoDevedorTO.getAdeCodigo(), responsavel)) {
                        throw new SaldoDevedorControllerException("mensagem.erro.informacoes.saldo.devedor.consignacao.nao.podem.ser.alteradas.pagamento.saldo.ja.registrado.sistema", responsavel);
                    }
                }

                // Se não é cadastro de saldo devedor executado pelo sistema, então valida o saldo devedor
                if (!CodedValues.USU_CODIGO_SISTEMA.equals(responsavel.getUsuCodigo()) || importacao) {
                    validarSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, responsavel);
                }

                // Data de Modificação - Data atual
                final java.sql.Timestamp now = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());

                // Cria o objeto de gravação de log
                final LogDelegate log = new LogDelegate(responsavel, Log.SALDO_DEVEDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(saldoDevedorTO.getAdeCodigo());
                if (!TextHelper.isNull(saldoDevedorTO.getUsuCodigo())) {
                    log.setUsuario(saldoDevedorTO.getUsuCodigo());
                }

                // Obtém o Home do entity bean e cria o registro de saldo devedor
                final SaldoDevedor sdvBean = SaldoDevedorHome.findByPrimaryKey(saldoDevedorTO.getAdeCodigo());

                // Seta data de modificação
                sdvBean.setSdvDataMod(now);

                // Compara a versão do cache com a passada por parâmetro
                final SaldoDevedorTransferObject sdvCache = SaldoDevedorDtoAssembler.createDto(sdvBean);
                final CustomTransferObject merge = log.getUpdatedFields(saldoDevedorTO.getAtributos(), sdvCache.getAtributos());

                if (merge.getAtributos().containsKey(Columns.SDV_BCO_CODIGO)) {
                    if (!TextHelper.isNull(merge.getAttribute(Columns.SDV_BCO_CODIGO))) {
                        sdvBean.setBanco(BancoHome.findByPrimaryKey((Short) merge.getAttribute(Columns.SDV_BCO_CODIGO)));
                    } else {
                        sdvBean.setBanco(null);
                    }
                }
                if (merge.getAtributos().containsKey(Columns.SDV_AGENCIA)) {
                    sdvBean.setSdvAgencia((String) merge.getAttribute(Columns.SDV_AGENCIA));
                }
                if (merge.getAtributos().containsKey(Columns.SDV_CONTA)) {
                    sdvBean.setSdvConta((String) merge.getAttribute(Columns.SDV_CONTA));
                }
                if (merge.getAtributos().containsKey(Columns.SDV_VALOR)) {
                    sdvBean.setSdvValor((BigDecimal) merge.getAttribute(Columns.SDV_VALOR));
                }
                if (merge.getAtributos().containsKey(Columns.SDV_VALOR_COM_DESCONTO)) {
                    sdvBean.setSdvValorComDesconto((BigDecimal) merge.getAttribute(Columns.SDV_VALOR_COM_DESCONTO));
                }
                if (merge.getAtributos().containsKey(Columns.SDV_USU_CODIGO)) {
                    sdvBean.setUsuario(UsuarioHome.findByPrimaryKey((String) merge.getAttribute(Columns.SDV_USU_CODIGO)));
                }
                if (merge.getAtributos().containsKey(Columns.SDV_NOME_FAVORECIDO)) {
                    sdvBean.setSdvNomeFavorecido((String) merge.getAttribute(Columns.SDV_NOME_FAVORECIDO));
                }
                if (merge.getAtributos().containsKey(Columns.SDV_CNPJ)) {
                    sdvBean.setSdvCnpj((String) merge.getAttribute(Columns.SDV_CNPJ));
                }
                if (merge.getAtributos().containsKey(Columns.SDV_NUMERO_CONTRATO)) {
                    sdvBean.setSdvNumeroContrato((String) merge.getAttribute(Columns.SDV_NUMERO_CONTRATO));
                }
                if (merge.getAtributos().containsKey(Columns.SDV_LINK_BOLETO_QUITACAO)) {
                    sdvBean.setSdvLinkBoletoQuitacao((String) merge.getAttribute(Columns.SDV_LINK_BOLETO_QUITACAO));
                }

                AbstractEntityHome.update(sdvBean);

                // Executa demais processos da atualização do saldo devedor
                processaAtualizacaoSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, isCompra, importacao, responsavel);

                // Grava log da operação
                log.write();

                if (!TextHelper.isNull(comunicacao.getAttribute(Columns.ADE_CODIGO))) {
                    criarComunicacaoEmail(comunicacao, responsavel);
                }
            }
        } catch (final AutorizacaoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException(ex);
        } catch (final SaldoDevedorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void processaAtualizacaoSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, boolean importacao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        final boolean enviaEmailSaldoDevedorParaExclusao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SALDO_DEVEDOR_EXCLUSAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        final boolean enviaEmailSaldoDevedorParaServidor = ParamSist.paramEquals(CodedValues.TPC_EMAIL_SERVIDOR_INFORMACAO_SALDO_CSA, CodedValues.TPC_SIM, responsavel);
        try {
            // Cadastra ocorrência indicando a criação do saldo devedor
            String ocaObs = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + NumberHelper.format(saldoDevedorTO.getSdvValor().doubleValue(), NumberHelper.getLang());
            if (saldoDevedorTO.getSdvValorComDesconto() != null) {
                ocaObs += " (" + ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.com.desconto.moeda.arg0", responsavel, NumberHelper.format(saldoDevedorTO.getSdvValorComDesconto().doubleValue(), NumberHelper.getLang())) + ")";
            }
            ocaObs += (!TextHelper.isNull(saldoDevedorTO.getObs()) ? ". " + saldoDevedorTO.getObs() : ".");

            if (CodedValues.USU_CODIGO_SISTEMA.equals(responsavel.getUsuCodigo()) && !importacao) {
                // Cadastro de saldo devedor calculado pelo próprio sistema
                autorizacaoController.criaOcorrenciaADE(saldoDevedorTO.getAdeCodigo(), CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.cadastro.saldo.devedor.arg0", responsavel, ocaObs), responsavel);
            } else {
                // Pesquisa o relacionamento de compra pela origem (contrato antigo) onde o novo está aguard. conf. (só deve ter 1)
                final Collection<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(saldoDevedorTO.getAdeCodigo(), CodedValues.TNT_CONTROLE_COMPRA, CodedValues.SAD_AGUARD_CONF);
                final boolean compra = isCompra && ((adesCompra != null) && (adesCompra.size() == 1));

                // Cadastro de saldo devedor após informação pela própria consignatária
                autorizacaoController.criaOcorrenciaADE(saldoDevedorTO.getAdeCodigo(), CodedValues.TOC_INFORMACAO_SALDO_DEVEDOR, ApplicationResourcesHelper.getMessage("mensagem.informacao.saldo.devedor.arg0", responsavel, ocaObs), responsavel);

                // Atualiza os dados dos múltiplos saldos, caso seja informado
                atualizaDadosSaldosDevedoresMultiplos(saldoDevedorTO.getAdeCodigo(), dadosSaldosDevedoresMultiplos, responsavel);

                // Atualiza os anexos de saldo devedor
                atualizaAnexosSaldoDevedor(saldoDevedorTO, compra, responsavel);

                // Atualiza as propostas de pagamento de saldo devedor
                financiamentoDividaController.atualizaPropostasPagamentoSaldo(saldoDevedorTO.getAdeCodigo(), propostasPgtSaldo, responsavel);

                if (compra) {
                    // Se existe relacionamento, então é saldo para compra: atualiza relacionamento de compra
                    compraContratoController.updateRelAutorizacaoCompra(saldoDevedorTO.getAdeCodigo(), OperacaoCompraEnum.INFORMAR_SALDO_DEVEDOR, responsavel);

                    if (ParamSist.paramEquals(CodedValues.TPC_SUSPENDE_DESC_CSA_BLOQ_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                        compraContratoController.reativarDescontoAposPendenciaCompra(saldoDevedorTO.getAdeCodigo(), false, responsavel);
                    }

                    // Manda email de notificação sobre o saldo devedor
                    EnviaEmailHelper.enviarEmailCompraContrato(EnviaEmailHelper.TIPO_CADASTRO_SALDO_DEVEDOR, saldoDevedorTO.getAdeCodigo(), saldoDevedorTO.getObs(), responsavel);
                } else {
                    // Envia email de notificação para Gestor/Órgão quando a solicitacao feita para exclusão de servidores
                    // Esta verificação deve ser feita antes a atualização do status da solicitação
                    if (enviaEmailSaldoDevedorParaExclusao) {
                        EnviaEmailHelper.enviarEmailSaldoDevedorExclusaoServidor(saldoDevedorTO, responsavel);
                    }

                    // Atualiza o status das solicitações de saldo pendentes
                    atualizaStatusSolicitacaoSaldoDevedor(saldoDevedorTO.getAdeCodigo(), StatusSolicitacaoEnum.FINALIZADA, responsavel);

                    // Manda email de notificação para o servidor com os anexos do saldo
                    EnviaEmailHelper.enviarEmailAnexosSaldoSolicServidor(saldoDevedorTO, responsavel);

                    // Caso o usuário não tenha recebido e-mail de anexo, envio e-mail para avisar da informação do saldo
                    if(enviaEmailSaldoDevedorParaServidor){
                        EnviaEmailHelper.enviarEmailSaldoSolicServidor(saldoDevedorTO, responsavel);
                    }

                }
            }
        } catch (AutorizacaoControllerException | FinanciamentoDividaControllerException | CompraContratoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException(ex);
        } catch (final FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeSaldoDevedor(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            // Obtém o Home do entity bean e cria o registro de saldo devedor
            final SaldoDevedor sdvBean = SaldoDevedorHome.findByPrimaryKey(adeCodigo);
            AbstractEntityHome.remove(sdvBean);

            // Grava log da operação
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.SALDO_DEVEDOR, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setAutorizacaoDesconto(adeCodigo);
            logDelegate.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (final FindException ex) {
            // Não dá erro, caso o saldo não seja encontrado
        } catch (final RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void informarPagamentoSaldoDevedor(String adeCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        informarPagamentoSaldoDevedor(adeCodigo, obs, null, null, null, responsavel);
    }

    // Informação de pagamento de saldo devedor
    @Override
    public void informarPagamentoSaldoDevedor(String adeCodigo, String obs, String idAnexo, String aadNome, String aadDescricao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            if (autorizacaoController.usuarioPodeModificarAdeCompra(adeCodigo, responsavel)) {
                // Se o sistema exige o ciclo de vida fixo no processo de compra
                if (ParamSist.getBoolParamSist(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, responsavel)) {
                    // Se não foi realizada a aprovação do saldo pelo servidor, o mesmo não pode ser pago
                    if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel) &&
                            !possuiAprovacaoSaldoDevedor(adeCodigo, responsavel)) {
                        throw new SaldoDevedorControllerException("mensagem.erro.saldo.devedor.nao.foi.aprovado", responsavel);
                    }
                    // Se o sistema exige o ciclo de vida fixo no processo de compra, impede
                    // que a informação de pagamento de saldo devedor seja feita antes do cadastro do saldo.
                    if (!possuiInformacaoSaldoDevedor(adeCodigo, responsavel)) {
                        throw new SaldoDevedorControllerException("mensagem.erro.saldo.devedor.nao.foi.cadastrado", responsavel);
                    }

                    // Se o sistema exige o ciclo de vida fixo no processo de compra, impede
                    // que a informação de pagamento de saldo devedor seja informada duas vezes.
                    if(!isAguardandoPagamentoSaldo(adeCodigo, responsavel)){
                        throw new SaldoDevedorControllerException("mensagem.erro.pagamento.saldo.devedor.ja.informado", responsavel);
                    }
                }

                // Atualiza relacionamento de compra
                compraContratoController.updateRelAutorizacaoCompra(adeCodigo, OperacaoCompraEnum.PAGAMENTO_SALDO_DEVEDOR, responsavel);

                // Cadastra ocorrência indicando o pagamento do saldo devedor
                final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.pagamento.saldo.devedor.arg0", responsavel, (!TextHelper.isNull(obs) ? ": " + obs : ""));
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_PAGAMENTO_SALDO_DEVEDOR, ocaObs, responsavel);

                if (responsavel.isCsaCor()) {
                    // Se é usuário da consignatária, verifica se a nova reserva pode ser pré-confirmada (ade_pode_confirmar=S)
                    // caso já não esteja, ou seja, foi incluída por um usuário sem permissão de confirmar reserva.
                    boolean preConfirma = true;
                    final List<TransferObject> paramCsa = parametroController.selectParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PRE_CONFIRMA_RESERVA_COMPRA_PGT_SALDO, responsavel);
                    if ((paramCsa != null) && (paramCsa.size() > 0)) {
                        final TransferObject cto = paramCsa.get(0);
                        if ((cto != null) && (cto.getAttribute(Columns.PCS_VLR) != null) && "N".equals(cto.getAttribute(Columns.PCS_VLR).toString())) {
                            preConfirma = false;
                        }
                    }
                    if (preConfirma) {
                        // Pesquisa o relacionamento de compra pela origem (contrato antigo) onde o novo está aguard. conf.
                        final Collection<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA, CodedValues.SAD_AGUARD_CONF);
                        if ((adesCompra != null) && (adesCompra.size() == 1)) {
                            // Se encontrou apenas um (pois não pode ocorrer de ter dois), busca o contrato para atualização
                            final RelacionamentoAutorizacao rad = adesCompra.iterator().next();
                            final AutDesconto adeDestino = AutDescontoHome.findByPrimaryKeyForUpdate(rad.getAdeCodigoDestino());
                            // Se AdePodeConfirmar <> "S", então verifica se o usuário atual possui permissão para
                            // confirmar reserva, antes de atualizar esta informação
                            if (((adeDestino.getAdePodeConfirmar() == null) || !"S".equals(adeDestino.getAdePodeConfirmar())) && responsavel.temPermissao(CodedValues.FUN_CONF_RESERVA)) {
                                adeDestino.setAdePodeConfirmar("S");
                                AbstractEntityHome.update(adeDestino);
                            }
                        }
                    }
                }

                /**
                 * Anexa arquivo junto da informação de pagamento de saldo.
                 * Valida a função de anexo de comprovante de saldo ou função para informar pagamento de saldo devedor.
                 */
                if (!TextHelper.isNull(idAnexo) && !TextHelper.isNull(aadNome) &&
                        (responsavel.temPermissao(CodedValues.FUN_ANEXAR_COMPROVANTE_PAG_SALDO) || responsavel.temPermissao(CodedValues.FUN_INFORMAR_PGT_SALDO_DEVEDOR))) {
                    final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);

                    // Verifica se é permitido a solicitação de saldo deste contrato
                    final String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo().trim();

                    if (!CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo)) {
                        throw new SaldoDevedorControllerException("mensagem.informar.pagamento.saldo.devedor.anexo.status.invalido", responsavel);
                    }

                    final Date aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(adeBean.getVerbaConvenio().getConvenio().getOrgao().getOrgCodigo(), responsavel);

                    final String[] nomesAnexos = aadNome.split(";");
                    for (final String nomeAnexo : nomesAnexos) {
                        // Anexo
                        final File anexo = UploadHelper.moverArquivoAnexoTemporario(nomeAnexo, adeCodigo, idAnexo, responsavel);
                        if ((anexo != null) && anexo.exists()) {
                            aadDescricao = (!TextHelper.isNull(aadDescricao) && (aadDescricao.length() <= 255)) ? aadDescricao : anexo.getName();
                            editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), aadDescricao, new java.sql.Date(aadPeriodo.getTime()), TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_COMPROVANTE_PAGAMENTO, responsavel);
                        } else {
                            throw new AutorizacaoControllerException("mensagem.informar.pagamento.saldo.devedor.anexo.falha.upload", responsavel);
                        }
                    }
                }

                // Manda email de notificação sobre o saldo devedor
                EnviaEmailHelper.enviarEmailCompraContrato(EnviaEmailHelper.TIPO_INF_PGT_SALDO_DEVEDOR, adeCodigo, obs, responsavel);
                // Grava log da operação
                final LogDelegate log = new LogDelegate(responsavel, Log.SALDO_DEVEDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.pagamento.saldo.devedor.arg0", responsavel, ""));
                log.write();
            }
        } catch (final AutorizacaoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException(ex);
        } catch (final SaldoDevedorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Remoção das ocorrências de informação de pagamento de saldo devedor
    @Override
    public void removePagamentoSaldoDevedor(String adeCodigo) throws SaldoDevedorControllerException {
        try {
            final List<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, new String[] {CodedValues.TOC_PAGAMENTO_SALDO_DEVEDOR, CodedValues.TOC_PAGAMENTO_REJEITADO_SALDO_DEVEDOR});
            if ((ocorrencias != null) && (ocorrencias.size() > 0)) {
                final Iterator<OcorrenciaAutorizacao> it = ocorrencias.iterator();
                while (it.hasNext()) {
                    AbstractEntityHome.remove(it.next());
                }
            }
        } catch (final FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } catch (final RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erro.interno.nao.possivel.remover.informacao.pagamento.saldo.ja.existente", (AcessoSistema) null, ex);
        }
    }

    /**
     * Verifica se existe solicitação de saldo dentro do período configurado no parâmetro
     * de serviço TPS_PRAZO_ENTRE_SOLICITACOES_SDO_DEVEDOR.
     * @param adeCodigo    Código da ADE a ser verificada.
     * @param tisCodigo Indica o tipo da solicitação de saldo devedor.
     * @throws SaldoDevedorControllerException
     */
    private void verificarPrazoSolicitacaoSaldoDevedor(String adeCodigo, String tisCodigo) throws SaldoDevedorControllerException {
        try {
            final boolean usaDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_ENTRE_SOLICIT_SALDO_DEV, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            final ListaSolicitacaoSaldoDevedorPrazoQuery query = new ListaSolicitacaoSaldoDevedorPrazoQuery(adeCodigo, tisCodigo);
            final List<TransferObject> solicitacoes = query.executarDTO();

            if ((solicitacoes != null) && !solicitacoes.isEmpty()) {
                final TransferObject to = solicitacoes.iterator().next();
                final Date soaData = (Date) to.getAttribute(Columns.SOA_DATA);
                final Integer pseVlr = (Integer) to.getAttribute(Columns.PSE_VLR);

                String data = "";
                if (usaDiasUteis) {
                    final ObtemProximoDiaUtilQuery proxDiaUtilQuery = new ObtemProximoDiaUtilQuery(soaData, pseVlr);
                    final List<TransferObject> proxDiaUtil = proxDiaUtilQuery.executarDTO();
                    if ((proxDiaUtil == null) || proxDiaUtil.isEmpty()) {
                        throw new SaldoDevedorControllerException("mensagem.erro.calendario.nenhum.item.encontrado", (AcessoSistema) null);
                    }
                    final TransferObject diaUtil = proxDiaUtil.iterator().next();
                    data = DateHelper.toDateString((Date) diaUtil.getAttribute(Columns.CAL_DATA));
                } else {
                    data = DateHelper.toDateString(DateHelper.addDays(soaData, pseVlr));
                }

                throw new SaldoDevedorControllerException("mensagem.erro.ja.existe.solicitacao.saldo.devedor.contrato.proxima.solicitacao.a.partir.arg0", (AcessoSistema) null, data);
            }
        } catch (final HQueryException ex) {
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Verifica se na solicitação de saldo devedor pelo servidor é obrigatório que este
     * possua e-mail cadastrado.
     * @param adeCodigo
     * @param responsavel
     * @throws SaldoDevedorControllerException
     */
    private void verificarEmailSerSolicitacaoSaldoDevedor(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            // Se exige anexos no cadastro de saldo para o servidor e
            // os anexos são entregues apenas por e-mail, não deixa solicitar se o servidor não possuir e-mail
            if ((ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel) ||
                    ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) &&
                    ParamSist.paramEquals(CodedValues.TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR, CodedValues.ANEXO_SALDO_DEVEDOR_SERVIDOR_ENVIA_EMAIL, responsavel)) {
                final ObtemServidorProprietarioAdeQuery query = new ObtemServidorProprietarioAdeQuery();
                query.adeCodigo = adeCodigo;
                final List<TransferObject> servidores = query.executarDTO();
                if ((servidores != null) && (servidores.size() == 1)) {
                    final TransferObject servidor = servidores.iterator().next();
                    final String serEmail = (String) servidor.getAttribute(Columns.SER_EMAIL);
                    if (TextHelper.isNull(serEmail)) {
                        throw new SaldoDevedorControllerException("mensagem.solicitacao.saldo.devedor.erro.email", responsavel);
                    }
                } else {
                    throw new SaldoDevedorControllerException("mensagem.erro.obter.dados.envio.email.solicitacao.saldo", responsavel);
                }
            }
        } catch (final HQueryException ex) {
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se o saldo devedor só pode ser solicitado por servidores beneficiários, de acordo
     * com o parâmetro de serviço TPS_PERMITE_SOLICITAR_SALDO_BENEFICIARIO.
     * @param svcCodigo
     * @param rseCodigo
     * @param responsavel
     * @throws SaldoDevedorControllerException
     */
    private void verificarServidorBeneficiarioSaldoDevedor(String svcCodigo, String rseCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
            try {
                // Busca parâmetro de serviço que determina se o saldo só pode ser solicitado por beneficiário
                final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                if (paramSvc.isTpsPermiteSolicitarSaldoBeneficiario()) {
                    // Se só servidores beneficiários podem solicitar o saldo, verifica se o registro
                    // servidor passado por parâmetro é beneficiário
                    final RegistroServidorTO rse = servidorController.findRegistroServidor(rseCodigo, responsavel);
                    if ((rse.getRseBeneficiarioFinanDvCart() == null) || !"S".equals(rse.getRseBeneficiarioFinanDvCart())) {
                        throw new SaldoDevedorControllerException("mensagem.solicitacao.saldo.devedor.erro.beneficiario", responsavel);
                    }
                }
            } catch (ParametroControllerException | ServidorControllerException ex) {
                throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Solicitação de saldo devedor pelo Gestor, Órgão ou Suporte, para exclusão do servidor.
     * Solicita o saldo devedor para todas as consignatárias que possuem contratos ativos de natureza empréstimo com o servidor.
     * @param rseCodigo
     * @param obs
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public String solicitarSaldoDevedorExclusaoServidor(String rseCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            String resultado = "";

            // Verifica se é permitido a solicitação de saldo deste servidor
            final RegistroServidorTO rse = servidorController.findRegistroServidor(rseCodigo, responsavel);
            if (!CodedValues.SRS_BLOQUEADOS.contains(rse.getSrsCodigo()) && !CodedValues.SRS_EXCLUIDO.equals(rse.getSrsCodigo()) && !CodedValues.SRS_ATIVO.equals(rse.getSrsCodigo())) {
                throw new SaldoDevedorControllerException("mensagem.erro.saldo.devedor.exclusao.servidor.status", responsavel);
            }

            // lista de status de autorização que permitem solicitação de saldo
            final List<String> sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_ESTOQUE);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
            sadCodigos.add(CodedValues.SAD_EMCARENCIA);

            // servicos da natureza EMPRÉSTIMO
            final ListaServicoNaturezaServicoQuery lstServicos = new ListaServicoNaturezaServicoQuery();
            lstServicos.nseCodigo = CodedValues.NSE_EMPRESTIMO;
            final List<TransferObject> lstSvcs = lstServicos.executarDTO();

            final List<String> svcCodigos = new ArrayList<>();
            for (final TransferObject servico : lstSvcs) {
                svcCodigos.add((String) servico.getAttribute(Columns.SVC_CODIGO));
            }

            // lista ativos de natureza empréstimo do servidor
            final List<TransferObject> ades = pesquisarConsignacaoController.pesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), rseCodigo, null, null, sadCodigos, svcCodigos, null, responsavel);

            // Solicita o saldo devedor para todas as consignatárias que possuem contratos ativos de natureza empréstimo com o servidor
            if ((ades != null) && !ades.isEmpty()) {
                for (final TransferObject ade : ades) {
                    resultado = solicitarSaldoDevedor(ade.getAttribute(Columns.ADE_CODIGO).toString(), obs, true, false, true, null, 0, responsavel);
                }
            } else {
                throw new SaldoDevedorControllerException("mensagem.erro.solicitacao.saldo.devedor.exclusao.servidor.nao.realizada", responsavel);
            }
            return resultado;
        } catch (final SaldoDevedorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Solicitação de saldo devedor pelo servidor. Se o parâmetro de serviço
     * for igual a CADASTRA_E_CALCULA_SALDO_DEVEDOR, então deve ver o parâmetro "solicitar"
     * para saber se deve calcular o saldo ou solicitar à consignatária.
     * @param adeCodigo
     * @param obs
     * @param solicitar
     * @param isLiquidacao
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public String solicitarSaldoDevedor(String adeCodigo, String obs, boolean solicitar, boolean isLiquidacao, int qtdParcelas, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return solicitarSaldoDevedor(adeCodigo, obs, solicitar, isLiquidacao, false, null, qtdParcelas, false, responsavel);
    }

    @Override
    public String solicitarSaldoDevedor(String adeCodigo, String obs, boolean solicitar, boolean isLiquidacao, boolean isExclusao, Date soaDataValidade, int qtdParcelas, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return solicitarSaldoDevedor(adeCodigo, obs, solicitar, isLiquidacao, isExclusao, soaDataValidade, qtdParcelas, false, responsavel);
    }


    private String solicitarSaldoDevedor(String adeCodigo, String obs, boolean solicitar, boolean isLiquidacao, boolean isExclusao, Date soaDataValidade, int qtdParcelas, boolean isRescisao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            if (autorizacaoController.usuarioPodeModificarAdeCompra(adeCodigo, responsavel)) {

                // Obtém os entity beans necessários
                final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);

                // Verifica se é permitido a solicitação de saldo deste contrato
                final String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo().trim();
                if (!CodedValues.SAD_DEFERIDA.equals(sadCodigo) &&
                        !CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) &&
                        !CodedValues.SAD_ESTOQUE.equals(sadCodigo) &&
                        !CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) &&
                        !CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) &&
                        !CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) {
                    throw new SaldoDevedorControllerException("mensagem.erro.solicitacao.saldo.devedor.nao.realizada.situacao.consignacao.nao.permite.operacao", responsavel);
                }

                final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
                final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());

                final String svcCodigo = cnvBean.getServico().getSvcCodigo();
                final String csaCodigo = cnvBean.getConsignataria().getCsaCodigo();
                final String orgCodigo = cnvBean.getOrgao().getOrgCodigo();
                final String rseCodigo = adeBean.getRegistroServidor().getRseCodigo();

                // DESENV-19379 Priorizar parametro de servico de consignataria 126 TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR
                // e caso nao exista, ai sim continuar como o fluxo anterios de buscar o parametro de serviço de consignatnte
                final List<String> tpsCodigo = new ArrayList<>();
                tpsCodigo.add(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR);
                final List<TransferObject> permiteCadastroSaldoDevedorCsaList = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigo, false, responsavel);
                final TransferObject objectParam = permiteCadastroSaldoDevedorCsaList.isEmpty() ? null : permiteCadastroSaldoDevedorCsaList.get(0);
                final String permiteCadastroSaldoDevedorCsa = objectParam != null ? (String) objectParam.getAttribute(Columns.PSC_VLR) : null;
                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                final String permiteCadastroSaldoDevedor = permiteCadastroSaldoDevedorCsa != null ? permiteCadastroSaldoDevedorCsa : paramSvcCse.getTpsPermiteCadastrarSaldoDevedor();
                boolean saldoDevedorAutRescisao = false;

                if(isRescisao) {
                    //Caso seja rescisão, porém a consignatária não está configurada com o parâmetro de criar saldo devedor automaticamente, deve-se respeitar a configuração do parâmetro de serviço
                    // para saldo devedor.
                    final String saldoDevedorAutRescisaoCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INCL_SALDO_DEVEDOR_AUT_MODULO_RESCISAO, responsavel);
                    saldoDevedorAutRescisao = !TextHelper.isNull(saldoDevedorAutRescisaoCsa) && CodedValues.TPA_SIM.equals(saldoDevedorAutRescisaoCsa);
                }

                if (!CodedValues.NAO_POSSUI_CADASTRO_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor) || saldoDevedorAutRescisao) {
                    // Determina se o saldo deve ser calculado ou solicitado, de acordo com o parâmetro de serviço TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR
                    if (CodedValues.SISTEMA_CALCULA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor) || saldoDevedorAutRescisao) {
                        // Se o parâmetro diz que o sistema calcula o saldo ou é rescisão e o parâmetro de consignatária inclui automaticamente, então não deve solicitar
                        solicitar = false;
                    } else if (CodedValues.USUARIO_CADASTRA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor)) {
                        // Se o parâmetro diz que o usuário cadastra, então é solicitação de saldo
                        solicitar = true;
                    } else if (CodedValues.CADASTRA_E_CALCULA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor)) {
                        // Se o parâmetro diz que o usuário cadastra e o sistema calcula, então
                        // o parâmetro já terá a operação selecionada pelo usuário
                    }

                    if (solicitar) {
                        // define o tipo da ocorrência de sado devedor
                        String tocCodigo = "";
                        // define o tipo de solicitação de saldo devedor
                        String tisCodigo = "";
                        if (isLiquidacao) {
                            tocCodigo = CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO;
                            tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo();
                        } else if (isExclusao) {
                            tocCodigo = CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO;
                            tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO.getCodigo();
                        } else {
                            tocCodigo = CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR;
                            tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo();
                        }

                        // Verifica se já pode solicitar novo saldo, de acordo com o parâmetro de dias entre solicitações
                        verificarPrazoSolicitacaoSaldoDevedor(adeCodigo, tisCodigo);

                        // Verifica o modo de entrega do saldo, se é por e-mail ou exibido no sistema.
                        verificarEmailSerSolicitacaoSaldoDevedor(adeCodigo, responsavel);

                        // Verifica se o saldo só pode ser solicitado por servidor beneficiário
                        verificarServidorBeneficiarioSaldoDevedor(svcCodigo, rseCodigo, responsavel);

                        // Cria ocorrência de solicitação de saldo devedor efetuada pelo servidor
                        autorizacaoController.criaOcorrenciaADE(adeCodigo, tocCodigo, obs, responsavel);

                        // Cria registro de solicitação de saldo para a autorização
                        SolicitacaoAutorizacaoHome.create(adeCodigo, responsavel.getUsuCodigo(), tisCodigo, StatusSolicitacaoEnum.PENDENTE.getCodigo(), soaDataValidade);
                    } else {
                        // Se é para cálculo de saldo pelo próprio sistema, registra apenas uma
                        // ocorrência de informação para não interferir nos processos de pendências
                        // por solicitação de saldo.
                        autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, obs, responsavel);
                    }

                    // Remove o saldo devedor antigo, caso exista
                    removeSaldoDevedor(adeCodigo, responsavel);

                    // Retorno do método de solicitação de saldo
                    String resultado = null;

                    if (!solicitar) {
                        // Se o sistema calcula o saldo devedor, então realiza o cálculo do saldo
                        final BigDecimal sdvValor = calcularSaldoDevedor(adeBean, svcCodigo, csaCodigo, orgCodigo, false, qtdParcelas, responsavel);
                        final int[] prazos = getPrazoCalculoSaldo(adeBean, svcCodigo, orgCodigo, qtdParcelas, responsavel);
                        final int prazo = prazos[0];
                        final String msgAdicionalCalculo = ApplicationResourcesHelper.getMessage("mensagem.adicional.calculo.saldo.devedor", responsavel);
                        final String compl = ApplicationResourcesHelper.getMessage("mensagem.informacao.prestacoes.arg0.arg1.aberto.simulacao.valida.para.dia.arg2.arg3", responsavel,
                                String.valueOf(prazo),
                                (prazos[1] > 0 ? " (+" + prazos[1] + ")" : ""),
                                DateHelper.toDateString(DateHelper.getSystemDatetime()),
                                !"".equals(msgAdicionalCalculo) ? "<BR>" + msgAdicionalCalculo : ""
                                );

                        // Cadastra o valor do saldo devedor
                        final SaldoDevedorTransferObject sdvTO = new SaldoDevedorTransferObject();
                        sdvTO.setAdeCodigo(adeCodigo);
                        sdvTO.setUsuCodigo(CodedValues.USU_CODIGO_SISTEMA);
                        sdvTO.setSdvValor(sdvValor);
                        sdvTO.setObs(compl);
                        setInfBancariasSaldoDevedor(sdvTO, svcCodigo, csaCodigo, responsavel);
                        createSaldoDevedor(sdvTO, null, null, false, null, AcessoSistema.getAcessoUsuarioSistema());

                        final boolean criaAutoContratoRescisaoAposSdv = ParamSist.paramEquals(CodedValues.TPC_CRIA_AUTO_CONTRATO_RESCISAO_APOS_INFO_SALDO_DEVEDOR, CodedValues.TPC_SIM, responsavel);

                        if(criaAutoContratoRescisaoAposSdv) {
                         // Confirmar retenção de verba rescisória
                            verbaRescisoriaController.confirmarVerbaRescisoria(sdvValor, adeCodigo, responsavel);
                        }

                        // Retorna mensagem de sucesso para o usuário
                        final String msgAdicionalSimulacao = ApplicationResourcesHelper.getMessage("mensagem.adicional.simulacao.saldo.devedor", responsavel);
                        resultado = ApplicationResourcesHelper.getMessage("mensagem.informacao.saldo.devedor.atualizado.sucesso.arg0", responsavel, msgAdicionalSimulacao);
                    } else {
                        // Envia e-mail para a consignatária com a solicitação do saldo devedor
                        EnviaEmailHelper.enviarEmailSolicitacaoSaldo(adeCodigo, isLiquidacao, isRescisao ? soaDataValidade : null, responsavel);

                        // Se o saldo devedor é cadastrado, então retorna mensagem para
                        // o usuário dizendo que a requisição foi enviada
                        if (isLiquidacao) {
                            final String msgAdicional = ApplicationResourcesHelper.getMessage("mensagem.adicional.solicitacao.saldo.devedor", responsavel);
                            resultado = ApplicationResourcesHelper.getMessage("mensagem.informacao.solicitacao.saldo.devedor.enviada.consignataria.arg0", responsavel, msgAdicional);
                        } else {
                            resultado = ApplicationResourcesHelper.getMessage("mensagem.informacao.solicitacao.saldo.devedor.enviada.consignataria.arg0", responsavel, "");
                        }
                    }

                    // Grava log da operação
                    final LogDelegate log = new LogDelegate(responsavel, Log.SALDO_DEVEDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(adeCodigo);
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.solicitacao.saldo.devedor.uppercase.arg0", responsavel, obs));
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.resultado.uppercase.arg0", responsavel, resultado));
                    log.write();

                    return resultado;
                }
            }

            // Saldo devedor não é cadastrado para este contrato
            throw new SaldoDevedorControllerException("mensagem.erro.servico.desta.consignacao.nao.permite.cadastro.saldo.devedor", responsavel);

        } catch (final SaldoDevedorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public BigDecimal calcularSaldoDevedor(String adeCodigo, boolean usaTaxaInformada, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
            final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
            final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());

            final String svcCodigo = cnvBean.getServico().getSvcCodigo();
            final String csaCodigo = cnvBean.getConsignataria().getCsaCodigo();
            final String orgCodigo = cnvBean.getOrgao().getOrgCodigo();

            return calcularSaldoDevedor(adeBean, svcCodigo, csaCodigo, orgCodigo, usaTaxaInformada, responsavel);
        } catch (final FindException ex) {
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Calcula o saldo devedor de um contrato, identificado pelo parâmetro "adeBean".
     * Verifica as parâmetrizações para considerar as parcelas vencidas ou apenas vincendas;
     * para corrigir a taxa de juros pelo SPREAD bancário; para determinar se usa Taxa de juros,
     * CET ou Coeficientes; etc.
     * @param adeBean
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param usaTaxaInformada
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public BigDecimal calcularSaldoDevedor(AutDesconto adeBean, String svcCodigo, String csaCodigo, String orgCodigo, boolean usaTaxaInformada, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return calcularSaldoDevedor(adeBean, svcCodigo, csaCodigo, orgCodigo, usaTaxaInformada, 0, responsavel);
    }

    private BigDecimal calcularSaldoDevedor(AutDesconto adeBean, String svcCodigo, String csaCodigo, String orgCodigo, boolean usaTaxaInformada, int qtdParcelas, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final String adeCodigo = adeBean.getAdeCodigo();

            final BigDecimal vlrParcela = adeBean.getAdeVlr();
            final short adePrazo = adeBean.getAdePrazo().shortValue();
            final Date adeData = adeBean.getAdeData();

            // A taxa de juros será calculada ou será obtida através do cadastro da CSA
            BigDecimal taxaJuros = null;

            // Parâmetro de sistema que indica se a simulação é por taxa de juros ou por coeficientes
            final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
            // Parâmetro de sistema que indica que o sistema utiliza CET ao invés de Juros
            final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

            // Verifica se apesar do sistema estar habilitado taxa de juros, o contrato atual foi incluído
            // quando o sistema estava configurado com cadastro de coeficientes
            boolean contratoIncluidoComCoef = false;
            if (simulacaoPorTaxaJuros) {
                // Parâmetro de sistema que indica quando foi alterado de Coeficientes para Taxa/CET
                Object dataMigracaoCftTaxa = ParamSist.getInstance().getParam(CodedValues.TPC_DATA_ALTERACAO_SISTEMA_CFT_PARA_TAXA, responsavel);
                if (!TextHelper.isNull(dataMigracaoCftTaxa)) {
                    try {
                        dataMigracaoCftTaxa = DateHelper.parse(dataMigracaoCftTaxa.toString(), "yyyy-MM-dd");
                        if (adeBean.getAdeData().compareTo((Date) dataMigracaoCftTaxa) < 0) {
                            // Se o contrato foi feito antes da alteração de Coeficiente para Taxas/CET, então assume
                            // que o cálculo deve ser feito com coeficientes.
                            contratoIncluidoComCoef = true;
                        }
                    } catch (final ParseException ex) {
                        throw new SaldoDevedorControllerException("mensagem.erro.interno.parametro.sistema.arg0.contem.valor.incorreto", responsavel, CodedValues.TPC_DATA_ALTERACAO_SISTEMA_CFT_PARA_TAXA);
                    }
                }
            }

            CoeficienteDesconto cdeBean = null;
            final boolean criaAutoContratoRescisaoAposSdv = ParamSist.paramEquals(CodedValues.TPC_CRIA_AUTO_CONTRATO_RESCISAO_APOS_INFO_SALDO_DEVEDOR, CodedValues.TPC_SIM, responsavel);
            BigDecimal cftVlrRef = null;

            try {
                // Tenta localizar o registro de coeficiente desconto para este contrato
                cdeBean = CoeficienteDescontoHome.findByAdeCodigo(adeCodigo);

                if (simulacaoPorTaxaJuros && !contratoIncluidoComCoef) {
                    // Se simula por taxa de juros, utiliza a taxa de juros cadastrada
                    // no ato da simulação do contrato
                    final Coeficiente cftBean = CoeficienteHome.findByPrimaryKey(cdeBean.getCoeficiente().getCftCodigo());
                    taxaJuros = cftBean.getCftVlr();
                    cftVlrRef = cftBean.getCftVlrRef();
                } else {
                    // Se simula por coeficientes, calcula a taxa de juros baseada
                    // no valor liberado, nas taxas cadastradas, e no valor da parcela
                    final List<String> tpsCodigos = new ArrayList<>();
                    tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
                    tpsCodigos.add(CodedValues.TPS_OP_FINANCIADA);

                    final ObtemParamSvcAdeQuery query = new ObtemParamSvcAdeQuery();
                    query.adeCodigo = adeCodigo;
                    query.tpsCodigos = tpsCodigos;
                    final Map<String, BigDecimal> taxas = query.executarMapa();

                    final BigDecimal vlrTac = taxas.get(CodedValues.TPS_TAC_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_TAC_FINANCIADA).toString());
                    final BigDecimal vlrOp  = taxas.get(CodedValues.TPS_OP_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_OP_FINANCIADA).toString());

                    final BigDecimal vlrLiberado = cdeBean.getCdeVlrLiberado() != null ? cdeBean.getCdeVlrLiberado() : new BigDecimal("0");
                    final BigDecimal vlrLiberadoTotal = vlrLiberado.add(vlrTac).add(vlrOp);

                    LOG.debug("vlrTac: " + vlrTac);
                    LOG.debug("vlrOp: " + vlrOp);
                    LOG.debug("vlrLiberado: " + vlrLiberado);
                    LOG.debug("vlrLiberadoTotal: " + vlrLiberadoTotal);

                    taxaJuros = SimulacaoHelper.calcularTaxaJuros(vlrLiberado, vlrParcela, adePrazo, adeBean.getAdeData(), adeBean.getAdeAnoMesIni(), orgCodigo, responsavel);
                }
            } catch (final FindException ex) {
                // Não foi encontrado o registro de coeficiente desconto, provavelmente o
                // contrato foi um histórico importado. Neste caso utiliza o valor atual
                // da taxa da consignatária, para o prazo do contrato, caso exista.
                final short dia = (short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                final boolean calculaSaldoDevedorImportados = ParamSist.paramEquals(CodedValues.TPC_CALCULA_SALDO_DEV_IMPORTADOS, CodedValues.TPC_SIM, responsavel);
                if (calculaSaldoDevedorImportados) {
                    // Verifica se o sistema deve calcular o saldo devedor para os contratos importados
                    // ou exibir mensagem de erro
                    final List<TransferObject> coeficientes = simulacaoController.getCoeficienteAtivo(csaCodigo, svcCodigo, adePrazo, dia, vlrParcela, new BigDecimal(0), responsavel);
                    if ((coeficientes != null) && (!coeficientes.isEmpty())) {
                        final TransferObject coeficiente = coeficientes.get(0);
                        final BigDecimal cftVlr = new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR).toString());
                        cftVlrRef = !TextHelper.isNull(coeficiente.getAttribute(Columns.CFT_VLR_REF)) ? new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR_REF).toString()) : null;

                        if (simulacaoPorTaxaJuros) {
                            // Se simula por taxa de juros, então utiliza a taxa de juros cadastrada pela consignatária
                            taxaJuros = cftVlr;
                        } else {
                            // Se simula por coeficientes, calcula o valor liberado pelo coeficiente,
                            // para depois calcular a taxa de juros
                            // Valor Liberado = (Valor Prestação / Coeficiente) -  (TAC + OP)
                            // OBS: Não subtrai a taxa, pois o valor liberado será utilizado para o cálculo
                            // da taxa de juros.
                            BigDecimal vlrLiberado = vlrParcela.divide(cftVlr, 2, java.math.RoundingMode.DOWN);
                            if (vlrLiberado.signum() == -1) {
                                vlrLiberado = new BigDecimal("0");
                            }

                            LOG.debug("vlrLiberadoTotal: " + vlrLiberado);

                            taxaJuros = SimulacaoHelper.calcularTaxaJuros(vlrLiberado, vlrParcela, adePrazo, adeBean.getAdeData(), adeBean.getAdeAnoMesIni(), orgCodigo, responsavel);
                        }
                    } else {
                        // Se não tem coeficientes ativos cadastrados para o prazo da consignação
                        // retorna mensagem de erro para o usuário
                        throw new SaldoDevedorControllerException("mensagem.erro.nao.possivel.calcular.saldo.devedor.informacoes.financeiras.nao.cadastradas.entre.contato.consignataria.solicitando.saldo", responsavel);
                    }
                } else {
                    // Se TPC_CALCULA_SALDO_DEV_IMPORTADOS=NAO, não pode calcular o saldo devedor para os contratos importados
                    // e deve emitir mensagem de erro
                    throw new SaldoDevedorControllerException("mensagem.erro.nao.possivel.calcular.saldo.devedor.informacoes.financeiras.nao.cadastradas.entre.contato.consignataria.solicitando.saldo", responsavel);
                }
            }

            // Define a data do primeiro desconto
            Date dtPrimeiroDesconto = null;

            // Obtém a parcela do contrato que está em processamento na folha (verifica pela tabela de parcelas do periodo)
            final List<ParcelaDescontoPeriodo> parcelas = ParcelaDescontoPeriodoHome.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO);
            if ((parcelas != null) && (parcelas.size() > 0)) {
                // Se tem uma parcela em processamento, a data de desconto desta parcela
                // será a data do primeiro desconto
                final ParcelaDescontoPeriodo parcela = parcelas.iterator().next();
                dtPrimeiroDesconto = parcela.getPrdDataDesconto();

                final Calendar cal = Calendar.getInstance();
                final int dataAtual = cal.get(Calendar.MONTH) + (cal.get(Calendar.YEAR) * 12);
                cal.setTime(dtPrimeiroDesconto);
                final int dataParcela = cal.get(Calendar.MONTH) + (cal.get(Calendar.YEAR) * 12);

                // Se a parcela está defasada em relação a data atual, então seta para null
                if (dataAtual > (dataParcela + 1)) {
                    dtPrimeiroDesconto = null;
                }
            }

            if (dtPrimeiroDesconto == null) {
                // Se não tem parcela em processamento ou tem mas está defasada em relação a data atual,
                // a data do desconto será o período atual de lançamento
                dtPrimeiroDesconto = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
            }

            // Se o sistema trabalha com CET e o contrato foi incluído com esta configuração
            // então aproxima o valor da taxa de juros a partir do CET encontrado
            if (simulacaoPorTaxaJuros && !contratoIncluidoComCoef && temCET) {
                try {
                    final Object paramVlrAprox = ParamSist.getInstance().getParam(CodedValues.TPC_PERCENTUAL_APROX_CET_PARA_TAXA_JUROS, responsavel);
                    final double vlrAprox = (!TextHelper.isNull(paramVlrAprox) ? Double.parseDouble(paramVlrAprox.toString()) / 100.00 : 0.00);
                    if (vlrAprox > 0.00) {
                        LOG.debug("CET: " + taxaJuros);
                        LOG.debug("% Aprox: " + vlrAprox);
                        taxaJuros = taxaJuros.multiply(new BigDecimal(1 - vlrAprox)).setScale(4, java.math.RoundingMode.HALF_UP);
                        LOG.debug("Taxa Juros Aprox: " + taxaJuros);
                    }
                } catch (final NumberFormatException ex) {
                    throw new SaldoDevedorControllerException("mensagem.erro.interno.parametro.sistema.arg0.contem.valor.incorreto", responsavel, CodedValues.TPC_PERCENTUAL_APROX_CET_PARA_TAXA_JUROS);
                }
            }

            if (usaTaxaInformada && (adeBean.getAdeTaxaJuros() != null) && (adeBean.getAdeTaxaJuros().signum() > 0)) {
                // Se deve utilizar a taxa de juros informada na inclusão do contrato, usa o campo "adeTaxaJuros"
                taxaJuros = adeBean.getAdeTaxaJuros();
            } else {
                // Corrige a taxa que será utilizada para o cálculo do saldo pelo Spread Bancário
                taxaJuros = corrigeTaxaCalculoSaldoDevedor(taxaJuros, adePrazo, adeData, svcCodigo, responsavel);
            }

            if(responsavel.isRescisao() && criaAutoContratoRescisaoAposSdv) {
                if (!TextHelper.isNull(cftVlrRef)) {
                    taxaJuros = taxaJuros.min(cftVlrRef);
                } else {
                    try {
                        final Coeficiente cftBean = CoeficienteHome.findByPrimaryKey(cdeBean.getCoeficiente().getCftCodigo());
                        cftVlrRef = cftBean.getCftVlrRef();

                        if (!TextHelper.isNull(cftVlrRef)) {
                            taxaJuros = taxaJuros.min(cftVlrRef);
                        }
                    } catch (final FindException ex) {
                        // Caso dê algum erro ao procurar o coeficiente então deixamos o sistema continuar com a taxa de juros encontrada
                    }
                }
            }

            LOG.debug("Taxa Juros: " + taxaJuros);
            LOG.debug("Dt. Primeiro Desconto: " + DateHelper.toDateString(dtPrimeiroDesconto));

            // Determina os prazos para o cálculo do saldo
            final int[] prazos = getPrazoCalculoSaldo(adeBean, svcCodigo, orgCodigo, qtdParcelas, responsavel);
            final int prazoRest = prazos[0];
            final int prazoVencido = prazos[1];
            LOG.debug("Prazo para Cálculo do Saldo Devedor: " + prazoRest + " (+" + prazoVencido + ")");

            // Realiza o cálculo de saldo devedor, realizando a "descorreção" de cada parcela
            // de acordo com a data de vencimento e a data atual
            BigDecimal saldoDevedor = SimulacaoHelper.calcularSaldoDevedor(vlrParcela, prazoRest, dtPrimeiroDesconto, taxaJuros, orgCodigo, responsavel);
            LOG.debug("Saldo Devedor Calculado: " + saldoDevedor.setScale(2, java.math.RoundingMode.HALF_UP));

            // Adiciona ao saldo, o valor nominal das parcelas vencidas
            saldoDevedor = saldoDevedor.add(vlrParcela.multiply(new BigDecimal(prazoVencido)));
            LOG.debug("Saldo Devedor Somando Parcelas Vencidas: " + saldoDevedor.setScale(2, java.math.RoundingMode.HALF_UP));

            // Retorna fazendo arredondamento para 2 casas decimais
            return saldoDevedor.setScale(2, java.math.RoundingMode.HALF_UP);
        } catch (final Exception ex) {
            if (ex instanceof SaldoDevedorControllerException) {
                throw (SaldoDevedorControllerException) ex;
            } else if (ex instanceof ViewHelperException) {
                throw new SaldoDevedorControllerException(ex);
            } else {
                LOG.error(ex.getMessage(), ex);
                throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Obtém o prazo utilizado para o cálculo de saldo.
     * SE TPS_CALCULA_SALDO_SOMENTE_VINCENDO:
     * IGUAL CALCULA_SALDO_TUDO_EM_ABERTO            ENTÃO retorna {prazoRestanteTotal, 0}
     * IGUAL CALCULA_SALDO_SOMENTE_VINCENDO          ENTÃO retorna {prazoVincendo, 0}
     * IGUAL CALCULA_SALDO_VINCENDO_SOMANDO_VENCIDAS ENTÃO retorna {prazoVincendo, prazoVencido}
     * @param adeBean
     * @param svcCodigo
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    private int[] getPrazoCalculoSaldo(AutDesconto adeBean, String svcCodigo, String orgCodigo, int qtdParcelas, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        if (qtdParcelas > 0) {
            // Se é cálculo parcial, retorna a quantidade de parcelas solicitada
            return new int[]{qtdParcelas, 0};
        }

        final int adePrazo = adeBean.getAdePrazo() != null ? adeBean.getAdePrazo() : 0;
        final int adePrdPagas = adeBean.getAdePrdPagas() != null ? adeBean.getAdePrdPagas() : 0;
        final int prazoRestante = adePrazo - adePrdPagas;
        int prazoVincendo = prazoRestante;
        int prazoVencido = 0;
        try {
            ParamSvcCseTO paramSvcCseTO = new ParamSvcCseTO();
            paramSvcCseTO.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
            paramSvcCseTO.setTpsCodigo(CodedValues.TPS_CALCULA_SALDO_SOMENTE_VINCENDO);
            paramSvcCseTO.setSvcCodigo(svcCodigo);
            paramSvcCseTO = parametroController.findParamSvcCse(paramSvcCseTO, responsavel);
            final String tpsFormaCalculaSaldo = (!TextHelper.isNull(paramSvcCseTO.getPseVlr()) ? paramSvcCseTO.getPseVlr() : CodedValues.CALCULA_SALDO_TUDO_EM_ABERTO);

            // Se calcula apenas vincendo ou as parcelas vencidas são somadas integralmente
            // determina o prazo restante vincendo.
            if (CodedValues.CALCULA_SALDO_SOMENTE_VINCENDO.equals(tpsFormaCalculaSaldo) ||
                    CodedValues.CALCULA_SALDO_VINCENDO_SOMANDO_VENCIDAS.equals(tpsFormaCalculaSaldo)) {
                // Contabiliza o numero de parcelas rejeitadas
                final List<String> spdCodigos = new ArrayList<>();
                spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
                final ObtemTotalParcelasStatusQuery parcelasStatusQuery = new ObtemTotalParcelasStatusQuery();
                parcelasStatusQuery.adeCodigo = adeBean.getAdeCodigo();
                parcelasStatusQuery.spdCodigos = spdCodigos;
                final int qtdPrdRejeitada = parcelasStatusQuery.executarContador();

                // Prazo vincendo é prazoRestTotal - prdRejeitadas
                prazoVincendo = (qtdPrdRejeitada > prazoRestante) ? 0 : prazoRestante - qtdPrdRejeitada;
            }
            if (CodedValues.CALCULA_SALDO_VINCENDO_SOMANDO_VENCIDAS.equals(tpsFormaCalculaSaldo)) {
                // Se soma as vencidas, então vencidas = prazoRestTotal - prazoVincendo
                prazoVencido = prazoRestante - prazoVincendo;
            }
        } catch (final ParametroControllerException ex) {
            // Não existe o parametro configurado para o serviço, então o ignora e usa (prazo - pagas).
        } catch (final HQueryException ex) {
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return new int[]{prazoVincendo, prazoVencido};
    }

    /**
     * Calcula o capital devido da consignação, de acordo com as configurações
     * do serviço. O capital devido é: Vlr Parcela x Qtd Parcelas Restantes.
     * Se TPS_CALCULA_SALDO_SOMENTE_VINCENDO = CALCULA_SALDO_SOMENTE_VINCENDO
     * então a qtd de parcelas restantes será apenas aquelas ainda não vencidas.
     * @param adeBean
     * @param svcCodigo
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    private BigDecimal calcularCapitalDevido(AutDesconto adeBean, String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        // Determina os prazos para o cálculo do saldo
        final int[] prazos = getPrazoCalculoSaldo(adeBean, svcCodigo, orgCodigo, 0, responsavel);
        final int prazoRest = prazos[0];
        final int prazoVencido = prazos[1];
        final BigDecimal capitalDevido = adeBean.getAdeVlr().multiply(new BigDecimal(prazoRest + prazoVencido));
        // Retorna fazendo arredondamento para 2 casas decimais
        return capitalDevido.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Resolução CMN nº 3.516 de 06/12/07:
     * No caso de contratos com prazo a decorrer superior a 12 meses e
     * a solicitação de liquidação antecipada ocorrer após sete dias da celebração do contrato
     * então a taxa utilizada deverá ser a soma do spread na data da contratação original com
     * a taxa Selic apurada na data do pedido de liquidação antecipada
     * @param taxaJuros
     * @param adePrazo
     * @param adeData
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    private BigDecimal corrigeTaxaCalculoSaldoDevedor(BigDecimal taxaJuros, int adePrazo, Date adeData, String svcCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        String tpsTabelaCalculoSpread = null;
        try {
            // Busca o parâmetro da tabela de correção para cálculo do Spread
            ParamSvcCseTO paramSvcCseTO = new ParamSvcCseTO();
            paramSvcCseTO.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
            paramSvcCseTO.setTpsCodigo(CodedValues.TPS_TABELA_CORRECAO_CALCULO_SPREAD);
            paramSvcCseTO.setSvcCodigo(svcCodigo);
            paramSvcCseTO = parametroController.findParamSvcCse(paramSvcCseTO, responsavel);
            tpsTabelaCalculoSpread = paramSvcCseTO.getPseVlr();
        } catch (final ParametroControllerException ex) {
            // Não existe o parametro configurado para o serviço, então a exceção
        }

        // Se existe tabela de correção para cálculo do Spread, então busca os coeficientes
        if (!TextHelper.isNull(tpsTabelaCalculoSpread)) {
            // Data do decreto: 06/12/07
            final Date dataDecreto = DateHelper.getDate(2007, 12, 6);
            // Se prazo do contrato maior que 12 meses e
            // data da solicitação do saldo após 7 dias da data do contrato e
            // o contrato feito após a publicação do decreto
            // então corrige a taxa pela tabela de correção
            if ((adePrazo > 12) && (DateHelper.dayDiff(adeData) > 7) && adeData.after(dataDecreto)) {
                try {
                    // Data da solicitação = Data atual
                    final Calendar dataSolicitacao = Calendar.getInstance();
                    // Data da contratação = adeData
                    final Calendar dataContratacao = Calendar.getInstance();
                    dataContratacao.setTime(adeData);

                    // Obtém a taxa selic da contratação do contrato
                    ListaCoeficienteCorrecaoMaisProximoQuery ccrQuery = new ListaCoeficienteCorrecaoMaisProximoQuery();
                    ccrQuery.tccCodigo = tpsTabelaCalculoSpread;
                    ccrQuery.mes = (short) (dataContratacao.get(Calendar.MONTH) + 1);
                    ccrQuery.ano = (short) (dataContratacao.get(Calendar.YEAR));
                    List<CoeficienteCorrecaoTransferObject> ccrList = ccrQuery.executarDTO(CoeficienteCorrecaoTransferObject.class);

                    if ((ccrList != null) && (ccrList.size() > 0)) {
                        final CoeficienteCorrecaoTransferObject ccrContratacao = ccrList.get(0);

                        // Obtém a taxa selic atual, ou seja, da data da solicitação de saldo
                        ccrQuery = new ListaCoeficienteCorrecaoMaisProximoQuery();
                        ccrQuery.tccCodigo = tpsTabelaCalculoSpread;
                        ccrQuery.mes = (short) (dataSolicitacao.get(Calendar.MONTH) + 1);
                        ccrQuery.ano = (short) (dataSolicitacao.get(Calendar.YEAR));
                        ccrList = ccrQuery.executarDTO(CoeficienteCorrecaoTransferObject.class);

                        if ((ccrList != null) && (ccrList.size() > 0)) {
                            final CoeficienteCorrecaoTransferObject ccrSolicitacao = ccrList.get(0);

                            final BigDecimal spread = taxaJuros.subtract(ccrContratacao.getCcrVlr());
                            final BigDecimal selicAtual = ccrSolicitacao.getCcrVlr();

                            LOG.debug("Taxa Juros Original: " + taxaJuros);
                            LOG.debug("Spread da Contratação: " + spread);
                            LOG.debug("Taxa Selic Atual: " + selicAtual);
                            taxaJuros = spread.add(selicAtual);

                        }
                    }
                } catch (final HQueryException ex) {
                    throw new SaldoDevedorControllerException("mensagem.erro.interno.localizacao.coeficientes.calculo.spread", responsavel, ex);
                }
            }
        }
        return taxaJuros;
    }

    private void setInfBancariasSaldoDevedor(SaldoDevedorTransferObject sdvTO, String svcCodigo, String csaCodigo, AcessoSistema responsavel) {
        // Busca os parâmetros de serviço da consignatária
        String codBancoPadraoCsa   = "";
        String codAgenciaPadraoCsa = "";
        String codContaPadraoCsa   = "";
        String nomeFavorecidoPadraoCsa = "";
        String cnpjFavorecidoPadraoCsa = "";
        try {
            final List<String> tpsCodigos = new ArrayList<>();
            // Parâmetros da conta padrão de depósito
            tpsCodigos.add(CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR);
            tpsCodigos.add(CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR);
            tpsCodigos.add(CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR);
            tpsCodigos.add(CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV);
            tpsCodigos.add(CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV);

            // Busca os parâmetros
            final List<TransferObject> parametros = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);

            final Iterator<TransferObject> itParametros = parametros.iterator();
            CustomTransferObject next = null;
            String tpsCodigo = null;
            String pscVlr = null;
            while (itParametros.hasNext()) {
                next = (CustomTransferObject) itParametros.next();
                tpsCodigo = next.getAttribute(Columns.TPS_CODIGO).toString();
                pscVlr = (next.getAttribute(Columns.PSC_VLR) != null) ? next.getAttribute(Columns.PSC_VLR).toString() : "";
                if (CodedValues.TPS_BANCO_DEPOSITO_SALDO_DEVEDOR.equals(tpsCodigo)) {
                    codBancoPadraoCsa = pscVlr;
                } else if (CodedValues.TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR.equals(tpsCodigo)) {
                    codAgenciaPadraoCsa = pscVlr;
                } else if (CodedValues.TPS_CONTA_DEPOSITO_SALDO_DEVEDOR.equals(tpsCodigo)) {
                    codContaPadraoCsa = pscVlr;
                } else if (CodedValues.TPS_NOME_FAVORECIDO_DEPOSITO_SDV.equals(tpsCodigo)) {
                    nomeFavorecidoPadraoCsa = pscVlr;
                } else if (CodedValues.TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV.equals(tpsCodigo)) {
                    cnpjFavorecidoPadraoCsa = pscVlr;
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

        sdvTO.setBcoCodigo(!TextHelper.isNull(codBancoPadraoCsa) ? Short.valueOf(codBancoPadraoCsa) : null);
        sdvTO.setSdvAgencia(codAgenciaPadraoCsa);
        sdvTO.setSdvConta(codContaPadraoCsa);
        sdvTO.setSdvNomeFavorecido(nomeFavorecidoPadraoCsa);
        sdvTO.setSdvCnpj(cnpjFavorecidoPadraoCsa);
    }

    /**
     * Valida o saldo devedor cadastrado pela consignatária,  de acordo com as configurações
     * do serviço. Se permite saldo fora da faixa e o saldo informado estiver fora, então
     * retorna "false" para indicar que o usuário deverá informar os detalhes do cálculo
     * utilizado. Se não permite e estiver fora, lança exceção com a mensagem de erro para
     * o usuário. Em qualquer outro caso retorna "true".
     * @param adeCodigo
     * @param valoresSaldoDevedorInformados
     * @param valorComDesconto
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    private boolean validarValoresSaldoDevedor(String adeCodigo, List<BigDecimal> valoresSaldoDevedorInformados, BigDecimal valorComDesconto, boolean usaTaxa, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            // Obtém os entity beans necessários
            final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
            final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
            final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());

            final String svcCodigo = cnvBean.getServico().getSvcCodigo();
            final String csaCodigo = cnvBean.getConsignataria().getCsaCodigo();
            final String orgCodigo = cnvBean.getOrgao().getOrgCodigo();

            // Busca os parâmetros de serviço para verificar se há limitação de saldo devedor
            final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            final boolean limitaSaldoDevedor = responsavel.isCseSupOrg() ? paramSvc.isTpsLimitaSaldoDevedorCadastradoCseOrgSup() : paramSvc.isTpsLimitaSaldoDevedorCadastrado();

            if (limitaSaldoDevedor) {
                // Se limita o valor do saldo devedor cadastrado, então calcula o saldo devedor,
                // aplica a margem de erro e verifica o valor informado pela consignatária
                BigDecimal saldoDevedorLimite = null;

                if (paramSvc.isTpsUsaCapitalDevidoBaseLimiteSaldo()) {
                    // Se usa o capital devido como base para limitação, então obtém este valor
                    saldoDevedorLimite = calcularCapitalDevido(adeBean, svcCodigo, orgCodigo, responsavel);
                } else {
                    try {
                        // Se utiliza o saldo calculado pelo sistema, então executa rotina de cálculo de saldo
                        saldoDevedorLimite = calcularSaldoDevedor(adeBean, svcCodigo, csaCodigo, orgCodigo, usaTaxa, responsavel);
                    } catch (final SaldoDevedorControllerException ex) {
                        // Se o cálculo do saldo devedor deu erro, provavelmente as informações financeiras não estão
                        // cadastradas o que torna impossível a limitação do saldo devedor
                        LOG.debug("SaldoDevedorControllerBean.validarSaldoDevedor: " + ex.getMessage());
                    }
                }

                // Se conseguiu calcular o saldo devedor, então continua a validação
                if ((saldoDevedorLimite != null) && !usaTaxa) {
                    // Obtém o parâmetro com a margem de erro a ser aplicada sobre o saldo devedor
                    final BigDecimal margemErroSuperior = (!TextHelper.isNull(paramSvc.getTpsMargemErroLimiteSaldoDevedor()) ? new BigDecimal(1.00 + (Double.valueOf(paramSvc.getTpsMargemErroLimiteSaldoDevedor()) / 100.00)).setScale(2, java.math.RoundingMode.HALF_UP) : new BigDecimal("1.00"));
                    final BigDecimal margemErroInferior = (!TextHelper.isNull(paramSvc.getTpsMargemErroLimiteSaldoDevedorRef()) ? new BigDecimal(1.00 - (Double.valueOf(paramSvc.getTpsMargemErroLimiteSaldoDevedorRef()) / 100.00)).setScale(2, java.math.RoundingMode.HALF_UP) : new BigDecimal("0.00"));

                    // Aplica a margem de erro sobre o saldo:  saldoLimite = saldoDv * margemErro (> 1.0 para superior | < 1.0 para inferior)
                    final BigDecimal saldoDevedorLimiteSuperior = saldoDevedorLimite.multiply(margemErroSuperior).setScale(2, java.math.RoundingMode.HALF_UP);
                    final BigDecimal saldoDevedorLimiteInferior = saldoDevedorLimite.multiply(margemErroInferior).setScale(2, java.math.RoundingMode.HALF_UP);

                    // Valida os saldos devedores informados de acordo com o calculado.
                    String nomeSaldoDevedor = null;
                    BigDecimal saldoDevedorInformado;
                    if ((valoresSaldoDevedorInformados != null) && (valoresSaldoDevedorInformados.size() > 0)) {

                        final Iterator<BigDecimal> itValorSaldo = valoresSaldoDevedorInformados.iterator();
                        for (int indice = 0; itValorSaldo.hasNext(); indice++) {
                            saldoDevedorInformado = itValorSaldo.next();

                            nomeSaldoDevedor = ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.singular", responsavel).toLowerCase();
                            // Se mais de um valor de saldo está sendo testado, determina o nome do saldo baseando-se na ordem.
                            if (valoresSaldoDevedorInformados.size() > 1) {
                                switch (indice) {
                                    case 0:
                                        nomeSaldoDevedor = ApplicationResourcesHelper.getMessage("rotulo.primeiro", responsavel).toLowerCase() + " " + nomeSaldoDevedor;
                                        break;
                                    case 1:
                                        nomeSaldoDevedor = ApplicationResourcesHelper.getMessage("rotulo.segundo", responsavel).toLowerCase() + " " + nomeSaldoDevedor;
                                        break;
                                    case 2:
                                        nomeSaldoDevedor = ApplicationResourcesHelper.getMessage("rotulo.terceiro", responsavel).toLowerCase() + " " + nomeSaldoDevedor;
                                        break;
                                    default:
                                        break;
                                }
                            }

                            // Verifica se o informado está acima do valor calculado com margem de erro
                            if ((saldoDevedorInformado.compareTo(saldoDevedorLimiteSuperior) == 1) ||
                                    (saldoDevedorInformado.compareTo(saldoDevedorLimiteInferior) == -1)) {
                                LOG.debug("Saldo Devedor Limite Base: " + saldoDevedorLimite);
                                LOG.debug("Margem de erro Superior: " + paramSvc.getTpsMargemErroLimiteSaldoDevedor());
                                LOG.debug("Valor Limite Superior: " + saldoDevedorLimiteSuperior);
                                LOG.debug("Margem de erro Inferior: " + paramSvc.getTpsMargemErroLimiteSaldoDevedorRef());
                                LOG.debug("Valor Limite Inferior: " + saldoDevedorLimiteInferior);
                                LOG.debug("Valor Informado: " + saldoDevedorInformado);

                                if(CanalEnum.WEB.equals(responsavel.getCanal()) && (adeBean.getAdeTaxaJuros() != null)) {
                                    return validarValoresSaldoDevedor(adeCodigo, valoresSaldoDevedorInformados, valorComDesconto, true, responsavel);
                                }

                                if (!paramSvc.isTpsPermiteSaldoForaFaixaLimite()) {
                                    // Se não permite saldo fora da faixa limite, lança exceção com erro para o usuário
                                    if (saldoDevedorInformado.compareTo(saldoDevedorLimiteSuperior) == 1) {
                                        throw new SaldoDevedorControllerException("mensagem.erro.arg0.informado.nao.pode.ser.maior.faixa.limite.permitida.esta.consignacao.arg1", responsavel, nomeSaldoDevedor, NumberHelper.format(saldoDevedorLimiteSuperior.doubleValue(), NumberHelper.getLang()));
                                    } else if (saldoDevedorInformado.compareTo(saldoDevedorLimiteInferior) == -1) {
                                        throw new SaldoDevedorControllerException("mensagem.erro.arg0.informado.nao.pode.ser.menor.faixa.limite.permitida.esta.consignacao.arg1", responsavel, nomeSaldoDevedor, NumberHelper.format(saldoDevedorLimiteInferior.doubleValue(), NumberHelper.getLang()));
                                    }

                                } else {
                                    // Se permite fora da faixa, retorna falso para indicar que o saldo está fora
                                    return false;
                                }
                            }
                        }
                    }
                }
            }

            // Valida o valor do saldo com desconto, caso o serviço exija a um valor mínimo de desconto
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel) &&
                    !TextHelper.isNull(paramSvc.getTpsPercentualMinimoDescontoVlrSaldo())) {

                if ((valorComDesconto == null) || (valorComDesconto.signum() <= 0)) {
                    throw new SaldoDevedorControllerException("mensagem.erro.valor.saldo.devedor.com.desconto.deve.ser.maior.zero", responsavel);
                }

                final BigDecimal percMinDesconto = new BigDecimal(Double.valueOf(paramSvc.getTpsPercentualMinimoDescontoVlrSaldo()) / 100.00).setScale(2, java.math.RoundingMode.HALF_UP);
                if ((percMinDesconto.signum() > 0) && ((valoresSaldoDevedorInformados != null) && (valoresSaldoDevedorInformados.size() > 0))) {
                    for (final BigDecimal saldoDevedorInformado : valoresSaldoDevedorInformados) {
                        final BigDecimal vlrMinimoDesconto = saldoDevedorInformado.multiply(percMinDesconto).setScale(2, java.math.RoundingMode.HALF_UP);
                        if (valorComDesconto.compareTo(saldoDevedorInformado.subtract(vlrMinimoDesconto)) == 1) {
                            throw new SaldoDevedorControllerException("mensagem.erro.valor.desconto.deve.ser.no.minimo.arg0", responsavel, NumberHelper.format(vlrMinimoDesconto.doubleValue(), NumberHelper.getLang()));
                        }
                    }
                }
            }

            return true;
        } catch (final SaldoDevedorControllerException ex) {
            throw ex;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Realiza a validação das informações de saldo, sejam saldos múltiplos, ou saldo único.
     * Executa validação de saldo pelo limite configurado nos parâmetros de serviço.
     * @param saldoDevedorTO
     * @param dadosSaldosDevedoresMultiplos
     * @param propostasPgtSaldo
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public boolean validarSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        final List<BigDecimal> valorAValidar = new ArrayList<>();
        if (dadosSaldosDevedoresMultiplos != null) {
            // Múltiplos Saldos Devedores, valida as datas de vencimento e os valores
            final String dataVcto1 = (String) dadosSaldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_DATA_VCTO1);
            final String dataVcto2 = (String) dadosSaldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_DATA_VCTO2);
            final String dataVcto3 = (String) dadosSaldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_DATA_VCTO3);

            if (TextHelper.isNull(dataVcto1) || TextHelper.isNull(dataVcto2) || TextHelper.isNull(dataVcto3)) {
                throw new SaldoDevedorControllerException("mensagem.erro.data.vencimento.nao.pode.ser.nula", responsavel);
            }

            if (dataVcto1.equals(dataVcto2) || dataVcto1.equals(dataVcto3) || dataVcto2.equals(dataVcto3)) {
                throw new SaldoDevedorControllerException("mensagem.erro.datas.vencimento.nao.podem.ser.iguais", responsavel);
            }

            final String valorVcto1 = (String) dadosSaldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO1);
            final String valorVcto2 = (String) dadosSaldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO2);
            final String valorVcto3 = (String) dadosSaldosDevedoresMultiplos.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO3);

            if (TextHelper.isNull(valorVcto1) || TextHelper.isNull(valorVcto2) || TextHelper.isNull(valorVcto3)) {
                throw new SaldoDevedorControllerException("mensagem.erro.valor.saldo.nao.pode.ser.nulo", responsavel);
            }

            if (valorVcto1.equals(valorVcto2) || valorVcto1.equals(valorVcto3) || valorVcto2.equals(valorVcto3)) {
                throw new SaldoDevedorControllerException("mensagem.erro.valores.saldo.nao.podem.ser.iguais", responsavel);
            }

            BigDecimal valorSaldo1;
            BigDecimal valorSaldo2;
            BigDecimal valorSaldo3;
            try {
                valorSaldo1 = new BigDecimal(NumberHelper.parse(valorVcto1, "en"));
                valorSaldo2 = new BigDecimal(NumberHelper.parse(valorVcto2, "en"));
                valorSaldo3 = new BigDecimal(NumberHelper.parse(valorVcto3, "en"));

                if ((valorSaldo1.signum() <= 0) || (valorSaldo2.signum() <= 0) || (valorSaldo3.signum() <= 0)) {
                    throw new SaldoDevedorControllerException("mensagem.erro.valores.saldo.devem.ser.maiores.zero", responsavel);
                }
            } catch (final ParseException e) {
                throw new SaldoDevedorControllerException("mensagem.erro.valores.saldo.devem.ser.numericos", responsavel);
            }

            // Lista os valores para validação
            valorAValidar.add(valorSaldo1);
            valorAValidar.add(valorSaldo2);
            valorAValidar.add(valorSaldo3);
        } else {
            // Saldo Devedor Único
            if (saldoDevedorTO.getSdvValor() == null) {
                throw new SaldoDevedorControllerException("mensagem.erro.valor.saldo.nao.pode.ser.nulo", responsavel);
            }

            if (saldoDevedorTO.getSdvValor().signum() <= 0) {
                throw new SaldoDevedorControllerException("mensagem.erro.valor.saldo.deve.ser.maior.zero", responsavel);
            }

            // Lista o valor para validação
            valorAValidar.add(saldoDevedorTO.getSdvValor());
        }

        // Realiza validação pelo limite configurado nos parâmetros de serviço
        final boolean valoresAceitos = validarValoresSaldoDevedor(saldoDevedorTO.getAdeCodigo(), valorAValidar, saldoDevedorTO.getSdvValorComDesconto(), false, responsavel);

        // Realiza validação das propostas de pagamento do saldo devedor
        try {
            final List<PropostaPagamentoDividaTO> propostasValidadas = financiamentoDividaController.validarPropostasPgtSaldoDevedor(saldoDevedorTO, propostasPgtSaldo, responsavel);
            if ((propostasValidadas != null) && !propostasValidadas.isEmpty()) {
                // Devolve as propostas atualizadas, para que a gravação obtenha os valores corretos.
                propostasPgtSaldo.clear();
                propostasPgtSaldo.addAll(propostasValidadas);
            }
        } catch (final FinanciamentoDividaControllerException ex) {
            throw new SaldoDevedorControllerException(ex);
        }

        return valoresAceitos;
    }

    /**
     * Solicita um novo cálculo de saldo devedor.
     * @param adeCodigo
     * @param obs
     * @param responsavel
     * @throws SaldoDevedorControllerException
     */
    @Override
    public void solicitarRecalculoSaldoDevedor(String adeCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            if (autorizacaoController.usuarioPodeModificarAdeCompra(adeCodigo, responsavel)) {
                // Se o sistema exige o ciclo de vida fixo no processo de compra, impede
                // que seja solicitado um novo recálculo do saldo devedor caso já tenha sido efetuado um pagamento de saldo.
                if (ParamSist.getBoolParamSist(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, responsavel) && existeSaldoDevedorPago(adeCodigo, responsavel)) {
                    throw new SaldoDevedorControllerException("mensagem.erro.informacoes.saldo.devedor.consignacao.nao.podem.ser.alteradas.pagamento.saldo.ja.registrado.sistema", responsavel);
                }

                // Se o serviço da reserva é origem de um relacionamento de financiamento de dívida,
                // então não permite solicitação de recálculo de saldo, já que o saldo do cartão não será alterado
                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                    final String svcCodigo = ServicoHome.findByAdeCodigo(adeCodigo).getSvcCodigo();
                    final List<TransferObject> servicos = parametroController.getRelacionamentoSvc(CodedValues.TNT_FINANCIAMENTO_DIVIDA, svcCodigo, null, responsavel);
                    if ((servicos != null) && !servicos.isEmpty()) {
                        throw new SaldoDevedorControllerException("mensagem.erro.informacoes.saldo.devedor.consignacao.nao.podem.ser.alteradas.saldo.devedor.ja.aprovado", responsavel);
                    }
                }

                // Se existe alguma ocorrência de informacao de saldo devedor então altera a ocorrência de informacao para saldo recalculado.
                final OcorrenciaAutorizacao ocaBean = recuperaOcorrenciaInfSaldoCompra(adeCodigo, responsavel);
                if (ocaBean != null) {
                    // Altera o tipo de ocorrência.
                    ocaBean.setTipoOcorrencia(TipoOcorrenciaHome.findByPrimaryKey(CodedValues.TOC_SALDO_DEVEDOR_RECALCULADO));
                    AbstractEntityHome.update(ocaBean);

                    // Atualiza relacionamento de compra
                    compraContratoController.updateRelAutorizacaoCompra(adeCodigo, OperacaoCompraEnum.SOLICITAR_RECALCULO_SALDO, responsavel);
                }

                // Cadastra ocorrência de recalculo de saldo devedor
                final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.solicitacao.recalculo.saldo.devedor.arg0", responsavel, (!TextHelper.isNull(obs) ? ": " + obs : ""));
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_RECALCULO_SALDO_DEVEDOR, ocaObs, responsavel);

                // Manda email de notificação sobre a solicitação
                EnviaEmailHelper.enviarEmailCompraContrato(EnviaEmailHelper.TIPO_SOL_RECALCULO_SALDO_DEVEDOR, adeCodigo, obs, responsavel);
                // Grava log da operação
                final LogDelegate log = new LogDelegate(responsavel, Log.SALDO_DEVEDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.solicitacao.recalculo.saldo.devedor.arg0", responsavel, ""));
                log.write();
            }
        } catch (final AutorizacaoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException(ex);
        } catch (final SaldoDevedorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Registra a rejeição de pagamento de saldo devedor.
     * @param adeCodigo
     * @param obs
     * @param responsavel
     * @throws SaldoDevedorControllerException
     */
    @Override
    public void rejeitarPagamentoSaldoDevedor(String adeCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            if (autorizacaoController.usuarioPodeModificarAde(adeCodigo, responsavel)) {
                final List<OcorrenciaAutorizacao> ocorrenciasPagamentoSaldo = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_PAGAMENTO_SALDO_DEVEDOR);

                // Se o sistema exige o ciclo de vida fixo no processo de compra, impede
                // que a rejeição de pagamento seja registrada antes do próprio pagamento.
                if (ParamSist.getBoolParamSist(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, responsavel) && ((ocorrenciasPagamentoSaldo == null) || (ocorrenciasPagamentoSaldo.size() == 0))) {
                    throw new SaldoDevedorControllerException("mensagem.erro.pagamento.saldo.devedor.nao.informado", responsavel);
                }

                // Se existe alguma ocorrência de pagamento de saldo devedor, mas nenhuma de pagamento rejeitado,
                // então altera uma ocorrência de pagamento para pagamento rejeitado.
                if ((ocorrenciasPagamentoSaldo != null) && (ocorrenciasPagamentoSaldo.size() > 0)) {
                    for (final OcorrenciaAutorizacao ocaBean : ocorrenciasPagamentoSaldo) {
                        ocaBean.setTipoOcorrencia(TipoOcorrenciaHome.findByPrimaryKey(CodedValues.TOC_PAGAMENTO_REJEITADO_SALDO_DEVEDOR));
                        AbstractEntityHome.update(ocaBean);
                    }

                    // Atualiza relacionamento de compra
                    compraContratoController.updateRelAutorizacaoCompra(adeCodigo, OperacaoCompraEnum.REJEITAR_PAGAMENTO_SALDO, responsavel);
                }

                // Cadastra ocorrência de informação indicando a rejeição do pagamento de saldo devedor
                final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.rejeicao.pagamento.saldo.devedor.arg0", responsavel, (!TextHelper.isNull(obs) ? ": " + obs : ""));
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_REJEICAO_PAGAMENTO_SALDO_DEVEDOR, ocaObs, responsavel);

                // Verifica se deve ser bloqueada as duas consignatárias, através do parâmetro de serviço
                final String svcCodigo = ServicoHome.findByAdeCodigo(adeCodigo).getSvcCodigo();
                final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                // Se as consignatárias devem ser bloqueadas por causa do rejeito de pagamento, realiza o bloqueio
                if (paramSvc.isTpsRejeicaoPgtSdvBloqueiaAmbasCsas()) {
                    // Obtém as consignatárias vendedora e compradora
                    final ObtemCsaRelacionamentoCompraQuery queryCsaCompra = new ObtemCsaRelacionamentoCompraQuery();
                    queryCsaCompra.stcCodigo = StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo();
                    queryCsaCompra.adeCodigoOrigem = adeCodigo;
                    List<String> consignatarias = queryCsaCompra.executarLista();

                    // Busca o adeNumero do contrato para inserir na ocorrência de bloqueio
                    final AutDesconto ade = AutDescontoHome.findByPrimaryKey(adeCodigo);
                    final Long adeNumero = ade.getAdeNumero();
                    final String obsBloqueio = ApplicationResourcesHelper.getMessage("mensagem.bloqueio.rejeicao.pagamento.saldo.devedor", responsavel, adeNumero != null ? adeNumero.toString() : "");

                    // Bloqueia as duas consignatárias: responsável será usuário do sistema (DESENV-740)
                    final AcessoSistema responsavelBloqueio = AcessoSistema.getAcessoUsuarioSistema();
                    consignatarias = consignatariaController.bloquearConsignatarias(consignatarias, obsBloqueio, CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, responsavelBloqueio);
                    // Se tem desbloqueio automático, então insere ocorrências para sinalização do bloqueio por pendência de compra
                    if (ParamSist.paramEquals(CodedValues.TPC_DESBL_AUTOMATICO_CSA_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                        consignatariaController.incluirOcorrenciaConsignatarias(consignatarias, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA, ApplicationResourcesHelper.getMessage("mensagem.bloqueio.pendencia.processo.compra", responsavel), responsavelBloqueio);
                    }
                }

                // Manda email de notificação sobre a rejeição
                EnviaEmailHelper.enviarEmailCompraContrato(EnviaEmailHelper.TIPO_REJ_PGT_SALDO_DEVEDOR, adeCodigo, obs, responsavel);
                // Grava log da operação
                final LogDelegate log = new LogDelegate(responsavel, Log.SALDO_DEVEDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.rejeicao.pagamento.saldo.devedor.arg0", responsavel, ""));
                log.write();
            }
        } catch (final AutorizacaoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException(ex);
        } catch (final SaldoDevedorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void aprovarSaldoDevedor(String adeCodigo, boolean aprovado, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            if (autorizacaoController.usuarioPodeModificarAde(adeCodigo, responsavel)) {
                // Se o sistema exige o ciclo de vida fixo no processo de compra, impede
                // que a informação de pagamento de saldo devedor seja feita antes do cadastro do saldo.
                if (ParamSist.getBoolParamSist(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, responsavel) && !possuiInformacaoSaldoDevedor(adeCodigo, responsavel)) {
                    throw new SaldoDevedorControllerException("mensagem.erro.saldo.devedor.nao.foi.cadastrado", responsavel);
                }

                // Atualiza relacionamento de compra
                compraContratoController.updateRelAutorizacaoCompra(adeCodigo, (aprovado ? OperacaoCompraEnum.APROVAR_SALDO_DEVEDOR : OperacaoCompraEnum.REJEITAR_SALDO_DEVEDOR), responsavel);

                // Cadastra ocorrência de aprovação de saldo devedor
                final String rotuloServidor = ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel).toUpperCase();
                String ocaObs = null;
                String tocCodigo = null;
                if (aprovado) {
                    ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.aprovacao.saldo.devedor.arg0", responsavel, rotuloServidor + (!TextHelper.isNull(obs) ? ": " + obs : ""));
                    tocCodigo = CodedValues.TOC_SALDO_DEVEDOR_APROVADO_SERVIDOR;
                } else {
                    ocaObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.rejeicao.saldo.devedor.arg0", responsavel, rotuloServidor + (!TextHelper.isNull(obs) ? ": " + obs : ""));
                    tocCodigo = CodedValues.TOC_SALDO_DEVEDOR_REJEITADO_SERVIDOR;
                }
                autorizacaoController.criaOcorrenciaADE(adeCodigo, tocCodigo, ocaObs, responsavel);

                // Manda email de notificação sobre a solicitação
                EnviaEmailHelper.enviarEmailCompraContrato((aprovado ? EnviaEmailHelper.TIPO_APROVACAO_SALDO_DEVEDOR : EnviaEmailHelper.TIPO_REJEICAO_SALDO_DEVEDOR), adeCodigo, obs, responsavel);
                // Grava log da operação
                final LogDelegate log = new LogDelegate(responsavel, Log.SALDO_DEVEDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);
                log.add(ocaObs);
                log.write();
            }
        } catch (final AutorizacaoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException(ex);
        } catch (final SaldoDevedorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Salva as informações sobre saldos devedores múltiplos
     * @param adeCodigo
     * @param dadosSaldosDevedores
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void atualizaDadosSaldosDevedoresMultiplos(String adeCodigo, TransferObject dadosSaldosDevedores, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        if (dadosSaldosDevedores != null) {
            try {
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_DATACADASTRO,  (String) dadosSaldosDevedores.getAttribute(CodedValues.TDA_SDV_DATACADASTRO), responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_QTDE_PRESTACOES, (String) dadosSaldosDevedores.getAttribute(CodedValues.TDA_SDV_QTDE_PRESTACOES), responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_DATA_VCTO1, (String) dadosSaldosDevedores.getAttribute(CodedValues.TDA_SDV_DATA_VCTO1), responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_VALOR_VCTO1, (String) dadosSaldosDevedores.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO1), responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_DATA_VCTO2, (String) dadosSaldosDevedores.getAttribute(CodedValues.TDA_SDV_DATA_VCTO2), responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_VALOR_VCTO2, (String) dadosSaldosDevedores.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO2), responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_DATA_VCTO3, (String) dadosSaldosDevedores.getAttribute(CodedValues.TDA_SDV_DATA_VCTO3), responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_VALOR_VCTO3, (String) dadosSaldosDevedores.getAttribute(CodedValues.TDA_SDV_VALOR_VCTO3), responsavel);
            } catch (final Exception ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Salva as informações sobre os anexos do saldo devedor
     * @param saldoDevedorTO
     * @param compra
     * @param responsavel
     * @throws AutorizacaoControllerException
     * @throws SaldoDevedorControllerException
     */
    private void atualizaAnexosSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, boolean compra, AcessoSistema responsavel) throws AutorizacaoControllerException, SaldoDevedorControllerException {
        if (saldoDevedorTO != null) {
            final boolean exigeAnexoDsdSaldo = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            boolean exigeAnexoBoletoSaldo = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            final boolean exigeAnexoDsdSaldoCompra = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
            final boolean exigeAnexoBoletoSaldoCompra = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_DEVEDOR_COMPRA, CodedValues.TPC_SIM, responsavel);

            final String adeCodigo = saldoDevedorTO.getAdeCodigo();

            if (temSolicitacaoSaldoInformacaoApenas(adeCodigo, responsavel)) {
                exigeAnexoBoletoSaldo = false;
            }

            if ((compra ? exigeAnexoDsdSaldoCompra : exigeAnexoDsdSaldo)) {
                if (saldoDevedorTO.getAnexoDsd() != null) {
                    final CustomTransferObject cto = new CustomTransferObject();
                    cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                    cto.setAttribute(Columns.AAD_TAR_CODIGO, TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo());
                    Date aadPeriodo = null;
                    try {
                        final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
                        aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(adeBean.getVerbaConvenio().getConvenio().getOrgao().getOrgCodigo(), responsavel);
                    } catch (FindException | PeriodoException ex) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        LOG.error(ex.getMessage(), ex);
                        throw new AutorizacaoControllerException(ex);
                    }

                    // Remove os anexos do mesmo tipo, deixando apenas um
                    editarAnexoConsignacaoController.removeAnexoAutorizacaoDesconto(cto, responsavel);
                    // Cria o novo anexo de autorização
                    editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, saldoDevedorTO.getAnexoDsd().getName(), ApplicationResourcesHelper.getMessage("rotulo.anexo.saldo.dsd", responsavel), new java.sql.Date(aadPeriodo.getTime()), TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD, responsavel);
                } else {
                    // Se entrou aqui, o DSD é obrigatório (pois vale tanto para compra quanto para saldo) e não
                    // foi informado. Verifica se já existe, e caso negativo, retorna erro para o usuário.
                    final CustomTransferObject cto = new CustomTransferObject();
                    cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                    cto.setAttribute(Columns.AAD_TAR_CODIGO, TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo());
                    cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
                    final int qtdAnexos = editarAnexoConsignacaoController.countAnexoAutorizacaoDesconto(cto, responsavel);
                    if (qtdAnexos == 0) {
                        throw new AutorizacaoControllerException("mensagem.informe.arquivo.anexo.de.arg0.calculo.saldo.devedor", responsavel, ApplicationResourcesHelper.getMessage("rotulo.anexo.saldo.dsd", responsavel));
                    }
                }
            }
            if ((compra ? exigeAnexoBoletoSaldoCompra : exigeAnexoBoletoSaldo)) {
                if (saldoDevedorTO.getAnexoBoleto() != null) {
                    final CustomTransferObject cto = new CustomTransferObject();
                    cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                    cto.setAttribute(Columns.AAD_TAR_CODIGO, TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo());

                    Date aadPeriodo = null;
                    try {
                        final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
                        aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(adeBean.getVerbaConvenio().getConvenio().getOrgao().getOrgCodigo(), responsavel);
                    } catch (FindException | PeriodoException ex) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        LOG.error(ex.getMessage(), ex);
                        throw new AutorizacaoControllerException(ex);
                    }
                    // Remove os anexos do mesmo tipo, deixando apenas um
                    editarAnexoConsignacaoController.removeAnexoAutorizacaoDesconto(cto, responsavel);
                    // Cria o novo anexo de autorização
                    editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, saldoDevedorTO.getAnexoBoleto().getName(), ApplicationResourcesHelper.getMessage("rotulo.anexo.saldo.boleto", responsavel), new java.sql.Date(aadPeriodo.getTime()), TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO, responsavel);
                } else {
                    // Se entrou aqui, o Boleto é obrigatório e não foi informado.
                    // Verifica se já existe, e caso negativo, retorna erro para o usuário.
                    final CustomTransferObject cto = new CustomTransferObject();
                    cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                    cto.setAttribute(Columns.AAD_TAR_CODIGO, TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo());
                    cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
                    final int qtdAnexos = editarAnexoConsignacaoController.countAnexoAutorizacaoDesconto(cto, responsavel);
                    if (qtdAnexos == 0) {
                        throw new AutorizacaoControllerException("mensagem.informe.arquivo.anexo.de.arg0.calculo.saldo.devedor", responsavel, ApplicationResourcesHelper.getMessage("rotulo.anexo.saldo.boleto", responsavel));
                    }
                }
            }
        }
    }

    /**
     * Recupera as informações sobre saldos devedores múltiplos
     * @param adeCodigo
     * @throws AutorizacaoControllerException
     */
    @Override
    public TransferObject recuperaDadosSaldosDevedoresMultiplos(String adeCodigo) {
        List<DadosAutorizacaoDesconto> dadosAutDesconto;
        try {
            dadosAutDesconto = DadosAutorizacaoDescontoHome.findByAdeCodigo(adeCodigo);
        } catch (final FindException e) {
            return null;
        }

        TransferObject dadosSaldosDevedores = null;
        if ((dadosAutDesconto != null) && (dadosAutDesconto.size() > 0)) {
            dadosSaldosDevedores = new CustomTransferObject();

            for (final DadosAutorizacaoDesconto dado : dadosAutDesconto) {
                if (CodedValues.TDA_SDV_DATACADASTRO.equals(dado.getTipoDadoAdicional().getTdaCodigo())) {
                    dadosSaldosDevedores.setAttribute(CodedValues.TDA_SDV_DATACADASTRO, dado.getDadValor());
                } else if (CodedValues.TDA_SDV_QTDE_PRESTACOES.equals(dado.getTipoDadoAdicional().getTdaCodigo())) {
                    dadosSaldosDevedores.setAttribute(CodedValues.TDA_SDV_QTDE_PRESTACOES, dado.getDadValor());
                } else if (CodedValues.TDA_SDV_DATA_VCTO1.equals(dado.getTipoDadoAdicional().getTdaCodigo())) {
                    dadosSaldosDevedores.setAttribute(CodedValues.TDA_SDV_DATA_VCTO1, dado.getDadValor());
                } else if (CodedValues.TDA_SDV_VALOR_VCTO1.equals(dado.getTipoDadoAdicional().getTdaCodigo())) {
                    dadosSaldosDevedores.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO1, dado.getDadValor());
                } else if (CodedValues.TDA_SDV_DATA_VCTO2.equals(dado.getTipoDadoAdicional().getTdaCodigo())) {
                    dadosSaldosDevedores.setAttribute(CodedValues.TDA_SDV_DATA_VCTO2, dado.getDadValor());
                } else if (CodedValues.TDA_SDV_VALOR_VCTO2.equals(dado.getTipoDadoAdicional().getTdaCodigo())) {
                    dadosSaldosDevedores.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO2, dado.getDadValor());
                } else if (CodedValues.TDA_SDV_DATA_VCTO3.equals(dado.getTipoDadoAdicional().getTdaCodigo())) {
                    dadosSaldosDevedores.setAttribute(CodedValues.TDA_SDV_DATA_VCTO3, dado.getDadValor());
                } else if (CodedValues.TDA_SDV_VALOR_VCTO3.equals(dado.getTipoDadoAdicional().getTdaCodigo())) {
                    dadosSaldosDevedores.setAttribute(CodedValues.TDA_SDV_VALOR_VCTO3, dado.getDadValor());
                }
            }
        }

        return dadosSaldosDevedores;
    }

    /**
     * Verifica se as solicitações de saldo devedor feitas pelo servidor estão sendo atendidas pelas
     * consignatárias. Se não foram atendidas, então bloqueia as consignatárias e envia email.</p>
     * @param responsavel
     * @throws SaldoDevedorControllerException
     */
    @Override
    public void verificarBloqueioCsaSolicitacaoSaldoDevedor(AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            // Lista contratos que nao tiveram o saldo devedor solicitado pelo servidor informado pelas consignatárias
            final ListaSolicitacaoSaldoDevedorSemRespostaQuery query = new ListaSolicitacaoSaldoDevedorSemRespostaQuery();
            query.responsavel = responsavel;
            List<TransferObject> contratos = query.executarDTO();

            if (!contratos.isEmpty()) {

                // Executa os bloqueios após a execução da query para criar ocorrência
                final List<String> csaCodigos = consignatariaController.bloquearConsignatariasContratos(contratos, ApplicationResourcesHelper.getMessage("mensagem.informe.bloqueio.automatico.consignataria.por.nao.atender.solicitacao.saldo.devedor.no.prazo", responsavel) , CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, responsavel);

                // Filtra a lista de ADEs com os códigos de consignatárias retornados que realmente foram bloqueadas
                contratos = contratos.stream().filter(t -> (csaCodigos != null) && csaCodigos.contains(t.getAttribute(Columns.CSA_CODIGO))).collect(Collectors.toList());

                // Envia e-mail para as consignatárias informando sobre o bloqueio
                try {
                    EnviaEmailHelper.enviarEmailBloqueioConsignatarias(contratos, CodedValues.BLOQUEIO_SOLIC_SALDO_DEVEDOR, responsavel);
                } catch (final ViewHelperException e) {
                    LOG.error("Erro ao enviar e-mail de bloqueio de consignatária.", e);
                }

                // Se tem desbloqueio automático, então insere ocorrências para sinalização do bloqueio por pendência de solicitacao de saldo devedor
                if (ParamSist.paramEquals(CodedValues.TPC_DESBL_AUTOMATICO_CSA_SOLICIT_SALDO, CodedValues.TPC_SIM, responsavel) && (csaCodigos.size() > 0)) {
                    final int qtdeDiasBloqCsaSolicSaldoDevedor = ParamSist.getIntParamSist(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_SOLIC_SALDO_DEVEDOR_NAO_ATENDIDA, 0, responsavel);
                    if(!TextHelper.isNull(qtdeDiasBloqCsaSolicSaldoDevedor) && (qtdeDiasBloqCsaSolicSaldoDevedor > 0)) {
                        final List<String> csaCodigosSemRescisao = new ArrayList<>();
                        final List<String> csaCodigosComRescisao = new ArrayList<>();
                        for(final TransferObject contrato : contratos) {
                            final boolean isRescisao = !TextHelper.isNull(contrato.getAttribute(Columns.VRR_CODIGO));
                            final String csaCodigo = contrato.getAttribute(Columns.CSA_CODIGO).toString();
                            if(isRescisao && !csaCodigosComRescisao.contains(csaCodigo)) {
                                csaCodigosComRescisao.add(csaCodigo);
                            } else if(!csaCodigosComRescisao.contains(csaCodigo) && !csaCodigosSemRescisao.contains(csaCodigo)) {
                                csaCodigosSemRescisao.add(csaCodigo);
                            }
                        }
                        consignatariaController.incluirOcorrenciaConsignatarias(csaCodigosSemRescisao, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO, ApplicationResourcesHelper.getMessage("mensagem.informacao.consignataria.possui.pendencias.com.solicitacao.saldo.devedor", responsavel), responsavel);
                        consignatariaController.incluirOcorrenciaConsignatarias(csaCodigosComRescisao, CodedValues.TOC_BLOQ_SOLICITACAO_SALDO_DEVEDOR_RESCISAO_NAO_ATENDIDA, ApplicationResourcesHelper.getMessage("mensagem.informacao.consignataria.possui.pendencias.com.solicitacao.saldo.devedor.rescisao.nao.atendida", responsavel), responsavel);
                    } else {
                        consignatariaController.incluirOcorrenciaConsignatarias(csaCodigos, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO, ApplicationResourcesHelper.getMessage("mensagem.informacao.consignataria.possui.pendencias.com.solicitacao.saldo.devedor", responsavel), responsavel);
                    }
                }
            }

        } catch (final HQueryException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erro.interno.nao.possivel.listar.consignatarias.que.nao.informaram.saldo.devedor", responsavel, ex);
        } catch (final ConsignatariaControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException(ex);
        }
    }

    @Override
    public Map<String, Boolean> consignatariaNaoPossuiPendenciaSaldoDevedor(String csaCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        final Map<String, Boolean> podeDesbloquear = new HashMap<>();
        podeDesbloquear.put(CodedValues.PODE_DESBLOQUEAR_CSA, true);
        try {
            // Verifica se a consignataria pode ser desbloqueada
            final ListaSolicitacaoSaldoDevedorSemRespostaQuery query = new ListaSolicitacaoSaldoDevedorSemRespostaQuery();
            query.csaCodigo = csaCodigo;
            query.responsavel = responsavel;
            final List<TransferObject> csaCodigos = query.executarDTO();
            if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
                podeDesbloquear.put(CodedValues.PODE_DESBLOQUEAR_CSA, false);
                podeDesbloquear.put(CodedValues.IS_CSA_COM_ADE_COM_RESCISAO, csaCodigos.stream().anyMatch(t -> !TextHelper.isNull(t.getAttribute(Columns.VRR_CODIGO))));
                return podeDesbloquear;
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return podeDesbloquear;
    }

    /**
     * Retorna a ocorrência mais recente de informação de saldo devedor que esteja relacionada ao controle de compra.
     * @param adeCodigo Código do contrato.
     * @param responsavel Responsavel pela operação.
     * @return Ocorrência de informação de saldo devedor.
     * @throws SaldoDevedorControllerException Exceção padrão da classe.
     */
    private OcorrenciaAutorizacao recuperaOcorrenciaInfSaldoCompra(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final Collection<OcorrenciaAutorizacao> ocorrenciasInfSaldo = OcorrenciaAutorizacaoHome.findByAdeTocCodigoOrdenado(adeCodigo, CodedValues.TOC_INFORMACAO_SALDO_DEVEDOR);
            final Collection<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA, CodedValues.SAD_AGUARD_CONF);
            if ((ocorrenciasInfSaldo != null) && !ocorrenciasInfSaldo.isEmpty()
                    && (adesCompra != null) && !adesCompra.isEmpty()) {
                final OcorrenciaAutorizacao ocaBean = ocorrenciasInfSaldo.iterator().next();
                final RelacionamentoAutorizacao radBean = adesCompra.iterator().next();
                if (ocaBean.getOcaData().compareTo(radBean.getRadData()) > 0) {
                    return ocaBean;
                }
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return null;
    }

    /**
     * Determina se o contrato informado por parâmetro possui solicitação de informação de saldo
     * devedor, solicitada pelo servidor, pendente ou não.
     * @param adeCodigo   : Contrato a ser verificado se existe solicitação de saldo
     * @param pendente    : Determina se pesquisa apenas por solicitações pendentes
     * @param responsavel : Responsável pela operação
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public boolean temSolicitacaoSaldoDevedor(String adeCodigo, boolean pendente, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo(), TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo()};
            String[] ssoCodigos = {StatusSolicitacaoEnum.PENDENTE.getCodigo()};
            if (!pendente) {
                ssoCodigos = new String[]{StatusSolicitacaoEnum.PENDENTE.getCodigo(), StatusSolicitacaoEnum.FINALIZADA.getCodigo()};
            }
            final Collection<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            return ((soaList != null) && !soaList.isEmpty());
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Determina se o contrato informado por parâmetro possui solicitação de informação de saldo
     * devedor solicitada pelo servidor já respondida pela consignatária.
     * @param adeCodigo   : Contrato a ser verificado se existe solicitação de saldo respondida
     * @param responsavel : Responsável pela operação
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public boolean temSolicitacaoSaldoDevedorRespondida(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo(), TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo()};
            final String[] ssoCodigos = {StatusSolicitacaoEnum.FINALIZADA.getCodigo()};
            final Collection<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            return ((soaList != null) && !soaList.isEmpty());
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Determina se o contrato informado por parâmetro possui solicitação de informação de saldo
     * devedor de liquidação solicitada pelo servidor, já respondida pela consignatária.
     * @param adeCodigo   : Contrato a ser verificado se existe solicitação de saldo respondida
     * @param responsavel : Responsável pela operação
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public boolean temSolicitacaoSaldoDevedorLiquidacaoRespondida(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo()};
            final String[] ssoCodigos = {StatusSolicitacaoEnum.FINALIZADA.getCodigo()};
            final Collection<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            return ((soaList != null) && !soaList.isEmpty());
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Determina se o contrato informado por parâmetro possui solicitação de informação de saldo
     * devedor de liquidação solicitada pelo servidor, pendente ou não.
     * @param adeCodigo   : Contrato a ser verificado se existe solicitação de saldo
     * @param pendente    : Determina se pesquisa apenas por solicitações pendentes
     * @param responsavel : Responsável pela operação
     * @return
     * @throws SaldoDevedorControllerException
     */
    private boolean temSolicitacaoLiquidacaoContrato(String adeCodigo, boolean pendente, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO.getCodigo()};
            String[] ssoCodigos = {StatusSolicitacaoEnum.PENDENTE.getCodigo()};
            if (!pendente) {
                ssoCodigos = new String[]{StatusSolicitacaoEnum.PENDENTE.getCodigo(), StatusSolicitacaoEnum.FINALIZADA.getCodigo()};
            }
            final Collection<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            return ((soaList != null) && !soaList.isEmpty());
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se possui solicitacao de saldo para informação e não possui solicitacao de saldo para liquidação.
     *
     * @param adeCodigo
     * @param responsavel
     * @return
     * @throws SimulacaoControllerException
     */
    @Override
    public boolean temSolicitacaoSaldoInformacaoApenas(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            //Verifica na tb_solicitacao_autorizacao se é tis_codigo 1
            //Se tis_codigo = 1 não é obrigatorio anexo do boleto
            final String[] tisCodigosSaldoInf = {TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo()};
            final String[] ssoCodigosSaldoInf = {StatusSolicitacaoEnum.PENDENTE.getCodigo()};

            final Collection<SolicitacaoAutorizacao> solicitacaoSaldoInf = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigosSaldoInf, ssoCodigosSaldoInf);

            //Se tis_codigo = 1 não é obrigatorio anexo do boleto
            final String[] tisCodigosSaldoLiq = {TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo()};
            final String[] ssoCodigosSaldoLiq = {StatusSolicitacaoEnum.PENDENTE.getCodigo()};
            final Collection<SolicitacaoAutorizacao> solicitacaoSaldoLiq = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigosSaldoLiq, ssoCodigosSaldoLiq);

            return ((solicitacaoSaldoInf != null) && !solicitacaoSaldoInf.isEmpty()) && ((solicitacaoSaldoLiq == null) || solicitacaoSaldoLiq.isEmpty());

        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Finaliza as solicitações de saldo devedor pendentes de liquidação de contrato.
     * @param adeCodigo   : Contrato a ser verificado se existe solicitação de saldo
     * @param responsavel : Responsável pela operação
     * @throws SaldoDevedorControllerException
     */
    @Override
    public void finalizaSolicitacaoSaldoDevedorLiquidacaoContrato(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO.getCodigo()};
            final StatusSolicitacaoEnum novoStatus = StatusSolicitacaoEnum.FINALIZADA;
            final Collection<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, StatusSolicitacaoEnum.PENDENTE.getCodigo());

            if ((soaList != null) && !soaList.isEmpty()) {
                for (final SolicitacaoAutorizacao solicitacaoAutorizacao : soaList) {
                    solicitacaoAutorizacao.setStatusSolicitacao(new StatusSolicitacao(novoStatus.getCodigo()));
                    AbstractEntityHome.update(solicitacaoAutorizacao);
                }
            }
        } catch (final FindException ex) {
            throw new SaldoDevedorControllerException("mensagem.erro.recuperar.solicitacao.saldo.devedor", responsavel, ex);
        } catch (final UpdateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException("mensagem.erro.recuperar.solicitacao.saldo.devedor", responsavel, ex);
        }
    }

    /**
     * Atualiza as solicitações de saldo devedor pendentes para o novo status indicado por parâmetro.
     * @param adeCodigo   : Contrato a ser verificado se existe solicitação de saldo
     * @param novoStatus  : Novo status a ser atribuido à solicitação de saldo
     * @param responsavel : Responsável pela operação
     * @throws SaldoDevedorControllerException
     */
    @Override
    public void atualizaStatusSolicitacaoSaldoDevedor(String adeCodigo, StatusSolicitacaoEnum novoStatus, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo(), TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo(), TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO.getCodigo()};
            final Collection<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, StatusSolicitacaoEnum.PENDENTE.getCodigo());

            if ((soaList != null) && !soaList.isEmpty()) {
                for (final SolicitacaoAutorizacao solicitacaoAutorizacao : soaList) {
                    solicitacaoAutorizacao.setStatusSolicitacao(new StatusSolicitacao(novoStatus.getCodigo()));
                    AbstractEntityHome.update(solicitacaoAutorizacao);
                }
            }
        } catch (final FindException ex) {
            throw new SaldoDevedorControllerException("mensagem.erro.recuperar.solicitacao.saldo.devedor", responsavel, ex);
        } catch (final UpdateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException("mensagem.erro.recuperar.solicitacao.saldo.devedor", responsavel, ex);
        }
    }

    /**
     * Verifica se o link para o boleto de pagamento do saldo devedor deve ser exibido
     * para o usuário responsável.
     * @param adeCodigo
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public boolean exibeLinkBoletoSaldo(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            boolean exibirLinkBoletoSdv = false;
            if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_LINK_BOLETO_SALDO_DEVEDOR, CodedValues.TPC_SIM, responsavel)) {
                // Verifica se o saldo existe e tem link de boleto cadastrado
                final SaldoDevedorTransferObject sdvBean = getSaldoDevedor(adeCodigo, false, responsavel);
                if ((sdvBean != null) && !TextHelper.isNull(sdvBean.getSdvLinkBoletoQuitacao())) {
                    // Verifica se a consignação está ativa
                    final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
                    final String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo();
                    if (!CodedValues.SAD_CODIGOS_INATIVOS.contains(sadCodigo)) {
                        // Verifica pelo papel do usuário se deve exibir o link
                        if (responsavel.isCsaCor()) {
                            final String csaCodigo = ConsignatariaHome.findByAdeCodigo(adeCodigo).getCsaCodigo();

                            // Se é usuário da csa/cor dona da consignação, exibe o ícone de boleto de saldo devedor
                            if (csaCodigo.equals(responsavel.getCsaCodigo())) {
                                exibirLinkBoletoSdv = true;
                            } else if (CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo)) {
                                // Caso não seja a csa/cor dona da consignação, verifica se é uma compradora
                                // e se a consignação está aguard. liq. de compra, ou seja, se a csa/cor está comprando esta consignação
                                final List<String> sadCodigos = new ArrayList<>();
                                sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
                                sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);

                                final Collection<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA, sadCodigos);
                                if ((adesCompra != null) && !adesCompra.isEmpty()) {
                                    final RelacionamentoAutorizacao radBean = adesCompra.iterator().next();
                                    final String csaCodigoDst = radBean.getConsignatariaByCsaCodigoDestino().getCsaCodigo();
                                    if (csaCodigoDst.equals(responsavel.getCsaCodigo())) {
                                        exibirLinkBoletoSdv = true;
                                    }
                                }
                            }
                        } else if (responsavel.isCseSup()) {
                            // Exibe para CseSup sempre que estiver cadastrado
                            exibirLinkBoletoSdv = true;
                        } else if (responsavel.isSer()) {
                            // Exibe para servidor apenas se tiver solicitação de saldo
                            exibirLinkBoletoSdv = (CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo)) &&
                                    temSolicitacaoSaldoDevedor(adeCodigo, false, responsavel);
                        }
                    }
                }
            }
            return exibirLinkBoletoSdv;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Informar pagamento de saldo com comprovante em anexo.
     *
     * @param adeCodigo
     * @param idAnexo
     * @param aadNome
     * @param aadDescricao
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public boolean informarComprovantePagamentoSaldoDevedor(String adeCodigo, String idAnexo, String aadNome, String aadDescricao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            // Verificar se é um usuário servidor/cor/csa e se possui permissão para anexar comprovante de pagamento de saldo
            if ((!responsavel.isSer() && !responsavel.isCsaCor()) || !responsavel.temPermissao(CodedValues.FUN_ANEXAR_COMPROVANTE_PAG_SALDO)) {
                throw new SaldoDevedorControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
            }


            final boolean permiteBloqCsaNaoLiqAdePagoAnexoSer = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, responsavel);
            final boolean temSolicitacaoSaldoDevedorLiquidacao = temSolicitacaoSaldoDevedorLiquidacaoRespondida(adeCodigo, responsavel);
            if (responsavel.isSer() && (!permiteBloqCsaNaoLiqAdePagoAnexoSer || !temSolicitacaoSaldoDevedorLiquidacao)) {
                throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel);
            }

            if (TextHelper.isNull(idAnexo) || TextHelper.isNull(aadNome)) {
                throw new SaldoDevedorControllerException("mensagem.informar.pagamento.saldo.devedor.anexo.obrigatorio", responsavel);
            }

            // Obtém os entity beans necessários
            final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);

            // Verifica se é permitido a solicitação de saldo deste contrato
            final String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo().trim();

            if (!CodedValues.SAD_DEFERIDA.equals(sadCodigo) &&
                    !CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) &&
                    !CodedValues.SAD_ESTOQUE.equals(sadCodigo) &&
                    !CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) &&
                    !CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) &&
                    !CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) &&
                    !CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) {
                throw new SaldoDevedorControllerException("mensagem.informar.pagamento.saldo.devedor.anexo.status.invalido", responsavel);
            }

            /**
             * Inclui solicitação de autorização tipo liquidação de contrato,
             * se já não tiver solicitação de liquidação de contrato,
             * com a data da execução da operação de anexo de comprovante e a
             * validade dada pelo parâmetro de quantidade de dias para bloqueio de csa.
             */
            if (!temSolicitacaoLiquidacaoContrato(adeCodigo, true, responsavel)) {
                SolicitacaoAutorizacaoHome.create(adeCodigo, responsavel.getUsuCodigo(), TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO.getCodigo(), StatusSolicitacaoEnum.PENDENTE.getCodigo(), null);
            }

            // Anexo
            final File anexo = UploadHelper.moverArquivoAnexoTemporario(aadNome, adeCodigo, idAnexo, responsavel);
            if ((anexo != null) && anexo.exists()) {
                aadDescricao = (!TextHelper.isNull(aadDescricao) && (aadDescricao.length() <= 255)) ? aadDescricao : anexo.getName();
                final Date aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(adeBean.getVerbaConvenio().getConvenio().getOrgao().getOrgCodigo(), responsavel);
                editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), aadDescricao, new java.sql.Date(aadPeriodo.getTime()), TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_COMPROVANTE_PAGAMENTO, responsavel);
            } else {
                throw new AutorizacaoControllerException("mensagem.informar.pagamento.saldo.devedor.anexo.falha.upload", responsavel);
            }

            return true;
        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException("mensagem.informar.pagamento.saldo.devedor.anexo.ja.cadastradas", responsavel, ex);
        } catch (final FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ParametroControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException(ex);
        }
    }

    /**
     * Lista solicitação de liquidação de contrato com saldo pago e anexo e não liquidado.
     *
     * @param adeCodigo
     * @param idAnexo
     * @param aadNome
     * @param aadDescricao
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public List<ConsignatariaTransferObject> lstSolicitacaoSaldoPagoComAnexoNaoLiquidado(String csaCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final ListaSolicitacaoSaldoPagoComAnexoNaoLiquidadoQuery query = new ListaSolicitacaoSaldoPagoComAnexoNaoLiquidadoQuery();
            query.csaCodigo = csaCodigo;
            return query.executarDTO(ConsignatariaTransferObject.class);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<SolicitacaoAutorizacao> lstSolicitacaoSaldoExclusaoPendente(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO.getCodigo()};
            final String ssoCodigo = StatusSolicitacaoEnum.PENDENTE.getCodigo();
            return SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Busca as solicitações de saldo devedor de exclusão
     * @param adeCodigo   : Contrato a ser verificado se existe solicitação de saldo
     * @param responsavel : Responsável pela operação
     * @throws SaldoDevedorControllerException
     */
    @Override
    public List<SolicitacaoAutorizacao> lstSolicitacaoSaldoExclusao(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            return SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, new String[]{TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO.getCodigo()});
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erro.recuperar.solicitacao.saldo.devedor", responsavel, ex);
        }
    }

    /**
     * Solicitação de saldo devedor para rescisão contratual do servidor/colaborador.
     * Solicita o saldo devedor para todas as consignatárias que possuem contratos ativos de natureza empréstimo com o servidor/colaborador.
     * @param rseCodigo
     * @param obs
     * @param responsavel
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public String solicitarSaldoDevedorRescisao(String rseCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            String resultado = "";

            // Recupera parâmetro de validade em dias do saldo devedor para rescisão contratual
            int qtdeDiasValidadeSdv = 0;
            final String paramQtdeDiasValidadeSdv = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_EXPIRACAO_INF_SALDO_DEVEDOR_RESCISAO, responsavel);
            if (!TextHelper.isNull(paramQtdeDiasValidadeSdv)) {
                qtdeDiasValidadeSdv = Integer.parseInt(paramQtdeDiasValidadeSdv);
            }

            // Verifica se é permitido a solicitação de saldo deste servidor
            final RegistroServidorTO rse = servidorController.findRegistroServidor(rseCodigo, responsavel);
            if (!CodedValues.SRS_BLOQUEADOS.contains(rse.getSrsCodigo()) && !CodedValues.SRS_EXCLUIDO.equals(rse.getSrsCodigo()) && !CodedValues.SRS_ATIVO.equals(rse.getSrsCodigo())) {
                throw new SaldoDevedorControllerException("mensagem.erro.saldo.devedor.exclusao.servidor.status", responsavel);
            }

            // lista de status de autorização que permitem solicitação de saldo
            final List<String> sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_ESTOQUE);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
            sadCodigos.add(CodedValues.SAD_EMCARENCIA);

            // servicos da natureza Empréstimo
            final ListaServicoNaturezaServicoQuery lstServicos = new ListaServicoNaturezaServicoQuery();
            lstServicos.nseCodigo = CodedValues.NSE_EMPRESTIMO;
            final List<TransferObject> lstSvcs = lstServicos.executarDTO();

            final List<String> svcCodigos = new ArrayList<>();
            for (final TransferObject servico : lstSvcs) {
                svcCodigos.add((String) servico.getAttribute(Columns.SVC_CODIGO));
            }

            // Lista contratos ativos de natureza empréstimo do servidor
            final List<TransferObject> ades = pesquisarConsignacaoController.pesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), rseCodigo, null, null, sadCodigos, svcCodigos, null, responsavel);

            // Solicita o saldo devedor para todas as consignatárias que possuem contratos ativos de natureza empréstimo com o servidor
            if ((ades != null) && !ades.isEmpty()) {
                SaldoDevedorTransferObject saldoDevedorTO = null;
                for (final TransferObject ade : ades) {
                    boolean solicitarSaldo = true;
                    // Verifica se a ade já possui saldo devedor válido
                    saldoDevedorTO = getSaldoDevedor(ade.getAttribute(Columns.ADE_CODIGO).toString(), false, responsavel);
                    // Verifica se a data de validade do saldo devedor existente está dentro do período válido.
                    // A data de validade deve ser maior que a data atual + qtde dias para expiração configurado no parâmetro.
                    if (((saldoDevedorTO != null) && (saldoDevedorTO.getSdvDataValidade() != null)) && saldoDevedorTO.getSdvDataValidade().after(DateHelper.addDays(new Date(), qtdeDiasValidadeSdv))) {
                        solicitarSaldo = false;
                    }
                    if (solicitarSaldo) {
                        final Date soaDataValidade = DateHelper.addDays(new Date(), qtdeDiasValidadeSdv);
                        resultado = solicitarSaldoDevedor(ade.getAttribute(Columns.ADE_CODIGO).toString(), obs, true, false, true, soaDataValidade, 0, true, responsavel);
                    }
                }
            }
            return resultado;
        } catch (final SaldoDevedorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Alteração da taxa de juros do contrato
     * @param adeCodigo
     * @param adeTaxaJuros
     * @return
     * @throws SaldoDevedorControllerException
     */
    @Override
    public void alterarAdeTaxaJuros(String adeCodigo, BigDecimal adeTaxaJuros, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        try {
            AutDescontoHome.updateAdeTaxaJuros(adeCodigo, adeTaxaJuros);
        } catch (final UpdateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new SaldoDevedorControllerException("mensagem.erro.saldo.devedor.alterar.taxa.juros.contrato", responsavel, ex);
        }
    }

    /**
     * Cria comunicação e envia email caso tenha proposta ao informar ou editar o saldo devedor
     * @param comunicacao
     * @return
     * @throws ZetraException
     */
    private void criarComunicacaoEmail(CustomTransferObject comunicacao, AcessoSistema responsavel) throws ZetraException {
        final String serEmail = comunicacao.getAttribute(Columns.SER_EMAIL) != null ? comunicacao.getAttribute(Columns.SER_EMAIL).toString() : "";
        final String textoProposta = comunicacao.getAttribute(Columns.CMN_TEXTO).toString();
        final String adeCodigo = comunicacao.getAttribute(Columns.ADE_CODIGO).toString();

        comunicacaoController.createComunicacao(comunicacao, responsavel);

        if (!TextHelper.isNull(serEmail)) {
            EnviaEmailHelper.enviarEmailRefinanciamentoParcela(adeCodigo, textoProposta, responsavel);
        }

        // Registra log de auditoria
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.SALDO_DEVEDOR, Log.PROPOSTA_REFINANCIAMENTO_SALDO_DEVEDOR, Log.LOG_INFORMACAO);
            log.add(textoProposta);
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

}
