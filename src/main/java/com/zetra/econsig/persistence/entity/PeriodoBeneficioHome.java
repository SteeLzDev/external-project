package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;


/**
 * <p>Title: PeriodoBeneficioHome</p>
 * <p>Description: Entidade Home para a tabela PeriodoBeneficio</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PeriodoBeneficioHome extends AbstractEntityHome {

    public static PeriodoBeneficio findByPrimaryKey(String orgCodigo, Date pbePeriodo) throws FindException {
        PeriodoBeneficio periodoBeneficio = new PeriodoBeneficio();
        PeriodoBeneficioId id = new PeriodoBeneficioId();
        id.setOrgCodigo(orgCodigo);
        id.setPbePeriodo(pbePeriodo);
        periodoBeneficio.setId(id);

        return find(periodoBeneficio, id);
    }

    public static Collection<PeriodoBeneficio> findByOrgCodigo(String orgCodigo) throws FindException {
        String query = "FROM PeriodoBeneficio pbe WHERE pbe.orgao.orgCodigo = :orgCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("orgCodigo", orgCodigo);

        return findByQuery(query, parameters);
    }

    public static PeriodoBeneficio create (String orgCodigo, Date pbePeriodo, Date pbePeriodoAnt, Date pbePeriodoPos,
            Short pbeDiaCorte, Date pbeDataIni, Date pbeDataFim, Short pbeSequencia) throws CreateException {
        Session session = SessionUtil.getSession();

        PeriodoBeneficio periodoBeneficio = new PeriodoBeneficio();
        PeriodoBeneficioId id = new PeriodoBeneficioId();
        id.setOrgCodigo(orgCodigo);
        id.setPbePeriodo(pbePeriodo);
        periodoBeneficio.setId(id);
        periodoBeneficio.setOrgao((Orgao) session.getReference(Orgao.class, orgCodigo));
        periodoBeneficio.setPbePeriodoAnt(pbePeriodoAnt);
        periodoBeneficio.setPbePeriodoPos(pbePeriodoPos);
        periodoBeneficio.setPbeDiaCorte(pbeDiaCorte);
        periodoBeneficio.setPbeDataIni(pbeDataIni);
        periodoBeneficio.setPbeDataFim(pbeDataFim);
        periodoBeneficio.setPbeSequencia(pbeSequencia);

        create(periodoBeneficio);

        return periodoBeneficio;
    }

}
