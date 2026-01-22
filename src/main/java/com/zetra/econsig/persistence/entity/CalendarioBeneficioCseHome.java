package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;


/**
 * <p>Title: CalendarioBeneficioCseHome</p>
 * <p>Description: Entidade Home para a tabela CalendarioBeneficioCse</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CalendarioBeneficioCseHome extends AbstractEntityHome {

    public static CalendarioBeneficioCse findByPrimaryKey(String cseCodigo, Date cbcPeriodo) throws FindException {
        CalendarioBeneficioCseId id = new CalendarioBeneficioCseId();
        id.setCseCodigo(cseCodigo);
        id.setCbcPeriodo(cbcPeriodo);

        CalendarioBeneficioCse calendarioBeneficioCse = new CalendarioBeneficioCse();
        calendarioBeneficioCse.setId(id);

        return find(calendarioBeneficioCse, id);
    }

    public static CalendarioBeneficioCse create(String cseCodigo, Date cbcPeriodo, Short cbcDiaCorte, Date cbcDataIni, Date cbcDataFim) throws CreateException {
        Session session = SessionUtil.getSession();

        CalendarioBeneficioCse calendarioBeneficioCse = new CalendarioBeneficioCse();
        CalendarioBeneficioCseId id = new CalendarioBeneficioCseId();
        id.setCseCodigo(cseCodigo);
        id.setCbcPeriodo(cbcPeriodo);

        calendarioBeneficioCse.setId(id);
        calendarioBeneficioCse.setConsignante((Consignante)session.getReference(Consignante.class, cseCodigo));
        calendarioBeneficioCse.setCbcDiaCorte(cbcDiaCorte);
        calendarioBeneficioCse.setCbcDataIni(cbcDataIni);
        calendarioBeneficioCse.setCbcDataFim(cbcDataFim);

        create(calendarioBeneficioCse);

        return calendarioBeneficioCse;
    }
}
