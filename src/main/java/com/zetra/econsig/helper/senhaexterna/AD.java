package com.zetra.econsig.helper.senhaexterna;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

/**
 * <p>Title: AD</p>
 * <p>Description: Classe para auxiliar para autenticação em ActiveDomain.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AD {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AD.class);
    public static String validaSenha(String dominioAD, String servidorAD, String usuario, String senha) {
        String ldapHost = "ldap://" + servidorAD;
        String DN = usuario + "@" + dominioAD;

        try {
            Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            props.put(Context.SECURITY_AUTHENTICATION, "simple");
            props.put(Context.SECURITY_CREDENTIALS, senha);
            props.put(Context.SECURITY_PRINCIPAL, DN);
            props.put(Context.PROVIDER_URL, ldapHost);

            new InitialDirContext(props);
            return senha;
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            return null;
        }
    }
}
