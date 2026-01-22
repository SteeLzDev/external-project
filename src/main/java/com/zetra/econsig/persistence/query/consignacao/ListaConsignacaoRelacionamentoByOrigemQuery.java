package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoRelacionamentoByOrigemQuery</p>
 * <p>Description: Listagem de relacionamentos de consignação retornando o adeNumero</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoRelacionamentoByOrigemQuery extends HQuery {

    public String adeCodigoOrigem;
    public String sadCodigoDestino;
    public String tntCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT DISTINCT ade.adeNumero FROM AutDesconto ade ");
        corpoBuilder.append("JOIN ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad ");
        corpoBuilder.append("WHERE rad.autDescontoByAdeCodigoOrigem.adeCodigo = :adeCodigoOrigem ");
        corpoBuilder.append("AND rad.tipoNatureza.tntCodigo = :tntCodigo ");
        corpoBuilder.append("AND ade.statusAutorizacaoDesconto.sadCodigo in (:sadCodigoDestino) ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(adeCodigoOrigem)) {
            defineValorClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem, query);
        }

        if (!TextHelper.isNull(sadCodigoDestino)) {
            defineValorClausulaNomeada("sadCodigoDestino", sadCodigoDestino, query);
        }

        if (!TextHelper.isNull(tntCodigo)) {
            defineValorClausulaNomeada("tntCodigo", tntCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            Columns.ADE_NUMERO
        };
    }
}
