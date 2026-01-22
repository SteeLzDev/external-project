package com.zetra.econsig.persistence.query.beneficios;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoBeneficiarioQuery</p>
 * <p>Description: Listagem de tipos de beneficiários</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoBeneficiarioQuery extends HQuery {
    public Object tibCodigo;

    public void setCriterios(TransferObject criterio) {
        tibCodigo = criterio.getAttribute("tibCodigo");

    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT tipoBeneficiario.tibCodigo, " +
            		"tipoBeneficiario.tibDescricao " +
            		"from TipoBeneficiario tipoBeneficiario ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(tibCodigo)) {
            corpoBuilder.append(" and tib.tibCodigo ").append(criaClausulaNomeada("tibCodigo", tibCodigo));
        }

        corpoBuilder.append("order by tipoBeneficiario.tibDescricao ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(tibCodigo)) {
            defineValorClausulaNomeada("tibCodigo", tibCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TIB_CODIGO,
                Columns.TIB_DESCRICAO

        };
    }
}
