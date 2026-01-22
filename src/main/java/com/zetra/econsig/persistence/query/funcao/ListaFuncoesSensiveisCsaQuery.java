package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFuncoesSensiveisCsaQuery</p>
 * <p>Description: Lista as funções sensíveis configuradas por CSA.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncoesSensiveisCsaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select fsc.funCodigo, fsc.csaCodigo, fsc.fscValor ");
        corpoBuilder.append("from FuncaoSensivelCsa fsc ");
        corpoBuilder.append("where fsc.fscValor != 'N' ");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            Columns.FSC_FUN_CODIGO,
            Columns.FSC_CSA_CODIGO,
            Columns.FSC_VALOR
        };
    }
}
