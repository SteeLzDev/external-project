package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.persistence.SessionUtil;

public class CampoUsuarioHome extends AbstractEntityHome{


	public static CampoUsuario findByPrimaryKey(CampoUsuarioId pk) throws FindException {
		CampoUsuario campoUsuario = new CampoUsuario();
		campoUsuario.setId(pk);
		return find(campoUsuario, pk);
	}

	public static List<CampoUsuario> findByUsuario(String usuCodigo) throws FindException {
		String query = "FROM CampoUsuario AS cau WHERE cau.id.usuCodigo = :usuCodigo";

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("usuCodigo", usuCodigo);

		return findByQuery(query, parameters);
	}

	public static List<CampoUsuario> findByChave(String cauChave) throws FindException{
		String query = "FROM CampoUsuario AS cau WHERE cau.id.cauChave = :cauChave";

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("cauChave", cauChave);

		return findByQuery(query, parameters);
	}

	 public static void updateCauValor(String usuCodigo,String cauChave, String cauValor) throws UpdateException {
	        Session session = SessionUtil.getSession();
	        try {
	            String hql = "UPDATE CampoUsuario set cauValor = :cauValor WHERE usuCodigo = :usuCodigo and cauChave = :cauChave";
	            MutationQuery queryUpdate = session.createMutationQuery(hql);
	            queryUpdate.setParameter("usuCodigo", usuCodigo);
	            queryUpdate.setParameter("cauChave", cauChave);
	            queryUpdate.setParameter("cauValor", cauValor);
	            queryUpdate.executeUpdate();
	            session.flush();
	        } catch (Exception ex) {
	            throw new UpdateException(ex);
	        } finally {
	            SessionUtil.closeSession(session);
	        }
	    }

	public static CampoUsuario create(String usuCodigo,String cauChave, String cauValor) throws CreateException {
		CampoUsuario bean = new CampoUsuario();

		CampoUsuarioId id = new CampoUsuarioId();
		id.setUsuCodigo(usuCodigo);
		id.setCauChave(cauChave);
		bean.setId(id);
		bean.setCauValor(cauValor);
		create(bean);
		return bean;
	}
}
