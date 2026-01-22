package com.zetra.econsig.helper.email.command;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailNotificacaoCsaCredenciamentoConcluidoCommand </p>
 * <p>Description: Envia email informando que o credenciamento foi concluído
 * de consignante.</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: 2022-08-29 14:28:22 -0300 (Ter, 29 ago 2022) $
 */
public class EnviarEmailNotificacaoCseCredenciamentoConcluidoCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificacaoCseCredenciamentoConcluidoCommand.class);

    private String email;
    private String csaNomeAbrev;
    private String anexo;

    @Override
    public void execute() throws ViewHelperException {
		try {
			// 1. Busca o template do e-mail
			ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_CREDENCIAMENTO_CONCLUIDO_CSE, responsavel);

			if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

			if (TextHelper.isNull(email)) {
	            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
	        }

			CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("csa_nome", csaNomeAbrev);
			interpolador.setDados(dados);

			List<String> anexos = new ArrayList<>();
			anexos.add(anexo);
			// 3. Interpola o template gerando os textos finais prontos para uso.
			String titulo = interpolador.interpolateTitulo();
			String corpo = interpolador.interpolateTexto();

			// Envia os emails.
			MailHelper mailHelper = new MailHelper();
            if (ParamSist.paramEquals(CodedValues.TPC_INSERE_ANEXO_CREDENCIAMENTO_CONSIGNATARIA, CodedValues.TPC_SIM, responsavel)) {
                mailHelper.send(TipoNotificacaoEnum.EMAIL_CONSIGNANTE_CREDENCIAMENTO, email, null, null, titulo, corpo, anexos, null, responsavel);
            } else {
                mailHelper.send(TipoNotificacaoEnum.EMAIL_CONSIGNANTE_CREDENCIAMENTO, email, null, null, titulo, corpo, null, null, responsavel);
            }
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

    public String getCsaNomeAbrev() {
        return csaNomeAbrev;
    }

    public void setCsaNomeAbrev(String csaNomeAbrev) {
        this.csaNomeAbrev = csaNomeAbrev;
    }

    public String getAnexo() {
        return anexo;
    }

    public void setAnexo(String anexo) {
        this.anexo = anexo;
    }
}
