package com.zetra.econsig.helper.email.command;

import java.util.Arrays;
import java.util.List;

import jakarta.mail.MessagingException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EmailAlteracaoOperacaoSensivel;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailAlteracaoOperacaoSensivelCommand</p>
 * <p>Description: Command para envio de email de alteração (confirmação ou reprovação) de operação sensível.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailAlteracaoOperacaoSensivelCommand extends AbstractEnviarEmailCommand {

    private EmailAlteracaoOperacaoSensivel email;
    private boolean confirmacaoOperacaoSensivel = false;

    public void setEmail(EmailAlteracaoOperacaoSensivel email) {
        this.email = email;
    }

    public void setConfirmacaoOperacaoSensivel(boolean confirmacaoOperacaoSensivel) {
        this.confirmacaoOperacaoSensivel = confirmacaoOperacaoSensivel;
    }

    @Override
    public void execute() throws ViewHelperException {
        /**
            1) Caso o usuário executor tenha e-mail cadastrado no sistema, enviar uma mensagem informado que sua operação foi confirmada/descartada.
            2) A rotina de e-mail deve seguir o novo padrão onde o título e corpo da mensagem são gravados na tabela tb_modelo_email.
            3) Deve estar disponível para substituição no corpo e título da mensagem os seguintes campos:
            3.1) Nome e login do usuário executor;
            3.2) Nome e login do usuário autenticador;
            3.3) Descrição da função realizada;
            3.4) Data de solicitação da operação;
            3.5) Data de confirmação/descarte da operação;
            3.6) Motivo informado ao descartar a operação;
            OK 3.7) URL do sistema (parâmetro de sistema 256);
            OK 3.8) E-mail do suporte (parâmetro de sistema 207);
            OK 3.9) Telefone do suporte (parâmetro de sistema 258).
            4) Fazer dois modelos de e-mail, um para operação confirmada e outro para operação descartada.
            5) A rotina de envio deve ser uma classe Command que estende AbstractEnviarEmailCommand, exemplo "EnviarEmailAlertaRetornoServidorCommand.java"
         */

        Object urlSistema = ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
        Object emailSuporte = ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel);
        Object telefoneSuporte = ParamSist.getInstance().getParam(CodedValues.TPC_TELEFONE_SUPORTE_ZETRASOFT, responsavel);

        String destinatario = email.getUsuEmailExecutor();
        if (TextHelper.isNull(destinatario)) {
            return;
        }

        if (confirmacaoOperacaoSensivel) {
            // Envia email de confirmação de operação sensível
            enviarEmailAlteracaoOperacaoSensivel(urlSistema, emailSuporte, telefoneSuporte, email, destinatario, ModeloEmailEnum.ENVIAR_EMAIL_CONFIRMACAO_OPERACAO_SENSIVEL, TipoNotificacaoEnum.EMAIL_CONFIRMACAO_OPERACAO_SENSIVEL);
        } else {
            // Envia email de reprovação de operação sensível
            enviarEmailAlteracaoOperacaoSensivel(urlSistema, emailSuporte, telefoneSuporte, email, destinatario, ModeloEmailEnum.ENVIAR_EMAIL_REPROVACAO_OPERACAO_SENSIVEL, TipoNotificacaoEnum.EMAIL_REPROVACAO_OPERACAO_SENSIVEL);
        }
    }

    private void enviarEmailAlteracaoOperacaoSensivel(Object urlSistema, Object emailSuporte, Object telefoneSuporte, EmailAlteracaoOperacaoSensivel email, String destinatario, ModeloEmailEnum modeloEmail, TipoNotificacaoEnum tipoNotificacao) throws ViewHelperException {
        // Envia o email
        String[] emails = destinatario.replace(" ", "").split(",|;");
        List<String> destinatarios = Arrays.asList(emails);

        // 1. Busca o template do e-mail
        ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(modeloEmail, responsavel);

        // 2. Preenche dos dados disponíveis para uso no template
        CustomTransferObject dados = new CustomTransferObject();

        dados.setAttribute("usu_nome_executor", email.getUsuNomeExecutor());
        dados.setAttribute("usu_login_executor", email.getUsuLoginExecutor());
        dados.setAttribute("usu_nome_autenticador", email.getUsuNomeAutenticador());
        dados.setAttribute("usu_login_autenticador", email.getUsuLoginAutenticador());
        dados.setAttribute("operacao", email.getOperacao());
        dados.setAttribute("motivo_operacao", email.getMotivoOperacao());
        dados.setAttribute("data_operacao", email.getDataOperacao());
        dados.setAttribute("data_alteracao_operacao", email.getDataAlteracaoOperacao());

        // Adiciona os parâmetros abaixo:
        // URL do sistema (parâmetro de sistema 256);
        dados.setAttribute("url_sistema", urlSistema);
        // E-mail do suporte (parâmetro de sistema 207);
        dados.setAttribute("email_suporte", emailSuporte);
        // Telefone do suporte (parâmetro de sistema 258).
        dados.setAttribute("telefone_suporte", telefoneSuporte);
        interpolador.setDados(dados);

        // 3. Interpola o template gerando os textos finais prontos para uso.
        String titulo = interpolador.interpolateTitulo();
        String corpo = interpolador.interpolateTexto();

        try {
            MailHelper mailHelper = new MailHelper();
            if (destinatarios.size() == 1) {
                mailHelper.send(tipoNotificacao, destinatarios.get(0), null, null, titulo, corpo, null, null, responsavel);
            } else {
                // Se há mais de um destinatário, envia para todos via cópia carbono.
                mailHelper.send(tipoNotificacao, TextHelper.join(destinatarios, ","), null, null, titulo, corpo, null, null, responsavel);
            }
        } catch (MessagingException e) {
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, e);
        }
    }

}
