package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FuncoesEnvioEmailSerQuery</p>
 * <p>Description: Lista as funções que tem envio de e-mail habilitado para servidor</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncoesEnvioEmailSerQuery extends HQuery {
    private final String serCodigo;

    public FuncoesEnvioEmailSerQuery(String serCodigo) {
        this.serCodigo = serCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT fun.funCodigo, upper(fun.funDescricao), pap.papCodigo, upper(pap.papDescricao), des.desReceber ");
        sql.append("FROM DestinatarioEmail dem ");
        sql.append("INNER JOIN dem.funcao fun ");
        sql.append("INNER JOIN dem.papelOperador pap ");
        sql.append("LEFT OUTER JOIN fun.destinatarioEmailSerSet des ");
        sql.append("WITH des.serCodigo = :serCodigo ");
        sql.append("AND des.papCodigo = pap.papCodigo ");
        sql.append("WHERE dem.papelDestinatario.papCodigo = :papCodigo ");
        sql.append("ORDER BY fun.funDescricao, pap.papCodigo ");

        Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("serCodigo", serCodigo, query);
        defineValorClausulaNomeada("papCodigo", CodedValues.PAP_SERVIDOR, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.PAP_CODIGO,
                Columns.PAP_DESCRICAO,
                Columns.DES_RECEBER
        };
    }
}
