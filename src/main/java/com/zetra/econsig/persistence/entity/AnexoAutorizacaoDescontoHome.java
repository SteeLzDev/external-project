package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: AnexoAutorizacaoDescontoHome</p>
 * <p>Description: Classe Home para a entidade AnexoAutorizacaoDesconto</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AnexoAutorizacaoDescontoHome extends AbstractEntityHome {

    public static AnexoAutorizacaoDesconto findByPrimaryKey(AnexoAutorizacaoDescontoId pk) throws FindException {
        AnexoAutorizacaoDesconto anexoAutorizacaoDesconto = new AnexoAutorizacaoDesconto();
        anexoAutorizacaoDesconto.setId(pk);
        return find(anexoAutorizacaoDesconto, pk);
    }

    public static Collection<AnexoAutorizacaoDesconto> findByAdeTarCodigo(String adeCodigo, String tarCodigo) throws FindException {
        String query = "FROM AnexoAutorizacaoDesconto aad WHERE aad.autDesconto.adeCodigo = :adeCodigo AND aad.tipoArquivo.tarCodigo = :tarCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tarCodigo", tarCodigo);

        return findByQuery(query, parameters);
    }

    public static AnexoAutorizacaoDesconto create(String adeCodigo, String aadNome, String usuCodigo, String aadDescricao, java.sql.Date aadPeriodo, Short aadAtivo, Date aadData, String aadIpAcesso,
            TipoArquivoEnum tipoArquivo, String aadExibeSup, String aadExibeCse, String aadExibeOrg, String aadExibeCsa, String aadExibeCor, String aadExibeSer) throws CreateException {

        Session session = SessionUtil.getSession();
        AnexoAutorizacaoDesconto bean = new AnexoAutorizacaoDesconto();
        try {
            bean.setId(new AnexoAutorizacaoDescontoId(adeCodigo, aadNome));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setTipoArquivo(session.getReference(TipoArquivo.class, tipoArquivo.getCodigo()));
            bean.setAadDescricao(aadDescricao);
            bean.setAadPeriodo(aadPeriodo);
            bean.setAadAtivo(aadAtivo);
            bean.setAadData(aadData);
            bean.setAadIpAcesso(aadIpAcesso);
            bean.setAadExibeSup(aadExibeSup);
            bean.setAadExibeCse(aadExibeCse);
            bean.setAadExibeOrg(aadExibeOrg);
            bean.setAadExibeCsa(aadExibeCsa);
            bean.setAadExibeCor(aadExibeCor);
            bean.setAadExibeSer(aadExibeSer);

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void updateDataPeriodo(String adeCodigo, String aadNome, java.sql.Date novaDtaPeriodo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            String hql = "UPDATE AnexoAutorizacaoDesconto aad set aad.aadPeriodo = :novaDtaPeriodo WHERE aad.id.aadNome = :aadNome and aad.autDesconto.adeCodigo = :adeCodigo";
            MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("adeCodigo", adeCodigo);
            queryUpdate.setParameter("novaDtaPeriodo", novaDtaPeriodo);
            queryUpdate.setParameter("aadNome", aadNome);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static List<AnexoAutorizacaoDesconto> lstAnexosTipoArquivoPeriodo(String adeCodigo, List<String> tarCodigos, Date periodo) throws FindException {
        String query = "FROM AnexoAutorizacaoDesconto aad WHERE aad.autDesconto.adeCodigo = :adeCodigo AND aad.tipoArquivo.tarCodigo IN (:tarCodigos) AND aad.aadPeriodo = :periodo ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tarCodigos", tarCodigos);
        parameters.put("periodo", periodo);

        return findByQuery(query, parameters);
    }

    public static List<AnexoAutorizacaoDesconto> lstAnexosTipoArquivoMaxPeriodo(String adeCodigo, List<String> tarCodigos) throws FindException {
        StringBuilder query = new StringBuilder();
        query.append("FROM AnexoAutorizacaoDesconto aad WHERE aad.autDesconto.adeCodigo = :adeCodigo AND aad.tipoArquivo.tarCodigo IN (:tarCodigos) ");
        query.append(" AND aad.aadPeriodo in (select max(aadMax.aadPeriodo) from AnexoAutorizacaoDesconto aadMax where aadMax.id.adeCodigo = aad.id.adeCodigo) ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("tarCodigos", tarCodigos);

        return findByQuery(query.toString(), parameters);
    }

    public static List<AnexoAutorizacaoDesconto> lstAllAnexosTipoArquivoPeriodo(List<String> tarCodigos, Date periodo) throws FindException {
        String query = "FROM AnexoAutorizacaoDesconto aad WHERE aad.tipoArquivo.tarCodigo IN (:tarCodigos) AND aad.aadPeriodo = :periodo ORDER BY aad.adeCodigo, aad.aadPeriodo DESC, aad.aadData DESC ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tarCodigos", tarCodigos);
        parameters.put("periodo", periodo);

        return findByQuery(query, parameters);
    }

    public static List<AnexoAutorizacaoDesconto> lstAnexosAdePosPeriodo(String adeCodigo, Date periodo) throws FindException {
        String query = "FROM AnexoAutorizacaoDesconto aad WHERE aad.autDesconto.adeCodigo = :adeCodigo AND aad.aadPeriodo >= :periodo ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("periodo", periodo);

        return findByQuery(query, parameters);
    }
}
