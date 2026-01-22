package com.zetra.econsig.delegate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.AlongarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReativarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.dto.parametros.SuspenderConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.consignacao.AlongarConsignacaoController;
import com.zetra.econsig.service.consignacao.AlterarConsignacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.AutorizarConsignacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.ConfirmarConsignacaoController;
import com.zetra.econsig.service.consignacao.DeferirConsignacaoController;
import com.zetra.econsig.service.consignacao.InserirSolicitacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReimplantarConsignacaoController;
import com.zetra.econsig.service.consignacao.RenegociarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReservarMargemController;
import com.zetra.econsig.service.consignacao.SuspenderConsignacaoController;
import com.zetra.econsig.service.consignacao.TransferirConsignacaoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ConsignacaoDelegate</p>
 * <p>Description: Delegate para as operações sobre as consignações.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsignacaoDelegate extends AbstractDelegate {

    private AutorizacaoController adeController;
    private ReservarMargemController reservarController;
    private RenegociarConsignacaoController renegociarController;
    private AlongarConsignacaoController alongarController;
    private AlterarConsignacaoController alterarController;
    private CancelarConsignacaoController cancelarController;
    private LiquidarConsignacaoController liquidarController;
    private DeferirConsignacaoController deferirController;
    private ConfirmarConsignacaoController confirmarController;
    private AutorizarConsignacaoController autorizarController;
    private SuspenderConsignacaoController suspenderController;
    private TransferirConsignacaoController transferirController;
    private ReimplantarConsignacaoController reimplantarController;
    private InserirSolicitacaoController insSolicitacaoController;

    public ConsignacaoDelegate() {
    }

    private AutorizacaoController getAutorizacaoController() throws AutorizacaoControllerException {
        try {
            if (adeController == null) {
                adeController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
            }
            return adeController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private ReservarMargemController getReservarMargemController() throws AutorizacaoControllerException {
        try {
            if (reservarController == null) {
                reservarController = ApplicationContextProvider.getApplicationContext().getBean("reservarMargemController", ReservarMargemController.class);
            }
            return reservarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private RenegociarConsignacaoController getRenegociarConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (renegociarController == null) {
                renegociarController = ApplicationContextProvider.getApplicationContext().getBean("renegociarConsignacaoController", RenegociarConsignacaoController.class);
            }
            return renegociarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private AlongarConsignacaoController getAlongarConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (alongarController == null) {
                alongarController = ApplicationContextProvider.getApplicationContext().getBean(AlongarConsignacaoController.class);
            }
            return alongarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private AlterarConsignacaoController getAlterarConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (alterarController == null) {
                alterarController = ApplicationContextProvider.getApplicationContext().getBean(AlterarConsignacaoController.class);
            }
            return alterarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private CancelarConsignacaoController getCancelarConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (cancelarController == null) {
                cancelarController = ApplicationContextProvider.getApplicationContext().getBean(CancelarConsignacaoController.class);
            }
            return cancelarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private LiquidarConsignacaoController getLiquidarConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (liquidarController == null) {
                liquidarController = ApplicationContextProvider.getApplicationContext().getBean(LiquidarConsignacaoController.class);
            }
            return liquidarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private DeferirConsignacaoController getDeferirConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (deferirController == null) {
                deferirController = ApplicationContextProvider.getApplicationContext().getBean("deferirConsignacaoController", DeferirConsignacaoController.class);
            }
            return deferirController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private ConfirmarConsignacaoController getConfirmarConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (confirmarController == null) {
                confirmarController = ApplicationContextProvider.getApplicationContext().getBean(ConfirmarConsignacaoController.class);
            }
            return confirmarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private AutorizarConsignacaoController getAutorizarConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (autorizarController == null) {
                autorizarController = ApplicationContextProvider.getApplicationContext().getBean(AutorizarConsignacaoController.class);
            }
            return autorizarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private SuspenderConsignacaoController getSuspenderConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (suspenderController == null) {
                suspenderController = ApplicationContextProvider.getApplicationContext().getBean(SuspenderConsignacaoController.class);
            }
            return suspenderController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private TransferirConsignacaoController getTransferirConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (transferirController == null) {
                transferirController = ApplicationContextProvider.getApplicationContext().getBean(TransferirConsignacaoController.class);
            }
            return transferirController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private ReimplantarConsignacaoController getReimplantarConsignacaoController() throws AutorizacaoControllerException {
        try {
            if (reimplantarController == null) {
                reimplantarController = ApplicationContextProvider.getApplicationContext().getBean(ReimplantarConsignacaoController.class);
            }
            return reimplantarController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private InserirSolicitacaoController getInserirSolicitacaoController() throws AutorizacaoControllerException {
        try {
            if (insSolicitacaoController == null) {
                insSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(InserirSolicitacaoController.class);
            }
            return insSolicitacaoController;
        } catch (Exception ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**************************** MÉTODOS DE NEGÓCIO *******************************/

    public void alterarConsignacao(AlterarConsignacaoParametros alterarParam, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getAlterarConsignacaoController().alterar(alterarParam, responsavel);
    }

    public void autorizarConsignacao(String adeCodigo, String corCodigo, String senhaUtilizada, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getAutorizarConsignacaoController().autorizar(adeCodigo, corCodigo, senhaUtilizada, tipoMotivoOperacao, responsavel);
    }

    public void cancelarConsignacao(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getCancelarConsignacaoController().cancelar(adeCodigo, responsavel);
    }

    public void cancelarConsignacao(String adeCodigo, boolean verificaStatus, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getCancelarConsignacaoController().cancelar(adeCodigo, verificaStatus, responsavel);
    }

    public void cancelarConsignacao(String adeCodigo, boolean verificaStatusAde, boolean verificaStatusServidor, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getCancelarConsignacaoController().cancelar(adeCodigo, verificaStatusAde, verificaStatusServidor, responsavel);
    }

    public void cancelarConsignacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getCancelarConsignacaoController().cancelar(adeCodigo, tipoMotivoOperacao, responsavel);
    }

    public void cancelarConsignacao(String adeCodigo, boolean verificaStatusAde, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getCancelarConsignacaoController().cancelar(adeCodigo, verificaStatusAde, tipoMotivoOperacao, responsavel);
    }

    public void criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getAutorizacaoController().criaOcorrenciaADE(adeCodigo, tocCodigo, ocaObs, responsavel);
    }

    public void criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, java.util.Date ocaPeriodo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getAutorizacaoController().criaOcorrenciaADE(adeCodigo, tocCodigo, ocaObs, ocaPeriodo, responsavel);
    }

    public void criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, BigDecimal ocaAdeVlrAnt, BigDecimal ocaAdeVlrNovo, Date ocaData, Date ocaPeriodo, String tmoCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getAutorizacaoController().criaOcorrenciaADE(adeCodigo, tocCodigo, ocaObs, ocaAdeVlrAnt, ocaAdeVlrNovo, ocaData, ocaPeriodo, tmoCodigo, responsavel);
    }

    public void confirmarConsignacao(String adeCodigo, BigDecimal adeVlr, String adeIdentificador, String adeBanco, String adeAgencia, String adeConta, String corCodigo, Integer adePrazo, String senhaUtilizada, String codAutorizacao, boolean comSerSenha, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getConfirmarConsignacaoController().confirmar(adeCodigo, adeVlr, adeIdentificador, adeBanco, adeAgencia, adeConta, corCodigo, adePrazo, senhaUtilizada, codAutorizacao, comSerSenha, tipoMotivoOperacao, responsavel);
    }

    public void confirmarConsignacao(String adeCodigo, BigDecimal adeVlr, String adeIdentificador, String adeBanco, String adeAgencia, String adeConta, String corCodigo, Integer adePrazo, String senhaUtilizada, String codAutorizacao, boolean comSerSenha, String tdaModalidadeOp, String tdaMatriculaCsa, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getConfirmarConsignacaoController().confirmar(adeCodigo, adeVlr, adeIdentificador, adeBanco, adeAgencia, adeConta, corCodigo, adePrazo, senhaUtilizada, codAutorizacao, comSerSenha, tdaModalidadeOp, tdaMatriculaCsa, tipoMotivoOperacao, responsavel);
    }

    public void confirmarConsignacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getConfirmarConsignacaoController().confirmar(adeCodigo, null, null, null, null, null, null, null, null, null, false, tipoMotivoOperacao, responsavel);
    }

    public void executarDeferimentoAutomatico(AcessoSistema responsavel) throws AutorizacaoControllerException {
        getDeferirConsignacaoController().executarDeferimentoAutomatico(responsavel);
    }

    public void liquidarConsignacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, LiquidarConsignacaoParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getLiquidarConsignacaoController().liquidar(adeCodigo, tipoMotivoOperacao, parametros, responsavel);
    }

    public void reativarConsignacao(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getSuspenderConsignacaoController().reativar(adeCodigo, null, null, responsavel);
    }

    public void reativarConsignacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, ReativarConsignacaoParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getSuspenderConsignacaoController().reativar(adeCodigo, tipoMotivoOperacao, parametros, responsavel);
    }

    public void removerDataReativacaoAutomatica(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getSuspenderConsignacaoController().removerDataReativacaoAutomatica(adeCodigo, responsavel);
    }

    public String solicitarReservaMargem(ReservarMargemParametros reservaMargem, ServidorTransferObject servidor, RegistroServidorTO registroServidor, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getInserirSolicitacaoController().solicitarReservaMargem(reservaMargem, servidor, registroServidor, svcCodigo, responsavel);
    }

    public String reservarMargem(ReservarMargemParametros margemParam, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getReservarMargemController().reservarMargem(margemParam, responsavel);
    }

    public String alongarContrato(AlongarConsignacaoParametros alongarParam, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getAlongarConsignacaoController().alongar(alongarParam, responsavel);
    }

    public String renegociarContrato(RenegociarConsignacaoParametros renegociarParam, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getRenegociarConsignacaoController().renegociar(renegociarParam, responsavel);
    }

    public void suspenderConsignacao(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getSuspenderConsignacaoController().suspender(adeCodigo, null, null, responsavel);
    }

    public void suspenderConsignacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getSuspenderConsignacaoController().suspender(adeCodigo, tipoMotivoOperacao, null, responsavel);
    }

    public void suspenderConsignacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, SuspenderConsignacaoParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getSuspenderConsignacaoController().suspender(adeCodigo, tipoMotivoOperacao, parametros, responsavel);
    }

    public void desliquidarConsignacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getLiquidarConsignacaoController().desliquidar(adeCodigo, tipoMotivoOperacao, responsavel);
    }

    public void desliquidarConsignacao(String adeCodigo, boolean validarMargem, boolean reimplantar, boolean ignoraCompra, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getLiquidarConsignacaoController().desliquidar(adeCodigo, validarMargem, reimplantar, ignoraCompra, tipoMotivoOperacao, responsavel);
    }

    public boolean sistemaPreservaParcela(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getReimplantarConsignacaoController().sistemaPreservaParcela(adeCodigo, responsavel);
    }

    public void reimplantarConsignacao(String adeCodigo, String obsOca, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getReimplantarConsignacaoController().reimplantar(adeCodigo, obsOca, tipoMotivoOperacao, responsavel);
    }

    public void reimplantarConsignacao(String adeCodigo, String obsOca, CustomTransferObject tipoMotivoOperacao, boolean alterarNumeroAde, boolean reduzirValorAde, boolean reativacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getReimplantarConsignacaoController().reimplantar(adeCodigo, obsOca, tipoMotivoOperacao, alterarNumeroAde, reduzirValorAde, reativacao, responsavel);
    }

    public List<TransferObject> pesquisarConsignacaoRelacionamento(List<String> adeCodigoList, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return getTransferirConsignacaoController().pesquisarConsignacaoRelacionamento(adeCodigoList, responsavel);
    }

    public void cancelarRenegociacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getCancelarConsignacaoController().cancelarRenegociacao(adeCodigo, tipoMotivoOperacao, responsavel);
    }

    public void descancelarConsignacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        getCancelarConsignacaoController().descancelar(adeCodigo, tipoMotivoOperacao, responsavel);
    }
}
