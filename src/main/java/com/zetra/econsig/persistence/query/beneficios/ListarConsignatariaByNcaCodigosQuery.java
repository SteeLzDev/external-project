package com.zetra.econsig.persistence.query.beneficios;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServicoQuery</p>
 * <p>Description: Query que tras a consignataria pela a natureza do servico.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author: igor.lucas $
 * $Revision: 26246 $
 * $Date: 2019-02-14 09:27:49 -0200 (Qui, 14 fev 2019) $
 */
public class ListarConsignatariaByNcaCodigosQuery extends HQuery {

    public List<String> ncaCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT csa.csaCodigo, csa.csaIdentificador, csa.csaNome");
        sql.append(" FROM Consignataria csa");
        sql.append(" INNER JOIN csa.naturezaConsignataria nca");
        sql.append(" WHERE 1=1");

        if (ncaCodigos != null && !ncaCodigos.isEmpty()) {
            sql.append(" AND nca.ncaCodigo ").append(criaClausulaNomeada("ncaCodigos", ncaCodigos));
        }

        sql.append(" group by csa.csaCodigo, csa.csaIdentificador, csa.csaNome");

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (ncaCodigos != null && !ncaCodigos.isEmpty()) {
            defineValorClausulaNomeada("ncaCodigos", ncaCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME
        };
    }
}
