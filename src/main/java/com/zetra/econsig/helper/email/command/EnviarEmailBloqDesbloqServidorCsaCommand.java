package com.zetra.econsig.helper.email.command;

import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailBloqDesbloqServidorCsaCommand</p>
 * <p>Description: Command para envio de email bloqueio/desbloqueio de servidor pela consignatária.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: 2022-08-29 14:28:22 -0300 (Ter, 29 ago 2022) $
 */
public class EnviarEmailBloqDesbloqServidorCsaCommand extends AbstractEnviarEmailCommand {

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailBloqDesbloqServidorCsaCommand.class);

	private final String rseCodigo;
	private final List<TransferObject> bloqueado;
	private final List<TransferObject> desbloqueado;
	private final List<TransferObject> alterado;
	private final AcessoSistema responsavel;

	public EnviarEmailBloqDesbloqServidorCsaCommand(String rseCodigo, List<TransferObject> bloqueado, List<TransferObject> desbloqueado, List<TransferObject> alterado, AcessoSistema responsavel) {
		this.rseCodigo = rseCodigo;
		this.bloqueado = bloqueado;
		this.desbloqueado = desbloqueado;
		this.alterado = alterado;
		this.responsavel = responsavel;
	}

	@Override
	public void execute() throws ViewHelperException {
		try {
			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_BLOQ_DESBLOQ_SERVIDOR_CSA, responsavel);

			if (TextHelper.isNull(interpolador)) {
				throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
			}

			if (TextHelper.isNull(rseCodigo)) {
				throw new ViewHelperException("mensagem.nenhumServidorEncontrado", responsavel);
			}

			// se não teve alteração, retorna sem enviar email
			if (bloqueado.isEmpty() && desbloqueado.isEmpty() && alterado.isEmpty()) {
				return;
			}

			String serNome = "";
			String serEmail = "";

			StringBuilder strBloqueado = new StringBuilder();
			StringBuilder strDesbloqueado = new StringBuilder();
			StringBuilder strAlterado = new StringBuilder();

			// Busca o servidor
			ServidorDelegate serDelegate = new ServidorDelegate();

			TransferObject servidor = serDelegate.buscaServidor(rseCodigo, responsavel);
			if (servidor == null) {
				throw new ViewHelperException("mensagem.nenhumServidorEncontrado", responsavel);
			}

			serNome = (String) servidor.getAttribute(Columns.SER_NOME);
			serEmail = (String) servidor.getAttribute(Columns.SER_EMAIL);

			// Verifica se o servidor possui e-mail cadastrado
			if (TextHelper.isNull(serEmail)) {
				throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
			}

			// Texto com os dados do bloqueio
			if (!bloqueado.isEmpty()) {
				strBloqueado.append(ApplicationResourcesHelper.getMessage("mensagem.email.bloqueio.servidor.csa", responsavel));
				strBloqueado.append("\r\n");

				Iterator<TransferObject> it = bloqueado.iterator();

				while (it.hasNext()) {
					TransferObject to = it.next();
					strBloqueado.append(to.getAttribute(Columns.CSA_NOME));
					if (it.hasNext()) {
						strBloqueado.append(", ");
					}
				}
				strBloqueado.append(".\r\n");
			}

			// Texto com os dados do desbloqueio
			if (!desbloqueado.isEmpty()) {
				strDesbloqueado.append("\r\n");
				Iterator<TransferObject> it = desbloqueado.iterator();
				StringBuilder msg = new StringBuilder();
				while (it.hasNext()) {
					TransferObject to = it.next();
					msg.append(to.getAttribute(Columns.CSA_NOME));
					if (it.hasNext()) {
						msg.append(", ");
					}
				}

				strDesbloqueado.append(ApplicationResourcesHelper.getMessage("mensagem.email.desbloqueio.servidor.csa", responsavel, msg.toString(), JspHelper.getNomeSistema(responsavel)));
				strDesbloqueado.append("\r\n");
			}

			// Texto com os dados dos alterados
			if (!alterado.isEmpty()) {
				strAlterado.append("\r\n");
				Iterator<TransferObject> it = alterado.iterator();
				while (it.hasNext()) {
					TransferObject to = it.next();
					strAlterado.append(ApplicationResourcesHelper.getMessage("mensagem.email.alteracao.servidor.csa", responsavel, to.getAttribute(Columns.CSA_NOME).toString(), to.getAttribute(Columns.PRC_VLR).toString()));
					if (it.hasNext()) {
						strAlterado.append("\r\n");
					}
				}
				strAlterado.append("\r\n");
			}

			CustomTransferObject dados = new CustomTransferObject();
            setDadosTemplateEmail(dados);
			dados.setAttribute("ser_nome", serNome);
			dados.setAttribute("bloqueado", strBloqueado);
			dados.setAttribute("desbloqueado", strDesbloqueado);
			dados.setAttribute("alterado", strAlterado);
			interpolador.setDados(dados);

			// 3. Interpola o template gerando os textos finais prontos para uso.
			String titulo = interpolador.interpolateTitulo();
			String corpo = interpolador.interpolateTexto();

			// Envia o email
			MailHelper mailHelper = new MailHelper();
			mailHelper.send(TipoNotificacaoEnum.EMAIL_BLOQUEIO_DESBLOQUEIO_SERVIDOR_CSA, serEmail, null, null, titulo, corpo, null, null, responsavel);

		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
		}

	}
}
