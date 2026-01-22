package com.zetra.econsig.persistence.query.folha;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarConveniosProcessamentoQuery</p>
 * <p>Description: Obtém os convênios para criação de banco de dados em memória</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarConveniosProcessamentoQuery  extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ");
        corpoBuilder.append("cnv.cnvCodigo, ");
        corpoBuilder.append("cnv.cnvCodVerba, ");
        corpoBuilder.append("svc.svcCodigo, ");
        corpoBuilder.append("svc.svcIdentificador, ");
        corpoBuilder.append("csa.csaCodigo, ");
        corpoBuilder.append("csa.csaIdentificador, ");
        corpoBuilder.append("csa.csaCnpj, ");
        corpoBuilder.append("est.estCodigo, ");
        corpoBuilder.append("est.estIdentificador, ");
        corpoBuilder.append("est.estCnpj, ");
        corpoBuilder.append("org.orgCodigo, ");
        corpoBuilder.append("org.orgIdentificador, ");
        corpoBuilder.append("org.orgCnpj ");

        corpoBuilder.append("FROM Convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("INNER JOIN cnv.orgao org ");
        corpoBuilder.append("INNER JOIN org.estabelecimento est ");
        corpoBuilder.append("WHERE cnv.statusConvenio.scvCodigo != '").append(CodedValues.SCV_CANCELADO).append("' ");
        corpoBuilder.append("AND nullif(substituir(trim(cnv.cnvCodVerba), '0', ''), '') is not null ");

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
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_CNPJ,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_CNPJ,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_CNPJ
        };
    }
}
