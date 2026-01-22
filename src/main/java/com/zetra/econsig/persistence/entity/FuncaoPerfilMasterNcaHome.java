package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: FuncaoPerfilMasterNcaHome</p>
 * <p>Description: CRUD para função perfil por natureza de CSA de usuário master</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 24444 $
 * $Date: 2018-05-28 10:13:09 -0300 (seg, 28 mai 2018) $
 */
public class FuncaoPerfilMasterNcaHome extends AbstractEntityHome {

    public static List<FuncaoPerfilMasterNca> findByNcaCodigo(String ncaCodigo) throws FindException {
        String query = "FROM FuncaoPerfilMasterNca AS fpm WHERE fpm.id.ncaCodigo = :ncaCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ncaCodigo", ncaCodigo);

        return findByQuery(query, parameters);
    }

}
