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
 * <p>Title: FuncaoPermitidaNcaHome</p>
 * <p>Description: Classe Home para a entidade FuncaoPermitidaNca </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoPermitidaNcaHome extends AbstractEntityHome {

    public static Collection<FuncaoPermitidaNca> findByNcaCodigo(String ncaCodigo) throws FindException {
        String query = "FROM FuncaoPermitidaNca AS fpn WHERE fpn.id.ncaCodigo = :ncaCodigo ORDER BY fpn.funcao.funDescricao";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ncaCodigo", ncaCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<String> findFuncoesByNcaCodigo(String ncaCodigo) throws FindException {
        String query = "FROM FuncaoPermitidaNca AS fpn WHERE fpn.id.ncaCodigo = :ncaCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ncaCodigo", ncaCodigo);

        List<String> retorno = new ArrayList<>();
        List<FuncaoPermitidaNca> funcAudit = findByQuery(query, parameters);

        if (funcAudit != null && !funcAudit.isEmpty()) {
            Iterator<FuncaoPermitidaNca> it = funcAudit.iterator();

            while (it.hasNext()) {
                FuncaoPermitidaNca func = it.next();
                retorno.add(func.getFunCodigo());
            }
        }

        return retorno;
    }

    public static FuncaoPermitidaNca create(String ncaCodigo, String funCodigo) throws CreateException {
        FuncaoPermitidaNca bean = new FuncaoPermitidaNca();

        FuncaoPermitidaNcaId id = new FuncaoPermitidaNcaId(ncaCodigo, funCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }
}
