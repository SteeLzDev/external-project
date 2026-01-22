package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: PapelFuncaoHome</p>
 * <p>Description: Classe Home para a entidade PapelFuncao</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PapelFuncaoHome extends AbstractEntityHome {

    public static PapelFuncao findByPrimaryKey(PapelFuncaoId pk) throws FindException {
        PapelFuncao papelFuncao = new PapelFuncao();
        papelFuncao.setId(pk);
        return find(papelFuncao, pk);
    }

    public static Collection<PapelFuncao> findByFunCodigo(String funCodigo) throws FindException {
        String query = "FROM PapelFuncao pfu WHERE pfu.id.funCodigo = :funCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("funCodigo", funCodigo);

        return findByQuery(query, parameters);
    }

    public static PapelFuncao create(String funCodigo, String papCodigo) throws CreateException {
        PapelFuncao bean = new PapelFuncao();

        PapelFuncaoId id = new PapelFuncaoId();
        id.setFunCodigo(funCodigo);
        id.setPapCodigo(papCodigo);
        bean.setId(id);

        create(bean);
        return bean;
    }
}
