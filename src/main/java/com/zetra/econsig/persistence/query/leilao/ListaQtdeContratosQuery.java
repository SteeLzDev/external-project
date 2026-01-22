package com.zetra.econsig.persistence.query.leilao;

import static com.zetra.econsig.helper.texto.TextHelper.sqlJoin;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

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
public class ListaQtdeContratosQuery extends HQuery {

    public String rseCodigo;
    public Date dataInicial;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {


        String[] suspenso = { CodedValues.SAD_SUSPENSA, CodedValues.SAD_SUSPENSA_CSE };
        String[] naoAtivo = { CodedValues.SAD_SOLICITADO, CodedValues.SAD_AGUARD_CONF, CodedValues.SAD_AGUARD_DEFER,
                CodedValues.SAD_INDEFERIDA, CodedValues.SAD_CANCELADA, CodedValues.SAD_LIQUIDADA, CodedValues.SAD_CONCLUIDO, CodedValues.SAD_ENCERRADO };

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT rse.rseCodigo, ");
        corpoBuilder.append("sum(case when ade.statusAutorizacaoDesconto.sadCodigo in ('").append(CodedValues.SAD_CONCLUIDO).append("') then 1 else 0 end) as QTDE_CONTRATOS_CONCLUIDOS, ");
        corpoBuilder.append("sum(case when ade.statusAutorizacaoDesconto.sadCodigo in (").append(sqlJoin(suspenso)).append(") then 1 else 0 end) as QTDE_CONTRATOS_SUSPENSOS, ");
        corpoBuilder.append("(sum(case when ade.statusAutorizacaoDesconto.sadCodigo not in (").append(sqlJoin(naoAtivo)).append(") then ade.adeVlr else 0 end) * 100) / ");
        corpoBuilder.append("(rse.rseMargem + sum(case when ade.statusAutorizacaoDesconto.sadCodigo not in (").append(sqlJoin(naoAtivo)).append(") then ade.adeVlr else 0 end)) as PORCENT_MARGEM_UTILIZADA ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("inner join ade.registroServidor rse ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");

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
                "QTDE_CONTRATOS_CONCLUIDOS",
                "QTDE_CONTRATOS_SUSPENSOS",
                "PORCENT_MARGEM_UTILIZADA"
        };
    }
}
