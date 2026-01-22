package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: IgnoraInconsistenciaAdeHome</p>
 * <p>Description: Classe Home para a entidade IgnoraInconsistenciaAde</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class IgnoraInconsistenciaAdeHome extends AbstractEntityHome {

    public static IgnoraInconsistenciaAde findByPrimaryKey(IgnoraInconsistenciaAdeId id) throws FindException {
        IgnoraInconsistenciaAde ignoraInconsistencia = new IgnoraInconsistenciaAde();
        ignoraInconsistencia.setId(id);
        return find(ignoraInconsistencia, id);
    }

    public static IgnoraInconsistenciaAde create(String adeCodigo, Short iiaItem, String iiaObs, Date iiaData, String iiaUsuario, Boolean iiaPermanente) throws CreateException {
        Session session = SessionUtil.getSession();
        IgnoraInconsistenciaAdeId id = new IgnoraInconsistenciaAdeId(adeCodigo, iiaItem);
        IgnoraInconsistenciaAde bean = new IgnoraInconsistenciaAde(id);
        try {
            bean.setIiaObs(iiaObs);
            bean.setIiaData(iiaData);
            bean.setIiaUsuario(iiaUsuario);
            bean.setIiaPermanente(iiaPermanente);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void removeIfExists(String adeCodigo, Short iiaItem) throws RemoveException {
        IgnoraInconsistenciaAdeId id = new IgnoraInconsistenciaAdeId(adeCodigo, iiaItem);
        try {
            IgnoraInconsistenciaAde bean = findByPrimaryKey(id);
            remove(bean);
        } catch (FindException ex) {
            // Se não existe, não faz nada
        }
    }
}
