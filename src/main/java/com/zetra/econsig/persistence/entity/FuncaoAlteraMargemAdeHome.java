package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.FindException;

public class FuncaoAlteraMargemAdeHome extends AbstractEntityHome {

    public static FuncaoAlteraMargemAde findByFunCodigoAndPapCodigo(String funCodigo, String papCodigo) throws FindException {
        final String query = "FROM FuncaoAlteraMargemAde WHERE funCodigo = :funCodigo and papCodigo = :papCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("funCodigo", funCodigo);
        parameters.put("papCodigo", papCodigo);

        final List<FuncaoAlteraMargemAde> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

}