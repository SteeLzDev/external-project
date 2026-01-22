package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemTotalConsignacaoCsaQuery</p>
 * <p>Description: Lista o total de contratos de um servidor por consignatária.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalConsignacaoCsaQuery extends HQuery {

    public String rseCodigo;
    public List<String> sadCodigos;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        
        corpoBuilder.append("select csa.csaCodigo, csa.csaNome, count(*) as QTDE ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join ade.registroServidor rse ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        
        corpoBuilder.append("where rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        if (sadCodigos != null && sadCodigos.size() > 0) {
            corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        corpoBuilder.append("group by csa.csaCodigo, csa.csaNome ");
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        if (sadCodigos != null && sadCodigos.size() > 0) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            Columns.CSA_CODIGO,
            Columns.CSA_NOME,
            "QTDE"
        };
    }
}
