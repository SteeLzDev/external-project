package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaLeilaoPassivelReversaoQuery</p>
 * <p>Description: Lista quantidade de contratos de leilao cancelados com proposta</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: ricardo.magno $
 * $Revision: $
 * $Date: $
 */
public class ListaLeilaoPassivelReversaoQuery extends HQuery {

    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {


        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT 1 as OCORRENCIA ");
        corpoBuilder.append("from OcorrenciaAutorizacao oca ");
        corpoBuilder.append(" WHERE oca.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" AND oca.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_CANCELAMENTO_LEILAO_COM_PROPOSTA + "' ");
        corpoBuilder.append(" AND NOT EXISTS (select 1 from OcorrenciaAutorizacao oca2 where oca2.autDesconto.adeCodigo = oca.autDesconto.adeCodigo and oca2.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_REVER_LEILAO_NAO_CONCRETIZADO + "') ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                "OCORRENCIA"
        };
    }

}
