package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.values.CodedValues;
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
public class EnviarEmailOTPServidorCommand extends AbstractEnviarEmailCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailOTPServidorCommand.class);

    private String nomeSistema;
    private String cseNome;
    private String tituloEmail;
    private String email;
    private String otp;
    private String nome;
    private String primeiroNome;

    @Override
    public void execute() throws ViewHelperException {
		try {

			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(
					ModeloEmailEnum.ENVIAR_EMAIL_OTP_SERVIDOR, responsavel);

			String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);

			CustomTransferObject dados = new CustomTransferObject();
			dados.setAttribute("tituloEmail", tituloEmail);
			dados.setAttribute("nomeSistema", nomeSistema);
			dados.setAttribute("cseNome", cseNome);
			dados.setAttribute("nome", nome);
			dados.setAttribute("primeiroNome", primeiroNome);
			dados.setAttribute("otp", otp);
			dados.setAttribute("url_sistema", urlSistema);

			interpolador.setDados(dados);

			// 3. Interpola o template gerando os textos finais prontos para uso.
			String titulo = interpolador.interpolateTitulo();
			String corpo = interpolador.interpolateTexto();

			// Envia os emails.
			MailHelper mailHelper = new MailHelper();
			mailHelper.send(TipoNotificacaoEnum.EMAIL_OTP_SERVIDOR, email, null, null, titulo,
			        corpo, null, null, responsavel);

		} catch (Exception ex) {
		    LOG.error(ex.getMessage(), ex);
			throw new ViewHelperException("rotulo.email.otp.servidor.erro", responsavel, ex);
		}

    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPrimeiroNome() {
        return primeiroNome;
    }

    public void setPrimeiroNome(String primeiroNome) {
        this.primeiroNome = primeiroNome;
    }

    public String getCseNome() {
        return cseNome;
    }

    public void setCseNome(String cseNome) {
        this.cseNome = cseNome;
    }

    public String getNomeSistema() {
        return nomeSistema;
    }

    public void setNomeSistema(String nomeSistema) {
        this.nomeSistema = nomeSistema;
    }

    public String getTituloEmail() {
        return tituloEmail;
    }

    public void setTituloEmail(String tituloEmail) {
        this.tituloEmail = tituloEmail;
    }

}
