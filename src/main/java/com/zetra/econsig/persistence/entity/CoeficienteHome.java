package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: CoeficienteHome</p>
 * <p>Description: Classe Home para a entidade Coeficiente</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CoeficienteHome extends AbstractEntityHome {

    public static Coeficiente findByPrimaryKey(String cftCodigo) throws FindException {
        Coeficiente coeficiente = new Coeficiente();
        coeficiente.setCftCodigo(cftCodigo);
        return find(coeficiente, cftCodigo);
    }

    public static Coeficiente create(String przCsaCodigo, Short cftDia, BigDecimal cftVlr, java.util.Date cftDataIniVig, java.util.Date cftDataFimVig) throws CreateException {
        return create(null, przCsaCodigo, cftDia, cftVlr, cftDataIniVig, cftDataFimVig, null, null, null);
    }

    public static Coeficiente create(String cftCodigo, String przCsaCodigo, Short cftDia, BigDecimal cftVlr, java.util.Date cftDataIniVig, java.util.Date cftDataFimVig, java.util.Date cftDataCadastro, BigDecimal cftVlrRef, BigDecimal cftVlrMinimo) throws CreateException {

        Session session = SessionUtil.getSession();
        Coeficiente bean = new Coeficiente();

        try {
            bean.setCftCodigo(cftCodigo == null ? DBHelper.getNextId() : cftCodigo);
            bean.setPrazoConsignataria(session.getReference(PrazoConsignataria.class, przCsaCodigo));
            bean.setCftDia(cftDia);
            bean.setCftVlr(cftVlr);
            bean.setCftDataCadastro(cftDataCadastro == null ? DateHelper.getSystemDatetime() : cftDataCadastro);
            bean.setCftDataIniVig(cftDataIniVig);
            bean.setCftDataFimVig(cftDataFimVig);
            bean.setCftVlrRef(cftVlrRef);
            bean.setCftVlrMinimo(cftVlrMinimo);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
