package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioCodVerbaRefQuery</p>
 * <p>Description: Lista de convênios ativos com código de verba de referência diferente de nulo.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioPelosIdentificadoresQuery extends HQuery {

    public String csaIdentificador;
    public String estIdentificador;
    public String orgIdentificador;
    public String svcIdentificador;
    public String cnvCodVerba;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select distinct ");
        corpoBuilder.append("svc.svcDescricao, svc.svcIdentificador, org.orgNome, org.orgNomeAbrev, org.orgIdentificador, ");
        corpoBuilder.append("est.estCodigo, est.estNome, est.estNomeAbrev, est.estIdentificador, ");
        corpoBuilder.append("csa.csaNome, csa.csaNomeAbrev, csa.csaIdentificador, ");
        corpoBuilder.append("cnv.cnvCodigo, svc.svcCodigo, org.orgCodigo, csa.csaCodigo, cnv.cnvCodVerba, cnv.cnvCodVerbaRef, cnv.statusConvenio.scvCodigo ");
        corpoBuilder.append("from Convenio cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("inner join org.estabelecimento est ");
        corpoBuilder.append("where 1 = 1 ");
        if (!TextHelper.isNull(csaIdentificador)) {
            corpoBuilder.append(" and csa.csaIdentificador ").append(criaClausulaNomeada("csaIdentificador", csaIdentificador));
        }
        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" and est.estIdentificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }
        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" and org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        }
        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append(" and svc.svcIdentificador ").append(criaClausulaNomeada("svcIdentificador", svcIdentificador));
        }
        if (!TextHelper.isNull(cnvCodVerba)) {
            corpoBuilder.append(" AND cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba",cnvCodVerba));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }
        if (!TextHelper.isNull(csaIdentificador)) {
            defineValorClausulaNomeada("csaIdentificador", csaIdentificador, query);
        }
        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }
        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }
        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_DESCRICAO,
                Columns.SVC_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.ORG_NOME_ABREV,
                Columns.ORG_IDENTIFICADOR,
                Columns.EST_CODIGO,
                Columns.EST_NOME,
                Columns.EST_NOME_ABREV,
                Columns.EST_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_IDENTIFICADOR,
                Columns.CNV_CODIGO,
                Columns.CNV_SVC_CODIGO,
                Columns.CNV_ORG_CODIGO,
                Columns.CNV_CSA_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.CNV_COD_VERBA_REF,
                Columns.CNV_SCV_CODIGO
        };
    }
}
