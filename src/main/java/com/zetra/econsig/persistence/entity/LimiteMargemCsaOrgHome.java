package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: LimiteMargemCsaOrgHome</p>
 * <p>Description: Classe Home para a entidade LimiteMargemCsaOrg</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LimiteMargemCsaOrgHome extends AbstractEntityHome {

    public static LimiteMargemCsaOrg findByPrimaryKey(Short marCodigo, String csaCodigo, String orgCodigo) throws FindException {
        LimiteMargemCsaOrgId id = new LimiteMargemCsaOrgId();
        LimiteMargemCsaOrg limiteMargemCsaOrg = new LimiteMargemCsaOrg();
        id.setMarCodigo(marCodigo);
        id.setCsaCodigo(csaCodigo);
        id.setOrgCodigo(orgCodigo);
        limiteMargemCsaOrg.setId(id);
        return find(limiteMargemCsaOrg, id);
    }

    public static LimiteMargemCsaOrg create(Short marCodigo, String csaCodigo, String orgCodigo, BigDecimal lmcValor, Date lmcData) throws CreateException {
        Session session = SessionUtil.getSession();
        LimiteMargemCsaOrg bean = new LimiteMargemCsaOrg();
        LimiteMargemCsaOrgId id = new LimiteMargemCsaOrgId();

        try {
            id.setMarCodigo(marCodigo);
            id.setCsaCodigo(csaCodigo);
            id.setOrgCodigo(orgCodigo);
            bean.setId(id);
            bean.setLmcData(lmcData != null ? lmcData : DateHelper.getSystemDatetime());
            bean.setLmcValor(lmcValor != null ? lmcValor : new BigDecimal(1));
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }
}
