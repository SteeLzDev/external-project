package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;


/**
 * <p>Title: ObtemTotalConsignacaoAtivaCsaQuery</p>
 * <p>Description: Lista total de contratos ativos de uma csa</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalConsignacaoAtivaCsaQuery extends HQuery  {

    public String csaCodigo;
    public String adeIdentificador;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT count(*) ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("WHERE ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
        corpoBuilder.append("AND cnv.consignataria.csaCodigo = :csaCodigo ");

        if (!TextHelper.isNull(adeIdentificador)) {
            corpoBuilder.append("AND ade.adeIdentificador = :adeIdentificador ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);


        if (!TextHelper.isNull(adeIdentificador)) {
            defineValorClausulaNomeada("adeIdentificador", adeIdentificador, query);
        }

        return query;

    }



}
