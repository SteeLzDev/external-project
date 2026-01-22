package com.zetra.econsig.helper.email.command;

import jakarta.mail.MessagingException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailMensalAtualizarTaxasCommand</p>
 * <p>Description: Command para envio de email mensal para consignatárias que configuraram parâmetro de serviço com prazo
 *                 de validade para taxas na criação de que aquelas devem se atentar para o vencimento destas.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailMensalAtualizarTaxasCommand extends AbstractEnviarEmailCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailMensalAtualizarTaxasCommand.class);

    private TransferObject csa;

    @SuppressWarnings("java:S3358")
    @Override
    public void execute() throws ViewHelperException {
        try {
            String csaEmail = (String) csa.getAttribute(Columns.CSA_EMAIL);

            if (TextHelper.isNull(csaEmail)) {
                throw new ViewHelperException("mensagem.erro.consignataria.email.nao.informado", responsavel, (String) csa.getAttribute(Columns.CSA_NOME_ABREV));
            }

            // 1. Busca o template do e-mail
            ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_MENSAL_ATUALIZAR_TAXAS, responsavel);

            String titulo = interpolador.interpolateTitulo();

            CustomTransferObject dadosTemplateEmail = new CustomTransferObject();
            setDadosTemplateEmail(dadosTemplateEmail);
            dadosTemplateEmail.setAttribute("cet_abreviado", ApplicationResourcesHelper.getMessage("rotulo.cet.abreviado", responsavel));
            dadosTemplateEmail.setAttribute("csaResponsavel", !TextHelper.isNull(csa.getAttribute(Columns.CSA_RESPONSAVEL)) ? csa.getAttribute(Columns.CSA_RESPONSAVEL) :
                !TextHelper.isNull(csa.getAttribute(Columns.CSA_RESPONSAVEL_2)) ? csa.getAttribute(Columns.CSA_RESPONSAVEL_2) :
                !TextHelper.isNull(csa.getAttribute(Columns.CSA_RESPONSAVEL_3)) ? csa.getAttribute(Columns.CSA_RESPONSAVEL_3) : csa.getAttribute(Columns.CSA_NOME));

            interpolador.setDados(dadosTemplateEmail);
            String corpo = interpolador.interpolateTexto();

            // 4. Envia o email
            MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_MENSAL_ALERTA_ATUALIZACAO_CET, csaEmail, null, null, titulo, corpo, null, null, responsavel);


        } catch (MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, ex);
        }

    }

    public TransferObject getCsaCodigo() {
        return csa;
    }

    public void setCsaCodigo(TransferObject csa) {
        this.csa = csa;
    }

}
