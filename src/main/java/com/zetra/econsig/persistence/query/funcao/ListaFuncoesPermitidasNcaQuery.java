package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFuncoesPermitidasNcaQuery</p>
 * <p>Description: Lista as funções permitidas por natureza de consignatária para criação de um novo usuário, de acordo com a tabela função_permitida_nca. </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncoesPermitidasNcaQuery extends HQuery {
    public String ncaCodigo;

    /**
     * Lista as funções permitidas para criação de um novo usuário, de acordo com a tabela função_permitida_nca.
     * O parâmetro ncaCodigo é a natureza da consignatária
     */
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select fpn.funCodigo, fpn.ncaCodigo ");
        corpoBuilder.append(" from FuncaoPermitidaNca fpn ");
        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(ncaCodigo)) {
            corpoBuilder.append(" AND fpn.naturezaConsignataria.ncaCodigo = :ncaCodigo");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(ncaCodigo)) {
             defineValorClausulaNomeada("ncaCodigo", ncaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            Columns.FPN_FUN_CODIGO,
            Columns.FPN_NCA_CODIGO
        };
    }
}
