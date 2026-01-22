package com.zetra.econsig.persistence.query.sdp.permissionario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPermissionarioByEnderecoQuery</p>
 * <p>Description: Listagem de permissionários por endereço</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPermissionarioByEnderecoQuery extends HQuery {

    public String echCodigo;

    public String prmComplEndereco;

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder(" SELECT prm.prmCodigo ");
        corpoBuilder.append(" FROM Permissionario prm");
        corpoBuilder.append(" INNER JOIN prm.enderecoConjHabitacional ech");
        corpoBuilder.append(" INNER JOIN prm.consignataria csa");
        corpoBuilder.append(" WHERE ");

        corpoBuilder.append(" prm.prmAtivo ").append(criaClausulaNomeada("status", CodedValues.STS_ATIVO.toString()));

        if (!TextHelper.isNull(prmComplEndereco)) {
            corpoBuilder.append(" AND prm.prmComplEndereco ").append(criaClausulaNomeada("prmComplEndereco", prmComplEndereco));
        }

        if (!TextHelper.isNull(echCodigo)) {
            corpoBuilder.append(" AND ech.echCodigo ").append(criaClausulaNomeada("echCodigo", echCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("status", CodedValues.STS_ATIVO.toString(), query);

        if (!TextHelper.isNull(prmComplEndereco)) {
            defineValorClausulaNomeada("prmComplEndereco", prmComplEndereco, query);
        }

        if (!TextHelper.isNull(echCodigo)) {
            defineValorClausulaNomeada("echCodigo", echCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.PRM_CODIGO };
    }
}
