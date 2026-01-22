package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: TipoParametroPlanoHome</p>
 * <p>Description: Classe Home para a entidade TipoParametroPlano</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoParametroPlanoHome extends AbstractEntityHome {

    public static TipoParametroPlano findByPrimaryKey(String tppCodigo) throws FindException {
        TipoParametroPlano tipoParametroPlano = new TipoParametroPlano();
        tipoParametroPlano.setTppCodigo(tppCodigo);
        return find(tipoParametroPlano, tppCodigo);
    }
}
