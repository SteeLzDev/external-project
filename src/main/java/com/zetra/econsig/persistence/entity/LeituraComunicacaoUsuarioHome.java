package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: LeituraComunicacaoUsuarioHome</p>
 * <p>Description: Home da Bean Class LeituraComunicacaoUsuario.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeituraComunicacaoUsuarioHome extends AbstractEntityHome {

    public static LeituraComunicacaoUsuario create(String cmnCodigo, String usuCodigo) throws CreateException {
        LeituraComunicacaoUsuario bean = new LeituraComunicacaoUsuario();
        LeituraComunicacaoUsuarioId id = new LeituraComunicacaoUsuarioId(cmnCodigo, usuCodigo);

        bean.setLcuData(DateHelper.getSystemDatetime());
        bean.setId(id);

        create(bean);
        return bean;
    }

    public static List<LeituraComunicacaoUsuario> findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM LeituraComunicacaoUsuario lcu WHERE lcu.id.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("csaCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static List<LeituraComunicacaoUsuario> findByCmnCodigo(String cmnCodigo) throws FindException {
        String query = "FROM LeituraComunicacaoUsuario lcu WHERE lcu.id.cmnCodigo = :cmnCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("cmnCodigo", cmnCodigo);

        return findByQuery(query, parameters);
    }

    public static LeituraComunicacaoUsuario findLeituraComunicacaoUsuByPK(LeituraComunicacaoUsuarioId id) throws FindException {
        String query = "FROM LeituraComunicacaoUsuario lcu WHERE lcu.id.cmnCodigo = :cmnCodigo and lcu.id.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("cmnCodigo", id.getCmnCodigo());
        parameters.put("usuCodigo", id.getUsuCodigo());

        List<LeituraComunicacaoUsuario> leituraList = findByQuery(query, parameters);

        if (leituraList != null && !leituraList.isEmpty()) {
            return leituraList.iterator().next();
        }

        return null;

    }

}
