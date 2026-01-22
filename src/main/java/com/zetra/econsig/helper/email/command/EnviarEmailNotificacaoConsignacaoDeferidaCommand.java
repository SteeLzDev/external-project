package com.zetra.econsig.helper.email.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.mail.MessagingException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailNotificacaoConsignacaoDeferidaCommand</p>
 * <p>Description: Envia email de notificação ao servidor/funcionário informando que sua consignação foi deferida.</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author:$
 * $Revision:$
 * $Date:$
 */
public class EnviarEmailNotificacaoConsignacaoDeferidaCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificacaoConsignacaoDeferidaCommand.class);

    private String emailDestinatario;

    private String adeNumero;

    private String csaNome;

    private String corpo;

    public void setEmailDestinatario(String emailDestinatario) {
        this.emailDestinatario = emailDestinatario;
    }

    public void setAdeNumero(String adeNumero) {
        this.adeNumero = adeNumero;
    }

    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    @Override
    public void execute() throws ViewHelperException {

        MailHelper mailHelper = new MailHelper();

        // 1. Busca o template do e-mail
        ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIA_EMAIL_CONSIGNACAO_DEFERIDA, responsavel);
        CustomTransferObject dados = new CustomTransferObject();

        LocalDateTime dt = LocalDateTime.now();
        String horario = dt.format(DateTimeFormatter.ofPattern(ApplicationResourcesHelper.getMessage("mensagem.email.data.titulo", responsavel)));

        // 2. Setando dados no interpolador
        dados.setAttribute("horario", horario);
        dados.setAttribute("csa_nome", csaNome);
        dados.setAttribute("solicitacao_aprovada", ApplicationResourcesHelper.getMessage("mensagem.email.solicitacao.aprovada.titulo", responsavel));
        dados.setAttribute("ade_numero", adeNumero);

        interpolador.setDados(dados);

        // 3. Interpola o template gerando os textos finais prontos para uso.
        String titulo = interpolador.interpolateTitulo();

        if (TextHelper.isNull(corpo)) {
            corpo = interpolador.interpolateTexto();
        }

        // Envia os emails
        try {
            mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_CONSIGNACAO_DEFERIDA, emailDestinatario.replaceAll(";", ","), null, null, titulo, corpo, null, null, responsavel);
        } catch (MessagingException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
