package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioAtivoHistoricoQuery</p>
 * <p>Description: Listagem de convênios ativos pela natureza do serviço.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioAtivoHistoricoQuery extends HQuery {
    public String nseCodigo;
    public String orgCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo =
            "select " +
            "cnv.cnvCodigo, " +
            "cnv.orgao.orgCodigo, " +
            "cnv.consignataria.csaCodigo, " +
            "cnv.servico.svcCodigo, " +
            "vco.vcoCodigo, " +
            "coalesce(pseIncMargem.pseVlr, '1'), " +
            "coalesce(pseIntFolha.pseVlr, '1'), " +
            "coalesce(pseTipoVlr.pseVlr, 'F'), " +
            "coalesce(pseMaxPrazo.pseVlr, '-1') "
            ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from VerbaConvenio vco");
        corpoBuilder.append(" inner join vco.convenio cnv");
        corpoBuilder.append(" inner join cnv.servico svc");

        corpoBuilder.append(" left outer join svc.paramSvcConsignanteSet pseIncMargem");
        corpoBuilder.append(" with pseIncMargem.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("'");
        corpoBuilder.append(" left outer join svc.paramSvcConsignanteSet pseIntFolha");
        corpoBuilder.append(" with pseIntFolha.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_INTEGRA_FOLHA).append("'");
        corpoBuilder.append(" left outer join svc.paramSvcConsignanteSet pseTipoVlr");
        corpoBuilder.append(" with pseTipoVlr.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_TIPO_VLR).append("'");
        corpoBuilder.append(" left outer join svc.paramSvcConsignanteSet pseMaxPrazo");
        corpoBuilder.append(" with pseMaxPrazo.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_MAX_PRAZO).append("'");

        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" AND svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO,
                Columns.ORG_CODIGO,
                Columns.CSA_CODIGO,
                Columns.SVC_CODIGO,
                Columns.VCO_CODIGO,
                Columns.ADE_INC_MARGEM,
                Columns.ADE_INT_FOLHA,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_PRAZO
        };
    }
}
