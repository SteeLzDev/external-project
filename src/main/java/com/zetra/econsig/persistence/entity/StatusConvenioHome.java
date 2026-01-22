package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: StatusConvenioHome</p>
 * <p>Description: Classe Home para a entidade StatusConvenio</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusConvenioHome extends AbstractEntityHome {

    public static StatusConvenio findByPrimaryKey(String scvCodigo) throws FindException {
        StatusConvenio statusConvenio = new StatusConvenio();
        statusConvenio.setScvCodigo(scvCodigo);
        return find(statusConvenio, scvCodigo);
    }

    public static StatusConvenio create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
