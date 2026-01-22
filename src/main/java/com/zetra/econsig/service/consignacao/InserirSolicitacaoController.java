package com.zetra.econsig.service.consignacao;


import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: InserirSolicitacaoController</p>
 * <p>Description: Session Bean para a operação Inserir Solicitação.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface InserirSolicitacaoController  {

    public String solicitarReservaMargem(ReservarMargemParametros reservaMargem, ServidorTransferObject servidor,
                                         RegistroServidorTO registroServidor, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean temSolicitacaoCreditoEletronicoPendenteDocumentacao(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    
    public boolean temSolicitacaoAutorizacao(String adeCodigo, TipoSolicitacaoEnum tipoSolicitacaoEnum, StatusSolicitacaoEnum statusSolicitacaoEnum, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void incluirSolicitacaoCreditoEletronico(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
