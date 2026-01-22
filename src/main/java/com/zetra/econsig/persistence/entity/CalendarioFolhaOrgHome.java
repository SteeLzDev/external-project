package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: CalendarioFolhaOrgHome</p>
 * <p>Description: Classe Home para a entidade CalendarioFolhaOrg</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CalendarioFolhaOrgHome extends AbstractEntityHome {

    public static CalendarioFolhaOrg findByPrimaryKey(String orgCodigo, Date cfoPeriodo) throws FindException {
        CalendarioFolhaOrgId id = new CalendarioFolhaOrgId(orgCodigo, cfoPeriodo);
        CalendarioFolhaOrg calendario = new CalendarioFolhaOrg(id);
        return find(calendario, id);
    }

    public static CalendarioFolhaOrg create(String orgCodigo, Date cfoPeriodo, Short cfoDiaCorte, Date cfoDataIni, Date cfoDataFim, Date cfoDataFimAjustes, String cfoApenasReducoes, Date cfoDataPrevistaRetorno, Date cfoDataIniFiscal, Date cfoDataFimFiscal, Short cfoNumPeriodo) throws CreateException {
        CalendarioFolhaOrgId id = new CalendarioFolhaOrgId(orgCodigo, cfoPeriodo);
        CalendarioFolhaOrg bean = new CalendarioFolhaOrg(id);

        bean.setCfoDiaCorte(cfoDiaCorte);
        bean.setCfoDataIni(cfoDataIni);
        bean.setCfoDataFim(cfoDataFim);
        bean.setCfoDataFimAjustes(cfoDataFimAjustes);
        bean.setCfoApenasReducoes(cfoApenasReducoes);
        bean.setCfoDataPrevistaRetorno(cfoDataPrevistaRetorno);
        bean.setCfoDataIniFiscal(cfoDataIniFiscal);
        bean.setCfoDataFimFiscal(cfoDataFimFiscal);
        bean.setCfoNumPeriodo(cfoNumPeriodo);

        create(bean);
        return bean;
    }

    public static List<CalendarioFolhaOrg> findByDateBetween(Date dataFiltro) throws FindException {
        String query = "FROM CalendarioFolhaOrg calOrg WHERE :dataFiltro BETWEEN calOrg.cfoDataIni AND calOrg.cfoDataFim ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dataFiltro", dataFiltro);

        return findByQuery(query, parameters);
    }
}
