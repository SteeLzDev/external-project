package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: MemoriaCalculoSubsidio</p>
 * <p>Description: Classe Home para a entidade de memória de cálculo de subsídio</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MemoriaCalculoSubsidioHome extends AbstractEntityHome {

    public static MemoriaCalculoSubsidio findByPrimaryKey(String mcsCodigo) throws FindException {
        MemoriaCalculoSubsidio entity = new MemoriaCalculoSubsidio();
        entity.setMcsCodigo(mcsCodigo);
        return find(entity, mcsCodigo);
    }

    public static MemoriaCalculoSubsidio create(String cbeCodigo, Date mcsPeriodo, BigDecimal mcsValorBeneficio, BigDecimal mcsValorSubsidio, String mcsObs) throws CreateException {
        Session session = SessionUtil.getSession();
        MemoriaCalculoSubsidio entity = new MemoriaCalculoSubsidio();

        try {
            entity.setMcsCodigo(DBHelper.getNextId());
            entity.setMcsPeriodo(mcsPeriodo);
            entity.setMcsData(DateHelper.getSystemDatetime());
            entity.setMcsValorBeneficio(mcsValorBeneficio);
            entity.setMcsValorSubsidio(mcsValorSubsidio);
            entity.setMcsObs(mcsObs);
            entity.setContratoBeneficio((ContratoBeneficio) session.getReference(ContratoBeneficio.class, cbeCodigo));

            create(entity);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return entity;
    }
}
