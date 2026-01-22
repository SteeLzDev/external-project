package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: FuncaoPerfilOrgHome</p>
 * <p>Description: CRUD para função perfil ORG</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoPerfilOrgHome extends AbstractEntityHome {
    public static List<FuncaoPerfilOrg> findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM FuncaoPerfilOrg AS forg WHERE forg.id.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static List<FuncaoPerfilOrg> findByUsuFunCodigo(String usuCodigo, String funCodigo) throws FindException {
        String query = "FROM FuncaoPerfilOrg AS forg WHERE forg.id.usuCodigo = :usuCodigo AND forg.id.funCodigo = :funCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);
        parameters.put("funCodigo", funCodigo);

        return findByQuery(query, parameters);
    }

    public static FuncaoPerfilOrg create(String orgCodigo, String usuCodigo, String funCodigo) throws CreateException {
        FuncaoPerfilOrg bean = new FuncaoPerfilOrg();

        FuncaoPerfilOrgId id = new FuncaoPerfilOrgId(orgCodigo, usuCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
