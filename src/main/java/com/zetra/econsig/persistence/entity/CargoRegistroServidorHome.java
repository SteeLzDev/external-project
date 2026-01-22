package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: CargoRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade CargoRegistroServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CargoRegistroServidorHome extends AbstractEntityHome {

    public static CargoRegistroServidor findByPrimaryKey(String crsCodigo) throws FindException {
        CargoRegistroServidor cargoRegistroServidor = new CargoRegistroServidor();
        cargoRegistroServidor.setCrsCodigo(crsCodigo);
        return find(cargoRegistroServidor, crsCodigo);
    }

    public static CargoRegistroServidor create(String crsIdentificador, String crsDescricao) throws CreateException {
        try {
            CargoRegistroServidor bean = new CargoRegistroServidor();

            bean.setCrsCodigo(DBHelper.getNextId());
            bean.setCrsIdentificador(crsIdentificador);
            bean.setCrsDescricao(crsDescricao);
            create(bean);
            return bean;
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }
}
