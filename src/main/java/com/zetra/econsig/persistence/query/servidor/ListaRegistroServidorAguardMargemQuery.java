package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegistroServidorAguardMargemQuery</p>
 * <p>Description: Listagem de registros servidores não excluídos que possuem
 * consignações na situação aguard. margem.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegistroServidorAguardMargemQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT rse.rseCodigo");
        corpoBuilder.append(" FROM RegistroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" WHERE rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        corpoBuilder.append(" AND EXISTS (");
        corpoBuilder.append(" SELECT 1 FROM rse.autDescontoSet ade");
        corpoBuilder.append(" WHERE ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_MARGEM).append("'");
        corpoBuilder.append(" )");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO
        };
    }
}
