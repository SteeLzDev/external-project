package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegistrosServidoresTransferidosQuery</p>
 * <p>Description: Lista registros servidores excluídos.</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegistrosServidoresExcluidosQuery extends HQuery {

    public boolean count = false;

    public ListaRegistrosServidoresExcluidosQuery() {
    }

    public ListaRegistrosServidoresExcluidosQuery(boolean count) {
        this.count = count;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";
        if (count) {
            corpo = "SELECT COUNT(DISTINCT rse.rseCodigo) AS TOTAL ";
        } else {
            corpo = "SELECT " +
                    "rse.rseCodigo, " +
                    "rse.rseMatricula ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM RegistroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");
        corpoBuilder.append(" WHERE 1 = 1");

        corpoBuilder.append(" AND srs.srsCodigo ").append(criaClausulaNomeada("srsCodigo", CodedValues.SRS_EXCLUIDO));
        corpoBuilder.append(" AND ser.serCpf NOT IN ");
        corpoBuilder.append(" (SELECT ser2.serCpf FROM RegistroServidor rse2 ");
        corpoBuilder.append(" INNER JOIN rse2.servidor ser2 ");
        corpoBuilder.append(" INNER JOIN rse2.statusRegistroServidor srs2 ");
        corpoBuilder.append(" WHERE srs2.srsCodigo ").append(criaClausulaNomeada("srsCodigo2", CodedValues.SRS_ATIVOS));
        corpoBuilder.append(" ) ");

        if (!count) {
            corpoBuilder.append(" ORDER BY rse.rseMatricula");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("srsCodigo", CodedValues.SRS_EXCLUIDO, query);
        defineValorClausulaNomeada("srsCodigo2", CodedValues.SRS_ATIVOS, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA
        };
    }
}
