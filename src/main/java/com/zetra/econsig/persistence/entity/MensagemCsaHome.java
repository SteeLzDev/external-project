package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: MensagemCsaHome</p>
 * <p>Description: Classe Home para a entidade MensagemCsa</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MensagemCsaHome extends AbstractEntityHome {

    public static MensagemCsa findByPrimaryKey(MensagemCsaId pk) throws FindException {
        MensagemCsa mensagemCsa = new MensagemCsa();
        mensagemCsa.setId(pk);
        return find(mensagemCsa, pk);
    }

    public static List<MensagemCsa> findByMenCodigo(String menCodigo) throws FindException {
        String query = "FROM MensagemCsa menCsa WHERE menCsa.mensagem.menCodigo = :menCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("menCodigo", menCodigo);

        return findByQuery(query, parameters);
    }

    public static MensagemCsa create(String menCodigo, String csaCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        MensagemCsa bean = new MensagemCsa();

        try {
            MensagemCsaId id = new MensagemCsaId(menCodigo, csaCodigo);
            bean.setId(id);
            bean.setConsignataria((Consignataria) session.getReference(Consignataria.class, csaCodigo));
            bean.setMensagem((Mensagem) session.getReference(Mensagem.class, menCodigo));
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
