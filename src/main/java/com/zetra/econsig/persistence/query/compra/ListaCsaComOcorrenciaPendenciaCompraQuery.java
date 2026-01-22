package com.zetra.econsig.persistence.query.compra;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCsaComOcorrenciaPendenciaCompraQuery</p>
 * <p>Description: Listagem de Consignatárias que possuem ocorrência de pendência de compra</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsaComOcorrenciaPendenciaCompraQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select csa.csaCodigo ");
        corpoBuilder.append("from Consignataria csa ");
        corpoBuilder.append("where csa.csaAtivo = 0 ");

        corpoBuilder.append("and exists (select 1 from csa.ocorrenciaConsignatariaSet occ ");
        corpoBuilder.append("where occ.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA).append("'");
        corpoBuilder.append(")");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.CSA_CODIGO
    	};
    }
}
