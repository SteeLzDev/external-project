package com.zetra.econsig.persistence.query.indice;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaIndiceQuery</p>
 * <p>Description: Lista de indices.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaIndiceQuery extends HQuery {

    public boolean count = false;
    public String svcCodigo;
    public String csaCodigo;
    public String indCodigo;
    public String indDescricao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String fields = "ind.indCodigo, ind.indDescricao, ind.svcCodigo, ind.csaCodigo " ;
        String corpo = "";

        if (!count) {
            corpo = "select " + fields;
        } else {
            corpo = "select count(*) as total ";
        }
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Indice ind ");
        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and ind.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and ind.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(indCodigo)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("ind.indCodigo", "indCodigo", indCodigo));
        }

        if (!TextHelper.isNull(indDescricao)) {
            corpoBuilder.append(" and ").append(criaClausulaNomeada("ind.indDescricao", "indDescricao", indDescricao));
        }

        if (!count) {
            corpoBuilder.append(" order by ind.indCodigo ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(indCodigo)) {
            defineValorClausulaNomeada("indCodigo", indCodigo, query);
        }

        if (!TextHelper.isNull(indDescricao)) {
            defineValorClausulaNomeada("indDescricao", indDescricao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.IND_CODIGO,
                Columns.IND_DESCRICAO,
                Columns.IND_SVC_CODIGO,
                Columns.IND_CSA_CODIGO
        };
    }

}
