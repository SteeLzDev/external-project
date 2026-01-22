package com.zetra.econsig.persistence.entity;

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
 * <p>Title: CategoriaFaqHome</p>
 * <p>Description: Classe Home para a entidade Categoria Faq</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CategoriaFaqHome extends AbstractEntityHome {

    public static CategoriaFaq findByPrimaryKey(String cafCodigo) throws FindException {
        CategoriaFaq categoriaFaq = new CategoriaFaq();
        categoriaFaq.setCafCodigo(cafCodigo);
        return find(categoriaFaq, cafCodigo);
    }

    public static CategoriaFaq create(String cafDescricao) throws CreateException {

        Session session = SessionUtil.getSession();
        CategoriaFaq bean = new CategoriaFaq();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setCafCodigo(objectId);
            bean.setCafDescricao(cafDescricao);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static List<CategoriaFaq> lstCategoriaFaq() throws FindException{
        String query = "FROM CategoriaFaq";
        Map<String, Object> parameters = new HashMap<>();

        return findByQuery(query, parameters);
    }
}
