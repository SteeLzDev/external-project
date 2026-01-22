package com.zetra.econsig.persistence.query.retorno;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemOrgaoParaRetornoQuery</p>
 * <p>Description: Listagem de Órgãos para identificadores de ORG/EST para
 * separação do arquivo de retorno entre as entidades</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemOrgaoParaRetornoQuery extends HQuery {

    public String estIdentificador;
    public String orgIdentificador;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select org.orgCodigo");
        corpoBuilder.append(" from Orgao org");
        corpoBuilder.append(" inner join org.estabelecimento est");
        corpoBuilder.append(" where 1=1");

        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" and est.estIdentificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }
        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" and org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        }

        corpoBuilder.append(" order by est.estIdentificador");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }
        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_CODIGO
        };
    }
}
