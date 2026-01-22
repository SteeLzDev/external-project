package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ProtocoloSenhaAutorizacaoHome</p>
 * <p>Description: Classe Home para a entidade ProtocoloSenhaAutorizacao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProtocoloSenhaAutorizacaoHome extends AbstractEntityHome {

    public static ProtocoloSenhaAutorizacao findByPrimaryKey(String psaCodigo) throws FindException {
        ProtocoloSenhaAutorizacao bean = new ProtocoloSenhaAutorizacao();
        bean.setPsaCodigo(psaCodigo);
        return find(bean, psaCodigo);
    }

    public static ProtocoloSenhaAutorizacao create(String psaCodigo, String usuCodigoAfetado, String usuCodigoResponsavel) throws CreateException {
        Session session = SessionUtil.getSession();
        ProtocoloSenhaAutorizacao bean = new ProtocoloSenhaAutorizacao();

        try {
            if (TextHelper.isNull(psaCodigo)) {
                psaCodigo = DBHelper.getNextId();
            }

            bean.setPsaCodigo(psaCodigo);
            bean.setUsuarioAfetado((Usuario) session.getReference(Usuario.class, usuCodigoAfetado));
            bean.setUsuarioResponsavel((Usuario) session.getReference(Usuario.class, usuCodigoResponsavel));
            Timestamp agora = new Timestamp(Calendar.getInstance().getTimeInMillis());
            bean.setPsaData(agora);

            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
