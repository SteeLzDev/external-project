package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: MargemRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade MargemRegistroServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MargemRegistroServidorHome extends AbstractEntityHome {

    public static MargemRegistroServidor findByPrimaryKey(MargemRegistroServidorId pk) throws FindException {
        final MargemRegistroServidor margemRegistroServidor = new MargemRegistroServidor();
        margemRegistroServidor.setId(pk);
        return find(margemRegistroServidor, pk);
    }

    public static MargemRegistroServidor findByPrimaryKeyForUpdate(MargemRegistroServidorId pk) throws FindException {
        final MargemRegistroServidor margemRegistroServidor = new MargemRegistroServidor();
        margemRegistroServidor.setId(pk);
        return find(margemRegistroServidor, pk, true);
    }

    public static List<MargemRegistroServidor> findByRseCodigo(String rseCodigo) throws FindException {
        final String query = "FROM MargemRegistroServidor mrs WHERE mrs.id.rseCodigo = :rseCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        return findByQuery(query, parameters);
    }

    public static MargemRegistroServidor create(Margem margem, String rseCodigo, BigDecimal mrsMargem, BigDecimal mrsMargemRest, BigDecimal mrsMargemUsada, Date mrsPeriodoIni, Date mrsPeriodoFim) throws CreateException {
        RegistroServidor registroServidor;
        try {
            registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
        } catch (final FindException e) {
            throw new CreateException(e);
        }

        final MargemRegistroServidor bean = new MargemRegistroServidor();

        final MargemRegistroServidorId id = new MargemRegistroServidorId();
        id.setMarCodigo(margem.getMarCodigo());
        id.setRseCodigo(rseCodigo);
        bean.setId(id);
        bean.setMrsMargem(mrsMargem);
        bean.setMrsMargemRest(mrsMargemRest);
        bean.setMrsMargemUsada(mrsMargemUsada);
        bean.setMrsPeriodoIni(mrsPeriodoIni);
        bean.setMrsPeriodoFim(mrsPeriodoFim);

        create(bean);
        // DESENV-15683 : seta os campos depois de criar para não correr risco de incompatibilidade
        bean.setMargem(margem);
        bean.setRegistroServidor(registroServidor);

        return bean;
    }

    public static void copy(String rseCodigoOrigem, String rseCodigoDestino) throws CreateException, FindException {
        final List<MargemRegistroServidor> margens = findByRseCodigo(rseCodigoOrigem);
        for (final MargemRegistroServidor margem : margens) {
            create(margem.getMargem(), rseCodigoDestino, margem.getMrsMargem(), margem.getMrsMargemRest(), margem.getMrsMargemUsada(), margem.getMrsPeriodoIni(), margem.getMrsPeriodoFim());
        }
    }

    public static void zerarMargemRegistroServidor(String rseCodigo) throws UpdateException {
        final Session session = SessionUtil.getSession();
        try {
            final StringBuilder hql = new StringBuilder();

            hql.append("UPDATE MargemRegistroServidor mrs SET ");
            hql.append("mrs.mrsMargem = 0, mrs.mrsMargemRest = 0 - mrs.mrsMargemUsada ");
            hql.append("WHERE mrs.id.rseCodigo = :rseCodigo");

            final MutationQuery queryUpdate = session.createMutationQuery(hql.toString());
            queryUpdate.setParameter("rseCodigo", rseCodigo);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        final Session session = SessionUtil.getSession();
        try {
            final StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM MargemRegistroServidor mrs WHERE mrs.registroServidor.rseCodigo = :rseCodigo ");

            final MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("rseCodigo", rseCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (final Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void updateMargemRegistroServidor(String rseCodigo, Short adeIncMargem, BigDecimal mrsMargem, Short marCodAdequacao) throws UpdateException {
        try {
            final MargemRegistroServidorId margemRegistroServidorId = new MargemRegistroServidorId();
            margemRegistroServidorId.setRseCodigo(rseCodigo);
            margemRegistroServidorId.setMarCodigo(adeIncMargem);

            final MargemRegistroServidor margemRegistroServidor = findByPrimaryKey(margemRegistroServidorId);
            margemRegistroServidor.setMrsMargem(mrsMargem);
            margemRegistroServidor.setMarCodAdequacao(marCodAdequacao);
            update(margemRegistroServidor);
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        }
    }

    public static void restaurarValorParcelaUltimaAlteracao(String rseCodigo, Short adeIncMargem) throws UpdateException {
        try {
            final MargemRegistroServidorId margemRegistroServidorId = new MargemRegistroServidorId();
            margemRegistroServidorId.setRseCodigo(rseCodigo);
            margemRegistroServidorId.setMarCodigo(adeIncMargem);

            final MargemRegistroServidor margemRegistroServidor = findByPrimaryKey(margemRegistroServidorId);
            margemRegistroServidor.setMarCodAdequacao(null);
            update(margemRegistroServidor);
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        }
    }

    public static List<MargemRegistroServidor> lstMargensComAdequacao(String rseCodigo) throws UpdateException {
        try {
            final String query = "FROM MargemRegistroServidor mrs WHERE mrs.id.rseCodigo = :rseCodigo AND mrs.marCodAdequacao IS NOT NULL";

            final Map<String, Object> parameters = new HashMap<>();
            parameters.put("rseCodigo", rseCodigo);

            return findByQuery(query, parameters);
        } catch (final Exception ex) {
            throw new UpdateException(ex);
        }
    }
}
