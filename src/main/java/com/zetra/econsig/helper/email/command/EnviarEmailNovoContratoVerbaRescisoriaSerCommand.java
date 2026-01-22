package com.zetra.econsig.helper.email.command;

import java.math.BigDecimal;
import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;

/**
 * <p>Title: EnviarEmailNovoContratoVerbaRescisoriaSerCommand</p>
 * <p>Description: Command para envio de email de notificação de novo contrato de verba rescisória para servidor.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailNovoContratoVerbaRescisoriaSerCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNovoContratoVerbaRescisoriaSerCommand.class);

    private String email;
    private String serNome;
    private String rseMatricula;
    private String serCpf;
    private String adeNumero;
    private Date adeData;
    private BigDecimal adeVlr;

    @Override
    public void execute() throws ViewHelperException {
        try {
            // 1. Busca o template do e-mail
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOVO_CONTRATO_VERBA_RESCISORIA_SER, responsavel);

            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            if (TextHelper.isNull(email)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }

            final CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("ser_nome", serNome);
            dados.setAttribute("rse_matricula", rseMatricula);
            dados.setAttribute("ser_cpf", serCpf);
            dados.setAttribute("ade_numero", adeNumero);
            dados.setAttribute("ade_data", adeData);
            dados.setAttribute("ade_vlr", adeVlr);
            interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
            final String titulo = interpolador.interpolateTitulo();
            final String corpo = interpolador.interpolateTexto();

            // Envia os emails.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(null, email, null, null, titulo, corpo, null, null, responsavel);
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

	public String getSerNome() {
		return serNome;
	}

	public void setSerNome(String serNome) {
		this.serNome = serNome;
	}

	public String getRseMatricula() {
		return rseMatricula;
	}

	public void setRseMatricula(String rseMatricula) {
		this.rseMatricula = rseMatricula;
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

	public Date getAdeData() {
        return adeData;
    }

    public void setAdeData(Date adeData) {
        this.adeData = adeData;
    }

    public BigDecimal getAdeVlr() {
		return adeVlr;
	}

	public void setAdeVlr(BigDecimal adeVlr) {
		this.adeVlr = adeVlr;
	}
}