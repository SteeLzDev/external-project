package com.zetra.econsig.persistence.query.servico;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoParametroCompraQuery</p>
 * <p>Description: Busca os serviços com a respectiva configuração de parâmetros de compra
 * de contrato.
 * Retorna apenas os serviços ativos que possuam algum convênio ativo, com um órgão e
 * consignatária ativas.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoParametroCompraQuery extends HQuery {
    public String orgCodigo;
    public String csaCodigo;
    public String nseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo =
                "select distinct svc.svcCodigo, " +
                "   svc.svcIdentificador, " +
                "   svc.svcDescricao, " +
                "   pseQuantidadeMinParcelaPaga.pseVlr, " +
                "   psePercentualMinParcelaPaga.pseVlr, " +
                "   pseQuantidadeMinVigencia.pseVlr, " +
                "   psePercentualMinVigencia.pseVlr ";

            final StringBuilder corpoBuilder = new StringBuilder(corpo);

            corpoBuilder.append("from Servico svc ");
            corpoBuilder.append("inner join svc.convenioSet cnv ");
            corpoBuilder.append("inner join cnv.consignataria csa ");
            corpoBuilder.append("inner join cnv.orgao org ");
            corpoBuilder.append("left outer join svc.paramSvcConsignanteSet pseQuantidadeMinParcelaPaga WITH ");
            corpoBuilder.append("  pseQuantidadeMinParcelaPaga.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA).append("' ");
            corpoBuilder.append("left outer join svc.paramSvcConsignanteSet psePercentualMinParcelaPaga WITH ");
            corpoBuilder.append("   psePercentualMinParcelaPaga.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_COMPRA).append("' ");
            corpoBuilder.append("left outer join svc.paramSvcConsignanteSet pseQuantidadeMinVigencia WITH ");
            corpoBuilder.append("   pseQuantidadeMinVigencia.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_MINIMO_VIGENCIA_COMPRA).append("' ");
            corpoBuilder.append("left outer join svc.paramSvcConsignanteSet psePercentualMinVigencia WITH ");
            corpoBuilder.append("   psePercentualMinVigencia.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA).append("' ");
            corpoBuilder.append("where ((pseQuantidadeMinParcelaPaga.pseCodigo is not null and nullif(trim(pseQuantidadeMinParcelaPaga.pseVlr), '') is not null) or ");
            corpoBuilder.append("       (psePercentualMinParcelaPaga.pseCodigo is not null and nullif(trim(psePercentualMinParcelaPaga.pseVlr), '') is not null) or ");
            corpoBuilder.append("       (pseQuantidadeMinVigencia.pseCodigo is not null and nullif(trim(pseQuantidadeMinVigencia.pseVlr), '') is not null) or ");
            corpoBuilder.append("       (psePercentualMinVigencia.pseCodigo is not null and nullif(trim(psePercentualMinVigencia.pseVlr), '') is not null)) ");
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
                "VLR_QUANTIDADE_MIN_PARCELA_PAGA",
                "VLR_PERCENTUAL_MIN_PARCELA_PAGA",
                "VLR_QUANTIDADE_MIN_VIGENCIA",
                "VLR_PERCENTUAL_MIN_VIGENCIA"
        };
    }
}