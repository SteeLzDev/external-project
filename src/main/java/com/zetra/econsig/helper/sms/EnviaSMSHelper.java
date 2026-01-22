package com.zetra.econsig.helper.sms;

import java.util.Arrays;
import java.util.List;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: EnviaSMSHelper</p>
 * <p>Description: Helper Class para Operação de Envio de SMS</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author:rodrigo $
 * $Revision$
 * $Date$
 */
public class EnviaSMSHelper {

    /**
     * Envia SMS de Criação de senha de autorização
     *
     * @param tipo int
     * @param adeCodigo String
     * @param observacao String
     * @param responsavel AcessoSistema
     * @return String com mensagem de erro
     * @throws ViewHelperException
     */
    public static final void enviarSMSSenhaAutorizacao(String destinatario, String matricula, String novaSenha, boolean reiniciacao, boolean senhaAutorizacaoServidor, boolean senhaApp, AcessoSistema responsavel) throws ZetraException {
        String destinatarioFormatado = LocaleHelper.formataCelular(destinatario);

        if (TextHelper.isNull(destinatario) || TextHelper.isNull(destinatarioFormatado)) {
            throw new ZetraException("mensagem.erro.sms.enviar", responsavel);
        }

        String corpo = "";

        if (!senhaAutorizacaoServidor) {

            // Determina quais dados do servidor devem ir no SMS.
            String paramDadosServidorEmail = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DADOS_SERVIDOR_EMAIL_ALTERACAO_SENHA, responsavel);
            if (TextHelper.isNull(paramDadosServidorEmail)) {
                paramDadosServidorEmail = CodedValues.DADOS_SERVIDOR_EMAIL_MATRICULA + CodedValues.DADOS_SERVIDOR_EMAIL_SEPARADOR + CodedValues.DADOS_SERVIDOR_EMAIL_SENHA;
            }
            List<String> dadosServidorEmail = Arrays.asList(TextHelper.dropBlankSpace(paramDadosServidorEmail).toUpperCase().split(CodedValues.DADOS_SERVIDOR_EMAIL_SEPARADOR));

            if (reiniciacao) {
                corpo += ApplicationResourcesHelper.getMessage("mensagem.sms.senha.servidor.reiniciada", responsavel);
            } else if (senhaApp) {
                corpo += ApplicationResourcesHelper.getMessage("mensagem.sms.senha.app.servidor.alterada", responsavel);
            } else {
                corpo += ApplicationResourcesHelper.getMessage("mensagem.sms.senha.servidor.alterada", responsavel);
            }

            if (dadosServidorEmail.contains(CodedValues.DADOS_SERVIDOR_EMAIL_MATRICULA)) {
                corpo += ApplicationResourcesHelper.getMessage("rotulo.sms.matricula.servidor", responsavel, matricula);
            }

            if (dadosServidorEmail.contains(CodedValues.DADOS_SERVIDOR_EMAIL_SENHA)) {
                corpo += ApplicationResourcesHelper.getMessage("rotulo.sms.nova.senha", responsavel, novaSenha);
            }

        } else {

            corpo += ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.corpo.sms", responsavel) + "\n"
                    + ApplicationResourcesHelper.getMessage("rotulo.sms.senha.servidor.autorizacao", responsavel, novaSenha) + "\n";
        }

        enviarSMS(destinatarioFormatado, corpo, responsavel);
    }

    public static final void enviarSMSOTP(String destinatario, String otp, AcessoSistema responsavel) throws ZetraException {
        String destinatarioFormatado = !TextHelper.isNull(destinatario) ? LocaleHelper.formataCelular(destinatario) : null;

        if (TextHelper.isNull(destinatario) || TextHelper.isNull(destinatarioFormatado)) {
            throw new ZetraException("mensagem.erro.celular.ser.nao.cadastrado", responsavel);
        }

        String corpo = TextHelper.removeAccent(ApplicationResourcesHelper.getMessage("rotulo.otp.gerado", responsavel, otp)) + "\n";
        enviarSMS(destinatarioFormatado, corpo, responsavel);
    }

    public static final void enviarSMSNovoBoleto(String destinatario, String remetente, AcessoSistema responsavel) throws ZetraException {
        String destinatarioFormatado = LocaleHelper.formataCelular(destinatario);

        if (TextHelper.isNull(destinatario) || TextHelper.isNull(destinatarioFormatado)) {
            throw new ZetraException("mensagem.erro.sms.enviar", responsavel);
        }

        String corpo = TextHelper.removeAccent(ApplicationResourcesHelper.getMessage("mensagem.sms.novo.boleto.servidor", responsavel, remetente)) + "\n";
        enviarSMS(destinatarioFormatado, corpo, responsavel);
    }

    private static final void enviarSMS(String destinatarioFormatado, String mensagem, AcessoSistema responsavel) throws ViewHelperException {
        try {
            String accountSid = ParamSist.getInstance().getParam(CodedValues.TPC_SID_CONTA_SMS, responsavel).toString();
            String authToken = ParamSist.getInstance().getParam(CodedValues.TPC_TOKEN_AUTENTICACAO_SMS, responsavel).toString();
            String fromNumber = ParamSist.getInstance().getParam(CodedValues.TPC_NUMERO_REMETENTE_SMS, responsavel).toString();

            new SMSHelper(accountSid, authToken, fromNumber).send(destinatarioFormatado, mensagem);
        } catch (ZetraException e) {
            throw new ViewHelperException ("mensagem.erro.sms.enviar", responsavel, e);
        }
    }
}
