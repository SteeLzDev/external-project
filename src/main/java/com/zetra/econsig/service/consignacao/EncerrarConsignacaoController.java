package com.zetra.econsig.service.consignacao;


import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: EncerrarConsignacaoController</p>
 * <p>Description: Session Bean para a operação de Encerramento e Reabertura de Consignação via Carga de Margem.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface EncerrarConsignacaoController  {

    public void encerrar(String rseCodigo, String motivoEncerramento, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void reabrir(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
