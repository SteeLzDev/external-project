package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaSolicitacaoLeilaoEncerradoQuery</p>
 * <p>Description: Lista solicitação de proposta de leilões encerrados.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSolicitacaoLeilaoEncerradoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO.getCodigo();

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT soa.soaCodigo, soa.statusSolicitacao.ssoCodigo, soa.autDesconto.adeCodigo");
        corpoBuilder.append(" FROM SolicitacaoAutorizacao soa ");
        corpoBuilder.append(" WHERE soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));
        corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigoPendente", StatusSolicitacaoEnum.PENDENTE.getCodigo()));
        corpoBuilder.append(" AND soa.soaDataValidade < current_timestamp() ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tisCodigo", tisCodigo, query);
        defineValorClausulaNomeada("ssoCodigoPendente", StatusSolicitacaoEnum.PENDENTE.getCodigo(), query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SOA_CODIGO,
                Columns.SSO_CODIGO,
                Columns.ADE_CODIGO
        };
    }
}
