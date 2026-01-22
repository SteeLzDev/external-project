package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TransferenciaMargemHome</p>
 * <p>Description: Classe Home para a entidade TransferenciaMargem</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TransferenciaMargemHome extends AbstractEntityHome {

    public static TransferenciaMargem findByPrimaryKey(TransferenciaMargemId transferenciaMargemId) throws FindException {
        TransferenciaMargem transferenciaMargem = new TransferenciaMargem();
        transferenciaMargem.setId(transferenciaMargemId);
        return find(transferenciaMargem, transferenciaMargemId);
    }

    public static TransferenciaMargem create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
