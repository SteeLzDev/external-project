package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCsaPenalidadeExpiradaQuery</p>
 * <p>Description: Lista as consignatárias que devem ser desbloqueadas pela expiração da penalidade.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsaPenalidadeExpiradaQuery extends HQuery {

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        Short csaInativo = CodedValues.STS_INATIVO;
        String tocCodigo = CodedValues.TOC_BLOQ_CSA_REGISTRO_PENALIDADE_PRAZO;

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select csa.csaCodigo ");
        corpoBuilder.append(" from Consignataria csa ");
        corpoBuilder.append(" inner join csa.ocorrenciaConsignatariaSet occ ");
        corpoBuilder.append(" inner join occ.tipoPenalidade tpe ");
        corpoBuilder.append(" where csa.csaAtivo ").append(criaClausulaNomeada("csaInativo", csaInativo));
        corpoBuilder.append(" and occ.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
        corpoBuilder.append(" and tpe.tpePrazoPenalidade is not null ");
        corpoBuilder.append(" and to_days(current_date()) - to_days(occ.occData) >= tpe.tpePrazoPenalidade");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaInativo", csaInativo, query);
        defineValorClausulaNomeada("tocCodigo", tocCodigo, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.CSA_CODIGO,
        };
    }
}
