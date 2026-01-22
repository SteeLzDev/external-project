package com.zetra.econsig.service.compra;


import java.util.Collection;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.values.OperacaoCompraEnum;
import com.zetra.econsig.values.StatusCompraEnum;

/**
 * <p>Title: CompraContratoControllerBean</p>
 * <p>Description: Session Bean para a operações relacionadas a compra de contratos.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CompraContratoController  {
    public void aplicarPenalidadesPrazosExcedidos(AcessoSistema responsavel) throws CompraContratoControllerException;
    public boolean consignatariaNaoPossuiPendenciaCompra(String csaCodigo, AcessoSistema responsavel) throws CompraContratoControllerException;

    public String recuperarAdeDestinoCompra(String adeCodigo) throws CompraContratoControllerException;
    public boolean ultimaAdeFinalizacaoCompra(String adeCodigo, String adeCodigoDestinoCompra) throws CompraContratoControllerException;
    public void finalizarCompra(String adeCodigo, AcessoSistema responsavel) throws CompraContratoControllerException;
    public void finalizarCompra(String adeCodigo, java.util.Date ocaPeriodo, AcessoSistema responsavel) throws CompraContratoControllerException;
    public void retirarContratoCompra(String adeCodigo, String textoObservacao, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws CompraContratoControllerException;
    public void executarDesbloqueioAutomaticoConsignatarias(Collection<String> adeCodigos, AcessoSistema responsavel);
    public List<String> recuperarAdesCodigosDestinoCompra(String adeCodigoOrigem) throws CompraContratoControllerException;
    public Collection<RelacionamentoAutorizacao> recuperarContratosOrigemCompra(String adeCodigoDestino) throws CompraContratoControllerException;
    public void updateRelAutorizacaoCompra(String adeCodigoOrigem, OperacaoCompraEnum operacao, AcessoSistema responsavel) throws CompraContratoControllerException;
    public void updateStatusRelacionamentoAdesCompradas(String adeCodigoDestino, StatusCompraEnum statusCompraEnum, AcessoSistema responsavel) throws CompraContratoControllerException;
    public void concluiContratosAguardLiquidCompra(AcessoSistema responsavel) throws CompraContratoControllerException;
    public List<TransferObject> lstHistoricoSuspensoesLiquidacoesAntecipadas(String rseCodigo, String nseCodigo, int countLiquidacoes, int countSuspensoes, AcessoSistema responsavel) throws CompraContratoControllerException;
    public List<TransferObject> lstHistoricoConsignacao(String rseCodigo, String nseCodigo, List<String> sadCodigos, List<String> tocCodigos, int count, boolean somenteValorReduzido, AcessoSistema responsavel) throws CompraContratoControllerException;
    public void liquidarAdeCompraNaoLiquidada(int diasLiqAutomatica, AcessoSistema responsavel) throws CompraContratoControllerException;
    public void reativarDescontoAposPendenciaCompra(String adeCodigo, boolean liquidacao, AcessoSistema responsavel) throws CompraContratoControllerException;
    public void reativarDescontoAposPendenciaCompra(String adeCodigo, boolean liquidacao, java.util.Date ocaPeriodo, AcessoSistema responsavel) throws CompraContratoControllerException;
    public void enviarMsgCsaPortabilidade(UploadHelper uploadHelper, AcessoSistema responsavel) throws CompraContratoControllerException;
    public String emailDestinatarioMsgCsaDestinoPortabilidade(String adeCodigo, String csaCodigo , AcessoSistema responsavel) throws CompraContratoControllerException;
    public Collection<RelacionamentoAutorizacao> getRelacionamentoCompra(String adeCodigo) throws CompraContratoControllerException;
    public Boolean temRelacionamentoCompraByOrigem(String adeCodigo) throws CompraContratoControllerException;
}
