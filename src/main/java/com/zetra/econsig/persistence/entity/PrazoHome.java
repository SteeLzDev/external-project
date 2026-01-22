package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: PrazoHome</p>
 * <p>Description: Classe Home para a entidade Prazo</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PrazoHome extends AbstractEntityHome {

    public static Prazo findByPrimaryKey(String przCodigo) throws FindException {
        final Prazo prazo = new Prazo();
        prazo.setPrzCodigo(przCodigo);
        return find(prazo, przCodigo);
    }

    public static Prazo findBySvcValor(String svcCodigo, Short przVlr) throws FindException {
        final String query = "FROM Prazo prz WHERE prz.servico.svcCodigo = :svcCodigo AND prz.przVlr = :przVlr";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcCodigo", svcCodigo);
        parameters.put("przVlr", przVlr);

        final List<Prazo> result = findByQuery(query, parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<Prazo> findByServico(String svcCodigo) throws FindException {
        final String query = "FROM Prazo prz WHERE prz.servico.svcCodigo = :svcCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcCodigo", svcCodigo);

        return findByQuery(query, parameters);
    }

    public static List<Prazo> findAtivoByServico(String svcCodigo) throws FindException {
        final String query = "FROM Prazo prz WHERE prz.servico.svcCodigo = :svcCodigo AND prz.przAtivo = " + CodedValues.STS_ATIVO + "";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcCodigo", svcCodigo);

        return findByQuery(query, parameters);
    }

    public static List<Prazo> findByCsaServico(String csaCodigo, String svcCodigo) throws FindException {
        final String query = "FROM Prazo prz WHERE prz.servico.svcCodigo = :svcCodigo AND EXISTS (SELECT 1 FROM prz.prazoConsignatariaSet pzc WHERE pzc.consignataria.csaCodigo = :csaCodigo)";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcCodigo", svcCodigo);
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static List<Prazo> findAtivoByCsaServico(String csaCodigo, String svcCodigo) throws FindException {
        final StringBuilder query = new StringBuilder("SELECT prz FROM Prazo prz");
        query.append(" INNER JOIN prz.prazoConsignatariaSet przCsa");
        query.append(" WHERE prz.servico.svcCodigo = :svcCodigo");
        query.append(" AND przCsa.consignataria.csaCodigo = :csaCodigo");
        query.append(" AND prz.przAtivo = " + CodedValues.STS_ATIVO + "");
        query.append(" AND przCsa.przCsaAtivo = " + CodedValues.STS_ATIVO + "");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcCodigo", svcCodigo);
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query.toString(), parameters);
    }

    public static Prazo create(String svcCodigo, Short przVlr) throws CreateException {

        final Session session = SessionUtil.getSession();
        final Prazo bean = new Prazo();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setPrzCodigo(objectId);
            bean.setServico(session.getReference(Servico.class, svcCodigo));
            bean.setPrzVlr(przVlr);
            bean.setPrzAtivo(CodedValues.STS_ATIVO);
            create(bean, session);
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
