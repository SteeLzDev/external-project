package com.zetra.econsig.service.sistema;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoMotivoOperacaoController</p>
 * <p>Description: Controller para gerenciamento de tipo de motivo da operacao</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface TipoMotivoOperacaoController  {

    public String createMotivoOperacao(TipoMotivoOperacaoTransferObject motivoOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public void removeMotivoOperacao(TipoMotivoOperacaoTransferObject motivoOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacaoConsignacao(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacaoUsuario(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacaoRegistroServidor(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacaoServico(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacaoConvenio(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacaoDispensaValidacaoDigital(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacaoConsignataria(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacao(List<String> tenCodigos, Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public void updateMotivoOperacao(TipoMotivoOperacaoTransferObject motivoOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public TipoMotivoOperacaoTransferObject findMotivoOperacao(String tmoCodigo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public TipoMotivoOperacaoTransferObject findMotivoOperacao(TipoMotivoOperacaoTransferObject motivoOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public TipoMotivoOperacaoTransferObject findMotivoOperacaoByCodIdent(String tmoIdentificador, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public void gravarMotivoOperacaoConsignacao(CustomTransferObject dadosOperacao, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TipoMotivoOperacaoTransferObject> findByTmoExigeObsObrigatorio(AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacaoAcao(List<String> tenCodigos, Short tmoAtivo, String acaCodigo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacao(String tmoCodigo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;

    public List<TransferObject> lstMotivoOperacaoBeneficioSaude(Short tmoAtivo, AcessoSistema responsavel) throws TipoMotivoOperacaoControllerException;
}
