package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioGerencialGeralTaxasQuery</p>
 * <p>Description: Consulta de ranking de taxa de juros.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialGeralBuscaSvcTaxasQuery extends ReportHQuery {

    public boolean internacional;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT prz.servico.svcCodigo, prz.servico.svcDescricao, COUNT(ca.cftCodigo) as qtde ");
        corpo.append("FROM Prazo prz ");
        corpo.append(" JOIN prz.prazoConsignatariaSet przCsa");
        corpo.append(" JOIN przCsa.coeficienteAtivoSet ca ");
        corpo.append(" WHERE ca.cftDataIniVig <= to_date(data_corrente()) ");
        corpo.append(" AND (ca.cftDataFimVig >= to_date(data_corrente()) OR ca.cftDataFimVig IS NULL)");
        if(internacional) {
            corpo.append(" AND prz.servico.naturezaServico.nseCodigo = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
        }
        corpo.append(" GROUP BY prz.servico.svcCodigo, prz.servico.svcDescricao");
        corpo.append(" ORDER BY qtde desc");

       final Query<Object[]> query = instanciarQuery(session, corpo.toString());
       query.setMaxResults(1);
       return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{Columns.SVC_CODIGO,
                            Columns.SVC_DESCRICAO,
                            "qtde"};
    }
}
