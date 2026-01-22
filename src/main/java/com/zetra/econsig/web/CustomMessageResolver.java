package com.zetra.econsig.web;

import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: CustomMessageResolver</p>
 * <p>Description: Classe para ser usada pelo Spring para pegar as mensagens internacionalizadas.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas, Leonel Martins
 */
public class CustomMessageResolver implements org.springframework.context.MessageSource {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CustomMessageResolver.class);

    public CustomMessageResolver() {
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return null;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return getMessage(code, args, null, locale);
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        String[] stringArray = null;
        if ((args != null) && (args.length > 0)) {
            stringArray = Arrays.copyOf(args, args.length, String[].class);
        }
        String message;
        try {
            message = ApplicationResourcesHelper.getMessage(code, AcessoSistema.getAcessoUsuarioSistema(), stringArray);
            if (TextHelper.isNull(message) && !TextHelper.isNull(defaultMessage)) {
                return defaultMessage;
            }
        } catch (final Exception e) {
            LOG.warn("chave: " + code + " - default: " + defaultMessage);
            if (StringUtils.isNotBlank(defaultMessage)) {
                return defaultMessage;
            } else {
                throw e;
            }
        }
        return message;
    }
}
