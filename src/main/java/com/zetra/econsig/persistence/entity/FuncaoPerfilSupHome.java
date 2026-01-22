package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: FuncaoPerfilSupHome</p>
 * <p>Description: CRUD para função perfil sup</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoPerfilSupHome extends AbstractEntityHome {
    public static List<FuncaoPerfilSup> findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM FuncaoPerfilSup AS fsup WHERE fsup.id.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static List<FuncaoPerfilSup> findByUsuFunCodigo(String usuCodigo, String funCodigo) throws FindException {
        String query = "FROM FuncaoPerfilSup AS fsup WHERE fsup.id.usuCodigo = :usuCodigo AND fsup.id.funCodigo = :funCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);
        parameters.put("funCodigo", funCodigo);

        return findByQuery(query, parameters);
    }

    public static FuncaoPerfilSup create(String cseCodigo, String usuCodigo, String funCodigo) throws CreateException {
        FuncaoPerfilSup bean = new FuncaoPerfilSup();

        FuncaoPerfilSupId id = new FuncaoPerfilSupId(cseCodigo, usuCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
