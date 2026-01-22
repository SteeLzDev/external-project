package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: StatusPropostaHome</p>
 * <p>Description: Classe Home para a entidade StatusProposta</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusPropostaHome extends AbstractEntityHome {

    public static StatusProposta findByPrimaryKey(String stpCodigo) throws FindException {
        StatusProposta statusProposta = new StatusProposta();
        statusProposta.setStpCodigo(stpCodigo);
        return find(statusProposta, stpCodigo);
    }

    public static StatusProposta create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
