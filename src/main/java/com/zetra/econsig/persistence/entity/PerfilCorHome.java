package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: PerfilCorHome</p>
 * <p>Description: Classe Home para a entidade PerfilCor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PerfilCorHome extends AbstractEntityHome {

    public static PerfilCor findByPrimaryKey(PerfilCorId pk) throws FindException {
        PerfilCor perfilCor = new PerfilCor();
        perfilCor.setId(pk);
        return find(perfilCor, pk);
    }

    public static PerfilCor create(String corCodigo, String perCodigo, Short pcoAtivo) throws CreateException {
        PerfilCor bean = new PerfilCor();

        PerfilCorId id = new PerfilCorId();
        id.setCorCodigo(corCodigo);
        id.setPerCodigo(perCodigo);
        bean.setId(id);
        bean.setPcoAtivo(pcoAtivo);
        create(bean);
        return bean;
    }

    public static List<PerfilCor> findByPerCodigo(String perCodigo) throws FindException {
        String query = "FROM PerfilCor AS p WHERE p.perfil.perCodigo = :perCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("perCodigo", perCodigo);

        return findByQuery(query, parameters);
    }
}
