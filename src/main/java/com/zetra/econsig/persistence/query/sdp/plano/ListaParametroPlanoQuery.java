package com.zetra.econsig.persistence.query.sdp.plano;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParametroPlanoQuery</p>
 * <p>Description: Listagem de parâmetros de plano de desconto SDP</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParametroPlanoQuery extends HQuery {

    public String plaCodigo;
    public String tppCodigo;
    public boolean count = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (count) {
            corpo =
                "select count(*) as total ";
        } else {
            corpo =
                "select tpp.tppCodigo, " +
                "   ppl.pplValor, " +
                "   ppl.pplData ";
        }
        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from ParametroPlano ppl ");
        corpoBuilder.append("inner join ppl.tipoParametroPlano tpp ");

        if (!TextHelper.isNull(plaCodigo)) {
            corpoBuilder.append("inner join ppl.plano pla ");
        }

        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(plaCodigo)) {
            corpoBuilder.append(" and pla.plaCodigo ").append(criaClausulaNomeada("plaCodigo", plaCodigo));
        }

        if (!TextHelper.isNull(tppCodigo)) {
            corpoBuilder.append(" and tpp.tppCodigo ").append(criaClausulaNomeada("tppCodigo", tppCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(plaCodigo)) {
            defineValorClausulaNomeada("plaCodigo", plaCodigo, query);
        }

        if (!TextHelper.isNull(tppCodigo)) {
            defineValorClausulaNomeada("tppCodigo", tppCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TPP_CODIGO,
                Columns.PPL_VALOR,
                Columns.PPL_DATA
        };
    }
}
