package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaVinculoRegistroServidorQuery</p>
 * <p>Description: Listagem de Vinculos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaVinculoRegistroServidorQuery extends HQuery {

    public boolean ativo;
    public String vrsIdentificador;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder("select ")
                .append("vrs.vrsCodigo, ")
                .append("vrs.vrsIdentificador, ")
                .append("vrs.vrsDescricao, ")
                .append("vrs.vrsAtivo ")
                .append("from VinculoRegistroServidor vrs ")
                .append("where 1=1 ")
                .append(ativo ? "and vrs.vrsAtivo = " + CodedValues.STS_ATIVO + " " : " ");

        if (!TextHelper.isNull(vrsIdentificador)) {
            corpo.append("and vrs.vrsIdentificador ").append(criaClausulaNomeada("vrsIdentificador", vrsIdentificador));
        }

        corpo.append(" order by vrs.vrsIdentificador");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(vrsIdentificador)) {
            defineValorClausulaNomeada("vrsIdentificador", vrsIdentificador, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.VRS_CODIGO,
                Columns.VRS_IDENTIFICADOR,
                Columns.VRS_DESCRICAO,
                Columns.VRS_ATIVO
    	};
    }
}
