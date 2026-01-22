package com.zetra.econsig.persistence.query.proposta;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaSolicitacaoPropostaExpiradaQuery</p>
 * <p>Description: Lista solicitação de proposta de pagamento de dívida
 * que estão pendentes, porém já deveriam estar expiradas.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSolicitacaoPropostaExpiradaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_PARCELAMENTO_DIVIDA.getCodigo();
        String ssoCodigo = StatusSolicitacaoEnum.PENDENTE.getCodigo();

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT soa.soaCodigo");
        corpoBuilder.append(" FROM SolicitacaoAutorizacao soa ");
        corpoBuilder.append(" WHERE soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));
        corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigo", ssoCodigo));
        corpoBuilder.append(" AND soa.soaDataValidade < CURRENT_DATE()");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tisCodigo", tisCodigo, query);
        defineValorClausulaNomeada("ssoCodigo", ssoCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SOA_CODIGO
        };
    }
}
