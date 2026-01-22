package com.zetra.econsig.persistence.query.reclamacao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaReclamacaoMotivoQuery</p>
 * <p>Description: Lista motivos da reclamação de servidores.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaReclamacaoMotivoQuery extends HQuery {

    public String rrsCodigo;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "";

        if (count) {
            corpo =
                "select count(*) as total ";
        } else {
            corpo =
                "select rrs.rrsCodigo, " +
                "   tmr.tmrCodigo, " +
                "   tmr.tmrDescricao ";
        }
        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from ReclamacaoRegistroSer rrs ");
        corpoBuilder.append("inner join rrs.reclamacaoMotivoSet rmo ");
        corpoBuilder.append("inner join rmo.tipoMotivoReclamacao tmr ");
        corpoBuilder.append("where 1 = 1 ");

        if (!TextHelper.isNull(rrsCodigo)) {
            corpoBuilder.append(" and rrs.rrsCodigo ").append(criaClausulaNomeada("rrsCodigo", rrsCodigo));
        }

        if (!count) {
            corpoBuilder.append(" order by tmr.tmrDescricao ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Seta os parâmetros na query
        if (!TextHelper.isNull(rrsCodigo)) {
            defineValorClausulaNomeada("rrsCodigo", rrsCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.RRS_CODIGO,
                Columns.TMR_CODIGO,
                Columns.TMR_DESCRICAO
        };
    }
}
