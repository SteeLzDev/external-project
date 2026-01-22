package com.zetra.econsig.persistence.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ConvenioHome</p>
 * <p>Description: Classe Home para a entidade Convenio</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConvenioHome extends AbstractEntityHome {

    public static Convenio findByPrimaryKey(String cnvCodigo) throws FindException {
        Convenio convenio = new Convenio();
        convenio.setCnvCodigo(cnvCodigo);
        return find(convenio, cnvCodigo);
    }

    public static List<Convenio> findByChave(String csaCodigo, String scvCodigo) throws FindException {
        String query = "FROM Convenio cnv WHERE cnv.consignataria.csaCodigo = :csaCodigo AND cnv.statusConvenio.scvCodigo = :scvCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("scvCodigo", scvCodigo);

        List<Convenio> result = findByQuery(query, parameters);
        if (result == null || result.isEmpty()) {
            result = new ArrayList<>();
        }
        return result;
    }

    public static Convenio findByChave(String svcCodigo, String csaCodigo, String orgCodigo) throws FindException {
        String query = "FROM Convenio cnv WHERE cnv.servico.svcCodigo = :svcCodigo AND cnv.consignataria.csaCodigo = :csaCodigo AND cnv.orgao.orgCodigo = :orgCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcCodigo", svcCodigo);
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("orgCodigo", orgCodigo);

        List<Convenio> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<Convenio> findByOrgSvc(String orgCodigo, String svcCodigo) throws FindException {
        String query = "FROM Convenio cnv WHERE cnv.orgao.orgCodigo = :orgCodigo AND cnv.servico.svcCodigo = :svcCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgCodigo", orgCodigo);
        parameters.put("svcCodigo", svcCodigo);

        return findByQuery(query, parameters);
    }

    public static List<Convenio> findByCodVerba(String cnvCodVerba) throws FindException {
        String query = "FROM Convenio cnv WHERE cnv.cnvCodVerba = :cnvCodVerba";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cnvCodVerba", cnvCodVerba);

        return findByQuery(query, parameters);
    }

    public static Convenio findByAdeCodigo(String adeCodigo) throws FindException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT cnv FROM AutDesconto ade ");
        query.append("INNER JOIN ade.verbaConvenio vco ");
        query.append("INNER JOIN vco.convenio cnv ");
        query.append("WHERE ade.adeCodigo = :adeCodigo ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        List<Convenio> result = findByQuery(query.toString(), parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Convenio create(String orgCodigo, String scvCodigo, String svcCodigo, String csaCodigo, String vceCodigo,
            String cnvIdentificador, String cnvDescricao, String vrbConvenio, String vrbConvenioRef, String vrbConvenioFerias, String vrbConvenioDirf) throws CreateException {

        Session session = SessionUtil.getSession();
        Convenio bean = new Convenio();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setCnvCodigo(objectId);
            bean.setOrgao(session.getReference(Orgao.class, orgCodigo));
            bean.setStatusConvenio(session.getReference(StatusConvenio.class, scvCodigo));
            bean.setServico(session.getReference(Servico.class, svcCodigo));
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setVerbaConsignante(session.getReference(VerbaConsignante.class, vceCodigo));
            bean.setCnvIdentificador(cnvIdentificador);
            bean.setCnvDescricao(cnvDescricao);
            bean.setCnvCodVerba(vrbConvenio);
            bean.setCnvCodVerbaRef(vrbConvenioRef);
            bean.setCnvCodVerbaFerias(vrbConvenioFerias);
            bean.setCnvCodVerbaDirf(vrbConvenioDirf);
            bean.setCnvDataIni(DateHelper.toSQLDate(DateHelper.getSystemDatetime()));
            //bean.setCnvDataFim(new java.sql.Date(0));
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
