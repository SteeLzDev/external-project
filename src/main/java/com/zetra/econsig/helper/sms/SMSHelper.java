package com.zetra.econsig.helper.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.texto.DateHelper;

import jakarta.mail.MessagingException;

/**
 * <p>Title: SMSHelper</p>
 * <p>Description: Classe auxiliar para envio de SMS.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Zetrasoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SMSHelper {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SMSHelper.class);

    public static String ACCOUNT_SID;
    public static String AUTH_TOKEN;
    public static String FROM_NUMBER;
    public static Twilio twilio;

    public SMSHelper(String account_sid, String auth_token, String from_number) {
        try {
            ACCOUNT_SID = account_sid;
            AUTH_TOKEN = auth_token;
            FROM_NUMBER = from_number;

            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia uma SMS
     * @param to String
     * @param conteudo String
     * @throws MessagingException
     */
    public final void send(String to, String conteudo) throws ZetraException {
        LOG.debug("Enviando SMS: " + DateHelper.getSystemDatetime());

        try {
            Message.creator(new PhoneNumber(to), // to
                    new PhoneNumber(FROM_NUMBER), // from
                    conteudo).create();
        } catch (Exception ex) {
            LOG.error("Erro ao enviar SMS: " + ex.getMessage());
            throw ZetraException.byMessage(ex.getMessage(), ex);
        }

        // Messages recebe erros no retorno
        LOG.debug("Fim SMS: " + DateHelper.getSystemDatetime());
    }
}
