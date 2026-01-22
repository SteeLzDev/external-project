package com.zetra.econsig.persistence.query.correcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemCoeficienteCorrecaoQuery</p>
 * <p>Description: Listagem de status de parcela</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemCoeficienteCorrecaoQuery extends HQuery {

    public String tccCodigo;
    public String correcaoVlr;
    public int mes;
    public int ano;
    public boolean primeiro;
    public boolean ultimo; 
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        int total = (ano * 12) + mes;

        StringBuilder corpoBuilder = new StringBuilder();
        
        if (correcaoVlr != null && correcaoVlr.equals(CodedValues.TIPO_CCR_VLR_ACUMULADO)) {
            corpoBuilder.append("SELECT ccr.ccrVlrAcumulado ");   
        } else {
            corpoBuilder.append("SELECT ccr.ccrVlr ");
        }
        
        corpoBuilder.append(" FROM CoeficienteCorrecao ccr ");
        corpoBuilder.append(" WHERE ccr.tccCodigo ").append(criaClausulaNomeada("tccCodigo", tccCodigo));

        if (!primeiro && !ultimo) {
            corpoBuilder.append(" AND ccr.ccrMes = :mes");
            corpoBuilder.append(" AND ccr.ccrAno = :ano");
            
        } else {            
            corpoBuilder.append(" AND ((ccr.ccrAno * 12) + ccr.ccrMes ");
            corpoBuilder.append((primeiro) ? " >= " : " <= ").append(":total)");
            corpoBuilder.append(" ORDER BY ((ccr.ccrAno * 12) + ccr.ccrMes)");
            if (ultimo) {
                corpoBuilder.append(" DESC");
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(1);
        
        defineValorClausulaNomeada("tccCodigo", tccCodigo, query);
        
        if (!primeiro && !ultimo) {
            defineValorClausulaNomeada("mes", mes, query);
            defineValorClausulaNomeada("ano", ano, query);
        } else {
            defineValorClausulaNomeada("total", total, query);
        }
        
        return query;
    }
}
