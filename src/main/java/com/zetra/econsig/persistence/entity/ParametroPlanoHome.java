package com.zetra.econsig.persistence.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: ParametroPlanoHome</p>
 * <p>Description: Classe Home para a entidade ParametroPlano</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParametroPlanoHome extends AbstractEntityHome {

    public static ParametroPlano findByPrimaryKey(String tppCodigo, String plaCodigo) throws FindException {
        ParametroPlano paramPlano = new ParametroPlano();
        ParametroPlanoId id = new ParametroPlanoId(tppCodigo, plaCodigo);
        paramPlano.setId(id);
        return find(paramPlano, id);
    }

    public static List<ParametroPlano> findByPlano(String plaCodigo) throws FindException {
        String query = "FROM ParametroPlano ppl WHERE ppl.id.plaCodigo = :plaCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("plaCodigo", plaCodigo);

        List<ParametroPlano> result = findByQuery(query, parameters);
        if (result == null || result.isEmpty()) {
            result = new ArrayList<ParametroPlano>();
        }
        return result;
    }

    public static ParametroPlano create(String tppCodigo, String plaCodigo, String pplValor) throws CreateException {
        ParametroPlano bean = new ParametroPlano();

        ParametroPlanoId id = new ParametroPlanoId(tppCodigo, plaCodigo);
        bean.setId(id);
        bean.setPplValor(pplValor);
        bean.setPplData(DateHelper.getSystemDatetime());

        create(bean);
        return bean;
    }
}
