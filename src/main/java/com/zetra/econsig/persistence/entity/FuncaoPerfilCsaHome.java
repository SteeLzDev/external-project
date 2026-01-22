package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: FuncaoPerfilCsaHome</p>
 * <p>Description: CRUD para função perfil CSA</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoPerfilCsaHome extends AbstractEntityHome {
    public static List<FuncaoPerfilCsa> findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM FuncaoPerfilCsa AS fcsa WHERE fcsa.id.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static List<FuncaoPerfilCsa> findByUsuFunCodigo(String usuCodigo, String funCodigo) throws FindException {
        String query = "FROM FuncaoPerfilCsa AS fcsa WHERE fcsa.id.usuCodigo = :usuCodigo AND fcsa.id.funCodigo = :funCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);
        parameters.put("funCodigo", funCodigo);

        return findByQuery(query, parameters);
    }

    public static FuncaoPerfilCsa create(String csaCodigo, String usuCodigo, String funCodigo) throws CreateException {
        FuncaoPerfilCsa bean = new FuncaoPerfilCsa();

        FuncaoPerfilCsaId id = new FuncaoPerfilCsaId(csaCodigo, usuCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
