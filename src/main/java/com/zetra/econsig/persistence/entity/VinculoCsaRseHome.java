package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VinculoCsaRseHome extends AbstractEntityHome {

    public static VinculoCsaRse create(VinculoCsaRse bean) throws CreateException {
        Session session = SessionUtil.getSession();
        try {
            create(bean, session);
            return bean;
        } catch (final CreateException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

    }

    public static List<VinculoCsaRse> verifyExists(String vcsCodigo) throws FindException {
        String query = "FROM VinculoCsaRse vcr WHERE vcr.vcsCodigo = :vcsCodigo ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("vcsCodigo", vcsCodigo);
        return findByQuery(query, parameters);
    }
}
