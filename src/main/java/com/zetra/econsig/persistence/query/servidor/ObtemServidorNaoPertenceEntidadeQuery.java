package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemServidorNaoPertenceEntidadeQuery</p>
 * <p>Description: Retorna dentre os registros servidores informados
 * por parâmetro, aqueles que não pertencem à entidade EST/ORG.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemServidorNaoPertenceEntidadeQuery extends HQuery {

    public List<String> rseCodigo;
    public String tipoEntidade;
    public String codigoEntidade;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT rse.rseCodigo");
        corpoBuilder.append(" FROM RegistroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" WHERE rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (tipoEntidade.equalsIgnoreCase("EST")) {
            corpoBuilder.append(" AND est.estCodigo != :codigoEntidade");
        } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
            corpoBuilder.append(" AND org.orgCodigo != :codigoEntidade");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO
        };
    }
}
