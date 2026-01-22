package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;

public class EnviarEmailRefinanciamentoCsaCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailRefinanciamentoCsaCommand.class);

    private String email;
    private String csaNome;
    private String adeNumeros;
    private String percentual;

    @Override
    public void execute() throws ViewHelperException {
        try {
            // 1. Busca o template do e-mail
            ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_OFERTA_REFINANCIANENTO_CSA, responsavel);

            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            if (TextHelper.isNull(email)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }

            CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("nome_csa", csaNome);
            dados.setAttribute("ade_numero", adeNumeros);
            dados.setAttribute("percentual", percentual);
            interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
            String titulo = interpolador.interpolateTitulo();
            String corpo = interpolador.interpolateTexto();

            // Envia os emails.
            MailHelper mailHelper = new MailHelper();
            mailHelper.send(null, email, null, null, titulo, corpo, null, null, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCsaNome() {
        return csaNome;
    }

    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }

    public String getAdeNumeros() { return adeNumeros; }

    public void setAdeNumeros(String adeNumeros) { this.adeNumeros = adeNumeros; }

    public String getPercentual() { return percentual; }

    public void setPercentual(String percentual) { this.percentual = percentual; }
}
