package com.zetra.econsig.persistence.query.comunicacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarAssuntoComunicacoesQuery</p>
 * <p>Description: lista de categorias de comunicação.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 */

public class ListarAssuntoComunicacaoQuery extends HQuery {

    public boolean count = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "";

        if (count) {
            corpo = "SELECT DISTINCT COUNT(*) AS TOTAL ";
        } else {
            corpo = "select " +
                    "ac.ascCodigo, " +
                    "ac.ascDescricao, " +
                    "ac.ascAtivo, " +
                    "ac.ascConsignacao";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM AssuntoComunicacao ac");
        corpoBuilder.append(" WHERE 1 = 1");
        corpoBuilder.append(" ORDER BY ac.ascDescricao");
        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ASC_CODIGO,
                Columns.ASC_DESCRICAO,
                Columns.ASC_ATIVO,
                Columns.ASC_CONSIGNACAO
        };
    }

}
