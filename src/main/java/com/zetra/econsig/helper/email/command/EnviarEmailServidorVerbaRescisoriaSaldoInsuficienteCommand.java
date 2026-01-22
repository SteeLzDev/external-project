package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.ModeloEmailEnum;

import jakarta.mail.MessagingException;

/**
 * <p>Title: EnviarEmailExpiracaoConsignatariaCommand</p>
 * <p>Description: Command para envio de email de expiracão de consignatárias para cse/org</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailServidorVerbaRescisoriaSaldoInsuficienteCommand extends AbstractEnviarEmailCommand {

    AcessoSistema responsavel;
    String email;
    String serNome;
    String detalheConsignacao;

    @Override
    public void execute() throws ViewHelperException {

        final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_SER_SALDO_INSUF_VERBA_RESCISORIA, responsavel);
        final CustomTransferObject dados = new CustomTransferObject();

        dados.setAttribute("nome_servidor", serNome);
        dados.setAttribute("detalhe_consignacao_label_html_noescape", detalheConsignacao);

        interpolador.setDados(dados);

        final String titulo = interpolador.interpolateTitulo();
        final String texto = interpolador.interpolateTexto();

        final MailHelper mailHelper = new MailHelper();
        try {
            mailHelper.send(email, null, null, titulo, texto, null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.servidor.verba.rescisoria.saldo.insuficiente", responsavel, e);
        }

    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSerNome(String serNome) {
        this.serNome = serNome;
    }

    public void setDetalheConsignacao(String detalheConsignacao) {
        this.detalheConsignacao = detalheConsignacao;
    }
}
