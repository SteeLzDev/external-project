package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: SubOrgaoHome</p>
 * <p>Description: Classe Home para a entidade SubOrgao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SubOrgaoHome extends AbstractEntityHome {

    public static SubOrgao findByPrimaryKey(String sboCodigo) throws FindException {
        SubOrgao subOrgao = new SubOrgao();
        subOrgao.setSboCodigo(sboCodigo);
        return find(subOrgao, sboCodigo);
    }

    public static SubOrgao create(String orgCodigo, String sboIdentificador, String sboDescricao) throws CreateException {
        Session session = SessionUtil.getSession();
        SubOrgao bean = new SubOrgao();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setSboCodigo(objectId);
            bean.setOrgao((Orgao) session.getReference(Orgao.class, orgCodigo));
            bean.setSboIdentificador(sboIdentificador);
            bean.setSboDescricao(sboDescricao);
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
