package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AvaliacaoFaqHome</p>
 * <p>Description: Classe Home para a entidade AvaliacaoFaq</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public class AvaliacaoFaqHome extends AbstractEntityHome {

    public static AvaliacaoFaq findByPrimaryKey(String avfCodigo) throws FindException {
    	AvaliacaoFaq avaliacaoFaq = new AvaliacaoFaq();
    	avaliacaoFaq.setAvfCodigo(avfCodigo);
        return find(avaliacaoFaq, avfCodigo);
    }
    
    public static AvaliacaoFaq create(String usuCodigo, String faqCodigo, String avfNota, Date avfData, String avfComentario) throws CreateException {

        Session session = SessionUtil.getSession();
        AvaliacaoFaq bean = new AvaliacaoFaq();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setAvfCodigo(objectId);
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setFaq((Faq) session.getReference(Faq.class, faqCodigo));
            bean.setAvfNota(avfNota);
            bean.setAvfData(avfData);
            bean.setAvfComentario(avfComentario);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }


}
