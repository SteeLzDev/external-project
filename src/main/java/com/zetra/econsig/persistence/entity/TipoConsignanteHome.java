package com.zetra.econsig.persistence.entity;

import java.util.List;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: TipoConsignanteHome</p>
 * <p>Description: Classe Home para Tipo Consignante</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoConsignanteHome extends AbstractEntityHome {

    public static List<TipoConsignante> findAll() throws FindException {
        String query = "FROM TipoConsignante tipoCse";

        return findByQuery(query, null);
    }
}
