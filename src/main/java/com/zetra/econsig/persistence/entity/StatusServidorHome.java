package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: StatusServidorHome</p>
 * <p>Description: Classe Home da entidade StatusServidor.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class StatusServidorHome extends AbstractEntityHome {

    public static StatusServidor findByPrimaryKey(String sseCodigo) throws FindException {
        StatusServidor statusServidor = new StatusServidor();
        statusServidor.setSseCodigo(sseCodigo);

        return find(statusServidor, sseCodigo);
    }

    public static StatusServidor create(String descricao) throws CreateException {
        StatusServidor statusServidor = new StatusServidor();

        try {
            statusServidor.setSseCodigo(DBHelper.getNextId());
            statusServidor.setSseDescricao(descricao);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }

        create(statusServidor);

        return statusServidor;
    }
}
