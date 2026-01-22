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
 * <p>Title: FuncaoAuditavelCorHome</p>
 * <p>Description: Classe Home para a entidade FuncaoAuditavelCor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoAuditavelCorHome extends AbstractEntityHome {

    public static Collection<FuncaoAuditavelCor> findByCorCodigo(String corCodigo) throws FindException {
        String query = "FROM FuncaoAuditavelCor AS fau WHERE fau.id.corCodigo = :corCodigo ORDER BY fau.funcao.funDescricao";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("corCodigo", corCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<String> findFuncoesByCorCodigo(String corCodigo) throws FindException {
        String query = "FROM FuncaoAuditavelCor AS fau WHERE fau.id.corCodigo = :corCodigo";
        List<String> retorno = new ArrayList<>();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("corCodigo", corCodigo);

        List<FuncaoAuditavelCor> funcAudit = findByQuery(query, parameters);

        if (funcAudit != null && !funcAudit.isEmpty()) {
            Iterator<FuncaoAuditavelCor> it = funcAudit.iterator();

            while (it.hasNext()) {
                FuncaoAuditavelCor func = it.next();
                retorno.add(func.getFunCodigo());
            }
        }

        return retorno;
    }

    public static FuncaoAuditavelCor create(String corCodigo, String funCodigo) throws CreateException {
        FuncaoAuditavelCor bean = new FuncaoAuditavelCor();

        FuncaoAuditavelCorId id = new FuncaoAuditavelCorId(corCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
