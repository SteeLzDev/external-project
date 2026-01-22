package com.zetra.econsig.persistence.query.servico;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoCorQuery</p>
 * <p>Description: Listagem de Serviços Ativos de um Correspondente (Convênios Ativos)</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoCorQuery extends HQuery {

    public String corCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = 
            "select distinct svc.svcCodigo, " +
            "   svc.svcIdentificador, " +
            "   svc.svcDescricao, " +
            "   svc.svcAtivo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        
        corpoBuilder.append("from Servico svc ");
        corpoBuilder.append("inner join svc.convenioSet cnv ");
        corpoBuilder.append("inner join cnv.correspondenteConvenioSet crc ");
        corpoBuilder.append("where crc.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());        
        defineValorClausulaNomeada("corCodigo", corCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.SVC_ATIVO
        };
    }
}
