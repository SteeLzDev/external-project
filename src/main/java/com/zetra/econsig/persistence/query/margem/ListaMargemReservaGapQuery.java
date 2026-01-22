package com.zetra.econsig.persistence.query.margem;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMargemReservaGapQuery</p>
 * <p>Description: Listagem de margens de Reserva GAP.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMargemReservaGapQuery extends HNativeQuery {

    public String rseCodigo;
    public Short marCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> statusAtivos = CodedValues.SAD_CODIGOS_ATIVOS;

        String fields = Columns.MAR_CODIGO + MySqlDAOFactory.SEPARADOR
                        + Columns.MRS_MARGEM_REST + MySqlDAOFactory.SEPARADOR
                        + Columns.ADE_CODIGO + MySqlDAOFactory.SEPARADOR
                        + Columns.ADE_ANO_MES_INI;

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ").append(fields).append(" FROM ").append(Columns.TB_MARGEM);
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_MARGEM_REGISTRO_SERVIDOR).append(" ON (");
        corpoBuilder.append(Columns.MAR_CODIGO).append(" = ").append(Columns.MRS_MAR_CODIGO).append(")");

        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ON (");
        corpoBuilder.append(Columns.MRS_RSE_CODIGO).append(" = ").append(Columns.ADE_RSE_CODIGO).append(" AND ");
        corpoBuilder.append(Columns.MAR_CODIGO).append(" = ").append(Columns.ADE_INC_MARGEM).append(" AND ");
        corpoBuilder.append(Columns.ADE_SAD_CODIGO).append(" ").append(criaClausulaNomeada("statusAtivos", statusAtivos)).append(") ");

        corpoBuilder.append(" WHERE ").append(Columns.MAR_CODIGO_PAI).append(" ").append(criaClausulaNomeada("marCodigo", marCodigo));
        corpoBuilder.append(" AND ").append(Columns.MRS_RSE_CODIGO).append(" ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" ORDER BY coalesce(").append(Columns.ADE_ANO_MES_INI).append(", '2999-12-31')").append(MySqlDAOFactory.SEPARADOR).append(Columns.MAR_SEQUENCIA);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("statusAtivos", statusAtivos, query);
        defineValorClausulaNomeada("marCodigo", marCodigo, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.MAR_CODIGO,
                Columns.MRS_MARGEM_REST,
                Columns.ADE_CODIGO,
                Columns.ADE_ANO_MES_INI
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
