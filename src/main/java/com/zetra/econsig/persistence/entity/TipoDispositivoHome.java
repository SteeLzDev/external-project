package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: TipoDispositivoHome</p>
 * <p>Description: Entidade Tipo de Dispositivo Home</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoDispositivoHome extends AbstractEntityHome {
    public static TipoDispositivo findByPrimaryKey(String codigo) throws FindException {
        TipoDispositivo tipoDispositivo = new TipoDispositivo();
        tipoDispositivo.setTdiCodigo(codigo);
        return find(tipoDispositivo, codigo);
    }

    public static TipoDispositivo create(String descricao) throws CreateException {
        TipoDispositivo bean = new TipoDispositivo();
        try {
            String objectId = DBHelper.getNextId();
            bean.setTdiCodigo(objectId);
            bean.setTdiDescricao(descricao);
        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        }

        create(bean);
        return bean;
    }
}
