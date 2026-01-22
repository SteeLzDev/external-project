package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Collection;
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
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ParcelaDescontoHome</p>
 * <p>Description: Classe Home para a entidade ParcelaDesconto</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParcelaDescontoHome extends AbstractEntityHome {

    /*
    @Deprecated
    public static ParcelaDesconto findByPrimaryKey(Integer prdCodigo) throws FindException {
        ParcelaDesconto parcelaDesconto = new ParcelaDesconto();
        parcelaDesconto.setPrdCodigo(prdCodigo);
        return find(parcelaDesconto, prdCodigo);
    }
    */

    public static ParcelaDesconto findByPrimaryKeyAndAutDesconto(Integer prdCodigo, String adeCodigo) throws FindException {
        String query = "FROM ParcelaDesconto prd WHERE prd.autDesconto.adeCodigo = :adeCodigo AND prd.prdCodigo = :prdCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("prdCodigo", prdCodigo);

        List<ParcelaDesconto> parcelas = findByQuery(query, parameters);

        if (parcelas == null || parcelas.isEmpty()) {
            throw new FindException("mensagem.erro.entidade.nao.encontrada", AcessoSistema.getAcessoUsuarioSistema());
        }

        return parcelas.get(0);
    }

    public static List<ParcelaDesconto> findByAutDescontoPrdNumero(String adeCodigo, Short prdNumero) throws FindException {
        String query = "FROM ParcelaDesconto prd WHERE prd.autDesconto.adeCodigo = :adeCodigo AND prd.prdNumero = :prdNumero";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("prdNumero", prdNumero);

        return findByQuery(query, parameters);
    }

    public static List<ParcelaDesconto> findByAutDesconto(String adeCodigo) throws FindException {
        String query = "FROM ParcelaDesconto prd WHERE prd.autDesconto.adeCodigo = :adeCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        return findByQuery(query, parameters);
    }

    public static List<ParcelaDesconto> findByAutDescontoStatus(String adeCodigo, String spdCodigo) throws FindException {
        String query = "FROM ParcelaDesconto prd WHERE prd.autDesconto.adeCodigo = :adeCodigo AND prd.statusParcelaDesconto.spdCodigo = :spdCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("spdCodigo", spdCodigo);

        return findByQuery(query, parameters);
    }

    public static List<ParcelaDesconto> findByAutDescontoPeriodo(String adeCodigo, Date prdDataDesconto) throws FindException {
        String query = "FROM ParcelaDesconto prd WHERE prd.autDesconto.adeCodigo = :adeCodigo AND prd.prdDataDesconto = :prdDataDesconto";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("prdDataDesconto", prdDataDesconto);

        return findByQuery(query, parameters);
    }

    public static List<ParcelaDesconto> findByAutDescontoPrdNumeroPrdDataDesconto(String adeCodigo, Short prdNumero, Date prdDataDesconto) throws FindException {
        StringBuilder query = new StringBuilder("FROM ParcelaDesconto prd WHERE prd.autDesconto.adeCodigo = :adeCodigo");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        if (prdNumero != null) {
            query.append(" AND prd.prdNumero = :prdNumero");
            parameters.put("prdNumero", prdNumero);
        }

        if (prdDataDesconto != null) {
            query.append(" AND prd.prdDataDesconto = :prdDataDesconto");
            parameters.put("prdDataDesconto", prdDataDesconto);
        }

        query.append(" ORDER BY prd.prdDataDesconto");

        return findByQuery(query.toString(), parameters);
    }

    public static ParcelaDesconto findLastByAutDesconto(String adeCodigo) throws FindException {
        String query = "FROM ParcelaDesconto prd WHERE prd.autDesconto.adeCodigo = :adeCodigo ORDER BY prd.prdDataDesconto DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        Collection<ParcelaDesconto> parcelas = findByQuery(query, parameters, 1, 0);
        if (parcelas != null && !parcelas.isEmpty()) {
            return parcelas.iterator().next();
        }

        return null;
    }

    public static Integer findMaxPrdNumero(String adeCodigo) throws FindException {
        String query = "FROM ParcelaDesconto prd WHERE prd.autDesconto.adeCodigo = :adeCodigo ORDER BY prd.prdNumero DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        List<ParcelaDesconto> parcelas = findByQuery(query, parameters, 1, 0);

        return (parcelas != null && !parcelas.isEmpty()) ? parcelas.get(0).getPrdNumero().intValue() : 0;
    }

    public static ParcelaDesconto create(String adeCodigo, Short prdNumero, String spdCodigo, Date prdDataDesconto, BigDecimal prdVlrPrevisto) throws CreateException {
        return create(adeCodigo, prdNumero, null, spdCodigo, prdDataDesconto, null, prdVlrPrevisto, null);
    }

    public static ParcelaDesconto create(String adeCodigo, Short prdNumero, String tdeCodigo, String spdCodigo, Date prdDataDesconto, Date prdDataRealizado, BigDecimal prdVlrPrevisto, BigDecimal prdVlrRealizado) throws CreateException {
        Session session = SessionUtil.getSession();
        ParcelaDesconto bean = new ParcelaDesconto();
        try {
            bean.setAutDesconto(session.getReference(AutDesconto.class, adeCodigo));
            if (!TextHelper.isNull(tdeCodigo)) {
                bean.setTipoDesconto(session.getReference(TipoDesconto.class, tdeCodigo));
            }
            bean.setStatusParcelaDesconto(session.getReference(StatusParcelaDesconto.class, spdCodigo));
            bean.setPrdNumero(prdNumero);
            bean.setPrdDataDesconto(prdDataDesconto);
            bean.setPrdDataRealizado(prdDataRealizado);
            bean.setPrdVlrPrevisto(prdVlrPrevisto);
            bean.setPrdVlrRealizado(prdVlrRealizado);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static ParcelaDesconto update(String adeCodigo, Short prdNumero, String tdeCodigo, String spdCodigo, Date prdDataDesconto, Date prdDataRealizado, BigDecimal prdVlrPrevisto, BigDecimal prdVlrRealizado) throws UpdateException {
        Session session = SessionUtil.getSession();
        ParcelaDesconto bean = new ParcelaDesconto();

        try {
            bean.setAutDesconto(session.getReference(AutDesconto.class, adeCodigo));
            if (!TextHelper.isNull(tdeCodigo)) {
                bean.setTipoDesconto(session.getReference(TipoDesconto.class, tdeCodigo));
            }
            bean.setStatusParcelaDesconto(session.getReference(StatusParcelaDesconto.class, spdCodigo));
            bean.setPrdNumero(prdNumero);
            bean.setPrdDataDesconto(prdDataDesconto);
            bean.setPrdDataRealizado(prdDataRealizado);
            bean.setPrdVlrPrevisto(prdVlrPrevisto);
            bean.setPrdVlrRealizado(prdVlrRealizado);
            update(bean);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static void deleteByAdeCodigoSpdCodigo(String adeCodigo, String spdCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            String hql = "DELETE FROM ParcelaDesconto prd WHERE prd.autDesconto.adeCodigo = :adeCodigo AND prd.statusParcelaDesconto.spdCodigo = :spdCodigo";
            MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            queryUpdate.setParameter("spdCodigo", spdCodigo);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
