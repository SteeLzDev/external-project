package com.zetra.econsig.helper.email.command;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.ModeloEmailEnum;

import jakarta.mail.MessagingException;

public class EnviarEmailExpiracaoParaConsignatariaCommand extends AbstractEnviarEmailCommand {

    String email;

    String cseNome;

    String csaNome;

    int prazoConsignataria;

    public void setPrazoConsignatarias(int prazoConsignataria) { this.prazoConsignataria = prazoConsignataria; }

    public void setEmail(String email) { this.email = email; }

    public void setCseNome(String cseNome) {
        this.cseNome = cseNome;
    }

    public void setCsaNome(String csaNome) { this.csaNome = csaNome; }

    @Override
    public void execute() throws ViewHelperException {
        final String nomeSistema = JspHelper.getNomeSistema(responsavel);

        ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_EXPIRACAO_PARA_CSA, responsavel);
        final CustomTransferObject dados = new CustomTransferObject();

        dados.setAttribute("nome_sistema", nomeSistema);
        dados.setAttribute("cse_nome", cseNome);

        interpolador.setDados(dados);

        final String titulo = interpolador.interpolateTitulo();

        final StringBuilder texto = new StringBuilder();
        texto.append(interpolador.interpolateTexto());

        final CustomTransferObject dadosCsa = new CustomTransferObject();
        interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_EXPIRACAO_PARA_CSA_TEXTO, responsavel);

        dadosCsa.setAttribute("dias", prazoConsignataria);
        interpolador.setDados(dadosCsa);
        texto.append(interpolador.interpolateTitulo());

        dadosCsa.setAttribute("csa_nome", csaNome);
        interpolador.setDados(dadosCsa);
        texto.append(interpolador.interpolateTexto());

        final MailHelper mailHelper = new MailHelper();
        try {
            mailHelper.send(email, null, null, titulo, texto.toString(), null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.alerta.dias.expiracao.consignataria", responsavel, e);
        }
    }
}
