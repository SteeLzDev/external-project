package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: DespesaComum</p>
 * <p>Description: Classe Home para a entidade Despesa Comum</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DespesaComumHome extends AbstractEntityHome {

    public static DespesaComum findByPrimaryKey(String decCodigo) throws FindException {
        DespesaComum despesaComum = new DespesaComum();
        despesaComum.setDecCodigo(decCodigo);
        return find(despesaComum, decCodigo);
    }

    public static DespesaComum create(String echCodigo, String plaCodigo, String posCodigo, String sdcCodigo, BigDecimal decValor,
                                      BigDecimal decValorRateio, Integer decPrazo, Date decDataIni, Date decDataFim, String decIdentificador) throws CreateException {

        Session session = SessionUtil.getSession();
        DespesaComum bean = new DespesaComum();

        try {
            String objectId = DBHelper.getNextId();
            bean.setDecCodigo(objectId);
            bean.setEnderecoConjHabitacional(session.getReference(EnderecoConjHabitacional.class, echCodigo));
            bean.setPlano(session.getReference(Plano.class, plaCodigo));
            if (!TextHelper.isNull(posCodigo)) {
                bean.setPostoRegistroServidor(session.getReference(PostoRegistroServidor.class, posCodigo));
            }
            bean.setStatusDespesaComum(session.getReference(StatusDespesaComum.class, sdcCodigo));
            bean.setDecValor(decValor);
            if (decValorRateio != null) {
                bean.setDecValorRateio(decValorRateio);
            }
            if (decPrazo != null) {
                bean.setDecPrazo(decPrazo);
            }
            bean.setDecData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setDecDataIni(decDataIni);
            if (decDataFim != null) {
                bean.setDecDataFim(decDataFim);
            }
            if (!TextHelper.isNull(decIdentificador)) {
                bean.setDecIdentificador(decIdentificador);
            }
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
