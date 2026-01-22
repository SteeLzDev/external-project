package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: FaqHome</p>
 * <p>Description: Classe Home para a entidade Faq</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FaqHome extends AbstractEntityHome {

    public static Faq findByPrimaryKey(String faqCodigo) throws FindException {
        Faq faq = new Faq();
        faq.setFaqCodigo(faqCodigo);
        return find(faq, faqCodigo);
    }

    public static Faq create(String usuCodigo, String faqTitulo1, String faqTitulo2, String faqTexto, Date faqData, Short faqSequencia, String faqExibeCse,
            String faqExibeOrg, String faqExibeCsa, String faqExibeCor, String faqExibeSer, String faqExibeSup, String faqExibeMobile, String cafCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        Faq bean = new Faq();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setFaqCodigo(objectId);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setFaqTitulo1(faqTitulo1);
            bean.setFaqTitulo2(faqTitulo2);
            bean.setFaqTexto(faqTexto);
            bean.setFaqData(faqData);
            bean.setFaqSequencia(faqSequencia);
            bean.setFaqExibeCse(faqExibeCse);
            bean.setFaqExibeOrg(faqExibeOrg);
            bean.setFaqExibeCsa(faqExibeCsa);
            bean.setFaqExibeCor(faqExibeCor);
            bean.setFaqExibeSer(faqExibeSer);
            bean.setFaqExibeSup(faqExibeSup);
            bean.setFaqHtml("N");
            bean.setFaqExibeMobile(faqExibeMobile);
            bean.setCafCodigo(cafCodigo);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }


}
