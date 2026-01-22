package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegraLimiteOperacaoHome extends AbstractEntityHome {

    public static RegraLimiteOperacao create(RegraLimiteOperacao bean) throws CreateException {
        final Session session = SessionUtil.getSession();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setRloCodigo(objectId);
            create(bean, session);
        } catch (final MissingPrimaryKeyException e) {
            throw new CreateException(e);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static RegraLimiteOperacao findByPrimaryKey(String rloCodigo) throws FindException {
        String query = "FROM RegraLimiteOperacao rlo WHERE rlo.rloCodigo = :rloCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rloCodigo", rloCodigo);
        List<RegraLimiteOperacao> result = findByQuery(query, parameters);
        return result.get(0);
    }
}
