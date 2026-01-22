package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: HistoricoProcMargemHome</p>
 * <p>Description: Classe Home para a entidade HistoricoProcMargem</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoProcMargemHome extends AbstractEntityHome {

    public static HistoricoProcMargem findByPrimaryKey(Long hpmCodigo) throws FindException {
        HistoricoProcMargem bean = new HistoricoProcMargem();
        bean.setHpmCodigo(hpmCodigo);
        return find(bean, hpmCodigo);
    }

    public static HistoricoProcMargem create(String usuCodigo, Date hpmPeriodo, Date hpmDataProc,
                                             Integer hpmQtdServidoresAntes, Integer hpmQtdServidoresDepois) throws CreateException {

        Session session = SessionUtil.getSession();
        HistoricoProcMargem bean = new HistoricoProcMargem();

        try {
            if (hpmDataProc == null) {
                hpmDataProc = DateHelper.getSystemDatetime();
            }
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setHpmPeriodo(hpmPeriodo);
            bean.setHpmDataProc(hpmDataProc);
            bean.setHpmQtdServidoresAntes(hpmQtdServidoresAntes);
            bean.setHpmQtdServidoresDepois(hpmQtdServidoresDepois);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
