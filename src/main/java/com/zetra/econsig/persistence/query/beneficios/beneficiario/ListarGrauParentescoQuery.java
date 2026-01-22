package com.zetra.econsig.persistence.query.beneficios.beneficiario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarGrauParentescoQuery</p>
 * <p>Description: Listagem de graus de parentesco</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarGrauParentescoQuery extends HQuery {
    public String grpCodigo;

    public void setCriterios(TransferObject criterio) {
        grpCodigo = (String) criterio.getAttribute("grpCodigo");

    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT grp.grpCodigo, " +
                    "grp.grpDescricao " +
                    "from GrauParentesco grp ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(grpCodigo)) {
            corpoBuilder.append(" and grp.grpCodigo ").append(criaClausulaNomeada("grpCodigo", grpCodigo));
        }

        corpoBuilder.append("order by grp.grpDescricao ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(grpCodigo)) {
            defineValorClausulaNomeada("grpCodigo", grpCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.GRP_CODIGO,
                Columns.GRP_DESCRICAO

        };
    }
}