package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnviarEmailLimiteAtigindoInclusaoCsaCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailLimiteAtigindoInclusaoCsaCommand.class);

    private String cseNome;

    private String csaNome;

    private String csaNomeAbrev;

    private String cseEmail;

    private int contadorAtual;
    private AcessoSistema responsavel;

    @Override
    public void execute() throws ViewHelperException {
        try {
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_LIMITE_ATINGIDO_CSA, responsavel);
            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            if (TextHelper.isNull(cseEmail)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }

            final CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("cse_nome", cseNome);
            dados.setAttribute("csa_nome", csaNome);
            dados.setAttribute("csa_nome_abrev", csaNomeAbrev);
            dados.setAttribute("numero_contratos", contadorAtual);
            interpolador.setDados(dados);

            final String titulo = interpolador.interpolateTitulo();
            final String corpo = interpolador.interpolateTexto();

            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_LIMITE_CONSIGNACAO_DIARIO_CSA, cseEmail, null, null, titulo, corpo, null, null, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
        }
    }
}
