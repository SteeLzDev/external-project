package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamOrgaoQuery</p>
 * <p>Description: Lista parâmetros de órgão</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: anderson.assis $
 * $Revision: 29461 $
 * $Date: 2020-07-02 15:28:39 -0300 (qui, 02 jul 2020) $
*/
public class ListaParamOrgaoQuery extends HQuery {

    public String estCodigo;
    public String orgCodigo;

    public String taoCodigo;
    public String paoVlr;

    public boolean count = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        if (!count) {
            corpo.append("select ");
            corpo.append("pao.orgCodigo, ");
            corpo.append("pao.taoCodigo, ");
            corpo.append("pao.paoVlr ");

        } else {
            corpo.append("select count(*)");
        }

        corpo.append("from ParamOrgao pao ");
        corpo.append("where 1 = 1 ");

        if (!TextHelper.isNull(estCodigo)) {
            corpo.append(" and pao.orgao.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpo.append(" and pao.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (!TextHelper.isNull(taoCodigo)) {
            corpo.append(" and pao.tipoParamOrgao.taoCodigo ").append(criaClausulaNomeada("taoCodigo", taoCodigo));
        }

        if (!TextHelper.isNull(paoVlr)) {
            corpo.append(" and pao.paoVlr ").append(criaClausulaNomeada("paoVlr", paoVlr));
        }

        corpo.append(" order by pao.orgCodigo");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(taoCodigo)) {
            defineValorClausulaNomeada("taoCodigo", taoCodigo, query);
        }

        if (!TextHelper.isNull(paoVlr)) {
            defineValorClausulaNomeada("paoVlr", paoVlr, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PAO_ORG_CODIGO,
                Columns.PAO_TAO_CODIGO,
                Columns.PAO_VLR
            };
    }
}
