package com.zetra.econsig.helper.geradoradenumero;

/**
 * <p>Title: GeradorAdeNumeroFactory</p>
 * <p>Description: Factory para criaçao de classe específica para cada Gestor.</p>
 * <p>Copyright: Copyright (c) 2004-2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GeradorAdeNumeroFactory {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GeradorAdeNumeroFactory.class);

    public static String getClassNameGerador(Class<? extends GeradorAdeNumero> classe) {
        return classe.getName();
    }

    public static GeradorAdeNumero getGerador(String className) {
        try {
            return (GeradorAdeNumero) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }
}
