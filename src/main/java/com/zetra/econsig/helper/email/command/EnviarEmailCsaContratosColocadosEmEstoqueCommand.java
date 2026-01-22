package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailCsaContratosColocadosEmEstoqueCommand</p>
 * <p>Description: Command para envio de email de notificação para a consignataria que o contrato foi colocado em estoque.
 * de consignante.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: 2022-03-28 14:28:22 -0300 (Seg, 28 mar 2022) $
 */
public class EnviarEmailCsaContratosColocadosEmEstoqueCommand extends AbstractEnviarEmailCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailCsaContratosColocadosEmEstoqueCommand.class);

    private String matricula;
    private String csaEmail;
    private String serNome;
    private String adeVlr;
    private String adePrazo;
    private String adeNumero;
    private String csaNomeAbrev;
    private String serCpf;

    @Override
    public void execute() throws ViewHelperException {
		try {

			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(
					ModeloEmailEnum.ENVIAR_EMAIL_CSA_CONTRATOS_COLOCADOS_EM_ESTOQUE, responsavel);

			if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

			CustomTransferObject dados = new CustomTransferObject();
			dados.setAttribute("matricula_servidor", matricula);
			dados.setAttribute("email_csa", csaEmail);
			dados.setAttribute("nome_servidor", serNome);
			dados.setAttribute("valor_contrato", adeVlr);
			dados.setAttribute("prazo_contrato", adePrazo);
			dados.setAttribute("numero_contrato", adeNumero);
			dados.setAttribute("nome_csa_abrev", csaNomeAbrev);
			dados.setAttribute("cpf_servidor", serCpf);

			interpolador.setDados(dados);

			// 3. Interpola o template gerando os textos finais prontos para uso.
			String titulo = interpolador.interpolateTitulo();
			String corpo = interpolador.interpolateTexto();

			// Envia os emails.
			MailHelper mailHelper = new MailHelper();
			mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_CSA_CONTRATO_COLOCADO_EM_ESTOQUE, csaEmail, null, null, titulo,
			        corpo, null, null, responsavel);

		} catch (Exception ex) {
		    LOG.error(ex.getMessage(), ex);
			throw new ViewHelperException("mensagem.erro.email.nao.enviado.csa.contrato.colocado.em.estoque.notificacao", responsavel, ex);
		}

    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCsaEmail() {
        return csaEmail;
    }

    public void setCsaEmail(String csaEmail) {
        this.csaEmail = csaEmail;
    }

    public String getSerNome() {
        return serNome;
    }

    public void setSerNome(String serNome) {
        this.serNome = serNome;
    }

    public String getAdeVlr() {
        return adeVlr;
    }

    public void setAdeVlr(String adeVlr) {
        this.adeVlr = adeVlr;
    }

    public String getAdePrazo() {
        return adePrazo;
    }

    public void setAdePrazo(String adePrazo) {
        this.adePrazo = adePrazo;
    }

    public String getAdeNumero() {
        return adeNumero;
    }

    public void setAdeNumero(String adeNumero) {
        this.adeNumero = adeNumero;
    }

    public String getCsaNomeAbrev() {
        return csaNomeAbrev;
    }

    public void setCsaNomeAbrev(String csaNomeAbrev) {
        this.csaNomeAbrev = csaNomeAbrev;
    }

    public String getSerCpf() {
        return serCpf;
    }

    public void setSerCpf(String serCpf) {
        this.serCpf = serCpf;
    }
}
