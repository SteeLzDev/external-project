package com.zetra.econsig.helper.email.command;

import jakarta.mail.MessagingException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarLinkCriarSenhaUsuarioCommand</p>
 * <p>Description: Command para envio de link para criação de senha para o email do novo usuário.</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 27237 $
 * $Date: 2019-12-31 11:12:28 -0300 (ter, 31 dez 2019) $
 */
public class EnviarLinkCriarSenhaUsuarioCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarLinkCriarSenhaUsuarioCommand.class);

    private String serEmail;

    private String serNome;

    private String link;

    private String usuLogin;

    @Override
    public void execute() throws ViewHelperException {
        if (TextHelper.isNull(serEmail)) {
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        // 1. Busca o template do e-mail
        ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIA_EMAIL_LINK_DEFINIR_SENHA_NOVO_USUARIO, responsavel);

        // 2. Interpola o template gerando os textos finais prontos para uso.
        String titulo = interpolador.interpolateTitulo();
        String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);

        if (!TextHelper.isNull(urlSistema)) {
            urlSistema = urlSistema.endsWith("/") ? urlSistema : urlSistema + "/";
        }
        String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);

        // 3. Preenche dos dados disponíveis para uso no template
        CustomTransferObject dados = new CustomTransferObject();
        dados.setAttribute("serPrimeiroNome", serNome);
        dados.setAttribute("linkRecuperacao", link);
        dados.setAttribute(Columns.USU_LOGIN, usuLogin);
        dados.setAttribute("url_sistema", urlSistema);
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
            mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_CRIAR_SENHA_NOVO_USUARIO, serEmail, null, null, titulo, corpo, null, null, responsavel);
        } catch (MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, ex);
        }
    }

    public void setSerEmail(String serEmail) {
        this.serEmail = serEmail;
    }

    public void setSerNome(String serPrimeiroNome) {
        serNome = serPrimeiroNome;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setUsuLogin(String usuLogin) {
        this.usuLogin = usuLogin;
    }
}