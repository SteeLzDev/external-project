package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: LimiteTaxaJurosHome</p>
 * <p>Description: Classe Home para a entidade LimiteTaxaJuros</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public class LimiteTaxaJurosHome extends AbstractEntityHome {

    public static LimiteTaxaJuros findByPrimaryKey(String ltjCodigo) throws FindException {
        LimiteTaxaJuros limiteTaxaJuros = new LimiteTaxaJuros();
        limiteTaxaJuros.setLtjCodigo(ltjCodigo);
        return find(limiteTaxaJuros, ltjCodigo);
    }

    public static String create(String svcCodigo, Short ltjPrazoRef, BigDecimal ltjJurosMax, BigDecimal ltjVlrRef) throws CreateException {
        Session session = SessionUtil.getSession();
        LimiteTaxaJuros bean = new LimiteTaxaJuros();
        String codigo = null;
        try {
        	codigo = DBHelper.getNextId();
            bean.setLtjCodigo(codigo);
            bean.setServico(session.getReference(Servico.class, svcCodigo));
            bean.setLtjPrazoRef(ltjPrazoRef);
            bean.setLtjJurosMax(ltjJurosMax);
            bean.setLtjVlrRef(ltjVlrRef);

            create(bean, session);
        } catch (MissingPrimaryKeyException e) {
            throw new CreateException(e);
        } finally {
            SessionUtil.closeSession(session);
        }
        return codigo;
    }
}
