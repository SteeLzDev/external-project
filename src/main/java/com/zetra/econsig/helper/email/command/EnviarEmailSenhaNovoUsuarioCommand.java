package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

public class EnviarEmailSenhaNovoUsuarioCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailSenhaNovoUsuarioCommand.class);

    private String usuNome;
    private String usuEmail;
    private String usuSenha;

    @Override
    public void execute() throws ViewHelperException {
        try {
            // 1. Busca o template do e-mail
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIA_EMAIL_SENHA_NOVO_USUARIO, responsavel);

            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            if (!TextHelper.isNull(usuEmail)) {
                final CustomTransferObject dados = new CustomTransferObject();
                dados.setAttribute("usuPrimeiroNome", !TextHelper.isNull(usuNome) ? usuNome.split(" ")[0] : "");
                dados.setAttribute("senha_inicial", usuSenha);

                interpolador.setDados(dados);

                // 3. Interpola o template gerando os textos finais prontos para uso.
                final String titulo = interpolador.interpolateTitulo();
                final String corpo = interpolador.interpolateTexto();

                // Envia os emails.
                final MailHelper mailHelper = new MailHelper();
                mailHelper.send(TipoNotificacaoEnum.EMAIL_SENHA_NOVO_USUARIO, usuEmail, null, null, titulo, corpo, null, null, responsavel);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
        }
    }

    public String getUsuNome() {
        return usuNome;
    }

    public void setUsuNome(String usuNome) {
        this.usuNome = usuNome;
    }

    public String getUsuEmail() {
        return usuEmail;
    }

    public void setUsuEmail(String usuEmail) {
        this.usuEmail = usuEmail;
    }

    public String getUsuSenha() {
        return usuSenha;
    }

    public void setUsuSenha(String usuSenha) {
        this.usuSenha = usuSenha;
    }
}
