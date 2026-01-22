package com.zetra.econsig.persistence.query.termoAdesao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTermosAdesaoPorUsuCodigoQuery</p>
 * <p>Description: Retornar os termos de adesão de um usuário específico.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTermosAdesaoPorUsuCodigoQuery extends HQuery {

    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder query = new StringBuilder();
        query.append(" SELECT tad.tadCodigo, tad.tadTitulo, max(ltu.ltuData) as max_data ");
        query.append(" FROM TermoAdesao tad ");
        query.append(" LEFT JOIN tad.leituraTermoUsuarioSet ltu ");
        query.append(" WHERE ltu.usuCodigo = :usuCodigo ");
        query.append(" OR tad.tadPermiteLerDepois = '").append(CodedValues.TPC_SIM).append("'");
        query.append(" GROUP BY tad.tadCodigo, tad.tadTitulo ");
        query.append(" ORDER BY max_data ASC ");

        final Query<Object[]> bean = instanciarQuery(session, query.toString());

        defineValorClausulaNomeada("usuCodigo", responsavel.getUsuCodigo(), bean);

        return bean;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TAD_CODIGO,
                Columns.TAD_TITULO,
                Columns.LTU_DATA
        };
    }
}
