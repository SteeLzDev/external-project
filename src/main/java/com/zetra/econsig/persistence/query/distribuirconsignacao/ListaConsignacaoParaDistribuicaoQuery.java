package com.zetra.econsig.persistence.query.distribuirconsignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoParaDistribuicaoQuery</p>
 * <p>Description: Listagem de consignações para distribuição entre serviços</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoParaDistribuicaoQuery extends HQuery {

    public String rseCodigo;
    public String vcoCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo, ade.adeNumero, ade.adeData, ade.adeVlr, ade.adePrazo, ser.serNome, ser.serCpf, rse.rseMatricula ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");

        corpoBuilder.append("WHERE ade.statusAutorizacaoDesconto.sadCodigo NOT IN (:sadCodigos) ");
        corpoBuilder.append("AND ade.registroServidor.rseCodigo = :rseCodigo ");
        corpoBuilder.append("AND ade.verbaConvenio.vcoCodigo = :vcoCodigo ");
        corpoBuilder.append("ORDER BY ade.adeData");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("sadCodigos", CodedValues.SAD_CODIGOS_INATIVOS, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("vcoCodigo", vcoCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_DATA,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.RSE_MATRICULA
        };
    }
}
