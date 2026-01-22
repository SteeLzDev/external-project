package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: NotificacaoUsuarioHome</p>
 * <p>Description: Classe para encapsular acesso a entidade NotificacaoUsuario.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class NotificacaoUsuarioHome extends AbstractEntityHome {

    public static NotificacaoUsuario findByPrimaryKey(NotificacaoUsuarioId notificacaoUsuarioId) throws FindException {
    	NotificacaoUsuario notificacaoUsuario = new NotificacaoUsuario();
    	notificacaoUsuario.setId(notificacaoUsuarioId);
        return find(notificacaoUsuario, notificacaoUsuarioId);
    }

    public static NotificacaoUsuario create(String tnoCodigo, String usuCodigo, Short nusAtivo) throws CreateException {

        NotificacaoUsuario bean = new NotificacaoUsuario();

        NotificacaoUsuarioId id = new NotificacaoUsuarioId();
        id.setTnoCodigo(tnoCodigo);
        id.setUsuCodigo(usuCodigo);

        bean.setId(id);
        bean.setNusAtivo(nusAtivo);
        create(bean);

        return bean;

    }
}
