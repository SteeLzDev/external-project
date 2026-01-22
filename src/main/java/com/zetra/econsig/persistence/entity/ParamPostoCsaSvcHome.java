package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;
import org.hibernate.Session;

public class ParamPostoCsaSvcHome extends AbstractEntityHome {


    public static ParamPostoCsaSvc create(String csaCodigo, String posCodigo, String svcCodigo, String ppoVlr) throws CreateException {
        Session session = SessionUtil.getSession();
        ParamPostoCsaSvc bean = new ParamPostoCsaSvc();

        try {
            bean.setCsaCodigo(csaCodigo);
            bean.setPosCodigo(posCodigo);
            bean.setSvcCodigo(svcCodigo);
            bean.setPpoVlr(ppoVlr);
            bean.setTpsCodigo(CodedValues.TPS_ADE_VLR);
            create(bean, session);

        } catch (CreateException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}

