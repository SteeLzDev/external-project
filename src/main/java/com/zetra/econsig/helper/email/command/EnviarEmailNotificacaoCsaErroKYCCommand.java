package com.zetra.econsig.helper.email.command;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailNotificacaoCsaErroKYCCommand</p>
 * <p>Description: Command para envio de email de notificação de erro no processo de KYC.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailNotificacaoCsaErroKYCCommand extends AbstractEnviarEmailCommand {

    private String csaCodigo;
    private String panNumber;
    private String status;

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void execute() throws ViewHelperException {
        try {
            ParametroDelegate pd = new ParametroDelegate();
            String csaEmail = pd.getParamCsa(csaCodigo, CodedValues.TPA_KYC_EMAIL_RECEBIMENTO_NOTIFICACOES, responsavel);

            if (TextHelper.isNull(csaEmail)) {
                throw new ViewHelperException("mensagem.erro.kyc.email.csa.nao.cadastrado", responsavel);
            }

            // 1. Busca o template do e-mail
            ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_CSA_ERRO_KYC, responsavel);

            // 2. Preenche dos dados disponíveis para uso no template
            String apiStatus;
            try {
                apiStatus = status + " - " + ApplicationResourcesHelper.getMessage("rotulo.kyc.status." + status, responsavel);
            } catch (Exception ex) {
                apiStatus = status;
            }
            OrgaoTransferObject orgTo = getOrgao(responsavel.getOrgCodigo(), responsavel);

            CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("kyc_pan_number", panNumber);
            dados.setAttribute("kyc_output_status", apiStatus);
            dados.setAtributos(orgTo.getAtributos());
            interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
            String titulo = interpolador.interpolateTitulo();
            String corpo = interpolador.interpolateTexto();

            // Envia os emails.
            MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_CSA_ERRO_KYC, csaEmail.replaceAll(";", ","), null, null, titulo, corpo, null, null, responsavel);

        } catch (Exception ex) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
