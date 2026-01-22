package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaSaldoDevedorServidorQuery</p>
 * <p>Description: Classe da query para buscar as consignatarias para o relatorio saldo devedor servidor
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ListaConsignatariaSaldoDevedorServidorQuery extends HQuery {

    public String csaCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT distinct csa.csaCodigo, csa.csaIdentificador, csa.csaNome ");
        corpo.append("FROM SaldoDevedorServidor sdr ");
        corpo.append("INNER JOIN sdr.consignataria csa ");
        corpo.append("WHERE 1 = 1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpo.append(" AND sdr.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        corpo.append("ORDER BY csa.csaNome");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                              Columns.CSA_CODIGO,
                              Columns.CSA_IDENTIFICADOR,
                              Columns.CSA_NOME
        };
    }

}
