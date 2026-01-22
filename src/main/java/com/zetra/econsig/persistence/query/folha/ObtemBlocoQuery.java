package com.zetra.econsig.persistence.query.folha;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemBlocosProcessamentoRegistroSerQuery</p>
 * <p>Description: Obtém os blocos de processamento agrupados de um mesmo registro servidor</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemBlocoQuery extends HQuery  {

    private final Integer bprCodigo;

    public ObtemBlocoQuery(Integer bprCodigo) {
        this.bprCodigo = bprCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ");
        corpoBuilder.append("  bpr.registroServidor.rseCodigo, ");
        corpoBuilder.append("  bpr.estIdentificador, ");
        corpoBuilder.append("  bpr.orgIdentificador, ");
        corpoBuilder.append("  bpr.rseMatricula ");
        corpoBuilder.append("FROM BlocoProcessamento bpr ");
        corpoBuilder.append("WHERE bpr.bprCodigo = :bprCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("bprCodigo", bprCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.ORG_IDENTIFICADOR,
                Columns.RSE_MATRICULA,
        };
    }
}
