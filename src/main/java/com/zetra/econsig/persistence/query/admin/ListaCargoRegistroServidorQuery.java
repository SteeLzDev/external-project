package com.zetra.econsig.persistence.query.admin;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCargoRegistroServidorQuery</p>
 * <p>Description: Listagem de Cargos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCargoRegistroServidorQuery extends HQuery {

    public String crsCodigo;
    public String crsIdentificador;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "crs.crsCodigo, " +
                       "crs.crsIdentificador, " +
                       "crs.crsDescricao, " +
                       "crs.crsVlrDescMax " +
                       "from CargoRegistroServidor crs " +
                       "where 1=1 ";

        if (!TextHelper.isNull(crsCodigo)) {
            corpo += "and crs.crsCodigo " + criaClausulaNomeada("crsCodigo", crsCodigo);
        }
        if (!TextHelper.isNull(crsIdentificador)) {
            corpo += "and crs.crsIdentificador " + criaClausulaNomeada("crsIdentificador", crsIdentificador);
        }

        corpo += " order by crs.crsDescricao asc";

        Query<Object[]> query = instanciarQuery(session, corpo);
        if (!TextHelper.isNull(crsCodigo)) {
            defineValorClausulaNomeada("crsCodigo", crsCodigo, query);
        }
        if (!TextHelper.isNull(crsIdentificador)) {
            defineValorClausulaNomeada("crsIdentificador", crsIdentificador, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.CRS_CODIGO,
                Columns.CRS_IDENTIFICADOR,
                Columns.CRS_DESCRICAO,
                Columns.CRS_VLR_DESC_MAX
    	};
    }
}
