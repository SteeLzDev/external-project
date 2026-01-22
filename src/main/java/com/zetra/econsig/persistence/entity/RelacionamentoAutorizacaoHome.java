package com.zetra.econsig.persistence.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.StatusCompraEnum;

/**
 * <p>Title: RelacionamentoAutorizacaoHome</p>
 * <p>Description: Classe Home para a entidade RelacionamentoAutorizacao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelacionamentoAutorizacaoHome extends AbstractEntityHome {

    public static RelacionamentoAutorizacao findByPrimaryKey(RelacionamentoAutorizacaoId pk) throws FindException {
        RelacionamentoAutorizacao relacionamentoAutorizacao = new RelacionamentoAutorizacao();
        relacionamentoAutorizacao.setId(pk);
        return find(relacionamentoAutorizacao, pk);
    }

    public static List<RelacionamentoAutorizacao> findByOrigem(String adeCodigoOrigem, String tntCodigo) throws FindException {
        List<String> tntCodigos = new ArrayList<>();
        tntCodigos.add(tntCodigo);
        return findByOrigem(adeCodigoOrigem, tntCodigos);
    }

    public static List<RelacionamentoAutorizacao> findByOrigem(String adeCodigoOrigem, List<String> tntCodigo) throws FindException {
        String query = "FROM RelacionamentoAutorizacao rad WHERE rad.autDescontoByAdeCodigoOrigem.adeCodigo = :adeCodigoOrigem AND rad.tipoNatureza.tntCodigo in (:tntCodigo)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigoOrigem", adeCodigoOrigem);
        parameters.put("tntCodigo", tntCodigo);

        return findByQuery(query, parameters);
    }

    public static List<RelacionamentoAutorizacao> findByOrigem(String adeCodigoOrigem, String tntCodigo, String sadCodigoDestino) throws FindException {
        StringBuilder query = new StringBuilder();

        query.append("SELECT rad FROM RelacionamentoAutorizacao rad ");
        query.append("INNER JOIN rad.autDescontoByAdeCodigoDestino ade ");
        query.append("WHERE rad.autDescontoByAdeCodigoOrigem.adeCodigo = :adeCodigoOrigem ");
        query.append("AND rad.tipoNatureza.tntCodigo = :tntCodigo ");
        query.append("AND ade.statusAutorizacaoDesconto.sadCodigo = :sadCodigoDestino ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigoOrigem", adeCodigoOrigem);
        parameters.put("tntCodigo", tntCodigo);
        parameters.put("sadCodigoDestino", sadCodigoDestino);

        return findByQuery(query.toString(), parameters);
    }

    public static List<RelacionamentoAutorizacao> findByOrigem(String adeCodigoOrigem, String tntCodigo, List<String> sadCodigoDestino) throws FindException {
        StringBuilder query = new StringBuilder();

        query.append("SELECT rad FROM RelacionamentoAutorizacao rad ");
        query.append("INNER JOIN rad.autDescontoByAdeCodigoDestino ade ");
        query.append("WHERE rad.autDescontoByAdeCodigoOrigem.adeCodigo = :adeCodigoOrigem ");
        query.append("AND rad.tipoNatureza.tntCodigo = :tntCodigo ");
        query.append("AND ade.statusAutorizacaoDesconto.sadCodigo in (:sadCodigoDestino) ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigoOrigem", adeCodigoOrigem);
        parameters.put("tntCodigo", tntCodigo);
        parameters.put("sadCodigoDestino", sadCodigoDestino);

        return findByQuery(query.toString(), parameters);
    }

    public static List<RelacionamentoAutorizacao> findByOrigem(String adeCodigoOrigem, List<String> tntCodigo, List<String> sadCodigoDestino) throws FindException {
        StringBuilder query = new StringBuilder();

        query.append("SELECT rad FROM RelacionamentoAutorizacao rad ");
        query.append("INNER JOIN rad.autDescontoByAdeCodigoDestino ade ");
        query.append("WHERE rad.autDescontoByAdeCodigoOrigem.adeCodigo = :adeCodigoOrigem ");
        query.append("AND rad.tipoNatureza.tntCodigo in (:tntCodigo) ");
        query.append("AND ade.statusAutorizacaoDesconto.sadCodigo in (:sadCodigoDestino) ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigoOrigem", adeCodigoOrigem);
        parameters.put("tntCodigo", tntCodigo);
        parameters.put("sadCodigoDestino", sadCodigoDestino);

        return findByQuery(query.toString(), parameters);
    }

    public static List<RelacionamentoAutorizacao> findByDestino(String adeCodigoDestino, String tntCodigo) throws FindException {
        String query = "FROM RelacionamentoAutorizacao rad WHERE rad.autDescontoByAdeCodigoDestino.adeCodigo = :adeCodigoDestino AND rad.tipoNatureza.tntCodigo = :tntCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigoDestino", adeCodigoDestino);
        parameters.put("tntCodigo", tntCodigo);

        return findByQuery(query, parameters);
    }

    public static List<RelacionamentoAutorizacao> findByDestino(String adeCodigoDestino, List<String> tntCodigo) throws FindException {
        String query = "FROM RelacionamentoAutorizacao rad WHERE rad.autDescontoByAdeCodigoDestino.adeCodigo = :adeCodigoDestino AND rad.tipoNatureza.tntCodigo in (:tntCodigo)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigoDestino", adeCodigoDestino);
        parameters.put("tntCodigo", tntCodigo);

        return findByQuery(query, parameters);
    }

    public static List<RelacionamentoAutorizacao> findByOrigemDestino(String adeCodigoOrigem, String adeCodigoDestino, String tntCodigo) throws FindException {
        String query = "FROM RelacionamentoAutorizacao rad WHERE rad.autDescontoByAdeCodigoOrigem.adeCodigo = :adeCodigoOrigem AND rad.autDescontoByAdeCodigoDestino.adeCodigo = :adeCodigoDestino AND rad.tipoNatureza.tntCodigo = :tntCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigoOrigem", adeCodigoOrigem);
        parameters.put("adeCodigoDestino", adeCodigoDestino);
        parameters.put("tntCodigo", tntCodigo);

        return findByQuery(query, parameters);
    }

    public static RelacionamentoAutorizacao create(String adeCodigoOrigem, String adeCodigoDestino, String tntCodigo, String usuCodigo) throws CreateException {
        Session session = SessionUtil.getSession();
        RelacionamentoAutorizacao bean = new RelacionamentoAutorizacao();
        try {
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setRadData(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            if (tntCodigo.equals(CodedValues.TNT_CONTROLE_COMPRA)) {
                bean.setConsignatariaByCsaCodigoOrigem(ConsignatariaHome.findByAdeCodigo(adeCodigoOrigem));
                bean.setConsignatariaByCsaCodigoDestino(ConsignatariaHome.findByAdeCodigo(adeCodigoDestino));
            }
            RelacionamentoAutorizacaoId id = new RelacionamentoAutorizacaoId(adeCodigoOrigem, adeCodigoDestino, tntCodigo);
            bean.setId(id);
            create(bean, session);
        } catch (FindException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static RelacionamentoAutorizacao create(String adeCodigoOrigem, String adeCodigoDestino, String tntCodigo, String usuCodigo, StatusCompraEnum statusCompraEnum) throws CreateException {
        Session session = SessionUtil.getSession();
        RelacionamentoAutorizacao bean = new RelacionamentoAutorizacao();
        try {
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setRadData(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setRadDataRefInfSaldo(bean.getRadData());
            bean.setStatusCompra(session.getReference(StatusCompra.class, statusCompraEnum.getCodigo()));
            bean.setConsignatariaByCsaCodigoOrigem(ConsignatariaHome.findByAdeCodigo(adeCodigoOrigem));
            bean.setConsignatariaByCsaCodigoDestino(ConsignatariaHome.findByAdeCodigo(adeCodigoDestino));
            RelacionamentoAutorizacaoId id = new RelacionamentoAutorizacaoId(adeCodigoOrigem, adeCodigoDestino, tntCodigo);
            bean.setId(id);
            create(bean, session);
        } catch (FindException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void updateCsaCodigo(String adeCodigo, String csaCodigoNovo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            // atualiza csa_codigo_origem caso existam relacionamentos com a ade informada na origem do relacionamento
            String hql = "UPDATE RelacionamentoAutorizacao SET consignatariaByCsaCodigoOrigem.csaCodigo = :csaCodigoNovo WHERE autDescontoByAdeCodigoOrigem.adeCodigo = :adeCodigo ";
            MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            queryUpdate.setParameter("csaCodigoNovo", csaCodigoNovo);
            queryUpdate.executeUpdate();
            // atualiza csa_codigo_destino caso existam relacionamentos com a ade informada no destino do relacionamento
            hql = "UPDATE RelacionamentoAutorizacao SET consignatariaByCsaCodigoDestino.csaCodigo = :csaCodigoNovo WHERE autDescontoByAdeCodigoDestino.adeCodigo = :adeCodigo ";
            queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            queryUpdate.setParameter("csaCodigoNovo", csaCodigoNovo);
            queryUpdate.executeUpdate();
            session.flush();

        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

}
