package com.zetra.econsig.persistence.entity;

import java.util.Date;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;

/**
 * <p>Title: FaturamentoBeneficioControllerHome</p>
 * <p>Description: Home para faturamento de beneficios</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: tadeu.cruz $
 * $Revision: 25571 $
 * $Date: 2018-10-10 13:59:39 -0300 (Qua, 10 out 2018) $
 */

public class FaturamentoBeneficioHome extends AbstractEntityHome {

	public static FaturamentoBeneficio findByPrimaryKey(String fatCodigo) throws FindException {
		FaturamentoBeneficio fatBeneficio = new FaturamentoBeneficio();
		fatBeneficio.setFatCodigo(fatCodigo);

        return find(fatBeneficio, fatCodigo);
	}

	public static FaturamentoBeneficio create(String fatCodigo, String csaCodigo, Date fatPeriodo, Date fatData) throws CreateException, MissingPrimaryKeyException {
        FaturamentoBeneficio fatBeneficio = new FaturamentoBeneficio();

        fatBeneficio.setCsaCodigo(csaCodigo);
		fatBeneficio.setFatCodigo(fatCodigo);
		fatBeneficio.setFatData(fatData);
		fatBeneficio.setFatPeriodo(fatPeriodo);

        create(fatBeneficio);

        return fatBeneficio;
    }
}
