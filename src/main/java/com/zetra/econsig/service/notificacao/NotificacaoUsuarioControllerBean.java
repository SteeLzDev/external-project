package com.zetra.econsig.service.notificacao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.NotificacaoUsuarioControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.NotificacaoUsuario;
import com.zetra.econsig.persistence.entity.NotificacaoUsuarioHome;
import com.zetra.econsig.persistence.entity.NotificacaoUsuarioId;

/**
 * <p>Title: NotificacaoUsuarioControllerBean</p>
 * <p>Description: Implementação session bean de NotificacaoUsuario.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class NotificacaoUsuarioControllerBean implements NotificacaoUsuarioController {

    @Override
    public NotificacaoUsuario findByPrimaryKey(NotificacaoUsuarioId notificacaoUsuarioId, AcessoSistema responsavel) throws NotificacaoUsuarioControllerException {
        try {
            return NotificacaoUsuarioHome.findByPrimaryKey(notificacaoUsuarioId);
        } catch (FindException e) {
            throw new NotificacaoUsuarioControllerException(e);
        }
    }

    @Override
    public NotificacaoUsuario find(String usuCodigo, String tnoCodigo, AcessoSistema responsavel) {
        try {
            NotificacaoUsuarioId notificacaoUsuarioId = new NotificacaoUsuarioId(usuCodigo, tnoCodigo);
            return NotificacaoUsuarioHome.findByPrimaryKey(notificacaoUsuarioId);
        } catch (FindException e) {
            return null;
        }
    }

    @Override
    public NotificacaoUsuario create(String usuCodigo, String tnoCodigo, Short nusAtivo, AcessoSistema responsavel) throws CreateException {
        return NotificacaoUsuarioHome.create(tnoCodigo, usuCodigo, nusAtivo);
    }

    @Override
    public void update(NotificacaoUsuario notificacaoUsuario, AcessoSistema responsavel) throws UpdateException {
        NotificacaoUsuarioHome.update(notificacaoUsuario);
    }
}
