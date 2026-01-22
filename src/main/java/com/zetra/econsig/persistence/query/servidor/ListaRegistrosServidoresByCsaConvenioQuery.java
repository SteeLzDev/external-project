package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegistrosServidoresByCsaConvenioQuery</p>
 * <p>Description: Lista registros servidores de acordo com os filtros passados.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegistrosServidoresByCsaConvenioQuery extends HQuery {

    public String csaCodigo;
    public String cnvCodVerba;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";
            corpo = "select DISTINCT " +
                    "rse.rseCodigo, " +
                    "ser.serCodigo, " +
                    "ser.serNome " ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM AutDesconto aut");
        corpoBuilder.append(" INNER JOIN aut.registroServidor rse");
        corpoBuilder.append(" INNER JOIN aut.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" WHERE 1 = 1");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(cnvCodVerba)) {
            corpoBuilder.append(" AND cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.SER_CODIGO,
                Columns.SER_NOME
        };
    }
}
