package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: PeriodoExportacaoHome</p>
 * <p>Description: Classe Home para a entidade PeriodoExportacao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PeriodoExportacaoHome extends AbstractEntityHome {

    public static PeriodoExportacao findByPrimaryKey(String orgCodigo, Date pexPeriodo) throws FindException {
        PeriodoExportacaoId id = new PeriodoExportacaoId(orgCodigo, pexPeriodo);
        PeriodoExportacao periodoExportacao = new PeriodoExportacao(id);
        return find(periodoExportacao, id);
    }

    public static Collection<PeriodoExportacao> findByOrgCodigo(String orgCodigo) throws FindException {
        String query = "FROM PeriodoExportacao pex WHERE pex.orgao.orgCodigo = :orgCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgCodigo", orgCodigo);

        return findByQuery(query, parameters);
    }

    public static PeriodoExportacao create(String orgCodigo, Short pexDiaCorte, Date pexDataIni, Date pexDataFim, Date pexPeriodo, Date pexPeriodoAnt, Date pexPeriodoPos, Short pexSequencia, Short pexNumPeriodo) throws CreateException {
        PeriodoExportacaoId id = new PeriodoExportacaoId(orgCodigo, pexPeriodo);
        PeriodoExportacao bean = new PeriodoExportacao(id);
        bean.setOrgCodigo(orgCodigo);
        bean.setPexDiaCorte(pexDiaCorte);
        bean.setPexDataIni(pexDataIni);
        bean.setPexDataFim(pexDataFim);
        bean.setPexPeriodo(pexPeriodo);
        bean.setPexPeriodoAnt(pexPeriodoAnt);
        bean.setPexPeriodoPos(pexPeriodoPos);
        bean.setPexSequencia(pexSequencia);
        bean.setPexNumPeriodo(pexNumPeriodo);
        create(bean);
        return bean;
    }
}
