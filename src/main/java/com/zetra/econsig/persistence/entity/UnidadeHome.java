package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: UnidadeHome</p>
 * <p>Description: Classe Home para a entidade Unidade</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UnidadeHome extends AbstractEntityHome {

    public static Unidade findByPrimaryKey(String uniCodigo) throws FindException {
        Unidade unidade = new Unidade();
        unidade.setUniCodigo(uniCodigo);
        return find(unidade, uniCodigo);
    }

    public static Unidade create(String sboCodigo, String uniIdentificador, String uniDescricao) throws CreateException {
        Session session = SessionUtil.getSession();
        Unidade bean = new Unidade();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setUniCodigo(objectId);
            bean.setSubOrgao((SubOrgao) session.getReference(SubOrgao.class, sboCodigo));
            bean.setUniIdentificador(uniIdentificador);
            bean.setUniDescricao(uniDescricao);
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
