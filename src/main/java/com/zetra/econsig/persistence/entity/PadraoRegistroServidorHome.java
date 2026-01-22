package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: PadraoRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade PadraoRegistroServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PadraoRegistroServidorHome extends AbstractEntityHome {

    public static PadraoRegistroServidor findByPrimaryKey(String prsCodigo) throws FindException {
        PadraoRegistroServidor padraoRegistroServidor = new PadraoRegistroServidor();
        padraoRegistroServidor.setPrsCodigo(prsCodigo);
        return find(padraoRegistroServidor, prsCodigo);
    }

    public static PadraoRegistroServidor create(String prsIdentificador, String prsDescricao) throws CreateException {
        try {
            PadraoRegistroServidor bean = new PadraoRegistroServidor();

            bean.setPrsCodigo(DBHelper.getNextId());
            bean.setPrsIdentificador(prsIdentificador);
            bean.setPrsDescricao(prsDescricao);
            create(bean);
            return bean;
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }
}
