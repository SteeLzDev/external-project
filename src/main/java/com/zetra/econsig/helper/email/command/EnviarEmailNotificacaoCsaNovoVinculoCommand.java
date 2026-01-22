package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
/**
 * <p>Title: EnviarEmailNotificacaoCsaNovoVinculoCommand </p>
 * <p>Description: Envia email informando todos os novos vínculos criados.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: $
 */
public class EnviarEmailNotificacaoCsaNovoVinculoCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificacaoCsaNovoVinculoCommand.class);

    private String csaNome;
    private String csaNomeAbrev;
    private String csaEmail;
    private String csaIdentificador;
    private String vinculos;
    private String situacaoVinculo;

    @Override
    public void execute() throws ViewHelperException {
		try {
			// 1. Busca o template do e-mail
			final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_NOVO_VINCULO, responsavel);

			if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

			if (TextHelper.isNull(csaEmail)) {
	            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
	        }

			final CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("csa_nome", csaNome);
            dados.setAttribute("csa_nome_abrev", csaNomeAbrev);
            dados.setAttribute("csa_identificador", csaIdentificador);
            dados.setAttribute("vinculos", vinculos);
            dados.setAttribute("situacao_vinculo", situacaoVinculo);
			interpolador.setDados(dados);

			// 3. Interpola o template gerando os textos finais prontos para uso.
			final String titulo = interpolador.interpolateTitulo();
			final String corpo = interpolador.interpolateTexto();

			// Envia os emails.
			final MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_CONSIGNATARIA_NOVO_VINCULO, csaEmail, null, null, titulo, corpo, null, null, responsavel);
		} catch (final Exception ex) {
		    LOG.error(ex.getMessage(), ex);
			throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
		}

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

    public String getCsaEmail() {
        return csaEmail;
    }

    public void setCsaEmail(String csaEmail) {
        this.csaEmail = csaEmail;
    }

    public String getCsaIdentificador() {
        return csaIdentificador;
    }

    public void setCsaIdentificador(String csaIdentificador) {
        this.csaIdentificador = csaIdentificador;
    }

    public String getVinculos() {
        return vinculos;
    }

    public void setVinculos(String vinculos) {
        this.vinculos = vinculos;
    }

    public String getSituacaoVinculo() {
        return situacaoVinculo;
    }

    public void setSituacaoVinculo(String situacaoVinculo) {
        this.situacaoVinculo = situacaoVinculo;
    }
}
