package com.zetra.econsig.persistence.query.folha;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemServidorProcessamentoQuery</p>
 * <p>Description: Obtém os servidores para criação de banco de dados em memória</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarServidoresProcessamentoQuery extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ");
        corpoBuilder.append("est.estCodigo, ");
        corpoBuilder.append("est.estIdentificador, ");
        corpoBuilder.append("est.estCnpj, ");
        corpoBuilder.append("org.orgCodigo, ");
        corpoBuilder.append("org.orgIdentificador, ");
        corpoBuilder.append("org.orgCnpj, ");
        corpoBuilder.append("rse.rseCodigo, ");
        corpoBuilder.append("rse.rseMatricula, ");
        corpoBuilder.append("ser.serCodigo, ");
        corpoBuilder.append("ser.serCpf, ");
        corpoBuilder.append("rse.statusRegistroServidor.srsCodigo ");

        corpoBuilder.append("FROM RegistroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN rse.orgao org ");
        corpoBuilder.append("INNER JOIN org.estabelecimento est ");
        corpoBuilder.append("WHERE rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.join(CodedValues.SRS_INATIVOS, "','")).append("') ");

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_CNPJ,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_CNPJ,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_CODIGO,
                Columns.SER_CPF,
                Columns.SRS_CODIGO
        };
    }
}
