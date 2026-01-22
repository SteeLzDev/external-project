package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OperacaoLiberaMargemHome</p>
 * <p>Description: Classe Home para operações CRUD de OperacaoLiberaMargem</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OperacaoLiberaMargemHome extends AbstractEntityHome {

    public static OperacaoLiberaMargem findByPrimaryKey(String olmCodigo) throws FindException {
        OperacaoLiberaMargem bean = new OperacaoLiberaMargem();
        bean.setOlmCodigo(olmCodigo);
        return find(bean, olmCodigo);
    }

    public static OperacaoLiberaMargem create(String rseCodigo, String usuCodigo, String csaCodigo, String olmIpAcesso, Date olmData, String olmBloqueio, String olmConfirmada, String adeCodigo) throws CreateException {
        Session session = SessionUtil.getSession();
        OperacaoLiberaMargem bean = new OperacaoLiberaMargem();

        try {
            bean.setOlmCodigo(DBHelper.getNextId());
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            if (!TextHelper.isNull(csaCodigo)) {
                bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            }
            bean.setOlmIpAcesso(olmIpAcesso);
            bean.setOlmData(olmData);
            bean.setOlmBloqueio(olmBloqueio);
            if (!TextHelper.isNull(olmConfirmada)) {
                bean.setOlmConfirmada(olmConfirmada);
            } else {
                bean.setOlmConfirmada("N"); // Valor default
            }
            if (!TextHelper.isNull(adeCodigo)) {
                bean.setAutDesconto(session.getReference(AutDesconto.class, adeCodigo));
            }

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void removeByDateLessThan(Date olmData) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM OperacaoLiberaMargem olm ");
            hql.append("WHERE olm.olmBloqueio = 'N' ");
            hql.append("AND olm.olmData < :olmData ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
            queryUpdate.setParameter("olmData", olmData);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void confirmarOperacaoLiberaMargemSemMultiplasAdes() throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("UPDATE OperacaoLiberaMargem olm SET ");
            hql.append("olm.olmConfirmada = 'S' ");
            hql.append("WHERE olm.olmBloqueio = 'N' ");
            hql.append("AND olm.olmConfirmada = 'N' ");
            hql.append("AND (olm.adeCodigo IS NULL ");
            hql.append("OR NOT EXISTS (");
            hql.append("SELECT 1 FROM olm.autDesconto ade JOIN ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rad WHERE rad.tipoNatureza.tntCodigo IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("','").append(CodedValues.TNT_CONTROLE_COMPRA).append("') ");
            hql.append(")) ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void removeByAdeCodigo(String adeCodigo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM OperacaoLiberaMargem olm ");
            hql.append("WHERE olm.olmBloqueio = 'N' ");
            hql.append("AND olm.olmConfirmada = 'N' ");
            hql.append("AND olm.autDesconto.adeCodigo =:adeCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
            queryUpdate.setParameter("adeCodigo", adeCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
