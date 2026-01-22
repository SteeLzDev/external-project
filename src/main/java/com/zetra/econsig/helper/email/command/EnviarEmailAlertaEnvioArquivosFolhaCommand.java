package com.zetra.econsig.helper.email.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jakarta.mail.MessagingException;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ModeloEmailEnum;

/**
 * <p>Title: EnviarEmailAlertaEnvioArquivosFolhaCommand</p>
 * <p>Description: Command para envio de email de alerta de envio de arquivos folha.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailAlertaEnvioArquivosFolhaCommand extends AbstractEnviarEmailCommand {

    private String destinatario;

    private Date dataPrevistaRetorno;

    private boolean alertaAntesDiaCorte;

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public Date getDataPrevistaRetorno() {
        return dataPrevistaRetorno;
    }

    public void setDataPrevistaRetorno(Date dataPrevistaRetorno) {
        this.dataPrevistaRetorno = dataPrevistaRetorno;
    }

    public boolean isAlertaAntesDiaCorte() {
        return alertaAntesDiaCorte;
    }

    public void setAlertaAntesDiaCorte(boolean alertaAntesDiaCorte) {
        this.alertaAntesDiaCorte = alertaAntesDiaCorte;
    }

    @Override
    public void execute() throws ViewHelperException {
        if (TextHelper.isNull(destinatario)) {
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        String[] emails = destinatario.replace(" ", "").split(",|;");
        List<String> destinatarios = Arrays.asList(emails);
        
        List<String> csaEmails = new ArrayList<>();
        // Verifica se envia email notificação CSA.
        boolean enviaEmailNotificacaoArqFolhaCsa = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_EMAIL_ALERTA_ARQUIVOS_FOLHA_CSA, responsavel);
        if(enviaEmailNotificacaoArqFolhaCsa) {
        	try {
            	List<Consignataria> consignatarias = new ConsignatariaDelegate().findConsignatariaComEmailCadastrado(responsavel);
            	for (Consignataria consignataria : consignatarias) {
    				csaEmails.add(consignataria.getCsaEmail());
    			}
    		} catch (ConsignatariaControllerException e) {
    			throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel, e);
    		}
        }

        // 1. Busca o template do e-mail
        ModeloEmailInterpolator interpolador = getModeloEmailInterpolator((alertaAntesDiaCorte ? ModeloEmailEnum.ENVIAR_EMAIL_ALERTA_ENVIO_ARQUIVOS_FOLHA_ANTES : ModeloEmailEnum.ENVIAR_EMAIL_ALERTA_ENVIO_ARQUIVOS_FOLHA_DEPOIS), responsavel);
        enviarEmail(destinatarios, interpolador);

        if (!csaEmails.isEmpty()) {
            ModeloEmailInterpolator interpoladorCsa = getModeloEmailInterpolator((alertaAntesDiaCorte ? ModeloEmailEnum.ENVIAR_EMAIL_ALERTA_ENVIO_ARQUIVOS_FOLHA_ANTES_CSA : ModeloEmailEnum.ENVIAR_EMAIL_ALERTA_ENVIO_ARQUIVOS_FOLHA_DEPOIS_CSA), responsavel);
            enviarEmail(csaEmails, interpoladorCsa);
        }

    }

    private void enviarEmail(List<String> destinatarios, ModeloEmailInterpolator interpolador) throws ViewHelperException {
        // 2. Preenche dos dados disponíveis para uso no template
        CustomTransferObject dados = new CustomTransferObject();
        dados.setAttribute("data_prevista_retorno", dataPrevistaRetorno);
        interpolador.setDados(dados);

        // 3. Interpola o template gerando os textos finais prontos para uso.
        String titulo = interpolador.interpolateTitulo();
        String corpo = interpolador.interpolateTexto();

        // Envia o email
        try {
            MailHelper mailHelper = new MailHelper();
            if (destinatarios.size() == 1) {
                mailHelper.send(destinatarios.get(0), null, null, titulo, corpo, null);
            } else {
                // Se há mais de um destinatário, envia para todos via cópia carbono.
                mailHelper.send(null, null, TextHelper.join(destinatarios, ","), titulo, corpo, null);
            }
        } catch (MessagingException e) {
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, e);
        }
    }
}
