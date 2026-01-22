package com.zetra.econsig.persistence.query.prazo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPrazoConsignatariaQuery</p>
 * <p>Description: Lista todos os prazos habilitados para as consignatárias e serviços</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPrazoConsignatariaQuery extends HQuery {

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "prz.servico.svcCodigo," +
                "pzc.consignataria.csaCodigo," +
                "prz.przVlr";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM Prazo prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" WHERE prz.przAtivo ").append(criaClausulaNomeada("przAtivo", CodedValues.STS_ATIVO));
        corpoBuilder.append(" and pzc.przCsaAtivo ").append(criaClausulaNomeada("przCsaAtivo", CodedValues.STS_ATIVO));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("przAtivo", CodedValues.STS_ATIVO, query);
        defineValorClausulaNomeada("przCsaAtivo", CodedValues.STS_ATIVO, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRZ_SVC_CODIGO,
                Columns.PZC_CSA_CODIGO,
                Columns.PRZ_VLR
        };
    }
}
