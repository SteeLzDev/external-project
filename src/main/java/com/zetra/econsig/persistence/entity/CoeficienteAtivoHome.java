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
 * <p>Title: CoeficienteAtivoHome</p>
 * <p>Description: Classe Home para a entidade CoeficienteAtivo</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CoeficienteAtivoHome extends AbstractEntityHome {

    public static CoeficienteAtivo findByPrimaryKey(String cftCodigo) throws FindException {
        CoeficienteAtivo coeficienteAtivo = new CoeficienteAtivo();
        coeficienteAtivo.setCftCodigo(cftCodigo);
        return find(coeficienteAtivo, cftCodigo);
    }

    public static CoeficienteAtivo create(String przCsaCodigo, Short cftDia, BigDecimal cftVlr, java.util.Date cftDataIniVig, java.util.Date cftDataFimVig, BigDecimal cftVlrRef, BigDecimal cftVlrMinimo) throws CreateException {

        Session session = SessionUtil.getSession();
        CoeficienteAtivo bean = new CoeficienteAtivo();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setCftCodigo(objectId);
            bean.setPrazoConsignataria(session.getReference(PrazoConsignataria.class, przCsaCodigo));
            bean.setCftDia(cftDia);
            bean.setCftVlr(cftVlr);
            bean.setCftDataCadastro(DateHelper.getSystemDatetime());
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
