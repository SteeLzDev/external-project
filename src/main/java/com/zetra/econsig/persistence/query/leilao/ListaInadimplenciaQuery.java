    package com.zetra.econsig.persistence.query.leilao;

import static com.zetra.econsig.helper.texto.TextHelper.sqlJoin;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaQtdeContratosQuery</p>
 * <p>Description: Lista quantidade de contratos em um determinado status</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaInadimplenciaQuery extends HQuery {

    public String rseCodigo;
    public Date dataInicial;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String[] processadas = { CodedValues.SPD_REJEITADAFOLHA, CodedValues.SPD_LIQUIDADAFOLHA };

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ade.registroServidor.rseCodigo, sum(case when prd.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_REJEITADAFOLHA).append("' then 1 else 0 end) * 100 / ");
        corpoBuilder.append("sum(case when prd.statusParcelaDesconto.spdCodigo in(").append(sqlJoin(processadas)).append(") then 1 else 0 end) as PORCENT_INADIMPLENCIA ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("left outer join ade.parcelaDescontoSet prd WITH ");
        corpoBuilder.append("prd.statusParcelaDesconto.spdCodigo IN (").append(sqlJoin(processadas)).append(")");

        corpoBuilder.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        corpoBuilder.append(" AND svc.naturezaServico.nseCodigo = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
        corpoBuilder.append(" AND ade.adeData > :dataInicial ");
        corpoBuilder.append(" GROUP BY ade.registroServidor.rseCodigo ");


        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("dataInicial", dataInicial, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                "PORCENT_INADIMPLENCIA"
        };
    }
}
