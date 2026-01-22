package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: HistoricoStatusAdeHome</p>
 * <p>Description: Classe Home para a entidade HistoricoStatusAde</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoStatusAdeHome extends AbstractEntityHome {

    public static HistoricoStatusAde findLastByAdeCodigoSadCodigoNovo(String adeCodigo, String sadCodigoNovo) throws FindException {
        String query = "FROM HistoricoStatusAde hsa WHERE hsa.id.adeCodigo = :adeCodigo AND hsa.id.sadCodigoNovo = :sadCodigoNovo ORDER BY hsa.id.hsaData DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("sadCodigoNovo", sadCodigoNovo);

        List<HistoricoStatusAde> result = findByQuery(query, parameters, 1, 0);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }

}
