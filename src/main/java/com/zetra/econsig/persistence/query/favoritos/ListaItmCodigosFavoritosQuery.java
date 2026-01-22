package com.zetra.econsig.persistence.query.favoritos;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaItmCodigosFavoritosQuery</p>
 * <p>Description: Lista os favoritos da tb_item_menu_favorito retornando seu itmCodigo.</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: ricardo.magno $
 * $Revision: $
 * $Date: $
 */
public class ListaItmCodigosFavoritosQuery extends HQuery {

    public String usuCodigo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        corpo.append(" select imf.itmCodigo");
        corpo.append(" from ItemMenuFavorito imf ");
        corpo.append(" WHERE imf.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        corpo.append(" ORDER BY imf.imfSequencia ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (usuCodigo != null) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        return query;

    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.IMF_USU_CODIGO,
                Columns.IMF_SEQUENCIA
        };
    }
}
