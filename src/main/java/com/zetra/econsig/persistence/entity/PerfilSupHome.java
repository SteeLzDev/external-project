package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: PerfilSupHome</p>
 * <p>Description: Classe Home para a entidade PerfilSup</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PerfilSupHome extends AbstractEntityHome {

    public static PerfilSup findByPrimaryKey(PerfilSupId pk) throws FindException {
        PerfilSup perfilSup = new PerfilSup();
        perfilSup.setId(pk);
        return find(perfilSup, pk);
    }

    public static PerfilSup create(String cseCodigo, String perCodigo, Short psuAtivo) throws CreateException {
        PerfilSup bean = new PerfilSup();

        PerfilSupId id = new PerfilSupId();
        id.setCseCodigo(cseCodigo);
        id.setPerCodigo(perCodigo);
        bean.setId(id);
        bean.setPsuAtivo(psuAtivo);

        create(bean);
        return bean;
    }

    public static List<PerfilSup> findByPerCodigo(String perCodigo) throws FindException {
        String query = "FROM PerfilSup AS p WHERE p.perfil.perCodigo = :perCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("perCodigo", perCodigo);

        return findByQuery(query, parameters);
    }
}
