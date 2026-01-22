package com.zetra.econsig.persistence.query.orgao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUnidadeQuery</p>
 * <p>Description: Listagem de Unidades</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUnidadeQuery extends HQuery {

    public String sboCodigo;
    public String uniIdentificador;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "uni.uniCodigo, " +
                       "uni.uniIdentificador, " +
                       "uni.uniDescricao " +
                       "from Unidade uni " +
                       "where 1=1 ";

        if (!TextHelper.isNull(sboCodigo)) {
            corpo += "and uni.subOrgao.sboCodigo " + criaClausulaNomeada("sboCodigo", sboCodigo);
        }
        if (!TextHelper.isNull(uniIdentificador)) {
            corpo += "and uni.uniIdentificador " + criaClausulaNomeada("uniIdentificador", uniIdentificador);
        }

        Query<Object[]> query = instanciarQuery(session, corpo);
        if (!TextHelper.isNull(sboCodigo)) {
            defineValorClausulaNomeada("sboCodigo", sboCodigo, query);
        }
        if (!TextHelper.isNull(uniIdentificador)) {
            defineValorClausulaNomeada("uniIdentificador", uniIdentificador, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.UNI_CODIGO,
                Columns.UNI_IDENTIFICADOR,
                Columns.UNI_DESCRICAO
    	};
    }
}
