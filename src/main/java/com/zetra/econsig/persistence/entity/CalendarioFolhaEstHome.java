package com.zetra.econsig.persistence.entity;

import java.util.Date;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: CalendarioFolhaEstHome</p>
 * <p>Description: Classe Home para a entidade CalendarioFolhaEst</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CalendarioFolhaEstHome extends AbstractEntityHome {

    public static CalendarioFolhaEst findByPrimaryKey(String estCodigo, Date cfePeriodo) throws FindException {
        CalendarioFolhaEstId id = new CalendarioFolhaEstId(estCodigo, cfePeriodo);
        CalendarioFolhaEst calendario = new CalendarioFolhaEst(id);
        return find(calendario, id);
    }

    public static CalendarioFolhaEst create(String estCodigo, Date cfePeriodo, Short cfeDiaCorte, Date cfeDataIni, Date cfeDataFim, Date cfeDataFimAjustes, String cfeApenasReducoes, Date cfeDataPrevistaRetorno, Date cfeDataIniFiscal, Date cfeDataFimFiscal, Short cfeNumPeriodo) throws CreateException {
        CalendarioFolhaEstId id = new CalendarioFolhaEstId(estCodigo, cfePeriodo);
        CalendarioFolhaEst bean = new CalendarioFolhaEst(id);

        bean.setCfeDiaCorte(cfeDiaCorte);
        bean.setCfeDataIni(cfeDataIni);
        bean.setCfeDataFim(cfeDataFim);
        bean.setCfeDataFimAjustes(cfeDataFimAjustes);
        bean.setCfeApenasReducoes(cfeApenasReducoes);
        bean.setCfeDataPrevistaRetorno(cfeDataPrevistaRetorno);
        bean.setCfeDataIniFiscal(cfeDataIniFiscal);
        bean.setCfeDataFimFiscal(cfeDataFimFiscal);
        bean.setCfeNumPeriodo(cfeNumPeriodo);

        create(bean);
        return bean;
    }
}
