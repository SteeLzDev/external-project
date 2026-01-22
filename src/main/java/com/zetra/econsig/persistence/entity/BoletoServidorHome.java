package com.zetra.econsig.persistence.entity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: BoletoServidorHome</p>
 * <p>Description: Classe Home para a entidade BoletoServidor</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BoletoServidorHome extends AbstractEntityHome {

    public static BoletoServidor findByPrimaryKey(String bosCodigo) throws FindException {
        BoletoServidor boletoServidor = new BoletoServidor();
        boletoServidor.setBosCodigo(bosCodigo);
        return find(boletoServidor, bosCodigo);
    }

    public static List<BoletoServidor> findBySerCodigo(String serCodigo) throws FindException {
        String query = "FROM BoletoServidor bos WHERE bos.servidor.serCodigo = :serCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCodigo", serCodigo);

        return findByQuery(query, parameters);
    }

    public static BoletoServidor create(String serCodigo, String csaCodigo, String usuCodigo, String arqCodigo) throws CreateException {
        Session session = SessionUtil.getSession();

        try {
            BoletoServidor bean = new BoletoServidor();
            Timestamp agora = new Timestamp(Calendar.getInstance().getTimeInMillis());

            bean.setBosCodigo(DBHelper.getNextId());
            if (!TextHelper.isNull(serCodigo)) {
                bean.setServidor(session.getReference(Servidor.class, serCodigo));

            }
            if (!TextHelper.isNull(csaCodigo)) {
                bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            }
            if (!TextHelper.isNull(usuCodigo)) {
                bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            }
            if (!TextHelper.isNull(arqCodigo)) {
                bean.setArquivo(session.getReference(Arquivo.class, arqCodigo));
            }
            bean.setBosDataUpload(agora);

            create(bean);
            return bean;
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }

    public static void removeBySer(String serCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM BoletoServidor bos WHERE bos.servidor.serCodigo = :serCodigo ");

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
