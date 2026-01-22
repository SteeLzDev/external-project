package com.zetra.econsig.persistence.query.correcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCoeficienteCorrecaoMaisProximoQuery</p>
 * <p>Description: Listagem de Coeficiente de Correção mais próximo
 * de um mês/ano informado</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCoeficienteCorrecaoMaisProximoQuery extends HQuery {
    
    public String tccCodigo;
    public Short mes, ano;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select distinct " +
                       "tcc.tccCodigo, " +
                       "tcc.tccDescricao, " +
                       "ccr.ccrVlr, " +
                       "ccr.ccrVlrAcumulado, " +
                       "ccr.ccrMes, " +
                       "ccr.ccrAno ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from CoeficienteCorrecao ccr ");
        corpoBuilder.append(" inner join ccr.tipoCoeficienteCorrecao tcc ");
        corpoBuilder.append(" where tcc.tccCodigo = :tccCodigo");

        corpoBuilder.append(" AND ((ccr.ccrAno * 12) + ccr.ccrMes) =");
        corpoBuilder.append(" (");
        corpoBuilder.append(" SELECT MAX((ccrMax.ccrAno * 12) + ccrMax.ccrMes)");
        corpoBuilder.append(" FROM CoeficienteCorrecao ccrMax");
        corpoBuilder.append(" WHERE ccrMax.tccCodigo = :tccCodigo");
        corpoBuilder.append(" AND ((ccrMax.ccrAno * 12) + ccrMax.ccrMes) <= ((:ano * 12) + :mes)");
        corpoBuilder.append(" )");        

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tccCodigo", tccCodigo, query);
        defineValorClausulaNomeada("mes", Integer.valueOf(mes), query);
        defineValorClausulaNomeada("ano", Integer.valueOf(ano), query);
        
        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.TCC_CODIGO,
                Columns.TCC_DESCRICAO,
                Columns.CCR_VLR,
                Columns.CCR_VLR_ACUMULADO,
                Columns.CCR_MES,
                Columns.CCR_ANO
    	};
    }
}
