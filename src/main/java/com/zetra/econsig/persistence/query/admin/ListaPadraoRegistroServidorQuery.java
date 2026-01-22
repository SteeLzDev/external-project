package com.zetra.econsig.persistence.query.admin;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPadraoRegistroServidorQuery</p>
 * <p>Description: Listagem de Padrões</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPadraoRegistroServidorQuery extends HQuery {

    public String prsCodigo;
    public String prsIdentificador;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "prs.prsCodigo, " +
                       "prs.prsIdentificador, " +
                       "prs.prsDescricao " +
                       "from PadraoRegistroServidor prs "+
                       "where 1=1 ";

        if (!TextHelper.isNull(prsCodigo)) {
            corpo += "and prs.prsCodigo " + criaClausulaNomeada("prsCodigo", prsCodigo);
        }
        if (!TextHelper.isNull(prsIdentificador)) {
            corpo += "and prs.prsIdentificador " + criaClausulaNomeada("prsIdentificador", prsIdentificador);
        }

        corpo += " order by prs.prsDescricao asc";

        Query<Object[]> query = instanciarQuery(session, corpo);
        if (!TextHelper.isNull(prsCodigo)) {
            defineValorClausulaNomeada("prsCodigo", prsCodigo, query);
        }
        if (!TextHelper.isNull(prsIdentificador)) {
            defineValorClausulaNomeada("prsIdentificador", prsIdentificador, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.PRS_CODIGO,
                Columns.PRS_IDENTIFICADOR,
                Columns.PRS_DESCRICAO
    	};
    }
}
