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
 * <p>Title: FuncaoAuditavelCsaHome</p>
 * <p>Description: Classe Home para a entidade FuncaoAuditavelCsa </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoAuditavelCsaHome extends AbstractEntityHome {

    public static Collection<FuncaoAuditavelCsa> findByCsaCodigo(String csaCodigo) throws FindException {
        String query = "FROM FuncaoAuditavelCsa AS fau WHERE fau.id.csaCodigo = :csaCodigo ORDER BY fau.funcao.funDescricao";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<String> findFuncoesByCsaCodigo(String csaCodigo) throws FindException {
        String query = "FROM FuncaoAuditavelCsa AS fau WHERE fau.id.csaCodigo = :csaCodigo";


        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);

        List<String> retorno = new ArrayList<>();
        List<FuncaoAuditavelCsa> funcAudit = findByQuery(query, parameters);

        if (funcAudit != null && !funcAudit.isEmpty()) {
            Iterator<FuncaoAuditavelCsa> it = funcAudit.iterator();

            while (it.hasNext()) {
                FuncaoAuditavelCsa func = it.next();
                retorno.add(func.getFunCodigo());
            }
        }

        return retorno;
    }

    public static FuncaoAuditavelCsa create(String csaCodigo, String funCodigo) throws CreateException {
        FuncaoAuditavelCsa bean = new FuncaoAuditavelCsa();

        FuncaoAuditavelCsaId id = new FuncaoAuditavelCsaId(csaCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
