package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

public class EnviarEmailNotificacaoAutorizacaoIraVencerCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificacaoAutorizacaoIraVencerCommand.class);

    private String email;
    private String serNome;
    private String csaNome;
    private String dataVencimento;

    @Override
    public void execute() throws ViewHelperException {
        try {
            // 1. Busca o template do e-mail
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_AUTORIZACAO_IRA_VENCER, responsavel);

            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            if (TextHelper.isNull(email)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }

            // 2. Preenche dos dados disponíveis para uso no template
            final CustomTransferObject dados = new CustomTransferObject();
            setDadosTemplateEmail(dados);
            dados.setAttribute("ser_nome", serNome);
            dados.setAttribute("csa_nome", csaNome);
            dados.setAttribute("data_vencimento_noescape", dataVencimento);
            interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
            final String titulo = interpolador.interpolateTitulo();
            final String corpo = interpolador.interpolateTexto();

            // Envia os emails.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_AUTORIZACAO_IRA_VENCER, email, null, null, titulo, corpo, null, null, responsavel);
        } catch (final Exception ex) {
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

    public String getSerNome() {
        return serNome;
    }

    public void setSerNome(String serNome) {
        this.serNome = serNome;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }
}
