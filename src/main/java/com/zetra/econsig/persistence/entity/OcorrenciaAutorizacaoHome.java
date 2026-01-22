package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaAutorizacaoHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaAutorizacao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaAutorizacaoHome extends AbstractEntityHome {

    public static OcorrenciaAutorizacao findByPrimaryKey(String ocaCodigo) throws FindException {
        OcorrenciaAutorizacao ocorrenciaAutorizacao = new OcorrenciaAutorizacao();
        ocorrenciaAutorizacao.setOcaCodigo(ocaCodigo);
        return find(ocorrenciaAutorizacao, ocaCodigo);
    }

    public static List<OcorrenciaAutorizacao> findByAdeTocCodigo(String adeCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaAutorizacao oca WHERE oca.autDesconto.adeCodigo = :adeCodigo AND oca.tipoOcorrencia.tocCodigo = :tocCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigo", tocCodigo);

        return findByQuery(query, parameters);
    }

    public static List<OcorrenciaAutorizacao> findByAdeTocUsuCodigo(String adeCodigo, String tocCodigo, String usuCodigo) throws FindException {
        String query = "FROM OcorrenciaAutorizacao oca WHERE oca.autDesconto.adeCodigo = :adeCodigo AND oca.tipoOcorrencia.tocCodigo = :tocCodigo AND oca.usuario.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigo", tocCodigo);
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static List<OcorrenciaAutorizacao> findByAdeTocCodigoOcaPeriodo(String adeCodigo, String tocCodigo, java.sql.Date ocaPeriodo) throws FindException {
        String query = "FROM OcorrenciaAutorizacao oca WHERE oca.autDesconto.adeCodigo = :adeCodigo AND oca.tipoOcorrencia.tocCodigo = :tocCodigo AND oca.ocaPeriodo = :ocaPeriodo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigo", tocCodigo);
        parameters.put("ocaPeriodo", ocaPeriodo);

        return findByQuery(query, parameters);
    }

    public static List<OcorrenciaAutorizacao> findByAdeTocCsaCodigo(String adeCodigo, String tocCodigo, String csaCodigo) throws FindException {
        String query = "FROM OcorrenciaAutorizacao oca ";
        query += " INNER JOIN oca.usuario usu ";
        query += " INNER JOIN usu.usuarioCsaSet csa ";
        query += " WHERE oca.autDesconto.adeCodigo = :adeCodigo AND oca.tipoOcorrencia.tocCodigo = :tocCodigo AND csa.consignataria.csaCodigo = :csaCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigo", tocCodigo);
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static List<OcorrenciaAutorizacao> findByAdeTocCodigoOrdenado(String adeCodigo, String tocCodigo) throws FindException {
        String query = "FROM OcorrenciaAutorizacao oca WHERE oca.autDesconto.adeCodigo = :adeCodigo AND oca.tipoOcorrencia.tocCodigo = :tocCodigo order by oca.ocaData desc";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigo", tocCodigo);

        return findByQuery(query, parameters);
    }

    public static List<OcorrenciaAutorizacao> findByAdeTocCodigo(String adeCodigo, String[] tocCodigos) throws FindException {
        if (tocCodigos == null || tocCodigos.length == 0) {
            return null;
        }

        String query = "FROM OcorrenciaAutorizacao oca WHERE oca.autDesconto.adeCodigo = :adeCodigo AND oca.tipoOcorrencia.tocCodigo IN (:tocCodigos)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigos", tocCodigos);

        return findByQuery(query, parameters);
    }

    public static List<OcorrenciaAutorizacao> findByAdeTocCodigoOrdenado(String adeCodigo, String[] tocCodigos) throws FindException {
        if (tocCodigos == null || tocCodigos.length == 0) {
            return null;
        }

        String query = "FROM OcorrenciaAutorizacao oca WHERE oca.autDesconto.adeCodigo = :adeCodigo AND oca.tipoOcorrencia.tocCodigo IN (:tocCodigos) order by oca.ocaData desc";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigos", tocCodigos);

        return findByQuery(query, parameters);
    }

    public static List<OcorrenciaAutorizacao> findByAdeTocCodigoMaiorPeriodoOrdenado(String adeCodigo, String tocCodigo, Date ocaPeriodo) throws FindException {
        String query = "FROM OcorrenciaAutorizacao oca WHERE oca.autDesconto.adeCodigo = :adeCodigo AND oca.tipoOcorrencia.tocCodigo = :tocCodigo AND oca.ocaPeriodo > :ocaPeriodo ORDER BY oca.ocaData";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tocCodigo", tocCodigo);
        parameters.put("ocaPeriodo", ocaPeriodo);

        return findByQuery(query, parameters);
    }

    public static OcorrenciaAutorizacao create(String adeCodigo, String tocCodigo, String usuCodigo, String ocaObs, BigDecimal ocaAdeVlrAnt, BigDecimal ocaAdeVlrNovo, String ocaIpAcesso, Date ocaData, Date ocaPeriodo, String tmoCodigo) throws CreateException {
        Session session = SessionUtil.getSession();
        OcorrenciaAutorizacao bean = new OcorrenciaAutorizacao();

        try {
            String objectId = DBHelper.getNextId();
            bean.setOcaCodigo(objectId);
            bean.setAutDesconto(session.getReference(AutDesconto.class, adeCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            if (ocaData == null) {
                bean.setOcaData(Calendar.getInstance().getTime());
            } else {
                bean.setOcaData(ocaData);
            }
            bean.setOcaPeriodo(ocaPeriodo);
            bean.setOcaObs(ocaObs);
            bean.setOcaAdeVlrAnt(ocaAdeVlrAnt);
            bean.setOcaAdeVlrNovo(ocaAdeVlrNovo);
            bean.setOcaIpAcesso(ocaIpAcesso);
            if (!TextHelper.isNull(tmoCodigo)) {
                bean.setTipoMotivoOperacao(session.getReference(TipoMotivoOperacao.class, tmoCodigo));
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
