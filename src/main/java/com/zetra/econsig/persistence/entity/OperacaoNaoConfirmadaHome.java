package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OperacaoNaoConfirmadaHome</p>
 * <p>Description: Classe Home para operações CRUD de  OperacaoNaoConfirmada</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 27160 $
 * $Date: 2019-07-08 15:28:39 -0300 (seg, 08 jul 2019) $
 */
public class OperacaoNaoConfirmadaHome extends AbstractEntityHome {

    public static Collection<OperacaoNaoConfirmada> findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM OperacaoNaoConfirmada onc WHERE onc.usuario.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static OperacaoNaoConfirmada findByPrimaryKey(String oncCodigo) throws FindException {
        OperacaoNaoConfirmada oncDesconto = new OperacaoNaoConfirmada();
        oncDesconto.setOncCodigo(oncCodigo);
        return find(oncDesconto, oncCodigo);
    }

    public static OperacaoNaoConfirmada create(String acrCodigo, String usuCodigo, String rseCodigo, String oncIpAcesso, String oncDetalhe,
            String oncParametros, Date oncData) throws CreateException {

        Session session = SessionUtil.getSession();
        OperacaoNaoConfirmada bean = new OperacaoNaoConfirmada();

        try {
            bean.setOncCodigo(DBHelper.getNextId());
            bean.setAcessoRecurso(session.getReference(AcessoRecurso.class, acrCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setRseCodigo(rseCodigo);
            bean.setOncIpAcesso(oncIpAcesso);
            bean.setOncDetalhe(oncDetalhe);
            bean.setOncParametros(oncParametros);
            bean.setOncData(oncData);

            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
