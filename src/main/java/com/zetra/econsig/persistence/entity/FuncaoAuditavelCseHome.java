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
 * <p>Title: FuncaoAuditavelCseHome</p>
 * <p>Description: Classe Home para a entidade FuncaoAuditavelCse </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoAuditavelCseHome extends AbstractEntityHome {

    public static Collection<FuncaoAuditavelCse> findByCseCodigo(String cseCodigo) throws FindException {
        String query = "FROM FuncaoAuditavelCse AS fau WHERE fau.id.cseCodigo = :cseCodigo ORDER BY fau.funcao.funDescricao";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cseCodigo", cseCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<String> findFuncoesByCseCodigo(String cseCodigo) throws FindException {
        String query = "FROM FuncaoAuditavelCse AS fau WHERE fau.id.cseCodigo = :cseCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cseCodigo", cseCodigo);

        List<String> retorno = new ArrayList<>();
        List<FuncaoAuditavelCse> funcAudit = findByQuery(query, parameters);

        if (funcAudit != null && !funcAudit.isEmpty()) {
            Iterator<FuncaoAuditavelCse> it = funcAudit.iterator();

            while (it.hasNext()) {
                FuncaoAuditavelCse func = it.next();
                retorno.add(func.getFunCodigo());
            }
        }

        return retorno;
    }

    public static FuncaoAuditavelCse create(String cseCodigo, String funCodigo) throws CreateException {
        FuncaoAuditavelCse bean = new FuncaoAuditavelCse();

        FuncaoAuditavelCseId id = new FuncaoAuditavelCseId(cseCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
