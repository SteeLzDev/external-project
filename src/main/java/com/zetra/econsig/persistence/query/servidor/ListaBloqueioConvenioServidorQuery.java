package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaBloqueioConvenioServidorQuery</p>
 * <p>Description: Listagem de bloqueio de convênios por servidor</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaBloqueioConvenioServidorQuery extends HQuery {

    public String rseCodigo;
    public String orgCodigo;
    public String csaCodigo;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (count) {
            corpo =
                "select count(*) as total ";
        } else {
            corpo =
                "select " +
                "cnv.cnvCodVerba, " +
                "svc.svcDescricao, " +
                "org.orgNome, " +
                "csa.csaNome, " +
                "pcr.pcrVlr, " +
                "pcr.pcrVlrSer, " +
                "pcr.pcrVlrCsa, " +
                "pcr.pcrVlrCse, " +
                "pcr.pcrObs ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from ParamConvenioRegistroSer pcr");
        corpoBuilder.append(" inner join pcr.convenio cnv ");
        corpoBuilder.append(" inner join cnv.orgao org ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" where nullif(trim(pcr.pcrVlr), '') is not null ");
        corpoBuilder.append(" and pcr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("' ");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" and pcr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!count) {
            corpoBuilder.append(" order by cnv.cnvCodVerba, org.orgNome, csa.csaNome");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_COD_VERBA,
                Columns.SVC_DESCRICAO,
                Columns.ORG_NOME,
                Columns.CSA_NOME,
                Columns.PCR_VLR,
                Columns.PCR_VLR_SER,
                Columns.PCR_VLR_CSA,
                Columns.PCR_VLR_CSE,
                Columns.PCR_OBS
        };
    }
}
