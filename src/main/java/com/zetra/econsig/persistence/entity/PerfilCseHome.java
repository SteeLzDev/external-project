package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: PerfilCseHome</p>
 * <p>Description: Classe Home para a entidade PerfilCse</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PerfilCseHome extends AbstractEntityHome {

    public static PerfilCse findByPrimaryKey(PerfilCseId pk) throws FindException {
        PerfilCse perfilCse = new PerfilCse();
        perfilCse.setId(pk);
        return find(perfilCse, pk);
    }

    public static PerfilCse create(String cseCodigo, String perCodigo, Short pceAtivo) throws CreateException {
        PerfilCse bean = new PerfilCse();

        PerfilCseId id = new PerfilCseId();
        id.setCseCodigo(cseCodigo);
        id.setPerCodigo(perCodigo);
        bean.setId(id);
        bean.setPceAtivo(pceAtivo);

        create(bean);
        return bean;
    }

    public static List<PerfilCse> findByPerCodigo(String perCodigo) throws FindException {
        String query = "FROM PerfilCse AS p WHERE p.perfil.perCodigo = :perCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("perCodigo", perCodigo);

        return findByQuery(query, parameters);
    }
}
