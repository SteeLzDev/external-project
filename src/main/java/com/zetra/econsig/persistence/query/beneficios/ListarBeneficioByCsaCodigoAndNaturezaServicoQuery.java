package com.zetra.econsig.persistence.query.beneficios;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServicoQuery</p>
 * <p>Description: Query que busca o relaciona de beneficio por serviço.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBeneficioByCsaCodigoAndNaturezaServicoQuery extends HQuery {

    public String csaCodigo;
    public String corCodigo;
    public String nseCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ben.benCodigo,");
        sql.append(" ben.benDescricao");
        sql.append(" FROM Beneficio ben");
        sql.append(" WHERE 1 = 1");

        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" AND ben.consignataria.csaCodigo = :csaCodigo");
        }

        if (!TextHelper.isNull(corCodigo)) {
            sql.append(" AND ben.correspondente.corCodigo = :corCodigo");
        }

        if (!TextHelper.isNull(nseCodigo)) {
            sql.append(" AND ben.naturezaServico.nseCodigo = :nseCodigo");
        }

        sql.append(" order by ben.benDescricao ");

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BEN_CODIGO,
                Columns.BEN_DESCRICAO,
        };
    }
}
