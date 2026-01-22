package com.zetra.econsig.webservice.soap.operacional.assembler;

import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v4.SituacaoServidor;


/**
 * <p>Title: SituacaoServidorAssembler</p>
 * <p>Description: Assembler para SituacaoServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class SituacaoServidorAssembler extends BaseAssembler {

    private SituacaoServidorAssembler() {
        //
    }

    public static com.zetra.econsig.webservice.soap.operacional.v6.SituacaoServidor toSituacaoServidorV6(SituacaoServidor situacaoServidorV4) {
        final com.zetra.econsig.webservice.soap.operacional.v6.SituacaoServidor situacaoServidor = new com.zetra.econsig.webservice.soap.operacional.v6.SituacaoServidor();

        situacaoServidor.setAtivo(situacaoServidorV4.getAtivo());
        situacaoServidor.setBloqueado(situacaoServidorV4.getBloqueado());
        situacaoServidor.setExcluido(situacaoServidorV4.getExcluido());
        situacaoServidor.setFalecido(situacaoServidorV4.getFalecido());
        situacaoServidor.setPendente(situacaoServidorV4.getPendente());

        return situacaoServidor;
    }
}
