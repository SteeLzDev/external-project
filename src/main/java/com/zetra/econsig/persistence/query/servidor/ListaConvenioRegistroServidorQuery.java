package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioRegistroServidorQuery</p>
 * <p>Description: Listagem de convênios do servidor, juntamente com os
 * bloqueios de verba, caso existam.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioRegistroServidorQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public String svcCodigo;
    public Boolean inativosSomenteComBloqueio;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "svc.svcCodigo, " +
                "svc.svcIdentificador, " +
                "svc.svcDescricao, " +
                "csa.csaCodigo, " +
                "csa.csaIdentificador, " +
                "csa.csaNome, " +
                "csa.csaNomeAbrev, " +
                "cnv.cnvCodigo, " +
                "cnv.cnvCodVerba, " +
                "pcr.pcrVlr, " +
                "pcr.pcrVlrSer, " +
                "pcr.pcrVlrCsa, " +
                "pcr.pcrVlrCse, " +
                "pcr.pcrObs ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" left outer join cnv.paramConvenioRegistroSerSet pcr WITH ");
        corpoBuilder.append(" pcr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("' and ");
        corpoBuilder.append(" pcr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        corpoBuilder.append(" where cnv.orgao.orgCodigo = (select rse.orgao.orgCodigo from RegistroServidor rse where rse.rseCodigo ");
        corpoBuilder.append(criaClausulaNomeada("rseCodigo", rseCodigo)).append(")");

        if (inativosSomenteComBloqueio != null && inativosSomenteComBloqueio) {
            corpoBuilder.append(" and ( cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" or pcr.pcrVlr is not null ) ");
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        corpoBuilder.append(" order by cnv.cnvCodVerba, svc.svcDescricao, csa.csaNome");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.PCR_VLR,
                Columns.PCR_VLR_SER,
                Columns.PCR_VLR_CSA,
                Columns.PCR_VLR_CSE,
                Columns.PCR_OBS
        };
    }
}
