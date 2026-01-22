package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: CalendarioHome</p>
 * <p>Description: Classe Home para a entidade Calendario</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CalendarioHome extends AbstractEntityHome {

    public static Calendario findByPrimaryKey(Date calData) throws FindException {
        Calendario calendario = new Calendario();
        calendario.setCalData(calData);
        return find(calendario, calData);
    }

    public static List<Calendario> lstDatesFrom(Date calData, boolean diasUteis, Integer numDatasRecuperar) throws FindException {
        String query = "FROM Calendario cal WHERE cal.calData > :calData" + (diasUteis ? " AND cal.calDiaUtil = 'S'" : "");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("calData", calData);

        if (numDatasRecuperar == null) {
            return findByQuery(query, parameters);
        } else {
            return findByQuery(query, parameters, numDatasRecuperar, 0);
        }
    }

    public static Calendario create(Date calData, String calDescricao, String calDiaUtil) throws CreateException {
        Calendario bean = new Calendario();

        bean.setCalData(calData);
        bean.setCalDescricao(calDescricao);
        bean.setCalDiaUtil(calDiaUtil);

        create(bean);
        return bean;
    }
}
