package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioCodVerbaFeriasQuery</p>
 * <p>Description: Lista de convênios ativos com código de verba de férias diferente de nulo.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioCodVerbaFeriasQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String scvAtivo = CodedValues.SCV_ATIVO;

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select distinct ");
        corpoBuilder.append("cnv.cnvCodVerbaFerias, cnv.cnvCodVerba ");
        corpoBuilder.append("from Convenio cnv ");
        corpoBuilder.append("where cnv.statusConvenio.scvCodigo ").append(criaClausulaNomeada("scvAtivo", scvAtivo));
        corpoBuilder.append(" and cnv.cnvCodVerbaFerias ").append(criaClausulaNomeada("", CodedValues.IS_NOT_NULL_KEY));
        corpoBuilder.append(" order by cnv.cnvCodVerbaFerias ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("scvAtivo", scvAtivo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_COD_VERBA_FERIAS,
                Columns.CNV_COD_VERBA
        };
    }
}
