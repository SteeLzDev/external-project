package com.zetra.econsig.persistence.query.historico;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarHistoricoServidorQuery</p>
 * <p>Description: Listagem de histórico de servidor.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarHistoricoServidorQuery extends HQuery {

    public String rseMatricula;
    public String serCpf;
    public String orgCodigo;
    public String estCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT ser.serCodigo, ser.serNome, ser.serCpf, rse.rseMatricula, ser.serNome AS USU_NOME ");
        corpoBuilder.append(" FROM HtServidor ser ");
        corpoBuilder.append(" INNER JOIN ser.registroServidorSet rse ");
        corpoBuilder.append(" WHERE 1 = 1 ");

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" AND rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" AND ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND rse.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" AND rse.orgao.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.RSE_MATRICULA,
                Columns.USU_NOME
        };
    }
}
