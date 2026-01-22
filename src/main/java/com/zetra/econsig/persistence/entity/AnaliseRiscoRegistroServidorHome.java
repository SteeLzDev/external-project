package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: AnaliseRiscoRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade AnaliseRiscoRegistroSer</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AnaliseRiscoRegistroServidorHome extends AbstractEntityHome {

    public static AnaliseRiscoRegistroSer findByRseCsa(String rseCodigo, String csaCodigo) throws FindException {
        String query = "FROM AnaliseRiscoRegistroSer arr WHERE arr.registroServidor.rseCodigo = :rseCodigo AND arr.consignataria.csaCodigo = :csaCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);
        parameters.put("csaCodigo", csaCodigo);

        List<AnaliseRiscoRegistroSer> resultado = findByQuery(query, parameters);
        if (resultado != null && resultado.size() > 0) {
            return resultado.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static AnaliseRiscoRegistroSer create(String usuCodigo, String rseCodigo, String csaCodigo, String arrRisco) throws CreateException {
        Session session = SessionUtil.getSession();
        AnaliseRiscoRegistroSer bean = new AnaliseRiscoRegistroSer();

        try {
            String objectId = DBHelper.getNextId();
            bean.setArrCodigo(objectId);
            bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setArrRisco(arrRisco);
            bean.setArrData(DateHelper.getSystemDatetime());

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM AnaliseRiscoRegistroSer arr WHERE arr.registroServidor.rseCodigo = :rseCodigo ");

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

}
