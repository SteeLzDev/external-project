package com.zetra.econsig.persistence.query.margem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaHistoricoVariacaoMargemBrutaQuery</p>
 * <p>Description: Recupera a variação da margem bruta em um dado período para um servidor.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaHistoricoVariacaoMargemBrutaQuery extends HQuery {

    public boolean count = false;
    public String rseCodigo;
    public Short marCodigo;
    public int qtdeMesesPesquisa = 25;

    public ListaHistoricoVariacaoMargemBrutaQuery(int qtdeMesesPesquisa) {
        this.qtdeMesesPesquisa = qtdeMesesPesquisa;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = null;

        corpo = "SELECT " +
                "hma.hmaData, " +
                "hma.hmaMargemFolha "
                ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM HistoricoMargemFolha hma ");
        corpoBuilder.append(" WHERE hma.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND hma.margem.marCodigo ").append(criaClausulaNomeada("marCodigo", marCodigo));
        corpoBuilder.append(" AND hma.hmaData >= add_month(current_date(), -1 * :qtdeMesesPesquisa) ");
        corpoBuilder.append(" ORDER BY hma.hmaData ASC ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("marCodigo", marCodigo, query);
        defineValorClausulaNomeada("qtdeMesesPesquisa", qtdeMesesPesquisa, query);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HMA_DATA,
                Columns.HMA_MARGEM_FOLHA
        };
    }
}
