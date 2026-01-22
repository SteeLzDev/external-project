package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

public class EnviarEmailCseBloqueioUsuarioCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailCseBloqueioUsuarioCommand.class);

    private String cseEmail;
    private String usuNome;
    private String usuCpf;
    private String usuLogin;
    private String usuEmail;

    @Override
    public void execute() throws ViewHelperException {
        try {
            // 1. Busca o template do e-mail
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_CSE_BLOQUEIO_USUARIO, responsavel);

            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            if (!TextHelper.isNull(cseEmail)) {
                // 2. Preenche dos dados disponíveis para uso no template
                final CustomTransferObject dados = new CustomTransferObject();
                setDadosTemplateEmail(dados);
                
                StringBuilder dadosUsuario = new StringBuilder();  
                appendIfNotNull(dadosUsuario, usuCpf);
                appendIfNotNull(dadosUsuario, usuNome);                
                appendIfNotNull(dadosUsuario, usuLogin);
                appendIfNotNull(dadosUsuario, usuEmail);         
                dados.setAttribute("dados_usuario", dadosUsuario.toString());
                
                interpolador.setDados(dados);
    
                // 3. Interpola o template gerando os textos finais prontos para uso.
                final String titulo = interpolador.interpolateTitulo();
                final String corpo = interpolador.interpolateTexto();
    
                // Envia os emails.
                final MailHelper mailHelper = new MailHelper();
                mailHelper.send(TipoNotificacaoEnum.EMAIL_CSE_BLOQUEIO_USUARIO, cseEmail, null, null, titulo, corpo, null, null, responsavel);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
        }
    }
    
    private void appendIfNotNull(StringBuilder dadosUsuario, String dado) {
        if (!TextHelper.isNull(dado)) {
            if (dadosUsuario.length() > 0) {
                dadosUsuario.append(" / ");
            }
            dadosUsuario.append(dado);
        }
    }

    public String getCseEmail() {
        return cseEmail;
    }

    public void setCseEmail(String cseEmail) {
        this.cseEmail = cseEmail;
    }

    public String getUsuNome() {
        return usuNome;
    }

    public void setUsuNome(String usuNome) {
        this.usuNome = usuNome;
    }

    public String getUsuCpf() {
        return usuCpf;
    }

    public void setUsuCpf(String usuCpf) {
        this.usuCpf = usuCpf;
    }

    public String getUsuLogin() {
        return usuLogin;
    }

    public void setUsuLogin(String usuLogin) {
        this.usuLogin = usuLogin;
    }

    public String getUsuEmail() {
        return usuEmail;
    }

    public void setUsuEmail(String usuEmail) {
        this.usuEmail = usuEmail;
    }
}
