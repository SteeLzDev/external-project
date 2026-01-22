package com.zetra.econsig.persistence.query.beneficios.beneficiario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarMotivoDependenciaQuery</p>
 * <p>Description: Listagem de motivo dependência.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarMotivoDependenciaQuery extends HQuery {

    public String mdeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select mdeCodigo, mdeDescricao from MotivoDependencia " +
                (!TextHelper.isNull(mdeCodigo) ? "where mdeCodigo " + criaClausulaNomeada("mdeCodigo", mdeCodigo) : "") +
                " order by mdeDescricao";

        Query<Object[]> query = instanciarQuery(session, corpo);
        if (!TextHelper.isNull(mdeCodigo)) {
            defineValorClausulaNomeada("mdeCodigo", mdeCodigo, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.MDE_CODIGO,
                Columns.MDE_DESCRICAO
        };
    }
}