package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarNaturezaConsignacaoPorServidorQuery</p>
 * <p>Description: Listagem de natureza por consignações do servidor</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarNaturezaConsignacaoPorServidorQuery extends HQuery {

    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("select nse.nseCodigo, nse.nseDescricao from AutDesconto ade ");
        builder.append("join ade.verbaConvenio vco ");
        builder.append("join vco.convenio cnv ");
        builder.append("join cnv.servico svc ");
        builder.append("join svc.naturezaServico nse ");
        
        builder.append("where ade.registroServidor.rseCodigo").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        
        builder.append("group by nse.nseCodigo, nse.nseDescricao");
        
        Query<Object[]> query = instanciarQuery(session, builder.toString());
        
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        
        return query;

        
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            Columns.NSE_CODIGO,
            Columns.NSE_DESCRICAO
        };
    }
}
