package com.zetra.econsig.persistence.entity;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GoogleAuthenticatorHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: UsuarioChaveSessao</p>
 * <p>Description: Home Entidade UsuarioChaveSessao.</p>
 * <p>Copyright: Copyright (c) 2002-2003</p>
 * <p>Company: ZetraSoft</p>
* $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioChaveSessaoHome extends AbstractEntityHome {

    public static UsuarioChaveSessao findByPrimaryKey(String usuCodigo) throws FindException {
        UsuarioChaveSessao usuarioChaveSessao = new UsuarioChaveSessao();
        usuarioChaveSessao.setUsuCodigo(usuCodigo);
        return find(usuarioChaveSessao, usuCodigo);
    }

    public static UsuarioChaveSessao findByPrimaryKeyForUpdate(String usuCodigo) throws FindException {
        UsuarioChaveSessao usuarioChaveSessao = new UsuarioChaveSessao();
        usuarioChaveSessao.setUsuCodigo(usuCodigo);
        return find(usuarioChaveSessao, usuCodigo, true);
    }

    public static UsuarioChaveSessao findByChaveSessao(String chaveSessao) throws FindException {
        String query = "FROM UsuarioChaveSessao usu WHERE ucsToken = :chaveSessao";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("chaveSessao", chaveSessao);

        List<UsuarioChaveSessao> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }



    public static UsuarioChaveSessao create(String usuCodigo) throws CreateException {
        Session session = SessionUtil.getSession();
        UsuarioChaveSessao bean = new UsuarioChaveSessao();
        try {
            String token = GoogleAuthenticatorHelper.generateSecretKey();
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setUsuCodigo(usuCodigo);
            bean.setUcsDataCriacao(DateHelper.getSystemDatetime());
            bean.setUcsToken(token);

            create(bean, session);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CreateException(e);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void delete(String usuCodigo) throws RemoveException, FindException {
    	remove(findByPrimaryKeyForUpdate(usuCodigo));
    }

    public static String updateToken(String usuCodigo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            String token = GoogleAuthenticatorHelper.generateSecretKey();
            String hql = "UPDATE UsuarioChaveSessao set ucsToken = :ucsToken, ucsDataCriacao =:ucsDataCriacao WHERE usuCodigo = :usuCodigo";
            MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("ucsToken", token);
            queryUpdate.setParameter("usuCodigo", usuCodigo);
            queryUpdate.setParameter("ucsDataCriacao", DateHelper.getSystemDatetime());
            queryUpdate.executeUpdate();
            session.flush();
            return token;
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

}
