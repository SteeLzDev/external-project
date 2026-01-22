package com.zetra.econsig.persistence.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: FuncaoAuditavelOrgHome</p>
 * <p>Description: Classe Home para a entidade FuncaoAuditavelOrg </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoAuditavelOrgHome extends AbstractEntityHome {

    public static Collection<FuncaoAuditavelOrg> findByOrgCodigo(String orgCodigo) throws FindException {
        String query = "FROM FuncaoAuditavelOrg AS fau WHERE fau.id.orgCodigo = :orgCodigo ORDER BY fau.funcao.funDescricao";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgCodigo", orgCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<String> findFuncoesByOrgCodigo(String orgCodigo) throws FindException {
        String query = "FROM FuncaoAuditavelOrg AS fau WHERE fau.id.orgCodigo = :orgCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgCodigo", orgCodigo);

        List<String> retorno = new ArrayList<>();
        List<FuncaoAuditavelOrg> funcAudit = findByQuery(query, parameters);

        if (funcAudit != null && !funcAudit.isEmpty()) {
            Iterator<FuncaoAuditavelOrg> it = funcAudit.iterator();

            while (it.hasNext()) {
                FuncaoAuditavelOrg func = it.next();
                retorno.add(func.getFunCodigo());
            }
        }

        return retorno;
    }

    public static FuncaoAuditavelOrg create(String orgCodigo, String funCodigo) throws CreateException {
        FuncaoAuditavelOrg bean = new FuncaoAuditavelOrg();

        FuncaoAuditavelOrgId id = new FuncaoAuditavelOrgId(orgCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
