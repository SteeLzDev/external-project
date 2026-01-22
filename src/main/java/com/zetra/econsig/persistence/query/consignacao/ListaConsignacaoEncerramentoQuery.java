package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoEncerramentoQuery</p>
 * <p>Description: Listagem de Consignações de um registro servidor para encerramento</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoEncerramentoQuery extends HQuery {

    private final String rseCodigo;

    public ListaConsignacaoEncerramentoQuery(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ade.adeCodigo, ade.statusAutorizacaoDesconto.sadCodigo ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("where ade.registroServidor.rseCodigo = :rseCodigo ");
        corpoBuilder.append("and ade.statusAutorizacaoDesconto.sadCodigo not in (:sadCodigo) ");
        corpoBuilder.append("order by ade.adeData desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_INATIVOS, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.SAD_CODIGO
        };
    }
}
