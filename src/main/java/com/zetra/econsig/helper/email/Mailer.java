package com.zetra.econsig.helper.email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.mail.MessagingException;

import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: Mailer</p>
 * <p>Description: Classe auxiliar para envio de mensagens em massa.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Zetrasoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Mailer {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Mailer.class);

    private final MailHelper mailHeper;

    public Mailer() {
        mailHeper = new MailHelper();
    }

    public void execute(String from, String subject, String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String linha;
            String params[];
            while ((linha = reader.readLine()) != null) {
                params = TextHelper.split(linha, "\\|");
                if (params.length == 3) {
                    LOG.debug("Enviando: " + params[0] + " " + params[1] + " " + params[2]);
                    try {
                        List<String> anexos = new ArrayList<String>();
                        anexos.add(params[2]);
                        mailHeper.send(null, from, params[0], null, null, subject, FileHelper.readAll(params[1]), anexos, null, null);
                    } catch (MessagingException e) {
                        LOG.error(e.getMessage(), e);
                    }
                } else {
                    LOG.error("Linha incorreta: " + linha);
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
