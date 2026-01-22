package com.zetra.econsig.persistence.query.correcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCoeficienteCorrecaoQuery</p>
 * <p>Description: Listagem de Coeficiente de Correção</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCoeficienteCorrecaoQuery extends HQuery {
    
    public String tccCodigo;
    public Short mes, ano;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select distinct " +
                       "tcc.tccCodigo, " +
                       "tcc.tccDescricao, " +
                       "tcc.tccFormaCalc, " +
                       "ccr.ccrVlr, " +
                       "ccr.ccrVlrAcumulado, " +
                       "ccr.ccrMes, " +
                       "ccr.ccrAno ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from CoeficienteCorrecao ccr ");
        corpoBuilder.append(" inner join ccr.tipoCoeficienteCorrecao tcc ");
        corpoBuilder.append(" where tcc.tccCodigo ").append(criaClausulaNomeada("tccCodigo", tccCodigo));
        
        if (mes != null && ano != null) {
            corpoBuilder.append(" AND ((ccr.ccrMes >= :mes AND ccr.ccrAno = :ano)");
            corpoBuilder.append("   OR (ccr.ccrAno > :ano))");            
        }
        
        corpoBuilder.append(" order by ccr.ccrAno, ccr.ccrMes");
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tccCodigo", tccCodigo, query);

        if (mes != null && ano != null) {
            defineValorClausulaNomeada("mes", mes, query);
            defineValorClausulaNomeada("ano", ano, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.TCC_CODIGO,
                Columns.TCC_DESCRICAO,
                Columns.TCC_FORMA_CALC,
                Columns.CCR_VLR,
                Columns.CCR_VLR_ACUMULADO,
                Columns.CCR_MES,
                Columns.CCR_ANO
    	};
    }
}
