package com.zetra.econsig.helper.email.command;

import java.util.List;

import jakarta.mail.MessagingException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailCadastroSenhaServidorCommand</p>
 * <p>Description: Command para envio de notificação de cadastro de senha pelo servidor.</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author:  $
 * $Revision:  $
 * $Date:  $
 */
public class EnviarEmailNotificacaoCadastroSenhaServidorCommand extends AbstractEnviarEmailCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificacaoCadastroSenhaServidorCommand.class);

    private String serEmail;
    private String serPrimeiroNome;
    private List<String> anexos;

    public void setSerEmail(String serEmail) {
        this.serEmail = serEmail;
    }

    public void setSerPrimeirNome(String serPrimeiroNome) {
        this.serPrimeiroNome = serPrimeiroNome;
    }

    public List<String> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<String> anexos) {
        this.anexos = anexos;
    }


    @Override
    public void execute() throws ViewHelperException {
        if (TextHelper.isNull(serEmail)) {
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        // 1. Busca o template do e-mail
        ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_CADASTRO_SENHA_SERVIDOR, responsavel);

        // 2. Interpola o template gerando os textos finais prontos para uso.
        String titulo = interpolador.interpolateTitulo();

        String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
        String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);

        // 3. Preenche dos dados disponíveis para uso no template
        CustomTransferObject dados = new CustomTransferObject();
        dados.setAttribute("serPrimeiroNome", serPrimeiroNome);
        dados.setAttribute("logoSistema", urlSistema + "img/logo_sistema.png");
        dados.setAttribute("logoZetra", (versaoLeiaute == null || "v4".equals(versaoLeiaute)) ? urlSistema + "img/logo_empresa_branco.png" : urlSistema + "img/econsig-logo-v5.png");
        dados.setAttribute("logoFacebook", urlSistema + "img/logo_facebook.png");
        dados.setAttribute("logoInstagram", urlSistema + "img/logo_instagram.png");
        dados.setAttribute("logoLinkedin", urlSistema + "img/logo_linkedin.png");
        dados.setAttribute("logoGooglePlay", urlSistema + "img/logo_googleplay.png");
        dados.setAttribute("logoAppleStore", urlSistema + "img/logo_applestore.png");

        interpolador.setDados(dados);
        String corpo = interpolador.interpolateTexto();

        // 4. Envia o email
        try {
            MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_CADASTRO_SENHA_SERVIDOR, serEmail, null, null, titulo, corpo, anexos, null, responsavel);
        } catch (MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, ex);
        }
    }
}
