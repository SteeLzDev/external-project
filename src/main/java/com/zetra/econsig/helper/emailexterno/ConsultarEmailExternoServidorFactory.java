package com.zetra.econsig.helper.emailexterno;

import com.zetra.econsig.exception.ZetraException;



/**
 * <p>Title: EmailExternoServidor</p>
 * <p>Description: Helper class para busca de email de servidores em outras fontes de dados.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public final class ConsultarEmailExternoServidorFactory {
    public static String getClassNameExportador(Class<? extends ConsultarEmailExternoServidor> classe) {
        return classe.getName();
    }

    public static ConsultarEmailExternoServidor getClasseConsultarEmailExternoServidor(String className) throws ZetraException {
        try {
            final Object classeConsultarEmailExternoServidor = Class.forName(className).getDeclaredConstructor().newInstance();
            return (ConsultarEmailExternoServidor) classeConsultarEmailExternoServidor;
        } catch (final Exception ex) {
            throw new ZetraException(ex);
        }
    }
}
