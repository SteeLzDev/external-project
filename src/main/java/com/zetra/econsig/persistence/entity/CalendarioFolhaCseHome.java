package com.zetra.econsig.persistence.entity;

import java.util.Date;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: CalendarioFolhaCseHome</p>
 * <p>Description: Classe Home para a entidade CalendarioFolhaCse</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CalendarioFolhaCseHome extends AbstractEntityHome {

    public static CalendarioFolhaCse findByPrimaryKey(String cseCodigo, Date cfcPeriodo) throws FindException {
        CalendarioFolhaCseId id = new CalendarioFolhaCseId(cseCodigo, cfcPeriodo);
        CalendarioFolhaCse calendario = new CalendarioFolhaCse(id);
        return find(calendario, id);
    }

    public static CalendarioFolhaCse create(String cseCodigo, Date cfcPeriodo, Short cfcDiaCorte, Date cfcDataIni, Date cfcDataFim, Date cfcDataFimAjustes, String cfcApenasReducoes, Date cfcDataPrevistaRetorno, Date cfcDataIniFiscal, Date cfcDataFimFiscal, Short cfcNumPeriodo) throws CreateException {
        CalendarioFolhaCseId id = new CalendarioFolhaCseId(cseCodigo, cfcPeriodo);
        CalendarioFolhaCse bean = new CalendarioFolhaCse(id);

        bean.setCfcDiaCorte(cfcDiaCorte);
        bean.setCfcDataIni(cfcDataIni);
        bean.setCfcDataFim(cfcDataFim);
        bean.setCfcDataFimAjustes(cfcDataFimAjustes);
        bean.setCfcApenasReducoes(cfcApenasReducoes);
        bean.setCfcDataPrevistaRetorno(cfcDataPrevistaRetorno);
        bean.setCfcDataIniFiscal(cfcDataIniFiscal);
        bean.setCfcDataFimFiscal(cfcDataFimFiscal);
        bean.setCfcNumPeriodo(cfcNumPeriodo);

        create(bean);
        return bean;
    }
}
