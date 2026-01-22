package com.zetra.econsig.service.notificacao;


import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.NotificacaoEmailControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: NotificacaoEmailController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface NotificacaoEmailController {

    public List<TransferObject> lstNotificacoes(TransferObject criterio, AcessoSistema responsavel) throws NotificacaoEmailControllerException;

    public String criarNotificacao(String tnoCodigo, String nemDestinatario, String nemTitulo, String nemTexto, Date nemData, Date nemDataEnvio, AcessoSistema responsavel) throws NotificacaoEmailControllerException;

    public void enviarNotificacao(AcessoSistema responsavel) throws NotificacaoEmailControllerException;

    public void registrarEnvioNotificacao(String ndiCodigo, AcessoSistema responsavel) throws NotificacaoEmailControllerException;

    public List<TransferObject> lstTipoNotificacao(AcessoSistema responsavel) throws NotificacaoEmailControllerException;
}
