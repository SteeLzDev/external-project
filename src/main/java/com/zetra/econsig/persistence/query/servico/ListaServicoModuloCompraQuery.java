package com.zetra.econsig.persistence.query.servico;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoModuloCompraQuery</p>
 * <p>Description: Busca os serviços com a respectiva configuração de parâmetros do módulo
 * avançado de controle de compra.
 * Retorna apenas os serviços ativos que possuam algum convênio ativo, com um órgão e
 * consignatária ativas.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoModuloCompraQuery extends HQuery {
    public String orgCodigo;
    public String csaCodigo;
    public String nseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo =
            "select distinct svc.svcCodigo, " +
            "   svc.svcIdentificador, " +
            "   svc.svcDescricao, " +
            "   psePrazoInformarSaldo.pseVlr, " +
            "   psePrazoEfetuarPagamento.pseVlr, " +
            "   psePrazoLiquidarContrato.pseVlr ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Servico svc ");
        corpoBuilder.append("inner join svc.convenioSet cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("left outer join svc.paramSvcConsignanteSet psePrazoInformarSaldo WITH ");
        corpoBuilder.append("  psePrazoInformarSaldo.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append("left outer join svc.paramSvcConsignanteSet psePrazoEfetuarPagamento WITH ");
        corpoBuilder.append("  psePrazoEfetuarPagamento.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append("left outer join svc.paramSvcConsignanteSet psePrazoLiquidarContrato WITH ");
        corpoBuilder.append("  psePrazoLiquidarContrato.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_LIQUIDACAO_ADE_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append("left outer join svc.paramSvcConsignanteSet psePrazoDesbloqueioAutomatico WITH (");
        corpoBuilder.append("  psePrazoDesbloqueioAutomatico.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF).append("')");
        corpoBuilder.append(" where ((psePrazoInformarSaldo.pseCodigo IS NOT NULL AND NULLIF(TRIM(psePrazoInformarSaldo.pseVlr), '') IS NOT NULL) OR ");
        corpoBuilder.append("        (psePrazoEfetuarPagamento.pseCodigo IS NOT NULL AND NULLIF(TRIM(psePrazoEfetuarPagamento.pseVlr), '') IS NOT NULL) OR ");
        corpoBuilder.append("        (psePrazoLiquidarContrato.pseCodigo IS NOT NULL AND NULLIF(TRIM(psePrazoLiquidarContrato.pseVlr), '') IS NOT NULL)) ");
        corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and (svc.svcAtivo IS NULL OR svc.svcAtivo = ").append(CodedValues.STS_ATIVO).append(")");
        corpoBuilder.append(" and (csa.csaAtivo IS NULL OR csa.csaAtivo = ").append(CodedValues.STS_ATIVO).append(")");
        corpoBuilder.append(" and (org.orgAtivo IS NULL OR org.orgAtivo = ").append(CodedValues.STS_ATIVO).append(")");

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" and svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                "VLR_PRAZO_INFORMAR_SALDO",
                "VLR_PRAZO_EFETUAR_PAGAMENTO",
                "VLR_PRAZO_LIQUIDAR_CONTRATO",
        };
    }
}
