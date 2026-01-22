package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaTaxasSuperioresLimiteQuery</p>
 * <p>Description: Listagem de consignatárias que possuem Taxas de Juros/CET que estão superiores
 * à tabela de taxas da consignatária limite.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaTaxasSuperioresLimiteQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "csa.csaCodigo, " +
                       "csa.csaIdentificador, " +
                       "csa.csaNome, " +
                       "csa.csaEmail ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM Consignataria csa");
        corpoBuilder.append(" WHERE EXISTS (SELECT 1");
        corpoBuilder.append(" FROM csa.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.coeficienteAtivoSet cft");
        corpoBuilder.append(" INNER JOIN pzc.prazo prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzcLimite");
        corpoBuilder.append(" INNER JOIN pzcLimite.coeficienteAtivoSet cftLimite");        

        corpoBuilder.append(" WHERE pzcLimite.przCsaCodigo <> pzc.przCsaCodigo");
        corpoBuilder.append(" AND prz.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" AND pzcLimite.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));

        corpoBuilder.append(" AND pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND pzcLimite.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND prz.przAtivo = ").append(CodedValues.STS_ATIVO);

        corpoBuilder.append(" AND cft.cftDataFimVig IS NULL");
        corpoBuilder.append(" AND cftLimite.cftDataFimVig IS NULL");
        corpoBuilder.append(" AND cftLimite.cftDataIniVig > current_date()");

        corpoBuilder.append(" AND cftLimite.cftVlr > 0");
        corpoBuilder.append(" AND cft.cftVlr >= cftLimite.cftVlr");

        corpoBuilder.append(")");

        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {        
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_EMAIL
        };
    }
}
