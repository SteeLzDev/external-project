package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: NotificacaoEmailHome</p>
 * <p>Description: Classe para encapsular acesso a entidade NotificacaoEmail.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class NotificacaoEmailHome extends AbstractEntityHome {

    public static NotificacaoEmail findByPrimaryKey(String nemCodigo) throws FindException {
        NotificacaoEmail notificacaoEmail = new NotificacaoEmail();
        notificacaoEmail.setNemCodigo(nemCodigo);
        return find(notificacaoEmail, nemCodigo);
    }

    public static NotificacaoEmail create(String usuCodigo, String funCodigo, String tnoCodigo, String nemDestinatario, String nemTitulo,
            String nemTexto, Date nemData, Date nemDataEnvio) throws CreateException {

        Session session = SessionUtil.getSession();
        NotificacaoEmail bean = new NotificacaoEmail();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setNemCodigo(objectId);
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            if(funCodigo!=null) {
                bean.setFuncao((Funcao) session.getReference(Funcao.class, funCodigo));
            }
            bean.setTipoNotificacao((TipoNotificacao) session.getReference(TipoNotificacao.class, tnoCodigo));
            bean.setNemDestinatario(nemDestinatario);
            bean.setNemTitulo(nemTitulo);
            bean.setNemTexto(nemTexto);
            bean.setNemData(nemData);

            if (!TextHelper.isNull(nemDataEnvio)) {
                bean.setNemDataEnvio(nemDataEnvio);
            }

            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
