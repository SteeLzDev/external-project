package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

import jakarta.mail.MessagingException;

/**
 * <p>Title: EnviarEmailAlteracaoPerfilUsuarioCommand</p>
 * <p>Description: Command para envio de e-mail de alerta de alterações realizadas em perfil de usuários da entidade.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailAlteracaoPerfilUsuarioCommand extends AbstractEnviarEmailCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailAlteracaoPerfilUsuarioCommand.class);

    private final String emailDestinatario;
    private final String perDescricao;
    private final AcessoSistema responsavel;

    public EnviarEmailAlteracaoPerfilUsuarioCommand(String emailDestinatario, String perDescricao, AcessoSistema responsavel) {
        this.emailDestinatario = emailDestinatario;
        this.perDescricao = perDescricao;
        this.responsavel = responsavel;
    }

    @Override
    public void execute() throws ViewHelperException {
        if (TextHelper.isNull(emailDestinatario)) {
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        // 1. Busca o template do e-mail
        final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_ALTERACAO_PERFIL_USUARIO, responsavel);

        // 2. Interpola o template gerando os textos finais prontos para uso.
        final String titulo = interpolador.interpolateTitulo();
        String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);

        if (!TextHelper.isNull(urlSistema)) {
            urlSistema = urlSistema.endsWith("/") ? urlSistema : urlSistema + "/";
        }

        String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
        // 3. Preenche dos dados disponíveis para uso no template
        final CustomTransferObject dados = new CustomTransferObject();
        dados.setAttribute("responsavel", responsavel.getUsuNome());
        dados.setAttribute("perDescricao", perDescricao);
        dados.setAttribute("url_sistema", urlSistema);

        dados.setAttribute("logoSistema", urlSistema + "img/logo_sistema.png");
        dados.setAttribute("logoZetra", (versaoLeiaute == null || "v4".equals(versaoLeiaute)) ? urlSistema + "img/logo_empresa_branco.png" : urlSistema + "img/econsig-logo-v5.png");
        dados.setAttribute("logoFacebook", urlSistema + "img/logo_facebook.png");
        dados.setAttribute("logoInstagram", urlSistema + "img/logo_instagram.png");
        dados.setAttribute("logoLinkedin", urlSistema + "img/logo_linkedin.png");
        dados.setAttribute("logoGooglePlay", urlSistema + "img/logo_googleplay.png");
        dados.setAttribute("logoAppleStore", urlSistema + "img/logo_applestore.png");

        interpolador.setDados(dados);
        final String corpo = interpolador.interpolateTexto();

        // 4. Envia o email
        try {
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_ALTERACAO_PERFIL_USUARIO, emailDestinatario, null, null, titulo, corpo, null, null, responsavel);
        } catch (final MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, ex);
        }
    }
}