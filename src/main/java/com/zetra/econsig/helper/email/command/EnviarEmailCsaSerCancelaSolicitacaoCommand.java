package com.zetra.econsig.helper.email.command;

import jakarta.mail.MessagingException;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailCsaSerCancelaSolicitacaoCommand</p>
 *
 * <p>Description: Command para envio de email de notificação à CSA quando um servidor cancelar uma solicitação</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author:  $
 * $Revision:  $
 * $Date:  $
 */
public class EnviarEmailCsaSerCancelaSolicitacaoCommand extends EnviarEmailCsaNovaSolicitacaoCommand {

    @Override
    public void execute() throws ViewHelperException {
        enviaEmail(getInterpolador(ModeloEmailEnum.ENVIA_EMAIL_CSA_SOLICITACAO_CANCELADA_POR_SERVIDOR));
    }

    @Override
    protected ModeloEmailInterpolator getInterpolador(ModeloEmailEnum modelo) throws ViewHelperException {
        return super.getInterpolador(modelo);
    }

    @Override
    protected void sendMail(String titulo, String corpo) throws MessagingException {
        // Envia os emails.
        MailHelper mailHelper = new MailHelper();
        mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_CSA_SOLICITACAO_CANCELADA_POR_SERVIDOR, getCsaMail(), null, null, titulo, corpo, null, null, responsavel);
    }
}
