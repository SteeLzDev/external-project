package com.zetra.econsig.service.notificacao;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.NotificacaoUsuarioControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.NotificacaoUsuario;
import com.zetra.econsig.persistence.entity.NotificacaoUsuarioId;

/**
 * <p>Title: NotificacaoUsuarioController</p>
 * <p>Description: Interface para o service bean de NotificacaoUsuarioController.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface NotificacaoUsuarioController {

    public NotificacaoUsuario findByPrimaryKey(NotificacaoUsuarioId notificacaoUsuarioId, AcessoSistema responsavel) throws NotificacaoUsuarioControllerException;

    public NotificacaoUsuario create(String usuCodigo, String tnoCodigo, Short nusAtivo, AcessoSistema responsavel) throws CreateException;

    public void update(NotificacaoUsuario notificacaoUsuario, AcessoSistema responsavel) throws UpdateException;

    public NotificacaoUsuario find(String usuCodigo, String tnoCodigo, AcessoSistema responsavel);
}
