package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegistrosServidoresLikeMatriculaQuery</p>
 * <p>Description: Lista registros servidores like matricula.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegistrosServidoresLikeMatriculaQuery extends HNativeQuery {

    public String rseMatricula;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select rse.rse_codigo, rse.rse_matricula, ser.ser_codigo ");
        corpoBuilder.append(" from tb_registro_servidor rse");
        corpoBuilder.append(" inner join tb_servidor ser on (ser.ser_codigo = rse.ser_codigo)");
        corpoBuilder.append(" where 1=1 ");
        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and rse.rse_matricula like :rseMatricula");
        }
        corpoBuilder.append(" order by rse.rse_matricula DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula + "%", query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_CODIGO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
