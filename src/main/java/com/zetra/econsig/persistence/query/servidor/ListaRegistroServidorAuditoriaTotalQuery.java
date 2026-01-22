package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegistroServidorAuditoriaTotalQuery</p>
 * <p>Description: Listagem de registro de servidores que devem ser auditados no módulo de auditoria.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegistroServidorAuditoriaTotalQuery extends HQuery {

    public boolean recuperaRseExcluido = true;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String auditoriaTotal = CodedValues.TPC_SIM;

        String corpo = "";
        corpo = "select " +
                "rse.rseCodigo, " +
                "rse.rseMatricula, " +
                "srs.srsCodigo, " +
                "srs.srsDescricao, " +
                "rse.rseTipo, " +
                "est.estIdentificador, " +
                "org.orgIdentificador, " +
                "org.orgNome ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM RegistroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");
        corpoBuilder.append(" WHERE 1 = 1");

        corpoBuilder.append(" AND rse.rseAuditoriaTotal ").append(criaClausulaNomeada("auditoriaTotal", auditoriaTotal));

        corpoBuilder.append(" AND srs.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("auditoriaTotal", auditoriaTotal, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SRS_CODIGO,
                Columns.SRS_DESCRICAO,
                Columns.RSE_TIPO,
                Columns.EST_IDENTIFICADOR,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME
        };
    }
}
