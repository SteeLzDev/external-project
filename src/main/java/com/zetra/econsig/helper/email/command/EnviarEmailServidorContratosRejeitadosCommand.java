package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailServidorContratosRejeitadosCommand</p>
 * <p>Description: Command para envio de email de notificação contratos rejeitados pela folha.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailServidorContratosRejeitadosCommand extends AbstractEnviarEmailCommand {

    private String email;
    private String corpoEmail;

    @Override
    public void execute() throws ViewHelperException {
		try {

			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(
					ModeloEmailEnum.ENVIA_EMAIL_SERVIDOR_CONTRATOS_REJEITADOS, responsavel);

	        CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("tabela_noescape", corpoEmail);

            interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
            String titulo = interpolador.interpolateTitulo();
            String corpo = interpolador.interpolateTexto();

			// Envia os emails.
			MailHelper mailHelper = new MailHelper();
			mailHelper.send(TipoNotificacaoEnum.EMAIL_CONTRATOS_REJEITADOS_FOLHA, email, null, null, titulo, corpo, null, null, responsavel);

		} catch (Exception ex) {
			throw new ViewHelperException("rotulo.habilita.email.servidor.contratos.rejeitados.erro", responsavel, ex);
		}

    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCorpoEmail() {
		return corpoEmail;
	}

	public void setCorpoEmail(String corpoEmail) {
		this.corpoEmail = corpoEmail;
	}
}
