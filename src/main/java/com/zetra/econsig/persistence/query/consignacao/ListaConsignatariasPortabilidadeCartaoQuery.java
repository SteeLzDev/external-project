package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListaConsignatariasPortabilidadeCartaoQuery extends HQuery {

    public String csaCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT csa.csaCodigo, ");
        sql.append(" csa.csaIdentificador, ");
        sql.append(" csa.csaNome, ");
        sql.append(" csa.csaNomeAbrev ");
        sql.append(" FROM Consignataria csa ");
        sql.append(" INNER JOIN paramConsignatariaSet pca102 with pca102.tpaCodigo = '" + CodedValues.TPA_CSA_PODE_COMPRAR_CONTRATO_CARTAO + "' ");
        sql.append(" AND pca102.pcsVlr = '" + CodedValues.TPA_SIM + "' ");
        sql.append(" WHERE csa.csaAtivo = ").append(CodedValues.STS_ATIVO);

        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" AND csa.csaCodigo != :csaCodigo ");
        }

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                              Columns.CSA_CODIGO,
                              Columns.CSA_IDENTIFICADOR,
                              Columns.CSA_NOME,
                              Columns.CSA_NOME_ABREV
        };
    }
}
