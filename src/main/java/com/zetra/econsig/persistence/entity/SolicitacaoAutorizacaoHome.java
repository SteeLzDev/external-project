package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: SolicitacaoAutorizacaoHome</p>
 * <p>Description: Classe Home para a entidade SolicitacaoAutorizacao</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SolicitacaoAutorizacaoHome extends AbstractEntityHome {

    private static final String SSO_CODIGO = "ssoCodigo";
	private static final String TIS_CODIGO = "tisCodigo";
	private static final String ADE_CODIGO = "adeCodigo";

	public static SolicitacaoAutorizacao findByPrimaryKey(String soaCodigo) throws FindException {
        SolicitacaoAutorizacao solicitacaoAutorizacao = new SolicitacaoAutorizacao();
        solicitacaoAutorizacao.setSoaCodigo(soaCodigo);
        return find(solicitacaoAutorizacao, soaCodigo);
    }

    public static List<SolicitacaoAutorizacao> findByAdeTipoStatus(String adeCodigo, String[] tisCodigo, String ssoCodigo) throws FindException {
        String query = "FROM SolicitacaoAutorizacao soa WHERE soa.autDesconto.adeCodigo = :adeCodigo AND soa.tipoSolicitacao.tisCodigo IN (:tisCodigo) AND soa.statusSolicitacao.ssoCodigo = :ssoCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ADE_CODIGO, adeCodigo);
        parameters.put(TIS_CODIGO, tisCodigo);
        parameters.put(SSO_CODIGO, ssoCodigo);

        return findByQuery(query, parameters);
    }

    public static List<SolicitacaoAutorizacao> findByAdeTipoStatus(String adeCodigo, String[] tisCodigo) throws FindException {
        String query = "FROM SolicitacaoAutorizacao soa WHERE soa.autDesconto.adeCodigo = :adeCodigo AND soa.tipoSolicitacao.tisCodigo IN (:tisCodigo) ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ADE_CODIGO, adeCodigo);
        parameters.put(TIS_CODIGO, tisCodigo);

        return findByQuery(query, parameters);
    }

    public static List<SolicitacaoAutorizacao> findByAdeTipoStatus(String adeCodigo, String[] tisCodigo, String[] ssoCodigo) throws FindException {
        String query = "FROM SolicitacaoAutorizacao soa WHERE soa.autDesconto.adeCodigo = :adeCodigo AND soa.tipoSolicitacao.tisCodigo IN (:tisCodigo) AND soa.statusSolicitacao.ssoCodigo IN (:ssoCodigo)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ADE_CODIGO, adeCodigo);
        parameters.put(TIS_CODIGO, tisCodigo);
        parameters.put(SSO_CODIGO, ssoCodigo);

        return findByQuery(query, parameters);
    }

    public static List<SolicitacaoAutorizacao> findByTipoStatus(String[] tisCodigo, String ssoCodigo) throws FindException {
        String query = "FROM SolicitacaoAutorizacao soa WHERE soa.tipoSolicitacao.tisCodigo IN (:tisCodigo) AND soa.statusSolicitacao.ssoCodigo = :ssoCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(TIS_CODIGO, tisCodigo);
        parameters.put(SSO_CODIGO, ssoCodigo);

        return findByQuery(query, parameters);
    }

    public static List<SolicitacaoAutorizacao> findByTipoStatus(String[] tisCodigo, String[] ssoCodigo) throws FindException {
        String query = "FROM SolicitacaoAutorizacao soa WHERE soa.tipoSolicitacao.tisCodigo IN (:tisCodigo) AND soa.statusSolicitacao.ssoCodigo IN (:ssoCodigo)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(TIS_CODIGO, tisCodigo);
        parameters.put(SSO_CODIGO, ssoCodigo);

        return findByQuery(query, parameters);
    }

    public static SolicitacaoAutorizacao create(String adeCodigo, String usuCodigo, String tisCodigo, String ssoCodigo, Date soaDataValidade) throws CreateException {
        Session session = SessionUtil.getSession();
        SolicitacaoAutorizacao bean = new SolicitacaoAutorizacao();

        try {
            String objectId = DBHelper.getNextId();
            bean.setSoaCodigo(objectId);
            bean.setAutDesconto(session.getReference(AutDesconto.class, adeCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setTipoSolicitacao(session.getReference(TipoSolicitacao.class, tisCodigo));
            bean.setStatusSolicitacao(session.getReference(StatusSolicitacao.class, ssoCodigo));
            bean.setSoaData(DateHelper.getSystemDatetime());
            bean.setSoaDataValidade(soaDataValidade);
            bean.setSoaDataResposta(null);
            bean.setSoaObs(null);
            bean.setOrigemSolicitacao(null);

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    @SuppressWarnings("java:S107")
    public static SolicitacaoAutorizacao createPendenteAprovacao(String adeCodigo, String usuCodigo, String tisCodigo, String ssoCodigo, Date soaDataValidade, Date soaDataResposta, String soaObs, String osoCodigo, Date soaPeriodo) throws CreateException {
        Session session = SessionUtil.getSession();
        SolicitacaoAutorizacao bean = new SolicitacaoAutorizacao();

        try {
            String objectId = DBHelper.getNextId();
            bean.setSoaCodigo(objectId);
            bean.setAutDesconto(session.getReference(AutDesconto.class, adeCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setTipoSolicitacao(session.getReference(TipoSolicitacao.class, tisCodigo));
            bean.setStatusSolicitacao(session.getReference(StatusSolicitacao.class, ssoCodigo));
            bean.setSoaData(DateHelper.getSystemDatetime());
            bean.setSoaDataValidade(soaDataValidade);
            bean.setSoaDataResposta(soaDataResposta);
            bean.setSoaObs(soaObs);
            bean.setOrigemSolicitacao(session.getReference(OrigemSolicitacao.class, osoCodigo));
            bean.setSoaPeriodo(soaPeriodo);

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static SolicitacaoAutorizacao findLastByAdeCodigoTisCodigo(String adeCodigo, String tisCodigo) throws FindException {
        String query = "FROM SolicitacaoAutorizacao soa WHERE soa.adeCodigo = :adeCodigo AND soa.tisCodigo = :tisCodigo ORDER BY soa.soaData DESC";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ADE_CODIGO, adeCodigo);
        parameters.put(TIS_CODIGO, tisCodigo);

        List<SolicitacaoAutorizacao> result = findByQuery(query, parameters, 1, 0);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }
}
