package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: FuncaoPerfilCorHome</p>
 * <p>Description: CRUD para função perfil correspondente</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoPerfilCorHome extends AbstractEntityHome {

    public static List<FuncaoPerfilCor> findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM FuncaoPerfilCor AS fcor WHERE fcor.id.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static List<FuncaoPerfilCor> findByUsuFunCodigo(String usuCodigo, String funCodigo) throws FindException {
        String query = "FROM FuncaoPerfilCor AS fcor WHERE fcor.id.usuCodigo = :usuCodigo AND fcor.id.funCodigo = :funCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);
        parameters.put("funCodigo", funCodigo);

        return findByQuery(query, parameters);
    }

    public static FuncaoPerfilCor create(String corCodigo, String usuCodigo, String funCodigo) throws CreateException {
        FuncaoPerfilCor bean = new FuncaoPerfilCor();

        FuncaoPerfilCorId id = new FuncaoPerfilCorId(corCodigo, usuCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
