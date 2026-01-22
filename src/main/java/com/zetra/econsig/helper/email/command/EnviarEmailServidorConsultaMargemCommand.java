package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import jakarta.mail.MessagingException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnviarEmailServidorConsultaMargemCommand extends AbstractEnviarEmailCommand {
    private static final Logger log = LoggerFactory.getLogger(EnviarEmailServidorConsultaMargemCommand.class);

    @Setter
    @Getter
    private String email;
    @Getter
    @Setter
    private String serNome;
    @Getter
    @Setter
    private String cseNome;
    @Getter
    @Setter
    private String csaNome;

    @Override
    public void execute() throws ViewHelperException {
        enviaEmail(getModeloEmailInterpolator(ModeloEmailEnum.EMAIL_CONSULTA_MARGEM_SERVIDOR, responsavel));
    }

    protected void enviaEmail(ModeloEmailInterpolator interpolador) throws ViewHelperException {
        try {
            CustomTransferObject dadosTemplateEmail = new CustomTransferObject();
            setDadosTemplateEmail(dadosTemplateEmail);
            dadosTemplateEmail.setAttribute("cse_nome", getCseNome());
            dadosTemplateEmail.setAttribute("ser_nome", getSerNome());
            dadosTemplateEmail.setAttribute("csa_nome", getCsaNome());

            interpolador.setDados(dadosTemplateEmail);

            String titulo = interpolador.interpolateTitulo();
            String corpo = interpolador.interpolateTexto();

            sendMail(titulo, corpo);
        } catch (MessagingException ex) {
            log.error("Erro ao enviar email de consulta de margem", ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    protected void sendMail(String titulo, String corpo) throws MessagingException {
        MailHelper mailHelper = new MailHelper();
        mailHelper.send(TipoNotificacaoEnum.EMAIL_CONSULTA_MARGEM_SERVIDOR, getEmail(), null, null, titulo, corpo, null, null, responsavel);
    }
    }
