package com.zetra.econsig.helper.email;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Properties;

import com.zetra.econsig.config.SysConfig;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.NotificacaoEmailControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoNotificacaoEnum;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/**
 * <p>Title: MailHelper</p>
 * <p>Description: Classe auxiliar para envio de mensagens.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Zetrasoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MailHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MailHelper.class);

    private static final String PROFILE_TEST = "test";
    private static final String MAIL_PROPERTIES_FILE_TEST = "mail-test.properties";
    private static final String MAIL_PROPERTIES_FILE = "mail.properties";
    private static final String SMTP_HOST_PROPERTY = "smtp.host";
    private static final String SMTP_PORT_PROPERTY = "smtp.port";
    private static final String SMTP_USER_PROPERTY = "smtp.user";
    private static final String SMTP_PASS_PROPERTY = "smtp.pass";
    private static final String SMTP_SSL_PROPERTY = "smtp.ssl";
    private static final String SMTP_CONNECTION_TIMEOUT_PROPERTY = "smtp.connectiontimeout";
    private static final String SMTP_TIMEOUT_PROPERTY = "smtp.timeout";
    private static final String MAILER_PROPERTY = "mailer.name";
    private static final String FROM_DEFAULT_PROPERTY = "email.from.default";
    private static final String REPLYTO_DEFAULT_PROPERTY = "email.replyto.default";

    private boolean skip;
    private String mailer;
    private String mailhost;
    private int port;
    private SimpleAuth auth;
    private String fromDefault;
    private String replyToDefault;
    private int connectionTimeout;
    private int timeout;
    private boolean ssl;

    /**
     * Constrói o MailHelper com os parâmetros padrões que estão no arquivo
     * de propriedades.
     */
    public MailHelper() {
        try {
            final Properties env = new Properties();

            // se o perfil ativo for o test ele ira carregar o mail-test.properties
            // caso contrario ele ira utilizar o mail.properties
            if (PROFILE_TEST.equals(SysConfig.get().getActiveProfile())) {
                env.load(this.getClass().getClassLoader().getResourceAsStream(MAIL_PROPERTIES_FILE_TEST));
                skip = true;
            } else {
                env.load(this.getClass().getClassLoader().getResourceAsStream(MAIL_PROPERTIES_FILE));
            }

            final String mailer = env.getProperty(MAILER_PROPERTY);
            final String host = env.getProperty(SMTP_HOST_PROPERTY);
            final String port = env.getProperty(SMTP_PORT_PROPERTY);
            final String user = env.getProperty(SMTP_USER_PROPERTY);
            final String pass = env.getProperty(SMTP_PASS_PROPERTY);
            final String connectionTimeout = env.getProperty(SMTP_CONNECTION_TIMEOUT_PROPERTY);
            final String timeout = env.getProperty(SMTP_TIMEOUT_PROPERTY);
            fromDefault = env.getProperty(FROM_DEFAULT_PROPERTY);
            replyToDefault = env.getProperty(REPLYTO_DEFAULT_PROPERTY);
            ssl = Boolean.valueOf(Optional.ofNullable(env.getProperty(SMTP_SSL_PROPERTY)).orElse("true"));

            setProperties(mailer, host, port, user, pass, connectionTimeout, timeout);
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Configura o MailHelper
     * @param mailer String
     * @param mailhost String
     * @param port String
     * @param mhUser String
     * @param mhPassword String
     */
    private void setProperties(String mailer, String mailhost, String port, String mhUser, String mhPassword, String connectionTimeout, String timeout) {
        this.mailer = mailer;
        this.mailhost = mailhost;
        this.port = (!TextHelper.isNull(port)) ? Integer.parseInt(port) : 25;
        this.connectionTimeout = (!TextHelper.isNull(connectionTimeout)) ? Integer.parseInt(connectionTimeout) : 15000;
        this.timeout = (!TextHelper.isNull(timeout)) ? Integer.parseInt(timeout) : 15000;

        auth = null;
        if (!TextHelper.isNull(mhUser) && !TextHelper.isNull(mhPassword)) {
            // Autenticação para o mail server
            auth = new SimpleAuth(mhUser, mhPassword);
        }
    }

    /**
     * Envia uma mensagem com Remetente padrão
     * @param to String
     * @param cc String
     * @param bcc String
     * @param subject String
     * @param conteudo String
     * @param anexos List<String>
     * @throws MessagingException
     */
    public final void send(String to, String cc, String bcc, String subject, String conteudo, List<String> anexos) throws MessagingException {
        send(null, fromDefault, to, cc, bcc, subject, conteudo, anexos, null, null);
    }

    /**
     * Envia uma mensagem com Remetente padrão
     * @param from String
     * @param to String
     * @param cc String
     * @param bcc String
     * @param subject String
     * @param conteudo String
     * @param anexos List<String>
     * @param customHeaders Map
     * @throws MessagingException
     */
    public final void send(String to, String cc, String bcc, String subject, String conteudo, List<String> anexos, Map<String, String> customHeaders) throws MessagingException {
        send(null, fromDefault, to, cc, bcc, subject, conteudo, anexos, customHeaders, null);
    }

    /**
     * Envia uma mensagem com Remetente padrão
     * @param tipoNotificacao Caso seja informado o tipo de notificação, verifica se o envio de email será automático ou agendado
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     * @param conteudo
     * @param anexos
     * @param customHeaders
     * @param responsavel
     * @throws MessagingException
     */
    public final void send(TipoNotificacaoEnum tipoNotificacao, String to, String cc, String bcc, String subject, String conteudo, List<String> anexos, Map<String, String> customHeaders, AcessoSistema responsavel) throws MessagingException {
        send(tipoNotificacao, fromDefault, to, cc, bcc, subject, conteudo, anexos, customHeaders, responsavel);
    }

    /**
     * Envia uma mensagem com os parametros informados
     * @param tipoNotificacao TipoNotificacaoEnum
     * @param from String
     * @param to String
     * @param cc String
     * @param bcc String
     * @param subject String
     * @param conteudo String
     * @param anexos List<String>
     * @param customHeaders Map
     * @param responsavel AcessoSistema
     * @throws MessagingException
     */
    public final void send(TipoNotificacaoEnum tipoNotificacao, String from, String to, String cc, String bcc, String subject, String conteudo, List<String> anexos, Map<String, String> customHeaders, AcessoSistema responsavel) throws MessagingException {
        if (skip) {
            return;
        }

        /**
         *  Caso seja informado o tipo de notificação, gera histórico do e-mail que será enviado
         *  e verifica se o email será de envio imediato ou agendado.
         */
        if (!TextHelper.isNull(tipoNotificacao)) {
            final TransferObject email = new CustomTransferObject();
            email.setAttribute(Columns.NEM_DESTINATARIO, to);
            email.setAttribute(Columns.NEM_TITULO, subject);
            email.setAttribute(Columns.NEM_TEXTO, conteudo);

            try {
                if (!ControleEnvioEmail.getInstance().enviar(tipoNotificacao, email, responsavel)) {
                    return;
                }
            } catch (final NotificacaoEmailControllerException e) {
                LOG.error(e.getMessage(), e);
                throw new MessagingException(e.getMessage(), e);
            }
        }

        final Properties props = System.getProperties();

        // Socket connection timeout value in milliseconds. Default is infinite timeout.
        props.put("mail.smtp.connectiontimeout", String.valueOf(connectionTimeout));
        // Socket I/O timeout value in milliseconds. Default is infinite timeout.
        props.put("mail.smtp.timeout", String.valueOf(timeout));

        if (!TextHelper.isNull(mailhost)) {
            props.put("mail.smtp.host", mailhost);
            props.put("mail.smtp.port", String.valueOf(port));
            if (port != 25 && ssl) {
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.socketFactory.port", String.valueOf(port));
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.fallback", "false");
            }
        }

        // Autenticação para o mail server
        if (auth != null) {
            props.put("mail.smtp.auth", "true");
        }

        // Get a Session object
        final Session session = Session.getInstance(props, auth);
        session.setDebug(false);

        // construct the message
        final MimeMessage msg = new MimeMessage(session);

        // Evita que o e-mail seja respondido
        msg.setReplyTo(InternetAddress.parse(verifyEmailList(replyToDefault), false));

        if (from != null) {
            try {
                final InternetAddress mailFrom = new InternetAddress(from);
                msg.setFrom(new InternetAddress(mailFrom.getAddress(), mailFrom.getPersonal(), "ISO-8859-1"));
            } catch (final UnsupportedEncodingException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            msg.setFrom();
        }
        if (to != null) {
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(verifyEmailList(to), false));
        }
        if (cc != null) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(verifyEmailList(cc), false));
        }
        if (bcc != null) {
            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(verifyEmailList(bcc), false));
        }

        msg.setHeader("X-Mailer", mailer);
        msg.setSentDate(new Date());
        msg.setSubject(subject, "ISO-8859-1");

        MimeBodyPart mpb = new MimeBodyPart();
        mpb.setContent(conteudo, "text/html; charset=ISO-8859-1");

        final Multipart mult = new MimeMultipart();
        mult.addBodyPart(mpb);

        if ((anexos != null) && (anexos.size() > 0)) {
            for (final String anexo : anexos) {


                //
                // Part two is attachment
                //

                // Create second body part
                mpb = new MimeBodyPart();

                // Get the attachment
                final DataSource source = new FileDataSource(anexo);

                // Set the data handler to the attachment
                mpb.setDataHandler(new DataHandler(source));

                // Set the filename
                mpb.setFileName(new File(anexo).getName());

                // Add part two
                mult.addBodyPart(mpb);
            }
        }

        // Se há cabeçalhos a serem incluídos na mensagem
        if (customHeaders != null) {
            for (final Entry<String, String> entry : customHeaders.entrySet()) {
                msg.setHeader(entry.getKey(), entry.getValue());
            }
        }

        msg.setContent(mult);

        LOG.debug("Enviando E-mail: " + DateHelper.getSystemDatetime());

        // send the thing off
        Transport.send(msg);

        LOG.debug("Fim E-mail: " + DateHelper.getSystemDatetime());
    }

    public static String verifyEmailList(String email) {
        if (!TextHelper.isNull(email)) {
            // Replace control character, whitespace character or semicolon with comma
            email = email.replaceAll("[\\p{Cntrl};\\p{Space}]+", ",");
        }
        return email;
    }

    static class SimpleAuth extends Authenticator {
        public String username = null;
        public String password = null;

        public SimpleAuth(String user, String pwd) {
            username = user;
            password = pwd;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
}
