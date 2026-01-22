package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

public class NotificacaoDispositivoHome extends AbstractEntityHome {

    public static NotificacaoDispositivo findByPrimaryKey(String ndiCodigo) throws FindException {
        NotificacaoDispositivo notificacaoDispositivo = new NotificacaoDispositivo();
        notificacaoDispositivo.setNdiCodigo(ndiCodigo);
        return find(notificacaoDispositivo, ndiCodigo);
    }

    public static NotificacaoDispositivo create(String usuCodigoOperador, String usuCodigoDestinatario, String funCodigo, String ndiTexto, Date ndiData,
            Date ndiDataEnvio, Short ndiStatus, String tnoCodigo ) throws CreateException {

        Session session = SessionUtil.getSession();
        NotificacaoDispositivo bean = new NotificacaoDispositivo();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setNdiCodigo(objectId);
            bean.setUsuarioDestinatario((Usuario) session.getReference(Usuario.class, usuCodigoDestinatario));
            bean.setUsuarioOperador((Usuario) session.getReference(Usuario.class, usuCodigoOperador));

            if (!TextHelper.isNull(funCodigo)) {
                bean.setFuncao((Funcao) session.getReference(Funcao.class, funCodigo));
            }

            bean.setNdiTexto(ndiTexto);
            bean.setNdiData(ndiData);
            bean.setNdiAtivo(ndiStatus);
            bean.setTipoNotificacao((TipoNotificacao) session.getReference(TipoNotificacao.class, tnoCodigo));

            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
