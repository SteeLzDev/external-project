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
 * <p>Title: FuncaoAuditavelSupHome</p>
 * <p>Description: Classe Home para a entidade FuncaoAuditavelSup </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoAuditavelSupHome extends AbstractEntityHome {

    public static Collection<FuncaoAuditavelSup> findByCseCodigo(String cseCodigo) throws FindException {
        String query = "FROM FuncaoAuditavelSup AS fau WHERE fau.id.cseCodigo = :cseCodigo ORDER BY fau.funcao.funDescricao";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cseCodigo", cseCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<String> findFuncoesByCseCodigo(String cseCodigo) throws FindException {
        String query = "FROM FuncaoAuditavelSup AS fau WHERE fau.id.cseCodigo = :cseCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cseCodigo", cseCodigo);

        List<String> retorno = new ArrayList<>();
        List<FuncaoAuditavelSup> funcAudit = findByQuery(query, parameters);

        if (funcAudit != null && !funcAudit.isEmpty()) {
            Iterator<FuncaoAuditavelSup> it = funcAudit.iterator();

            while (it.hasNext()) {
                FuncaoAuditavelSup func = it.next();
                retorno.add(func.getFunCodigo());
            }
        }

        return retorno;
    }

    public static FuncaoAuditavelSup create(String cseCodigo, String funCodigo) throws CreateException {
        FuncaoAuditavelSup bean = new FuncaoAuditavelSup();

        FuncaoAuditavelSupId id = new FuncaoAuditavelSupId(cseCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
