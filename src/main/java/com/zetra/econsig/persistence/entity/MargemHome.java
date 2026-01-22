package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: MargemHome</p>
 * <p>Description: Classe Home para a entidade Margem</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MargemHome extends AbstractEntityHome {

    public static Margem findByPrimaryKey(Short marCodigo) throws FindException {
        Margem margem = new Margem();
        margem.setMarCodigo(marCodigo);
        return find(margem, marCodigo);
    }

    public static Margem create(Short marCodigo, Short marCodigoPai,
            String marDescricao, Short marSequencia, BigDecimal marPorcentagem,
            String marExibeCse, String marExibeOrg,
            String marExibeSer, String marExibeCsa,
            String marExibeCor, String marExibeSup, String marTipoVlr) throws CreateException {

        Session session = SessionUtil.getSession();
        Margem bean = new Margem();

        try {
            bean.setMarCodigo(marCodigo);
            bean.setMargemPai(session.getReference(Margem.class, marCodigoPai));
            bean.setMarDescricao(marDescricao);
            bean.setMarSequencia(marSequencia);
            bean.setMarPorcentagem(marPorcentagem);
            bean.setMarExibeCse(marExibeCse);
            bean.setMarExibeOrg(marExibeOrg);
            bean.setMarExibeSer(marExibeSer);
            bean.setMarExibeCsa(marExibeCsa);
            bean.setMarExibeCor(marExibeCor);
            bean.setMarExibeSup(marExibeSup);
            bean.setMarTipoVlr(marTipoVlr);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
