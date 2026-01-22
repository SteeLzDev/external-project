package com.zetra.econsig.persistence.entity;

import java.util.List;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.query.favoritos.ListaItmCodigosFavoritosQuery;

/**
 * <p>Title: ItemMenuFavoritoHome</p>
 * <p>Description: Classe Home para a entidade ItemMenuFavorito</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ItemMenuFavoritoHome extends AbstractEntityHome {

    public static ItemMenuFavorito findByPrimaryKey(String usuCodigo, String itmCodigo) throws FindException {
        ItemMenuFavoritoId id = new ItemMenuFavoritoId(usuCodigo, itmCodigo);
        ItemMenuFavorito itemMenuFavorito = new ItemMenuFavorito();
        itemMenuFavorito.setId(id);
        return find(itemMenuFavorito, id);
    }

    public static ItemMenuFavorito findByPrimaryKeyForUpdate(String usuCodigo, String itmCodigo) throws FindException {
        ItemMenuFavoritoId id = new ItemMenuFavoritoId(usuCodigo, itmCodigo);
        ItemMenuFavorito itemMenuFavorito = new ItemMenuFavorito();
        itemMenuFavorito.setId(id);
        return find(itemMenuFavorito, id, true);
    }

    public static ItemMenuFavorito create(String usuCodigo, String itmCodigo, Short imfSequencia) throws CreateException {
        Session session = SessionUtil.getSession();
        ItemMenuFavorito bean = new ItemMenuFavorito();
        try {
            ItemMenuFavoritoId id = new ItemMenuFavoritoId(usuCodigo, itmCodigo);
            bean.setId(id);
            bean.setImfData(DateHelper.getSystemDatetime());
            bean.setImfSequencia(imfSequencia);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static void updateMenuFavoritoDashBoardByUsuCodigo(String usuCodigo, String itmCodigo, Short imfSequencia) throws UpdateException {
    	ItemMenuFavorito bean = null;
    	try {
    		bean = findByPrimaryKeyForUpdate(usuCodigo, itmCodigo);
    		if (bean != null) {
    			bean.setImfSequencia(imfSequencia);
    			update(bean);
    		}
    	} catch (FindException | UpdateException ex) {
    		throw new UpdateException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
    	}

    }

    public static List<Object> selectMenuFavoritoDashBoardByUsuCodigo(String usuCodigo) throws HQueryException {
		 ListaItmCodigosFavoritosQuery query = new ListaItmCodigosFavoritosQuery();
         query.usuCodigo = usuCodigo;

         List<Object> itmCodigos = query.executarLista();

         return itmCodigos;

    }
}
