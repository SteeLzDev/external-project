package com.zetra.econsig.persistence.query.juros;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaLimiteTaxaJurosQuery</p>
 * <p>Description: Lista de limite de taxa de juros.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public class ListaLimiteTaxaJurosQuery extends HQuery {
    public String svcCodigo;
    public Short ltjPrazoRef;
    public String notLtjCodigo;
    public boolean count = false;
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "";

        if (count) {
            corpo =
                "select count(*) as total ";
        } else {
            corpo =
                "select ltj.ltjCodigo, " +
                "   svc.svcCodigo, " +
                "   svc.svcDescricao, " +
                "   ltj.ltjPrazoRef, " +
                "   ltj.ltjJurosMax, " +
                "   ltj.ltjVlrRef ";
        }
        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from LimiteTaxaJuros ltj ");
        corpoBuilder.append("inner join ltj.servico svc ");
        corpoBuilder.append("where 1 = 1 ");

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (ltjPrazoRef!= null && ltjPrazoRef > 0) {
            corpoBuilder.append(" and ltj.ltjPrazoRef ").append(criaClausulaNomeada("ltjPrazoRef", ltjPrazoRef));
        }

        if (!TextHelper.isNull(notLtjCodigo)) {
            corpoBuilder.append(" and ltj.ltjCodigo != :ltjCodigo");
        }

        if (!count) {
            corpoBuilder.append(" order by svc.svcDescricao, ltj.ltjPrazoRef");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Seta os parâmetros na query
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (ltjPrazoRef!= null && ltjPrazoRef > 0) {
        	defineValorClausulaNomeada("ltjPrazoRef", ltjPrazoRef, query);
        }

        if (!TextHelper.isNull(notLtjCodigo)) {
            defineValorClausulaNomeada("ltjCodigo", notLtjCodigo, query);
        }


        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.LTJ_CODIGO,
                Columns.LTJ_SVC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.LTJ_PRAZO_REF,
                Columns.LTJ_JUROS_MAX,
                Columns.LTJ_VLR_REF
        };
    }
}
