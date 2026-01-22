package com.zetra.econsig.persistence.query.servico;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaTipoNaturezaEditavelServicoQuery extends HQuery {

    public String nseCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(" tnt.tntCodigo,");
        sql.append(" tnt.tntDescricao,");
        sql.append(" tnt.tntCseAltera,");
        sql.append(" tnt.tntSupAltera");
        sql.append(" FROM NaturezaEditavelNse nen");
        sql.append(" INNER JOIN nen.tipoNatureza tnt");
        sql.append(" WHERE 1 = 1");
        // Filtrando o nse_codigo
        sql.append(" AND nen.nseCodigo").append(criaClausulaNomeada("nseCodigo", nseCodigo));

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        defineValorClausulaNomeada("nseCodigo", nseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.TNT_CODIGO,
                Columns.TNT_DESCRICAO,
                Columns.TNT_CSE_ALTERA,
                Columns.TNT_SUP_ALTERA
        };
    }
}
