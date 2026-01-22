package com.zetra.econsig.persistence.query.orgao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUnidadeSubOrgaoQuery</p>
 * <p>Description: Listagem de Unidades e os seus SubÓrgãos</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUnidadeSubOrgaoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT ");
        corpo.append("uni.uniCodigo, ");
        corpo.append("uni.uniIdentificador,  ");
        corpo.append("uni.uniDescricao, ");
        corpo.append("subOrg.sboCodigo, " );
        corpo.append("subOrg.sboIdentificador, ");
        corpo.append("subOrg.sboDescricao ");
        corpo.append("FROM Unidade uni ");
        corpo.append("INNER JOIN uni.subOrgao subOrg ");
        corpo.append("ORDER BY subOrg.sboDescricao ");

        return instanciarQuery(session, corpo.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.UNI_CODIGO,
                Columns.UNI_IDENTIFICADOR,
                Columns.UNI_DESCRICAO,
                Columns.SBO_CODIGO,
                Columns.SBO_IDENTIFICADOR,
                Columns.SBO_DESCRICAO
        };
    }
}
