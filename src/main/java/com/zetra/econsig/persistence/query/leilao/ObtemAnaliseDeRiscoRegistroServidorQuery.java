package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemAnaliseDeRiscoRegistroServidorQuery</p>
 * <p>Description: Obtem avaliação de risco de um registro servidor por uma csa</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 */

public class ObtemAnaliseDeRiscoRegistroServidorQuery extends HQuery {

    public String csaCodigo;
    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT arr.arrRisco, arr.usuario.usuCodigo, arr.registroServidor.rseCodigo, arr.arrData ");
        corpoBuilder.append("FROM AnaliseRiscoRegistroSer arr ");
        corpoBuilder.append("WHERE arr.consignataria.csaCodigo = :csaCodigo ");
        corpoBuilder.append("AND arr.registroServidor.rseCodigo = :rseCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;

    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.ARR_RISCO,
                Columns.ARR_USU_CODIGO,
                Columns.ARR_RSE_CODIGO,
                Columns.ARR_DATA};
    }

}
