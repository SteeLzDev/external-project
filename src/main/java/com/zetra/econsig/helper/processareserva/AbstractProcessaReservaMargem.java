package com.zetra.econsig.helper.processareserva;

import jakarta.servlet.http.HttpServletRequest;

import com.zetra.econsig.exception.ViewHelperException;

/**
 * <p>Title: AbstractProcessaReservaMargem</p>
 * <p>Description: Classe Abstrata que implementa a interface para
 * processamento de reserva de margem. As classes específicas devem
 * estender esta classe, pois assim podem implementar apenas os
 * métodos necessários.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractProcessaReservaMargem implements ProcessaReservaMargem {

    /**
     * @see ProcessaReservaMargem#incluirPasso1(HttpServletRequest)
     */
    public String incluirPasso1(HttpServletRequest request) {
        return "";
    }

    /**
     * @see ProcessaReservaMargem#validarPasso1(HttpServletRequest)
     */
    public boolean validarPasso1(HttpServletRequest request) throws ViewHelperException {
        return true;
    }

    /**
     * @see ProcessaReservaMargem#incluirPasso2(HttpServletRequest)
     */
    public String incluirPasso2(HttpServletRequest request) {
        return "";
    }

    /**
     * @see ProcessaReservaMargem#validarPasso2(HttpServletRequest)
     */
    public boolean validarPasso2(HttpServletRequest request) throws ViewHelperException {
        return true;
    }

    /**
     * @see ProcessaReservaMargem#finalizar(HttpServletRequest)
     */
    public void finalizar(HttpServletRequest request, String adeCodigo) throws ViewHelperException {
    }
}
