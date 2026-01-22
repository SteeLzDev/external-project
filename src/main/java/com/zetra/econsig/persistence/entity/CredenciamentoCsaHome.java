package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: CredenciamentoCsaHome</p>
 * <p>Description: Classe Home para a entidade CredenciamentoCsaHome</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CredenciamentoCsaHome extends AbstractEntityHome {

    public static CredenciamentoCsa findByPrimaryKey(String creCodigo) throws FindException {
        String query = "SELECT cre FROM CredenciamentoCsa cre JOIN FETCH cre.consignataria csa JOIN FETCH cre.statusCredenciamento scr WHERE cre.creCodigo = :creCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("creCodigo", creCodigo);

        List<CredenciamentoCsa> credenciamentoCsa = findByQuery(query, parameters);
        if (credenciamentoCsa == null || credenciamentoCsa.isEmpty()) {
            return null;
        } else {
            return credenciamentoCsa.get(0);
        }
    }

    public static CredenciamentoCsa create(String csaCodigo, String scrCodigo, Date creDataIni, Date creDataFim) throws CreateException {
        Session session = SessionUtil.getSession();
        CredenciamentoCsa bean = new CredenciamentoCsa();

        try {
            String objectId = DBHelper.getNextId();
            bean.setCreCodigo(objectId);
            bean.setCsaCodigo(csaCodigo);
            bean.setScrCodigo(scrCodigo);
            bean.setCreDataIni(creDataIni);
            bean.setCreDataFim(creDataFim);

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static CredenciamentoCsa findByCsaCodigo(String csaCodigo) throws FindException {
        String query = "SELECT cre FROM CredenciamentoCsa cre JOIN FETCH cre.consignataria csa JOIN FETCH cre.statusCredenciamento scr WHERE cre.csaCodigo = :csaCodigo AND cre.creDataFim IS NULL";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);

        List<CredenciamentoCsa> credenciamentoCsa = findByQuery(query, parameters);
        if (credenciamentoCsa == null || credenciamentoCsa.isEmpty()) {
            return null;
        } else {
            return credenciamentoCsa.get(0);
        }
    }
}
