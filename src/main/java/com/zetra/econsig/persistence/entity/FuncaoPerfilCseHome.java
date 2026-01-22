package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: FuncaoPerfilCseHome</p>
 * <p>Description: CRUD para função perfil cse</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoPerfilCseHome extends AbstractEntityHome {
    public static List<FuncaoPerfilCse> findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM FuncaoPerfilCse AS fcse WHERE fcse.id.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static List<FuncaoPerfilCse> findByUsuFunCodigo(String usuCodigo, String funCodigo) throws FindException {
        String query = "FROM FuncaoPerfilCse AS fcse WHERE fcse.id.usuCodigo = :usuCodigo AND fcse.id.funCodigo = :funCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);
        parameters.put("funCodigo", funCodigo);

        return findByQuery(query, parameters);
    }

    public static FuncaoPerfilCse create(String cseCodigo, String usuCodigo, String funCodigo) throws CreateException {
        FuncaoPerfilCse bean = new FuncaoPerfilCse();

        FuncaoPerfilCseId id = new FuncaoPerfilCseId(cseCodigo, usuCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
