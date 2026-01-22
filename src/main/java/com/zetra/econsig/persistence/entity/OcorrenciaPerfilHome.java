package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaPerfilHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaPerfil</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision:  $
 * $Date: 2020-12-07 10:58:52 -0300 (seg, 07 dez 2020) $
 */
public class OcorrenciaPerfilHome extends AbstractEntityHome {

    public static OcorrenciaPerfil findByPrimaryKey(String oprCodigo) throws FindException {
        OcorrenciaPerfil ocorrenciaPerfil = new OcorrenciaPerfil();
        ocorrenciaPerfil.setOprCodigo(oprCodigo);
        return find(ocorrenciaPerfil, oprCodigo);
    }

    public static OcorrenciaPerfil create(String perCodigo, String tocCodigo, String usuCodigo, String oprObs, String ipAcesso, String tmoCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        OcorrenciaPerfil bean = new OcorrenciaPerfil();
        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOprCodigo(objectId);
            bean.setPerfil(session.getReference(Perfil.class, perCodigo));
            bean.setTipoOcorrencia(session.getReference(TipoOcorrencia.class, tocCodigo));
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setOprData(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            bean.setOprObs(oprObs);
            bean.setOprIpAcesso(ipAcesso);
            if(!TextHelper.isNull(tmoCodigo)) {
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

    public static void deleteTodasOcorrenciaPerfilSelecionado(String perCodigo) throws FindException {
        Session session = SessionUtil.getSession();
        String queryDelete = "DELETE FROM OcorrenciaPerfil opr WHERE opr.perfil.perCodigo = :perCodigo";
        MutationQuery query = session.createMutationQuery(queryDelete);
        query.setParameter("perCodigo", perCodigo);
        query.executeUpdate();
        SessionUtil.closeSession(session);
    }

}
