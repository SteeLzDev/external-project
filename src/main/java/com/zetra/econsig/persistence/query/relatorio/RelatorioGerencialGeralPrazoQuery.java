package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class RelatorioGerencialGeralPrazoQuery extends ReportHQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT max(prz.przVlr) as prazoMax, svc.nseCodigo ");
        corpo.append("FROM Prazo prz ");
        corpo.append("INNER JOIN prz.servico svc ");
        corpo.append("WHERE svc.nseCodigo = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
        corpo.append("AND prz.przAtivo = 1 ");
        corpo.append("GROUP BY svc.nseCodigo");

       Query<Object[]> query = instanciarQuery(session, corpo.toString());
       query.setMaxResults(1);
       return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "prazoMax",
                Columns.SVC_NSE_CODIGO
        };
    }

}
