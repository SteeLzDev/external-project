package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariaComTaxasQuery</p>
 * <p>Description: Listagem de consignatárias que possuem Taxas de Juros/CET ativos</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaComTaxasQuery extends HQuery {

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
        corpoBuilder.append(" WHERE pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND prz.przAtivo = ").append(CodedValues.STS_ATIVO);
        
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND prz.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        // Buscando as taxas de juros que estão ativas no momento
        corpoBuilder.append(" AND (cft.cftDataIniVig <= current_date())");
        corpoBuilder.append(" AND (cft.cftDataFimVig >= current_date()");
        corpoBuilder.append("   OR cft.cftDataFimVig IS NULL)");

        corpoBuilder.append(") ORDER BY csa.csaNome");
        
        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

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
