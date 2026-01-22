package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoLimiteQuery</p>
 * <p>Description: Listagem de Consignações para validação de limite de contratos</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoLimiteQuery extends HQuery {

    public String rseCodigo;
    public String cnvCodigo;
    public String svcCodigo;
    public String nseCodigo;
    public String csaCodigo;
    public List<String> adeCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("ade.adeCodigo, ");
        corpoBuilder.append("ade.adePrazo, ");
        corpoBuilder.append("ade.adePrdPagas, ");

        corpoBuilder.append("coalesce((select count(*) from ade.parcelaDescontoPeriodoSet prd where ");
        corpoBuilder.append("prd.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("'), 0) as QTD_PRD_EM_PROCESSAMENTO ");

        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.consignataria csa ");

        corpoBuilder.append(" where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (!TextHelper.isNull(cnvCodigo)) {
            corpoBuilder.append(" AND cnv.cnvCodigo ").append(criaClausulaNomeada("cnvCodigo", cnvCodigo));
        } else if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        } else if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" AND svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        } else if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        corpoBuilder.append(" AND ((ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS_LIMITE, "','")).append("'))");
        corpoBuilder.append("   OR (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_AGUARD_CONF, "','")).append("')");
        corpoBuilder.append("   AND NOT EXISTS (SELECT 1 FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad");
        corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem");
        corpoBuilder.append(" WHERE rad.tipoNatureza.tntCodigo IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO);
        corpoBuilder.append("','").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
        corpoBuilder.append(" AND adeOrigem.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_AGUARD_LIQ, "','")).append("')");
        corpoBuilder.append(" AND adeOrigem.verbaConvenio.vcoCodigo = vco.vcoCodigo");
        corpoBuilder.append(" AND adeOrigem.registroServidor.rseCodigo = ade.registroServidor.rseCodigo");
        corpoBuilder.append(")))");

        if (adeCodigos != null && adeCodigos.size() > 0) {
            List<String> codigos = new ArrayList<>(adeCodigos);
            codigos.add(CodedValues.NOT_EQUAL_KEY);
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", codigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(cnvCodigo)) {
            defineValorClausulaNomeada("cnvCodigo", cnvCodigo, query);
        } else if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        } else if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        } else if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (adeCodigos != null && adeCodigos.size() > 0) {
            defineValorClausulaNomeada("adeCodigos", adeCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                "QTD_PRD_EM_PROCESSAMENTO"
         };
    }
}
