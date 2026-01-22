package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ArquivoOpeNaoConfirmadasHome</p>
 * <p>Description: Classe Home para operações CRUD de  ArquivoOpeNaoConfirmadas</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 27160 $
 * $Date: 2019-07-08 15:28:39 -0300 (seg, 08 jul 2019) $
 */
public class ArquivoOpeNaoConfirmadasHome extends AbstractEntityHome {

    public static Collection<ArquivoOpeNaoConfirmadas> findByOncCodigo(String oncCodigo) throws FindException {
        String query = "FROM ArquivoOpeNaoConfirmadas aon WHERE aon.operacaoNaoConfirmada.oncCodigo = :oncCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("oncCodigo", oncCodigo);

        return findByQuery(query, parameters);
    }

    public static ArquivoOpeNaoConfirmadas create(String aonNome, Long aonTamanho, String aonConteudo, String oncCodigo) throws CreateException {
        Session session = SessionUtil.getSession();
        ArquivoOpeNaoConfirmadas bean = new ArquivoOpeNaoConfirmadas();

        try {
            bean.setAonCodigo(DBHelper.getNextId());
            bean.setOperacaoNaoConfirmada(session.getReference(OperacaoNaoConfirmada.class, oncCodigo));
            bean.setAonNome(aonNome);
            bean.setAonTamanho(aonTamanho);
            bean.setAonConteudo(aonConteudo);

            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
