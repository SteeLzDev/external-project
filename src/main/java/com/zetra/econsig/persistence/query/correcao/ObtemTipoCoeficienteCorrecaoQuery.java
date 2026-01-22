package com.zetra.econsig.persistence.query.correcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemTipoCoeficienteCorrecaoQuery</p>
 * <p>Description: Obtém o Tipo de Coeficiente de Correção pela Descrição ou Código</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTipoCoeficienteCorrecaoQuery extends HQuery {
    
    public boolean count = false;
    
    public String tccCodigo;
    public String notTccCodigo;
    public String tccDescricao;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = null;
        
        if (count) {
            corpo = "select count(*) ";
        } else {
            corpo = "select " +
                "tcc.tccCodigo, " +
                "tcc.tccFormaCalc, " +
                "tcc.tccDescricao ";
        }
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from TipoCoeficienteCorrecao tcc where 1=1");

        if (!TextHelper.isNull(tccCodigo)) {
            corpoBuilder.append(" and tcc.tccCodigo = :tccCodigo");
        } else if (!TextHelper.isNull(notTccCodigo)) {
            corpoBuilder.append(" and tcc.tccCodigo != :tccCodigo");
        }

        if (!TextHelper.isNull(tccDescricao)) {
            corpoBuilder.append(" and tcc.tccDescricao = :tccDescricao");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(tccCodigo)) {
            defineValorClausulaNomeada("tccCodigo", tccCodigo, query);
        } else if (!TextHelper.isNull(notTccCodigo)) {
            defineValorClausulaNomeada("tccCodigo", notTccCodigo, query);
        }

        if (!TextHelper.isNull(tccDescricao)) {
            defineValorClausulaNomeada("tccDescricao", tccDescricao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.TCC_CODIGO,
                Columns.TCC_FORMA_CALC,
                Columns.TCC_DESCRICAO
    	};
    }
}
