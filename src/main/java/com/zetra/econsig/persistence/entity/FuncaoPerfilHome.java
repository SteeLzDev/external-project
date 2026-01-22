package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: FuncaoPerfilHome</p>
 * <p>Description: CRUD para função perfil </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoPerfilHome extends AbstractEntityHome {
    public static FuncaoPerfil findByPrimaryKey(FuncaoPerfilId pk) throws FindException {
        FuncaoPerfil funcaoPerfil = new FuncaoPerfil();
        funcaoPerfil.setId(pk);
        return find(funcaoPerfil, pk);
    }

    public static List<FuncaoPerfil> findByPerfil(String perCodigo) throws FindException {
        String query = "FROM FuncaoPerfil AS fp WHERE fp.id.perCodigo = :perCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("perCodigo", perCodigo);

        return findByQuery(query, parameters);
    }

    public static FuncaoPerfil create(String funCodigo, String perCodigo) throws CreateException {
        FuncaoPerfil bean = new FuncaoPerfil();

        FuncaoPerfilId id = new FuncaoPerfilId();
        id.setFunCodigo(funCodigo);
        id.setPerCodigo(perCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
