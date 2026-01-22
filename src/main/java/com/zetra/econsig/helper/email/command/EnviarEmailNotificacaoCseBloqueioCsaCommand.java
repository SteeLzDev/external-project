package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailNotificacaoCseBloqueioCsaCommand</p>
 * <p>Description: Command para envio de email informando ao gestor que a consignatária foi bloqueada
 * de consignante.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 */
public class EnviarEmailNotificacaoCseBloqueioCsaCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificacaoCseBloqueioCsaCommand.class);

    private String email;
    private String csaNomeAbrev;

    private String csaNome;

    private String dataBloqueio;

    private String usuario;

    private String MotivoBloqueio;

    @Override
    public void execute() throws ViewHelperException {
		try {
			// 1. Busca o template do e-mail
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_CSE_BLOQUEIO_CONSIGNATARIA, responsavel);

			if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

			if (TextHelper.isNull(email)) {
	            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
	        }

			final CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("csa_nome_abrev", csaNomeAbrev);
            dados.setAttribute("csa_nome", csaNome);
            dados.setAttribute("data_bloqueio_noescape", dataBloqueio);
            dados.setAttribute("usuario_bloqueiou", usuario);
            dados.setAttribute("motivo_bloqueio", MotivoBloqueio);
			interpolador.setDados(dados);

			// 3. Interpola o template gerando os textos finais prontos para uso.
			final String titulo = interpolador.interpolateTitulo();
			final String corpo = interpolador.interpolateTexto();

			// Envia os emails.
			final MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICAO_CSE_BLOQUEIO_CONSIGNATARIA, email, null, null, titulo, corpo, null, null, responsavel);
		} catch (final Exception ex) {
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

    public String getCsaNomeAbrev() {
        return csaNomeAbrev;
    }

    public void setCsaNomeAbrev(String csaNomeAbrev) {
        this.csaNomeAbrev = csaNomeAbrev;
    }

    public String getCsaNome() {
        return csaNome;
    }

    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }

    public String getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(String dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMotivoBloqueio() {
        return MotivoBloqueio;
    }

    public void setMotivoBloqueio(String motivoBloqueio) {
        MotivoBloqueio = motivoBloqueio;
    }

}
