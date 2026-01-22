package com.zetra.econsig.persistence.entity;

import java.util.Date;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: CalendarioHome</p>
 * <p>Description: Classe Home para a entidade CalendarioBase</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CalendarioBaseHome extends AbstractEntityHome {

    public static CalendarioBase findByPrimaryKey(Date cabData) throws FindException {
        CalendarioBase calendarioBase = new CalendarioBase();
        calendarioBase.setCabData(cabData);
        return find(calendarioBase, cabData);
    }

    public static CalendarioBase create(Date cabData, String cabDescricao, String cabDiaUtil) throws CreateException {
        CalendarioBase bean = new CalendarioBase();

        bean.setCabData(cabData);
        bean.setCabDescricao(cabDescricao);
        bean.setCabDiaUtil(cabDiaUtil);

        create(bean);
        return bean;
    }
}
