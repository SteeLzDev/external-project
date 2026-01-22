package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaComBloqueioQuery</p>
 * <p>Description: Lista consignatárias com bloqueios</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsaComOcorrenciaSaldoDevedorQuery extends HQuery {

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select csa.csaCodigo ");
        corpoBuilder.append("from Consignataria csa ");
        corpoBuilder.append("where csa.csaAtivo = 0 ");

        corpoBuilder.append("and exists (select 1 from csa.ocorrenciaConsignatariaSet occ ");
        corpoBuilder.append("where occ.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO).append("'");
        corpoBuilder.append(")");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            Columns.CSA_CODIGO
        };
    }
}
