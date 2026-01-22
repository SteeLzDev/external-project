package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FuncoesEnvioEmailCseQuery</p>
 * <p>Description: Lista as funções que tem envio de e-mail habilitado para CSE
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */
public class FuncoesEnvioEmailCseQuery extends HQuery {
    private final String cseCodigo;

    public FuncoesEnvioEmailCseQuery(String cseCodigo) {
        this.cseCodigo = cseCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT fun.funCodigo, upper(fun.funDescricao), pap.papCodigo, upper(pap.papDescricao), dee.deeReceber, dee.deeEmail ");
        sql.append("FROM DestinatarioEmail des ");
        sql.append("INNER JOIN des.funcao fun ");
        sql.append("INNER JOIN des.papelOperador pap ");
        sql.append("LEFT OUTER JOIN fun.destinatarioEmailCseSet dee ");
        sql.append("WITH dee.cseCodigo = :cseCodigo ");
        sql.append("AND dee.papCodigo = pap.papCodigo ");
        sql.append("WHERE des.papelDestinatario.papCodigo = :papCodigo ");
        sql.append("ORDER BY fun.funDescricao, pap.papCodigo ");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("cseCodigo", cseCodigo, query);
        defineValorClausulaNomeada("papCodigo", CodedValues.PAP_CONSIGNANTE, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.PAP_CODIGO,
                Columns.PAP_DESCRICAO,
                Columns.DEE_RECEBER,
                Columns.DEE_EMAIL
        };
    }
}
