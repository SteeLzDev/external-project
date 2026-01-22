package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCsaCoeficienteAtivoQuery</p>
 * <p>Description: Listagem de consignatárias que possuem coeficientes ativos.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsaCoeficienteAtivoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT distinct csa.csaCodigo, ");
        corpoBuilder.append(" csa.csaIdentificador, ");
        corpoBuilder.append(" csa.csaNome, ");
        corpoBuilder.append(" csa.csaNomeAbrev, ");
        corpoBuilder.append(" csa.csaAtivo, ");
        corpoBuilder.append(" csa.csaEmail, ");
        corpoBuilder.append(" csa.csaResponsavel, ");
        corpoBuilder.append(" csa.csaResponsavel2, ");
        corpoBuilder.append(" csa.csaResponsavel3 ");

        corpoBuilder.append(" FROM CoeficienteAtivo cfa");
        corpoBuilder.append(" INNER JOIN cfa.prazoConsignataria pzc");
        corpoBuilder.append(" INNER JOIN pzc.prazo prz");
        corpoBuilder.append(" INNER JOIN pzc.consignataria csa");
        corpoBuilder.append(" WHERE 1=1 ");
        corpoBuilder.append(" AND pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND prz.przAtivo = ").append(CodedValues.STS_ATIVO);

        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_ATIVO,
                Columns.CSA_EMAIL,
                Columns.CSA_RESPONSAVEL,
                Columns.CSA_RESPONSAVEL_2,
                Columns.CSA_RESPONSAVEL_3
        };
    }
}
