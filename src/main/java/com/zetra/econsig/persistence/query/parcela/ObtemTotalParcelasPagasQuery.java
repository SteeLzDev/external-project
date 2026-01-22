package com.zetra.econsig.persistence.query.parcela;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTotalParcelasPagasQuery</p>
 * <p>Description: Retorna o total de parcelas pagas de um contrato.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalParcelasPagasQuery  extends HQuery {
    
    public String adeCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        
        corpoBuilder.append("SELECT COUNT(*) ");
        corpoBuilder.append("FROM ParcelaDesconto prd ");
        corpoBuilder.append("INNER JOIN prd.autDesconto ade ");
        corpoBuilder.append("WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" AND prd.statusParcelaDesconto.spdCodigo IN ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("')");
        corpoBuilder.append(" AND ade.adeAnoMesIni <= prd.prdDataDesconto");
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        return query;
    }
}
