package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
/**
 * <p>Title: EnviarEmailNotificaoCsaBloqSerCnvVariacaoMargemCommand </p>
 * <p>Description: Envia email informando à consignatária que uma determinada de servdiores foram bloqueados para cada verba por ultrapassarem o limite estabelecido de variação de margem.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: $
 */
public class EnviarEmailNotificaoCsaBloqSerCnvVariacaoMargemCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificaoCsaBloqSerCnvVariacaoMargemCommand.class);

    private String email;
    private String csaNome;
    private String quantidadePorVerba;

    @Override
    public void execute() throws ViewHelperException {
		try {
			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_CSA_BLOQ_SER_VARIACAO_MARGEM_LIMITE_CSA, responsavel);

			if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

			if (TextHelper.isNull(email)) {
	            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
	        }

			CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("csaNome", csaNome);
            dados.setAttribute("quantidadePorVerba", quantidadePorVerba);
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

    public String getCsaNome() {
        return csaNome;
    }

    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }

    public String getQuantidadePorVerba() {
        return quantidadePorVerba;
    }

    public void setQuantidadePorVerba(String quantidadePorVerba) {
        this.quantidadePorVerba = quantidadePorVerba;
    }
}
