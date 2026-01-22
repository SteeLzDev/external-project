package com.zetra.econsig.service.rescisao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.VerbaRescisoriaControllerException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusVerbaRescisoria;
import com.zetra.econsig.persistence.entity.StatusVerbaRescisoriaHome;
import com.zetra.econsig.persistence.entity.VerbaRescisoriaRse;
import com.zetra.econsig.persistence.entity.VerbaRescisoriaRseHome;
import com.zetra.econsig.persistence.query.rescisao.ListarColaboradoresReterVerbaRescisoriaQuery;
import com.zetra.econsig.persistence.query.rescisao.ListarContratosReterVerbaRescisoriaQuery;
import com.zetra.econsig.persistence.query.rescisao.ListarContratosSaldoDevedorPendenteQuery;
import com.zetra.econsig.persistence.query.rescisao.ListarContratosVerbaRescisoriaConcluidaQuery;
import com.zetra.econsig.persistence.query.rescisao.ListarVerbaRescisoriaQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoNaturezaServicoQuery;
import com.zetra.econsig.service.cartaocredito.ValidadorCartaoCreditoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReservarMargemController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusVerbaRescisoriaEnum;

/**
 * <p>Title: VerbaRescisoriaControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class VerbaRescisoriaControllerBean implements VerbaRescisoriaController {

    @Autowired
    SaldoDevedorController saldoDevedorController;

    @Autowired
    PesquisarConsignacaoController pesquisaAdeController;

    @Autowired
    CancelarConsignacaoController cancelarController;

    @Autowired
    LiquidarConsignacaoController liquidarController;

    @Autowired
    AutorizacaoController autorizacaoController;

    @Autowired
    ValidadorCartaoCreditoController validadorCartaoCreditoController;

    @Autowired
    @Qualifier("reservarMargemController")
    private ReservarMargemController reservarMargemController;

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VerbaRescisoriaControllerBean.class);

    @Override
    public List<TransferObject> listarVerbaRescisoriaRse(List<String> svrCodigos, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            final ListarVerbaRescisoriaQuery query = new ListarVerbaRescisoriaQuery();
            query.svrCodigos = svrCodigos;
            if (responsavel.isOrg()) {
                query.orgCodigo = responsavel.getOrgCodigo();
            }
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new VerbaRescisoriaControllerException(ex);
        }
    }

    @Override
    public int countVerbaRescisoriaRse(List<String> svrCodigos, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            final ListarVerbaRescisoriaQuery query = new ListarVerbaRescisoriaQuery();
            query.count = true;
            query.svrCodigos = svrCodigos;
            if (responsavel.isOrg()) {
                query.orgCodigo = responsavel.getOrgCodigo();
            }
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new VerbaRescisoriaControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listarVerbaRescisoriaRse(CustomTransferObject criterio, int offset, int size, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            final ListarVerbaRescisoriaQuery query = new ListarVerbaRescisoriaQuery();
            if (offset != -1) {
                query.firstResult = offset;
            }
            if (size != -1) {
                query.maxResults = size;
            }
            if (criterio.getAttribute(Columns.VRR_SVR_CODIGO) != null) {
                query.svrCodigos = (List<String>) criterio.getAttribute(Columns.VRR_SVR_CODIGO);
            }
            if (responsavel.isOrg()) {
                query.orgCodigo = responsavel.getOrgCodigo();
            }
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new VerbaRescisoriaControllerException(ex);
        }
    }

    @Override
    public void removerVerbaRescisoriaRse(String vrrCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            // Recupera a verba
            final VerbaRescisoriaRse verbaRescisoriaRse = VerbaRescisoriaRseHome.findByPrimaryKey(vrrCodigo);
            AbstractEntityHome.remove(verbaRescisoriaRse);
        } catch (FindException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new VerbaRescisoriaControllerException(ex);
        }
    }

    @Override
    public int countColaboradoresReterVerbaRescisoria(AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            final ListarColaboradoresReterVerbaRescisoriaQuery query = new ListarColaboradoresReterVerbaRescisoriaQuery();
            query.count = true;
            query.notSvrCodigos = Arrays.asList(StatusVerbaRescisoriaEnum.CANDIDATO.getCodigo());

            if (responsavel.isOrg()) {
                query.orgCodigo = responsavel.getOrgCodigo();
            }

            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new VerbaRescisoriaControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listarColaboradoresReterVerbaRescisoria(int offset, int size, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            final ListarColaboradoresReterVerbaRescisoriaQuery query = new ListarColaboradoresReterVerbaRescisoriaQuery();
            query.notSvrCodigos = Arrays.asList(StatusVerbaRescisoriaEnum.CANDIDATO.getCodigo());

            if (responsavel.isOrg()) {
                query.orgCodigo = responsavel.getOrgCodigo();
            }

            if (offset != -1) {
                query.firstResult = offset;
            }
            if (size != -1) {
                query.maxResults = size;
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new VerbaRescisoriaControllerException(ex);
        }
    }
    @Override
    public void incluirCandidatoVerbaRescisoria(String rseCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            // Valida o status do registro servidor
            final RegistroServidor rse = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            if (!CodedValues.SRS_ATIVOS.contains(rse.getStatusRegistroServidor().getSrsCodigo())) {
                throw new VerbaRescisoriaControllerException("mensagem.erro.incluir.colaborador.rescisao.inativo", responsavel);
            }

            // Valida se o registro servidor já foi incluso no processo de rescisão em outro momento
            final List<VerbaRescisoriaRse> verbaRescisoriaRse = VerbaRescisoriaRseHome.findByPrimaryRseCodigo(rseCodigo);
            if ((verbaRescisoriaRse != null) && !verbaRescisoriaRse.isEmpty()) {
                throw new VerbaRescisoriaControllerException("mensagem.erro.incluir.colaborador.rescisao.existente", responsavel);
            }

            // Inclui o registro servidor como candidato no processo de rescisão contratual
            VerbaRescisoriaRseHome.create(rseCodigo, StatusVerbaRescisoriaEnum.CANDIDATO.getCodigo());

        } catch (final VerbaRescisoriaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new VerbaRescisoriaControllerException("mensagem.erro.interno.contate.administrador", responsavel, ex);
        }
    }

    @Override
    public String processarInclusaoColaborador(TransferObject colaborador, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        // Observação para criação da ocorrência de solicitação de saldo devedor
        final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.solicitar.saldo.devedor.ocorrencia.obs.rescisao", responsavel, responsavel.getUsuNome());
        final StringBuilder msgErro = new StringBuilder();

        // Situações do contrato que permite cancelar ou liquidar
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.NOT_EQUAL_KEY);
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);

        try {
            // Recupera para cancelamento/liquidação serviços de naturezas diferentes de Empréstimo, que não participam da rescisão de verba,
            // ou serviços da natureza Cartão que participam da rescisão e são de reserva de cartão
            final ListaServicoNaturezaServicoQuery lstServicos = new ListaServicoNaturezaServicoQuery();
            final List<TransferObject> lstSvcs = lstServicos.executarDTO();
            final List<String> svcCodigos = new ArrayList<>();
            for (final TransferObject servico : lstSvcs) {
                final boolean isNaoEmprestimoNaoParticipaRescisao = !CodedValues.NSE_EMPRESTIMO.equals(servico.getAttribute(Columns.NSE_CODIGO)) && CodedValues.CAS_NAO.equals(servico.getAttribute(Columns.NSE_RETEM_VERBA));
                final boolean isCartaoParticipaRescisao =  CodedValues.NSE_CARTAO.equals(servico.getAttribute(Columns.NSE_CODIGO)) && CodedValues.CAS_SIM.equals(servico.getAttribute(Columns.NSE_RETEM_VERBA));
                final boolean isReservaCartao = validadorCartaoCreditoController.isReservaCartao(servico.getAttribute(Columns.SVC_CODIGO).toString());

                if (isNaoEmprestimoNaoParticipaRescisao || (isCartaoParticipaRescisao && isReservaCartao)) {
                    svcCodigos.add((String) servico.getAttribute(Columns.SVC_CODIGO));
                }
            }

            // Verifica se pode concluir a verba rescisória:
            // será verdadeiro caso o candidato não possua contratos de Empréstimo ou seus contratos já tenham saldo devedor válido
            boolean podeConcluirVerbaRescisoria = false;

            final String rseCodigo = (String) colaborador.getAttribute(Columns.RSE_CODIGO);

            // Bloqueia os colaboradores candidatos
            RegistroServidorHome.bloquearRegistroServidor(rseCodigo);

            // Solicitar saldo devedor para exclusão de contratos da natureza Empréstimo
            final String resultado = saldoDevedorController.solicitarSaldoDevedorRescisao(rseCodigo, ocaObs, responsavel);
            final boolean criaAutoContratoRescisaoAposSdv = ParamSist.paramEquals(CodedValues.TPC_CRIA_AUTO_CONTRATO_RESCISAO_APOS_INFO_SALDO_DEVEDOR, CodedValues.TPC_SIM, responsavel);
            if (TextHelper.isNull(resultado) || criaAutoContratoRescisaoAposSdv) {
                podeConcluirVerbaRescisoria = true;
            }

            // Liquidar/Cancelar contratos de naturezas diferente de Empréstimo, se houverem serviços que não são empréstimo
            if (!svcCodigos.isEmpty()) {
                final List<TransferObject> ades = pesquisaAdeController.pesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), rseCodigo, null, null, sadCodigos, svcCodigos, null, responsavel);
                if ((ades != null) && !ades.isEmpty()) {
                    for (final TransferObject ade : ades) {
                        final String sadCodigo = (String) ade.getAttribute(Columns.SAD_CODIGO);
                        final String adeCodigo = (String) ade.getAttribute(Columns.ADE_CODIGO);
                        if (CodedValues.SAD_SOLICITADO.equals(sadCodigo) ||
                                CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) ||
                                CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo) ||
                                CodedValues.SAD_AGUARD_MARGEM.equals(sadCodigo)) {
                            // Cancela o contrato
                            cancelarController.cancelar(adeCodigo, null, responsavel);
                        } else {
                            // Liquida o contrato
                            liquidarController.liquidar(adeCodigo, null, null, responsavel);
                        }
                    }
                }
            }

            // Modifica status da verba rescisória do colaborador
            String svrCodigo = StatusVerbaRescisoriaEnum.AGUARDANDO_VERBA_RESCISORIA.getCodigo();
            if (podeConcluirVerbaRescisoria) {
                svrCodigo = StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo();
            }
            final StatusVerbaRescisoria svr = StatusVerbaRescisoriaHome.findByPrimaryKey(svrCodigo);
            final VerbaRescisoriaRse vrr = VerbaRescisoriaRseHome.findByPrimaryKey((String) colaborador.getAttribute(Columns.VRR_CODIGO));
            vrr.setStatusVerbaRescisoria(svr);
            if(criaAutoContratoRescisaoAposSdv) {
                vrr.setVrrValor(null);
                vrr.setVrrDataUltAtualizacao(new Date());
            }
            AbstractEntityHome.update(vrr);
        } catch (SaldoDevedorControllerException | AutorizacaoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            // Cria mensagem de erro para retorno
            final StringBuilder matricula = new StringBuilder().append(ApplicationResourcesHelper.getMessage("rotulo.matricula.singular", responsavel));
            matricula.append(": ").append((String) colaborador.getAttribute(Columns.RSE_MATRICULA));
            msgErro.append(matricula.append(" - ").append(ex.getMessage()).toString());
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            // Cria mensagem de erro para retorno
            final StringBuilder matricula = new StringBuilder().append(ApplicationResourcesHelper.getMessage("rotulo.matricula.singular", responsavel));
            matricula.append(": ").append((String) colaborador.getAttribute(Columns.RSE_MATRICULA));
            msgErro.append(matricula.append(" - ").append(ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel)).toString());
        }
        return msgErro.toString();
    }

    @Override
    public List<TransferObject> listarContratosReterVerbaRescisoria(String vrrCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            // Recupera a verba rescisória
            final VerbaRescisoriaRse vrr = VerbaRescisoriaRseHome.findByPrimaryKey(vrrCodigo);
            // Recupera os contratos elegíveis para retenção de verba rescisória
            final ListarContratosReterVerbaRescisoriaQuery query = new ListarContratosReterVerbaRescisoriaQuery();
            query.rseCodigo = vrr.getRegistroServidor().getRseCodigo();
            return query.executarDTO();
        } catch (HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new VerbaRescisoriaControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> calcularPreviaPagamentoVerbaRescisoria(String vrrCodigo, BigDecimal vrrValor, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        // Recupera os contratos elegíveis para retenção de verba rescisória
        final List<TransferObject> contratos = listarContratosReterVerbaRescisoria(vrrCodigo, responsavel);
        if ((contratos != null) && !contratos.isEmpty()) {
            for (final TransferObject contrato : contratos) {
                BigDecimal valorPrevistoPagamento = BigDecimal.ZERO;
                // Verifica se tem verba rescisória para ser utilizada
                if (vrrValor.compareTo(BigDecimal.ZERO) > 0) {
                    // Recupera valor do saldo devedor do contrato
                    final BigDecimal sdvValor = ((BigDecimal) contrato.getAttribute(Columns.SDV_VALOR));
                    // Verifica se a verba rescisória permite o pagamento total ou parcial do saldo devedor do contrato
                    if ((sdvValor != null) && (vrrValor.compareTo(sdvValor) >= 0)) {
                        // Previsão para pagamento total
                        valorPrevistoPagamento = sdvValor;
                        // Subtrai do valor disponível de verba rescisória o valor pago de saldo devedor
                        vrrValor = vrrValor.subtract(sdvValor);
                    } else if ((sdvValor != null) && (vrrValor.compareTo(sdvValor) < 0)) {
                    	valorPrevistoPagamento = vrrValor;
                    	vrrValor = vrrValor.subtract(sdvValor);
                        contrato.setAttribute("EXISTE_PAGAMENTO_PARCIAL", Boolean.TRUE);
                    } else {
                        final String adeCodigo = (String) contrato.getAttribute(Columns.ADE_CODIGO);
                        //Cálculo automático do saldo devedor
                        BigDecimal sdvValorCalculadoAuto;
                        try {
                            sdvValorCalculadoAuto = saldoDevedorController.calcularSaldoDevedor(adeCodigo, false, responsavel);

                            if(sdvValorCalculadoAuto.compareTo(vrrValor) > 0) {
                                // Previsao para pagamento parcial
                                valorPrevistoPagamento = vrrValor;

                                //DESENV-19721 precisamos ter uma marcação no contrato para saber que está sendo pago valor parcial, com isso criarmos ocorrência de que existe saldo remanescente
                                contrato.setAttribute("EXISTE_PAGAMENTO_PARCIAL", Boolean.TRUE);
                            } else {
                                valorPrevistoPagamento = sdvValorCalculadoAuto;

                                // Subtrai do valor disponível de verba rescisória o valor calculado automaticamente do saldo devedor
                                vrrValor = vrrValor.subtract(sdvValorCalculadoAuto);
                            }
                        } catch (final SaldoDevedorControllerException e) {
                        }
                    }
                }
                // Valor previsto para pagamento do saldo devedor do contrato
                contrato.setAttribute("VALOR_PREVIA_PAGTO", valorPrevistoPagamento);
            }
        }

        return contratos;
    }

    @Override
    public void confirmarVerbaRescisoria(BigDecimal sdvValor, String adeCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException  {
        try {
            final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
            final String adeTipoVlr = adeBean.getAdeTipoVlr();
            final String rseCodigo = adeBean.getRseCodigo();
            final String corCodigo = adeBean.getCorCodigo();
            final String cnvCodigo = adeBean.getVerbaConvenio().getCnvCodigo();
            final String vcoCodigo = adeBean.getVcoCodigo();
            final String csaCodigo = adeBean.getVerbaConvenio().getConvenio().getCsaCodigo();

            final ReservarMargemParametros reservaParam = new ReservarMargemParametros();

            reservaParam.setRseCodigo(rseCodigo);
            reservaParam.setAdeVlr(sdvValor);
            reservaParam.setAdeTipoVlr(adeTipoVlr);
            reservaParam.setCorCodigo(corCodigo);
            reservaParam.setAdePrazo(1);
            reservaParam.setCnvCodigo(cnvCodigo);
            reservaParam.setAdeIdentificador(ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.verba.rescisoria", responsavel));
            reservaParam.setSadCodigo(CodedValues.SAD_DEFERIDA);
            reservaParam.setAdeIncMargem(CodedValues.INCIDE_MARGEM_NAO);
            reservaParam.setAdeIntFolha(CodedValues.INTEGRA_FOLHA_NAO);
            reservaParam.setAdeVlrLiquido(sdvValor);
            reservaParam.setAdeCarencia(0);
            reservaParam.setSerSenha(null);
            reservaParam.setComSerSenha(Boolean.FALSE);
            reservaParam.setValidar(Boolean.FALSE);
            reservaParam.setValidaLimiteAde(Boolean.FALSE);
            reservaParam.setValidaMargem(Boolean.FALSE);
            reservaParam.setPermitirValidacaoTaxa(Boolean.FALSE);
            reservaParam.setValidaTaxaJuros(Boolean.FALSE);
            reservaParam.setSerAtivo(Boolean.FALSE);
            reservaParam.setCnvAtivo(Boolean.FALSE);
            reservaParam.setSerCnvAtivo(Boolean.FALSE);
            reservaParam.setSvcAtivo(Boolean.FALSE);
            reservaParam.setCsaAtivo(Boolean.FALSE);
            reservaParam.setOrgAtivo(Boolean.FALSE);
            reservaParam.setEstAtivo(Boolean.FALSE);
            reservaParam.setCseAtivo(Boolean.FALSE);
            reservaParam.setValidaAnexo(Boolean.FALSE);
            reservaParam.setIsRetencaoVerbaRescisoria(Boolean.TRUE);
            reservaParam.setAcao("RESERVAR");

            final String adeCodigoNovo = reservarMargemController.reservarMargem(reservaParam, responsavel);

            RelacionamentoAutorizacaoHome.create(adeCodigo, adeCodigoNovo, CodedValues.TNT_VERBA_RESCISORIA, responsavel.getUsuCodigo());

            final LogDelegate log = new LogDelegate(responsavel, Log.RELACIONAMENTO_AUTORIZACAO, Log.RESERVAR_MARGEM, Log.LOG_INFORMACAO);
            log.setAutorizacaoDesconto(adeCodigo);
            log.setAutorizacaoDescontoDestino(adeCodigoNovo);
            log.setRegistroServidor(rseCodigo);
            log.setVerbaConvenio(vcoCodigo);
            log.setStatusAutorizacao(CodedValues.SAD_DEFERIDA);
            log.addChangedField(Columns.ADE_VLR, sdvValor);
            log.addChangedField(Columns.ADE_PRAZO, Integer.valueOf(1));
            log.addChangedField(Columns.ADE_INC_MARGEM, CodedValues.INCIDE_MARGEM_NAO);
            log.write();

            liquidarController.liquidar(adeCodigo, null, null, responsavel);

            EnviaEmailHelper.enviarEmailCsaNovoContratoVerbaRescisoria(csaCodigo, adeCodigoNovo, responsavel);

            final boolean tpcEnviarEmailSerNovoContratoSdvRescisao = (ParamSist.getInstance().getParam(CodedValues.TPC_ENVIA_EMAIL_SER_NOVO_CONTRATO_PAG_SALDO_DEVEDOR_RESCISAO, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_ENVIA_EMAIL_SER_NOVO_CONTRATO_PAG_SALDO_DEVEDOR_RESCISAO, responsavel));
            if(tpcEnviarEmailSerNovoContratoSdvRescisao) {
                EnviaEmailHelper.enviarEmailSerNovoContratoVerbaRescisoria(rseCodigo, adeCodigoNovo, responsavel);
            }
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new VerbaRescisoriaControllerException(ex);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new VerbaRescisoriaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void confirmarVerbaRescisoria(String vrrCodigo, BigDecimal vrrValor, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            // Recupera a prévia de pagamento de contratos com relação ao valor informado
            final List<TransferObject> contratos = calcularPreviaPagamentoVerbaRescisoria(vrrCodigo, vrrValor, responsavel);

            if ((contratos != null) && !contratos.isEmpty()) {
                // Verifica valor para retenção de verba rescisória
                if ((vrrValor != null) && (vrrValor.compareTo(new BigDecimal(0.00)) > 0)) {
                    for (final TransferObject contrato : contratos) {
                        final BigDecimal valorPagto = (BigDecimal) contrato.getAttribute("VALOR_PREVIA_PAGTO");
                        // Verifica se o contrato tem valor previsto para pagamento
                        if (valorPagto.compareTo(new BigDecimal(0.00)) > 0) {
                            // Cria novo contrato para pagamento do saldo devedor (total ou parcial)
                            final String adeCodigo = (String) contrato.getAttribute(Columns.ADE_CODIGO);
                            final String adeTipoVlr = (String) contrato.getAttribute(Columns.ADE_TIPO_VLR);
                            final String rseCodigo = (String) contrato.getAttribute(Columns.RSE_CODIGO);
                            final String corCodigo = (String) contrato.getAttribute(Columns.COR_CODIGO);
                            final String cnvCodigo = (String) contrato.getAttribute(Columns.CNV_CODIGO);
                            final String vcoCodigo = (String) contrato.getAttribute(Columns.VCO_CODIGO);
                            final String csaCodigo = (String) contrato.getAttribute(Columns.CSA_CODIGO);

                            // Monta o objeto de parâmetro da reserva
                            final ReservarMargemParametros reservaParam = new ReservarMargemParametros();

                            reservaParam.setRseCodigo(rseCodigo);
                            reservaParam.setAdeVlr(valorPagto);
                            reservaParam.setAdeTipoVlr(adeTipoVlr);
                            reservaParam.setCorCodigo(corCodigo);
                            reservaParam.setAdePrazo(1);
                            reservaParam.setCnvCodigo(cnvCodigo);
                            reservaParam.setAdeIdentificador(ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.verba.rescisoria", responsavel));
                            reservaParam.setSadCodigo(CodedValues.SAD_DEFERIDA);
                            reservaParam.setAdeIncMargem(CodedValues.INCIDE_MARGEM_NAO);
                            reservaParam.setAdeIntFolha(CodedValues.INTEGRA_FOLHA_NAO);
                            reservaParam.setAdeVlrLiquido(valorPagto);
                            reservaParam.setAdeCarencia(0);
                            reservaParam.setSerSenha(null);
                            reservaParam.setComSerSenha(Boolean.FALSE);
                            reservaParam.setValidar(Boolean.FALSE);
                            reservaParam.setValidaLimiteAde(Boolean.FALSE);
                            reservaParam.setValidaMargem(Boolean.FALSE);
                            reservaParam.setPermitirValidacaoTaxa(Boolean.FALSE);
                            reservaParam.setValidaTaxaJuros(Boolean.FALSE);
                            reservaParam.setSerAtivo(Boolean.FALSE);
                            reservaParam.setCnvAtivo(Boolean.FALSE);
                            reservaParam.setSerCnvAtivo(Boolean.FALSE);
                            reservaParam.setSvcAtivo(Boolean.FALSE);
                            reservaParam.setCsaAtivo(Boolean.FALSE);
                            reservaParam.setOrgAtivo(Boolean.FALSE);
                            reservaParam.setEstAtivo(Boolean.FALSE);
                            reservaParam.setCseAtivo(Boolean.FALSE);
                            reservaParam.setValidaAnexo(Boolean.FALSE);
                            reservaParam.setIsRetencaoVerbaRescisoria(Boolean.TRUE);
                            reservaParam.setAcao("RESERVAR");
                            // Cria nova ADE
                            final String adeCodigoNovo = reservarMargemController.reservarMargem(reservaParam, responsavel);

                            // Cria relacionamento da nova ADE com a original
                            RelacionamentoAutorizacaoHome.create(adeCodigo, adeCodigoNovo, CodedValues.TNT_VERBA_RESCISORIA, responsavel.getUsuCodigo());

                            // Gera o Log de auditoria
                            final LogDelegate log = new LogDelegate(responsavel, Log.RELACIONAMENTO_AUTORIZACAO, Log.RESERVAR_MARGEM, Log.LOG_INFORMACAO);
                            log.setAutorizacaoDesconto(adeCodigo);
                            log.setAutorizacaoDescontoDestino(adeCodigoNovo);
                            log.setRegistroServidor(rseCodigo);
                            log.setVerbaConvenio(vcoCodigo);
                            log.setStatusAutorizacao(CodedValues.SAD_DEFERIDA);
                            log.addChangedField(Columns.ADE_VLR, valorPagto);
                            log.addChangedField(Columns.ADE_PRAZO, Integer.valueOf(1));
                            log.addChangedField(Columns.ADE_INC_MARGEM, CodedValues.INCIDE_MARGEM_NAO);
                            log.write();

                            // Envia e-mail de notificação para a consignatária informando que um novo contrato foi criado para
                            // pagamento (total ou parcial) do saldo devedor do contrato
                            EnviaEmailHelper.enviarEmailCsaNovoContratoVerbaRescisoria(csaCodigo, adeCodigoNovo, responsavel);

                            final boolean tpcEnviarEmailSerNovoContratoSdvRescisao = (ParamSist.getInstance().getParam(CodedValues.TPC_ENVIA_EMAIL_SER_NOVO_CONTRATO_PAG_SALDO_DEVEDOR_RESCISAO, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_ENVIA_EMAIL_SER_NOVO_CONTRATO_PAG_SALDO_DEVEDOR_RESCISAO, responsavel));
                            if(tpcEnviarEmailSerNovoContratoSdvRescisao) {
                                // Envia e-mail de notificação para o servidor informando que um novo contrato foi criado para
                                // pagamento (total ou parcial) do saldo devedor do contrato
                                EnviaEmailHelper.enviarEmailSerNovoContratoVerbaRescisoria(rseCodigo, adeCodigoNovo, responsavel);
                            }
                        }
                    }
                }

                // Liquida os contratos originais
                for (final TransferObject contrato : contratos) {
                    final String adeCodigo = (String) contrato.getAttribute(Columns.ADE_CODIGO);
                    // Para os contratos que tiveram pagamento parcial, criamos a ocorrência neste contrato para mapear que ele é um contrato que não será pago completamente.
                    if(contrato.getAttribute("EXISTE_PAGAMENTO_PARCIAL") != null) {
                        autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_PGTO_SALDO_INSUFICIENTE_VERBA_RESCISORIA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.verba.rescisoria.observacao.ocorrencia", responsavel), responsavel);
                    }
                    liquidarController.liquidar(adeCodigo, null, null, responsavel);
                }
            }

            // Salva o valor e atualiza o status da verba rescisória para concluído
            final VerbaRescisoriaRse vrr = VerbaRescisoriaRseHome.findByPrimaryKey(vrrCodigo);
            vrr.setStatusVerbaRescisoria(StatusVerbaRescisoriaHome.findByPrimaryKey(StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo()));
            vrr.setVrrValor(vrrValor);
            vrr.setVrrDataUltAtualizacao(new Date());
            AbstractEntityHome.update(vrr);
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new VerbaRescisoriaControllerException(ex);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new VerbaRescisoriaControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarContratosVerbaRescisoriaConcluida(String vrrCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            // Recupera os contratos criados para reter a verba rescisória do colaborador
            final ListarContratosVerbaRescisoriaConcluidaQuery query = new ListarContratosVerbaRescisoriaConcluidaQuery();
            query.vrrCodigo = vrrCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new VerbaRescisoriaControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listarContratosSaldoDevedorPendente(String vrrCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            // Recupera os contratos com saldo devedor após retenção da verba rescisória do colaborador
            final ListarContratosSaldoDevedorPendenteQuery query = new ListarContratosSaldoDevedorPendenteQuery();
            query.vrrCodigo = vrrCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new VerbaRescisoriaControllerException(ex);
        }
    }

    @Override
    public void createVerbaRescisoriaLote(VerbaRescisoriaRse verbaRescisoriaRse) throws VerbaRescisoriaControllerException {
        try {
            VerbaRescisoriaRseHome.createLote(verbaRescisoriaRse);
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new VerbaRescisoriaControllerException(ex);
        }
    }

    @Override
    public List<AutDesconto> listarConsignacoesReterVerbaRescisoriaSaldoInsuficiente(String rseCodigo, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        try {
            return AutDescontoHome.listaAutDescontoByRseCodigoAndTocCodigo(null, CodedValues.TOC_PGTO_SALDO_INSUFICIENTE_VERBA_RESCISORIA);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new VerbaRescisoriaControllerException(ex);
        }
    }

    @Override
    public void concluirVerbaRescisoria(List<VerbaRescisoriaRse> verbaRescisoriaRse, AcessoSistema responsavel) throws VerbaRescisoriaControllerException {
        if((verbaRescisoriaRse != null) && !verbaRescisoriaRse.isEmpty()) {
            for(final VerbaRescisoriaRse vrr : verbaRescisoriaRse) {
                try {
                    vrr.setStatusVerbaRescisoria(StatusVerbaRescisoriaHome.findByPrimaryKey(StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo()));
                    vrr.setVrrValor(null);
                    vrr.setVrrDataUltAtualizacao(new Date());
                    AbstractEntityHome.update(vrr);
                } catch (FindException | UpdateException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new VerbaRescisoriaControllerException(ex);
                }
            }
        }
    }
}