package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCpfServidorQuery</p>
 * <p>Description: Retornar lista de CPFs que possuem matrícula não excluída.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCpfServidorQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ser.serCpf ");
        corpoBuilder.append("from Servidor ser ");
        corpoBuilder.append("where exists ( ");
        corpoBuilder.append("select 1 from ser.registroServidorSet rse ");
        corpoBuilder.append("where rse.statusRegistroServidor.srsCodigo not in ('").append(TextHelper.join(CodedValues.SRS_INATIVOS, "','")).append("') ");
        corpoBuilder.append(") ");
        corpoBuilder.append("group by ser.serCpf ");
        corpoBuilder.append("order by ser.serCpf ");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
       return new String[] {
                Columns.SER_CPF
        };
    }
}