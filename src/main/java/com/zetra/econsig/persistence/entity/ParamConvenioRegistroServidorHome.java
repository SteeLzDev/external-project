package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ParamConvenioRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade ParamConvenioRegistroSer</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamConvenioRegistroServidorHome extends AbstractEntityHome {

    public static ParamConvenioRegistroSer findByPrimaryKey(ParamConvenioRegistroSerId pk) throws FindException {
        ParamConvenioRegistroSer paramCnvRse = new ParamConvenioRegistroSer();
        paramCnvRse.setId(pk);
        return find(paramCnvRse, pk);
    }

    public static List<ParamConvenioRegistroSer> findByRseCodigo(String rseCodigo) throws FindException {
        String query = "FROM ParamConvenioRegistroSer pcr WHERE pcr.id.rseCodigo = :rseCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        return findByQuery(query, parameters);
    }

    public static ParamConvenioRegistroSer create(String tpsCodigo, String rseCodigo, String cnvCodigo, String pcrVlr, String pcrVlrSer, String pcrVlrCsa, String pcrVlrCse, String pcrObs) throws CreateException {
        ParamConvenioRegistroSer bean = new ParamConvenioRegistroSer();
        ParamConvenioRegistroSerId id = new ParamConvenioRegistroSerId();
        id.setCnvCodigo(cnvCodigo);
        id.setRseCodigo(rseCodigo);
        id.setTpsCodigo(tpsCodigo);
        bean.setId(id);
        bean.setPcrVlr(pcrVlr);
        bean.setPcrVlrSer(pcrVlrSer);
        bean.setPcrVlrCsa(pcrVlrCsa);
        bean.setPcrVlrCse(pcrVlrCse);
        bean.setPcrObs(pcrObs);
        bean.setPcrDataCadastro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        create(bean);
        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM ParamConvenioRegistroSer pcr WHERE pcr.registroServidor.rseCodigo = :rseCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("rseCodigo", rseCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static List<ParamConvenioRegistroSer> findByCpfCsa(String cpf, AcessoSistema responsavel) throws FindException {

        String query = "FROM ParamConvenioRegistroSer tpc INNER JOIN RegistroServidor trs ON tpc.rseCodigo = trs.rseCodigo INNER JOIN Convenio tc ON tc.cnvCodigo = tpc.cnvCodigo INNER JOIN Servidor ts ON ts.serCodigo = trs.serCodigo WHERE ts.serCpf = :cpf AND tc.csaCodigo = :csaCodigo AND EXISTS ( SELECT 1 FROM OcorrenciaRegistroSer tor WHERE tor.rseCodigo = tpc.rseCodigo AND tor.tocCodigo = :tocCodigo )";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cpf", cpf);
        parameters.put("csaCodigo", responsavel.getCodigoEntidade());
        parameters.put("tocCodigo", CodedValues.TOC_BLOQ_VARIACAO_PERCENTUAL_MARGEM_CSA);

        return findByQuery(query, parameters);

    }

}
