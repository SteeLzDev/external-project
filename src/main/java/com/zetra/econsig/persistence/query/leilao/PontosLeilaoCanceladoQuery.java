package com.zetra.econsig.persistence.query.leilao;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaQtdeContratosQuery</p>
 * <p>Description: Lista quantidade de contratos em um determinado status</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PontosLeilaoCanceladoQuery extends HQuery {

    public String rseCodigo;
    public Date dataInicial;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {


        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT count(distinct oca.autDesconto.adeCodigo) as QTDE_CANCELAMENTOS ");
        corpoBuilder.append("from OcorrenciaAutorizacao oca ");
        corpoBuilder.append("inner join oca.autDesconto.registroServidor rse ");
        corpoBuilder.append(" WHERE rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND oca.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_CANCELAMENTO_LEILAO_COM_PROPOSTA + "' ");
        corpoBuilder.append(" AND NOT EXISTS (select 1 from OcorrenciaAutorizacao oca2 where oca2.autDesconto.adeCodigo = oca.autDesconto.adeCodigo and oca2.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_REVER_LEILAO_NAO_CONCRETIZADO + "') ");
        corpoBuilder.append(" AND oca.ocaData > :dataInicial ");


        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("dataInicial", dataInicial, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "QTDE_CANCELAMENTOS"
        };
    }
}
