package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemTotalConsignacaoRseQuery</p>
 * <p>Description: Lista o total de contratos para um servidor de acordo 
 * com os demais parâmetros informados</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
  * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalConsignacaoRseQuery extends HQuery {

    public String rseCodigo;
    public Short adeIncMargem;
    public List<String> sadCodigos;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        
        corpoBuilder.append("select count(*) from AutDesconto ade ");
        
        corpoBuilder.append("where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        if (adeIncMargem != null) {
            corpoBuilder.append(" and ade.adeIncMargem ").append(criaClausulaNomeada("adeIncMargem", adeIncMargem));
        }
        if (sadCodigos != null && sadCodigos.size() > 0) {
            corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        if (adeIncMargem != null) {
            defineValorClausulaNomeada("adeIncMargem", adeIncMargem, query);
        }        
        if (sadCodigos != null && sadCodigos.size() > 0) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }
        
        return query;
    }
}
