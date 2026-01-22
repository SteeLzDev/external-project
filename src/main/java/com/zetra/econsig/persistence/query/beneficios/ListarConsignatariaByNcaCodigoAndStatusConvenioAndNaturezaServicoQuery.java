package com.zetra.econsig.persistence.query.beneficios;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServicoQuery</p>
 * <p>Description: Query que tras a consignataria pela a natureza do servico.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServicoQuery extends HQuery {

    public String ncaCodigo;

    public String scvCodigo;

    public String nseCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT csa.csaCodigo,");
        sql.append(" csa.csaNome");
        sql.append(" FROM Consignataria csa");
        sql.append(" INNER JOIN csa.naturezaConsignataria nca");
        sql.append(" INNER JOIN csa.convenioSet cnv");
        sql.append(" INNER JOIN cnv.servico svc");
        sql.append(" WHERE 1=1");

        if (!TextHelper.isNull(ncaCodigo)) {
            sql.append(" AND nca.ncaCodigo = :ncaCodigo");
        }

        if (!TextHelper.isNull(scvCodigo)) {
            sql.append(" AND cnv.statusConvenio.scvCodigo = :scvCodigo");
        }

        if (!TextHelper.isNull(nseCodigo)) {
            sql.append(" AND svc.naturezaServico.nseCodigo = :nseCodigo");
        }

        sql.append(" group by csa.csaCodigo");

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(ncaCodigo)) {
            defineValorClausulaNomeada("ncaCodigo", ncaCodigo, query);
        }

        if (!TextHelper.isNull(scvCodigo)) {
            defineValorClausulaNomeada("scvCodigo", scvCodigo, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_NOME
        };
    }
}
