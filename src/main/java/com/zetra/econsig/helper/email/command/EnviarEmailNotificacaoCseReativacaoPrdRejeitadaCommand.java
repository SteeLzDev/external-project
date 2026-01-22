package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailNotificacaoCseReativacaoPrdRejeitadaCommand</p>
 * <p>Description: Command para envio de email de informação ao consignante da reativação do contrato com parcela rejeitada.
 * de consignante.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: 2022-06-22 14:28:22 -0300 (Ter, 15 mar 2022) $
 */
public class EnviarEmailNotificacaoCseReativacaoPrdRejeitadaCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificacaoCseReativacaoPrdRejeitadaCommand.class);

    private String email;
    private String cseNome;
    private String nome;
    private String matricula;
    private String serCpf;
    private String adeNumero;

    @Override
    public void execute() throws ViewHelperException {
		try {

			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_CSE_REATIVACAO_PRD_REJEITADA, responsavel);

			if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

			if (TextHelper.isNull(email)) {
	            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
	        }

			CustomTransferObject dados = new CustomTransferObject();
			dados.setAttribute("cse_nome", cseNome);
			dados.setAttribute("nome_servidor", nome);
            dados.setAttribute("matricula", matricula);
            dados.setAttribute("cpf", serCpf);
            dados.setAttribute("ade_numero", adeNumero);

			interpolador.setDados(dados);

			// 3. Interpola o template gerando os textos finais prontos para uso.
			String titulo = interpolador.interpolateTitulo();
			String corpo = interpolador.interpolateTexto();

			// Envia os emails.
			MailHelper mailHelper = new MailHelper();
			mailHelper.send(TipoNotificacaoEnum.EMAIL_SERVIDOR_CONTRATO_REATIVACAO_PRD_REJEITADA, email, null, null, titulo, corpo, null, null, responsavel);

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

    public String getCseNome() {
        return cseNome;
    }

    public void setCseNome(String cseNome) {
        this.cseNome = cseNome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getSerCpf() {
        return serCpf;
    }

    public void setSerCpf(String serCpf) {
        this.serCpf = serCpf;
    }

    public String getAdeNumero() {
        return adeNumero;
    }

    public void setAdeNumero(String adeNumero) {
        this.adeNumero = adeNumero;
    }

}
