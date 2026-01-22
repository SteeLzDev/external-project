package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: PerfilOrgHome</p>
 * <p>Description: Classe Home para a entidade PerfilOrg</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PerfilOrgHome extends AbstractEntityHome {

    public static PerfilOrg findByPrimaryKey(PerfilOrgId pk) throws FindException {
        PerfilOrg perfilOrg = new PerfilOrg();
        perfilOrg.setId(pk);
        return find(perfilOrg, pk);
    }

    public static PerfilOrg create(String orgCodigo, String perCodigo, Short porAtivo) throws CreateException {
        PerfilOrg bean = new PerfilOrg();

        PerfilOrgId id = new PerfilOrgId();
        id.setOrgCodigo(orgCodigo);
        id.setPerCodigo(perCodigo);
        bean.setId(id);
        bean.setPorAtivo(porAtivo);

        create(bean);
        return bean;
    }

    public static List<PerfilOrg> findByPerCodigo(String perCodigo) throws FindException {
        String query = "FROM PerfilOrg AS p WHERE p.perfil.perCodigo = :perCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("perCodigo", perCodigo);

        return findByQuery(query, parameters);
    }
}
