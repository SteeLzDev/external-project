package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: StatusRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade StatusRegistroServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusRegistroServidorHome extends AbstractEntityHome {

    public static StatusRegistroServidor findByPrimaryKey(String srsCodigo) throws FindException {
        StatusRegistroServidor statusRegistroServidor = new StatusRegistroServidor();
        statusRegistroServidor.setSrsCodigo(srsCodigo);
        return find(statusRegistroServidor, srsCodigo);
    }

    public static StatusRegistroServidor create(String srsCodigo, String srsDescricao) throws CreateException {
        StatusRegistroServidor bean = new StatusRegistroServidor();

        bean.setSrsCodigo(srsCodigo);
        bean.setSrsDescricao(srsDescricao);
        create(bean);
        return bean;
    }
}
