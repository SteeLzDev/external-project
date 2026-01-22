package com.zetra.econsig.service.consignacao;


import java.util.List;

import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ReservarMargemControllerBean</p>
 * <p>Description: Session Bean para a operação de Reserva de Margem.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ReservarMargemController  {

    public List<String> reservarMargemGap(String rseCodigo, String orgCodigo, String cnvCodigo, String svcCodigo, String corCodigo,
            List<Short> marCodigos, String adeIdentificador, String serSenha, boolean comSerSenha,
            String numAgencia, String numBanco, String numConta,
            AcessoSistema responsavel) throws AutorizacaoControllerException;

    public String reservarMargem(ReservarMargemParametros margemParam, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
