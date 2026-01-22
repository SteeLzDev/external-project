package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: AjudaRecursoHome</p>
 * <p>Description: Home da classe AjudaRecurso.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class AjudaRecursoHome extends AbstractEntityHome {
    public static AjudaRecurso findByPrimaryKey(String ajrCodigo) throws FindException {
        AjudaRecurso ajudaRecurso = new AjudaRecurso();
        ajudaRecurso.setAjrCodigo(ajrCodigo);
        return find(ajudaRecurso, ajrCodigo);
    }

    public static List<AjudaRecurso> findByAcrCodigo(String acrCodigo) throws FindException {
        String query = "FROM AjudaRecurso ajr WHERE ajr.acessoRecurso.acrCodigo = :acrCodigo ORDER BY ajr.ajrSequencia";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("acrCodigo", acrCodigo);

        return findByQuery(query, parameters);
    }
}
