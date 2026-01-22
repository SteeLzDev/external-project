package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ReimplantarConsignacaoController</p>
 * <p>Description: Session Bean para a operação de reimplantação de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ReimplantarConsignacaoController  {

    public void reimplantar(String adeCodigo, String obsOca, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void reimplantar(String adeCodigo, String obsOca, CustomTransferObject tipoMotivoOperacao, boolean alterarNumeroAde, boolean reduzirValorAde, boolean reativacao, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void reimplantar(String adeCodigo, String obsOca, CustomTransferObject tipoMotivoOperacao, boolean alterarNumeroAde, boolean reduzirValorAde, boolean reativacao, boolean adeSuspensaReativaPermissaoGestor, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void reimplantar(String adeCodigo, String obsOca, CustomTransferObject tipoMotivoOperacao, boolean alterarNumeroAde, boolean reduzirValorAde, boolean reativacao, Date ocaPeriodo, boolean adeSuspensaReativaPermissaoGestor, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public boolean sistemaReimplanta(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public boolean sistemaPreservaParcela(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public boolean sistemaPreservaParcela(String adeCodigo, boolean arquivado, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void reimplantarConsignacoesValorReduzidoPago(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public BigDecimal calcularCapitalDevido(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void reimplantarCapitalDevido(TransferObject ade, BigDecimal adeVlrNovo, int prazoNovo, String tmoCodigo, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void reimplantarConsignacoesInclusaoAnexo(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void reimplantarConsignacoesPermissaoCse(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
