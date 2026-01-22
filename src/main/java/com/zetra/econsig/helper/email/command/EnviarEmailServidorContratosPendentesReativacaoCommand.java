package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailServidorContratosPendentesReativacaoCommand</p>
 * <p>Description: Command para envio de email de informação ao servidor que existem contratos pendentes de reativação.
 * de consignante.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: 2022-03-15 14:28:22 -0300 (Ter, 15 mar 2022) $
 */
public class EnviarEmailServidorContratosPendentesReativacaoCommand extends AbstractEnviarEmailCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailServidorContratosPendentesReativacaoCommand.class);

    private String nome;
    private String email;

    @Override
    public void execute() throws ViewHelperException {
		try {

			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(
					ModeloEmailEnum.ENVIAR_EMAIL_SERVIDOR_CONTRATOS_SUSPENSOS_PENDENTES_REATIVACAO, responsavel);

			if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

			if (TextHelper.isNull(email)) {
	            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
	        }

			CustomTransferObject dados = new CustomTransferObject();
			dados.setAttribute("nome_servidor", nome);

			interpolador.setDados(dados);

			// 3. Interpola o template gerando os textos finais prontos para uso.
			String titulo = interpolador.interpolateTitulo();
			String corpo = interpolador.interpolateTexto();

			// Envia os emails.
			MailHelper mailHelper = new MailHelper();
			mailHelper.send(TipoNotificacaoEnum.EMAIL_SERVIDOR_CONTRATO_PENDENTE_REATIVACAO, email, null, null, titulo,
			        corpo, null, null, responsavel);

		} catch (Exception ex) {
		    LOG.error(ex.getMessage(), ex);
			throw new ViewHelperException("rotulo.email.criacao.novo.usuario.cse.erro", responsavel, ex);
		}

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
