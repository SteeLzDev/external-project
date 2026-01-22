package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaStatusRegistroServidorQuery</p>
 * <p>Description: Listagem de Status de Registro Servidor</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaStatusRegistroServidorQuery extends HQuery {

    public boolean ignoraStatusExcluidos;
    public boolean ignoraStatusBloqSeguranca;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
    	String corpo = " select " +
                       " srs.srsCodigo, " +
                       " srs.srsDescricao " +
                       " from StatusRegistroServidor srs ";

    	StringBuilder corpoBuilder = new StringBuilder(corpo);
    	corpoBuilder.append(" where 1=1 ");

        if (ignoraStatusExcluidos) {
            corpoBuilder.append(" AND srs.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }
        if (ignoraStatusBloqSeguranca) {
            corpoBuilder.append(" AND srs.srsCodigo <> '").append(CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA).append("'");
        }
        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.SRS_CODIGO,
                Columns.SRS_DESCRICAO
    	};
    }
}
