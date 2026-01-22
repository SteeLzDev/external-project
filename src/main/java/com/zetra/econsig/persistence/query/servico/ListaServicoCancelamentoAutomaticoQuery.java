package com.zetra.econsig.persistence.query.servico;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoCancelamentoAutomaticoQuery</p>
 * <p>Description: Busca os serviços com a respectiva configuração de parâmetros
 * de cancelamento automático.
 * Retorna apenas os serviços ativos que possuam algum convênio ativo, com um órgão e
 * consignatária ativas.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoCancelamentoAutomaticoQuery extends HQuery {
    public String orgCodigo;
    public String csaCodigo;
    public String nseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo =
            "select distinct svc.svcCodigo, " +
            "   svc.svcIdentificador, " +
            "   svc.svcDescricao, " +
            "   psePrazoConfirmacaoSolicitacoes.pseVlr, " +
            "   psePrazoConfirmacaoReservas.pseVlr, " +
            "   psePrazoConfirmacaoCompras.pseVlr ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Servico svc ");
        corpoBuilder.append("inner join svc.convenioSet cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("left outer join svc.paramSvcConsignanteSet psePrazoConfirmacaoSolicitacoes WITH ");
        corpoBuilder.append("  psePrazoConfirmacaoSolicitacoes.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF).append("' ");
        corpoBuilder.append("left outer join svc.paramSvcConsignanteSet psePrazoConfirmacaoReservas WITH ");
        corpoBuilder.append("  psePrazoConfirmacaoReservas.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_DESBL_RES_NAO_CONF).append("' ");
        corpoBuilder.append("left outer join svc.paramSvcConsignanteSet psePrazoConfirmacaoCompras WITH ");
        corpoBuilder.append("  psePrazoConfirmacaoCompras.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_DESBL_COMP_NAO_CONF).append("' ");
        corpoBuilder.append(" where ((psePrazoConfirmacaoSolicitacoes.pseCodigo IS NOT NULL AND NULLIF(TRIM(psePrazoConfirmacaoSolicitacoes.pseVlr), '') IS NOT NULL) OR ");
        corpoBuilder.append("        (psePrazoConfirmacaoReservas.pseCodigo IS NOT NULL AND NULLIF(TRIM(psePrazoConfirmacaoReservas.pseVlr), '') IS NOT NULL) OR ");
        corpoBuilder.append("        (psePrazoConfirmacaoCompras.pseCodigo IS NOT NULL AND NULLIF(TRIM(psePrazoConfirmacaoCompras.pseVlr), '') IS NOT NULL)) ");
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
                "VLR_PRAZO_CONFIRMACAO_SOLICITACAO",
                "VLR_PRAZO_CONFIRMACAO_RESERVA",
                "VLR_PRAZO_CONFIRMACAO_COMPRA"
        };
    }
}
