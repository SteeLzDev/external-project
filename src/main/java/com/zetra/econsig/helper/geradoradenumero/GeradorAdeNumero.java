package com.zetra.econsig.helper.geradoradenumero;

import java.io.Serializable;
import java.sql.Date;

/**
 * <p>Title: GeradorAdeNumero</p>
 * <p>Description: Interface de definição das classes de gerador de ade_numero.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel
 * $Author$
 * $Revision$
 * $Date$
 */
public interface GeradorAdeNumero extends Serializable {
    public Long getNext(String vcoCodigo, Date anoMesIni);
}
