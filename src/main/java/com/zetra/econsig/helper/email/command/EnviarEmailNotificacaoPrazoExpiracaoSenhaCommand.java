package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

public class EnviarEmailNotificacaoPrazoExpiracaoSenhaCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificacaoPrazoExpiracaoSenhaCommand.class);

    private String cseNome;
    private String usuEmail;
    private String usuNome;
    private Integer qtdeDiasExpiracaoSenha;

    @Override
    public void execute() throws ViewHelperException {
        try {
            // 1. Busca o template do e-mail
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_PRAZO_EXPIRACAO_SENHA, responsavel);

            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            if (!TextHelper.isNull(usuEmail)) {
                // 2. Preenche dos dados disponíveis para uso no template
                final CustomTransferObject dados = new CustomTransferObject();
                setDadosTemplateEmail(dados);
                dados.setAttribute("cse_nome", cseNome);
                dados.setAttribute("usu_nome", usuNome);
                dados.setAttribute("qtd_dias_expiracao_senha", qtdeDiasExpiracaoSenha);
                interpolador.setDados(dados);
    
                // 3. Interpola o template gerando os textos finais prontos para uso.
                final String titulo = interpolador.interpolateTitulo();
                final String corpo = interpolador.interpolateTexto();
    
                // Envia os emails.
                final MailHelper mailHelper = new MailHelper();
                mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_PRAZO_EXPIRACAO_SENHA, usuEmail, null, null, titulo, corpo, null, null, responsavel);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
        }
    }

    public String getCseNome() {
        return cseNome;
    }

    public void setCseNome(String cseNome) {
        this.cseNome = cseNome;
    }

    public String getUsuEmail() {
        return usuEmail;
    }

    public void setUsuEmail(String usuEmail) {
        this.usuEmail = usuEmail;
    }

    public String getUsuNome() {
        return usuNome;
    }

    public void setUsuNome(String usuNome) {
        this.usuNome = usuNome;
    }

    public Integer getQtdeDiasExpiracaoSenha() {
        return qtdeDiasExpiracaoSenha;
    }

    public void setQtdeDiasExpiracaoSenha(Integer qtdeDiasExpiracaoSenha) {
        this.qtdeDiasExpiracaoSenha = qtdeDiasExpiracaoSenha;
    }
}