package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.persistence.SessionUtil;

public class ImagemServidorHome extends AbstractEntityHome {

    public static ImagemServidor create(String cpf, String nomeArquivo) throws CreateException {
        Session session =  SessionUtil.getSession();
        ImagemServidor bean = new ImagemServidor();

        try{
            bean.setCpf(cpf);
            bean.setNomeArquivo(nomeArquivo);
            create(bean, session);

        } catch (CreateException ex){
            throw new CreateException(ex);
        }
        finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static boolean updateImagemServidor(String cpf, String nomeArquivo) throws UpdateException{
        Session session = SessionUtil.getSession();

        try{
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE ImagemServidor ims ");
            sql.append(" SET ims.imsCpf = :imsCpf, ");
            sql.append(" ims.imsNomeArquivo = :imsNomeArquivo");
            sql.append(" WHERE ims.imsCpf = :imsCpf");

            MutationQuery queryUpdate = session.createMutationQuery(sql.toString());
            queryUpdate.setParameter("imsCpf", cpf);
            queryUpdate.setParameter("imsNomeArquivo", nomeArquivo);
            queryUpdate.executeUpdate();

            return true;
        } catch (Exception ex){
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

    }

}
