package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailNotifAlterCodVerbaConvCsaCommand</p>
 *
 * <p>Description: Command para envio de email de notificação referente à alteração do código da verba do convênio da CSA</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author: moises.souza $
 * $Revision: 25344 $
 * $Date: 2018-08-30 10:23:19 -0300 (Qui, 30 ago 2018) $
 */

public class EnviarEmailNotifAlterCodVerbaConvCsaCommand  extends AbstractEnviarEmailCommand {
    private String email;
    private String servico;
    private StringBuilder listaAtivado;
    private StringBuilder listaDesativado;
    private StringBuilder listaCodVerbaAlterado;

    @Override
    public void execute() throws ViewHelperException {
		try {
			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA, responsavel);

            // 2. Preenche dos dados disponíveis para uso no template
	        CustomTransferObject dados = new CustomTransferObject();
	            dados.setAttribute("servico", servico);
	            dados.setAttribute("orgaosAtivados", listaAtivado);
	            dados.setAttribute("orgaosDesativados", listaDesativado);
	            dados.setAttribute("codVerbaAlterado", listaCodVerbaAlterado);

	        interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
			String titulo = interpolador.interpolateTitulo();
			String corpo = interpolador.interpolateTexto().replaceAll("&lt;br&gt;", "<br>");

			// Envia o email.
			MailHelper mailHelper = new MailHelper();
			mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_ALTERACAO_CODVERBA_CONVENIO_CSA, email.replaceAll(";", ","), null, null, titulo, corpo, null, null, responsavel);

		} catch (Exception ex) {
		    throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
		}

    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public StringBuilder getListaAtivado() {
        return listaAtivado;
    }

    public void setListaAtivado(StringBuilder listaAtivado) {
        this.listaAtivado = listaAtivado;
    }

    public StringBuilder getListaDesativado() {
        return listaDesativado;
    }

    public void setListaDesativado(StringBuilder listaDesativado) {
        this.listaDesativado = listaDesativado;
    }

    public StringBuilder getListaCodVerbaAlterado() {
        return listaCodVerbaAlterado;
    }

    public void setListaCodVerbaAlterado(StringBuilder listaCodVerbaAlterado) {
        this.listaCodVerbaAlterado = listaCodVerbaAlterado;
    }

}
