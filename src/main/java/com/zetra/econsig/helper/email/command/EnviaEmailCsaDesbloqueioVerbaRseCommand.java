package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.notificacao.NotificacaoEmailController;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import lombok.Setter;

@Setter
public class EnviaEmailCsaDesbloqueioVerbaRseCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviaEmailCsaDesbloqueioVerbaRseCommand.class);

    private String csaEmail;
    private String csaNome;
    private int count;
    private AcessoSistema responsavel;

    @Override
    public void execute() throws ViewHelperException {
        try {
            final NotificacaoEmailController notificacaoEmailController = ApplicationContextProvider.getApplicationContext().getBean(NotificacaoEmailController.class);
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_CSA_DESBLOQUEIO_VERBA, responsavel);
            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            if (TextHelper.isNull(csaEmail)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }

            CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("csa_nome", csaNome);
            dados.setAttribute("usuario_logado", responsavel.getUsuNome());
            dados.setAttribute("qtde_desbloqueios", count);
            interpolador.setDados(dados);

            String titulo = interpolador.interpolateTitulo();
            String corpo = interpolador.interpolateTexto();

            MailHelper mailHelper = new MailHelper();
            mailHelper.send(null, csaEmail, null, null, titulo, corpo, null, null, responsavel);
            notificacaoEmailController.criarNotificacao(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_DESBLOQUEIO_VERBA_RSE.getCodigo(), csaEmail, titulo, corpo, DateHelper.getSystemDatetime(), DateHelper.getSystemDatetime(), responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
        }
    }
}
