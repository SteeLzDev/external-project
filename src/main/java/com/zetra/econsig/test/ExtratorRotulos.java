package com.zetra.econsig.test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zetra.econsig.helper.arquivo.FileHelper;

/**
 * <p>Title: ExtratorRotulos</p>
 * <p>Description: Classe para extrair todos os rotulos que estão no arquivo passado no argumento</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExtratorRotulos {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExtratorRotulos.class);

    public static void extrairRotulos(String path) {
        try {
            String text = FileHelper.readAll(path);
            Map<String, String> matchs = new HashMap<>();

            Pattern p = Pattern.compile("\"(rotulo|mensagem){1}\\.[^\"]*\"");

            Matcher m = p.matcher(text);

            while(m.find()) {
                String possivelMatch = m.group();
                if(!matchs.containsKey(possivelMatch)) {
                    matchs.put(possivelMatch, possivelMatch);
                    System.out.println(possivelMatch);
                }
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

    }

    public static void main(String[] args) {
        String path = "/home/junio/workspace_mars/eConsig/src/main/webapp/margem/lst_consignacao.jsp";

        if (args != null && args.length > 0) {
            path = args[0];
        }

        extrairRotulos(path);
    }
}
