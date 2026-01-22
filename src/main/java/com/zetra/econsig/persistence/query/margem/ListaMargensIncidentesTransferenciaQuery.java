package com.zetra.econsig.persistence.query.margem;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMargensIncidentesTransferenciaQuery</p>
 * <p>Description: Listagem de margens incidentes na transferencia.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMargensIncidentesTransferenciaQuery extends HQuery {

    public List<Short> margens = new ArrayList<>();
    public String papCodigo = "1";

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = " SELECT DISTINCT " +
                     " trm.margemByMarCodigoOrigem.marCodigo, " +
                     " trm.margemByMarCodigoDestino.marCodigo, " +
                     " trm.trmApenasTotal " ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM Margem mar ");
        corpoBuilder.append(" INNER JOIN mar.transferenciaMargemByMarCodigoOrigemSet trm ");
        corpoBuilder.append(" WHERE mar.marCodigo").append(criaClausulaNomeada("margens", margens));
        corpoBuilder.append(" AND trm.papel.papCodigo").append(criaClausulaNomeada("papCodigo", papCodigo));
        corpoBuilder.append(" AND trm.margemByMarCodigoDestino.marCodigo").append(criaClausulaNomeada("margens", margens));
        corpoBuilder.append(" ORDER BY trm.margemByMarCodigoOrigem.marCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("margens", margens, query);
        defineValorClausulaNomeada("papCodigo", papCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TRM_MAR_CODIGO_ORIGEM,
                Columns.TRM_MAR_CODIGO_DESTINO,
                Columns.TRM_APENAS_TOTAL
        };
    }
}
