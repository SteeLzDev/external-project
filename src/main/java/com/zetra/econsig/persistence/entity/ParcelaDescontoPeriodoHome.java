package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ParcelaDescontoPeriodoHome</p>
 * <p>Description: Classe Home para a entidade ParcelaDescontoPeriodo</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParcelaDescontoPeriodoHome extends AbstractEntityHome {

    /*
    @Deprecated
    public static ParcelaDescontoPeriodo findByPrimaryKey(Integer prdCodigo) throws FindException {
        ParcelaDescontoPeriodo parcelaDescontoPeriodo = new ParcelaDescontoPeriodo();
        parcelaDescontoPeriodo.setPrdCodigo(prdCodigo);
        return find(parcelaDescontoPeriodo, prdCodigo);
    }
    */

    public static ParcelaDescontoPeriodo findByPrimaryKeyAndAutDesconto(Integer prdCodigo, String adeCodigo) throws FindException {
        String query = "FROM ParcelaDescontoPeriodo pdp WHERE pdp.autDesconto.adeCodigo = :adeCodigo AND pdp.prdCodigo = :prdCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("prdCodigo", prdCodigo);

        List<ParcelaDescontoPeriodo> parcelas = findByQuery(query, parameters);

        if (parcelas == null || parcelas.isEmpty()) {
            throw new FindException("mensagem.erro.entidade.nao.encontrada", AcessoSistema.getAcessoUsuarioSistema());
        }

        return parcelas.get(0);
    }

    public static List<ParcelaDescontoPeriodo> findByAutDescontoPrdNumero(String adeCodigo, Short prdNumero) throws FindException {
        String query = "FROM ParcelaDescontoPeriodo pdp WHERE pdp.autDesconto.adeCodigo = :adeCodigo AND pdp.prdNumero = :prdNumero";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("prdNumero", prdNumero);

        return findByQuery(query, parameters);
    }

    public static List<ParcelaDescontoPeriodo> findByAutDesconto(String adeCodigo) throws FindException {
        String query = "FROM ParcelaDescontoPeriodo pdp WHERE pdp.autDesconto.adeCodigo = :adeCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        return findByQuery(query, parameters);
    }

    public static List<ParcelaDescontoPeriodo> findByAutDescontoStatus(String adeCodigo, String spdCodigo) throws FindException {
        String query = "FROM ParcelaDescontoPeriodo pdp WHERE pdp.autDesconto.adeCodigo = :adeCodigo AND pdp.statusParcelaDesconto.spdCodigo = :spdCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("spdCodigo", spdCodigo);

        return findByQuery(query, parameters);
    }

    public static List<ParcelaDescontoPeriodo> findByAutDescontoPeriodo(String adeCodigo, Date prdDataDesconto) throws FindException {
        String query = "FROM ParcelaDescontoPeriodo pdp WHERE pdp.autDesconto.adeCodigo = :adeCodigo AND pdp.prdDataDesconto = :prdDataDesconto";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("prdDataDesconto", prdDataDesconto);

        return findByQuery(query, parameters);
    }

    public static List<ParcelaDescontoPeriodo> findByAutDescontoPrdNumeroPrdDataDesconto(String adeCodigo, Short prdNumero, Date prdDataDesconto) throws FindException {
        StringBuilder query = new StringBuilder("FROM ParcelaDescontoPeriodo pdp WHERE pdp.autDesconto.adeCodigo = :adeCodigo");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        if (prdNumero != null) {
            query.append(" AND pdp.prdNumero = :prdNumero");
            parameters.put("prdNumero", prdNumero);
        }

        if (prdDataDesconto != null) {
            query.append(" AND pdp.prdDataDesconto = :prdDataDesconto");
            parameters.put("prdDataDesconto", prdDataDesconto);
        }

        query.append(" ORDER BY pdp.prdDataDesconto");

        return findByQuery(query.toString(), parameters);
    }

    public static Integer findMaxPrdNumero(String adeCodigo) throws FindException {
        String query = "FROM ParcelaDescontoPeriodo pdp WHERE pdp.autDesconto.adeCodigo = :adeCodigo ORDER BY pdp.prdNumero DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        List<ParcelaDescontoPeriodo> parcelas = findByQuery(query, parameters, 1, 0);

        return (parcelas != null && !parcelas.isEmpty()) ? parcelas.get(0).getPrdNumero().intValue() : 0;
    }

    public static ParcelaDescontoPeriodo create(String adeCodigo, Short prdNumero, String spdCodigo, Date prdDataDesconto, BigDecimal prdVlrPrevisto) throws CreateException {
        return create(adeCodigo, prdNumero, spdCodigo, prdDataDesconto, null, prdVlrPrevisto, null);
    }

    public static ParcelaDescontoPeriodo create(String adeCodigo, Short prdNumero, String spdCodigo, Date prdDataDesconto, Date prdDataRealizado, BigDecimal prdVlrPrevisto, BigDecimal prdVlrRealizado) throws CreateException {
        Session session = SessionUtil.getSession();
        ParcelaDescontoPeriodo bean = new ParcelaDescontoPeriodo();
        try {
            bean.setAutDesconto(session.getReference(AutDesconto.class, adeCodigo));
            bean.setPrdNumero(prdNumero);
            bean.setStatusParcelaDesconto(session.getReference(StatusParcelaDesconto.class, spdCodigo));
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

    public static ParcelaDescontoPeriodo findLastByAutDesconto(String adeCodigo) throws FindException {
        String query = "FROM ParcelaDescontoPeriodo prp WHERE prp.autDesconto.adeCodigo = :adeCodigo ORDER BY prp.prdDataDesconto DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        Collection<ParcelaDescontoPeriodo> parcelas = findByQuery(query, parameters, 1, 0);
        if (parcelas != null && !parcelas.isEmpty()) {
            return parcelas.iterator().next();
        }

        return null;
    }
}
