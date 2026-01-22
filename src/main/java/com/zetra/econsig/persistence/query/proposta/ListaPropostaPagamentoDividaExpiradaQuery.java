package com.zetra.econsig.persistence.query.proposta;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusPropostaEnum;

/**
 * <p>Title: ListaPropostaPagamentoDividaExpiradaQuery</p>
 * <p>Description: Lista propostas de pagamento de dívida
 * que estão pendentes, porém já deveriam estar expiradas.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPropostaPagamentoDividaExpiradaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String stpCodigo = StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo();

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ppd.ppdCodigo");
        corpoBuilder.append(" FROM PropostaPagamentoDivida ppd ");
        corpoBuilder.append(" WHERE ppd.statusProposta.stpCodigo ").append(criaClausulaNomeada("stpCodigo", stpCodigo));
        corpoBuilder.append(" AND ppd.ppdDataValidade < CURRENT_DATE()");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("stpCodigo", stpCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PPD_CODIGO
        };
    }
}
