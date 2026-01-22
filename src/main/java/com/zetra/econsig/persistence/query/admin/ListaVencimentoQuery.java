package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaVencimentoQuery</p>
 * <p>Description: Listagem de vencimentos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaVencimentoQuery extends HQuery {

    public String vctIdentificador;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "vct.vctCodigo, " +
                       "vct.vctIdentificador, " +
                       "vct.vctDescricao " +
                       "from Vencimento vct " +
                       "where 1=1 ";

        if (!TextHelper.isNull(vctIdentificador)) {
            corpo += "and vct.vctIdentificador " + criaClausulaNomeada("vctIdentificador", vctIdentificador);
        }

        corpo += " order by vct.vctIdentificador";

        Query<Object[]> query = instanciarQuery(session, corpo);

        if (!TextHelper.isNull(vctIdentificador)) {
            defineValorClausulaNomeada("vctIdentificador", vctIdentificador, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.VCT_CODIGO,
                Columns.VCT_IDENTIFICADOR,
                Columns.VCT_DESCRICAO
    	};
    }
}
