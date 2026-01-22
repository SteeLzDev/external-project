package com.zetra.econsig.persistence.entity;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaDadosServidorHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaDadosServidor</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaDadosServidorHome extends AbstractEntityHome {

    public static OcorrenciaDadosServidor findByPrimaryKey(String odsCodigo) throws FindException {
    	OcorrenciaDadosServidor ocorrenciaServidor = new OcorrenciaDadosServidor();
    	ocorrenciaServidor.setOdsCodigo(odsCodigo);
        return find(ocorrenciaServidor, odsCodigo);
    }

    public static OcorrenciaDadosServidor create(String serCodigo, String tocCodigo, String usuCodigo, String tdaCodigo, Date odsData, String odsObs, String odsValorAnt, String odsValorNovo, String odsIpAcesso) throws CreateException {
        Session session = SessionUtil.getSession();
        OcorrenciaDadosServidor bean = new OcorrenciaDadosServidor();

        try {
            String objectId = DBHelper.getNextId();
            bean.setOdsCodigo(objectId);
            bean.setServidor(session.getReference(Servidor.class, serCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setTipoDadoAdicional(session.getReference(TipoDadoAdicional.class, tdaCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            if (odsData == null) {
                bean.setOdsData(Calendar.getInstance().getTime());
            } else {
                bean.setOdsData(odsData);
            }
            bean.setOdsObs(odsObs);
            bean.setOdsValorAnt(odsValorAnt);
            bean.setOdsValorNovo(odsValorNovo);
            bean.setOdsIpAcesso(odsIpAcesso);

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static void removeBySer(String serCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM OcorrenciaDadosServidor ods WHERE ods.servidor.serCodigo = :serCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("serCodigo", serCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
