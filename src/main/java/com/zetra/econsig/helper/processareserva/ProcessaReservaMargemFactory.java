package com.zetra.econsig.helper.processareserva;

import java.lang.reflect.InvocationTargetException;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ProcessaReservaMargemFactory</p>
 * <p>Description: Factory para criação das classes de pré/pós
 * processamentos das reservas de margem.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaReservaMargemFactory {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaReservaMargemFactory.class);

    public static ProcessaReservaMargem getProcessador(String className) throws ViewHelperException {
        if (className != null) {
            try {
                Object processador = Class.forName(className).getDeclaredConstructor().newInstance();
                return (ProcessaReservaMargem) processador;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ViewHelperException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            } catch (ClassNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ViewHelperException("mensagem.erro.interno.configurar.parametros.servico", (AcessoSistema) null, ex);
            }
        }
        return null;
    }
}