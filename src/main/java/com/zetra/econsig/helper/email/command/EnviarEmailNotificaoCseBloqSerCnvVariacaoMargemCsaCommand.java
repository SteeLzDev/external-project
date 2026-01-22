package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
/**
 * <p>Title: EnviarEmailNotificaoCseBloqSerCnvVariacaoMargemCsaCommand </p>
 * <p>Description: Envia email informando ao consignante que foi gerado um arquivo com a lista de servidores bloqueados para determinada csa, pois o limite de variação foi superado</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: $
 */
public class EnviarEmailNotificaoCseBloqSerCnvVariacaoMargemCsaCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificaoCseBloqSerCnvVariacaoMargemCsaCommand.class);

    private String email;

    @Override
    public void execute() throws ViewHelperException {
		try {
			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_BLOQ_SER_VARIACAO_MARGEM_LIMITE_CSA, responsavel);

			if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

			if (TextHelper.isNull(email)) {
	            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
	        }

			CustomTransferObject dados = new CustomTransferObject();
			interpolador.setDados(dados);

			// 3. Interpola o template gerando os textos finais prontos para uso.
			String titulo = interpolador.interpolateTitulo();
			String corpo = interpolador.interpolateTexto();

			// Envia os emails.
			MailHelper mailHelper = new MailHelper();
            mailHelper.send(null, email, null, null, titulo, corpo, null, null, responsavel);
		} catch (Exception ex) {
		    LOG.error(ex.getMessage(), ex);
			throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
		}

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
