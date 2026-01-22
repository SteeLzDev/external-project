package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemTotalConsignacaoRegraLimiteQuery</p>
 * <p>Description: Obtém o total de consignações que o registro servidor possui seguindo
 * os campos preenchidos na regra de limite de operação</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 */
public class ObtemTotalConsignacaoRegraLimiteQuery extends HQuery {

    private final String rseCodigo;
    private final String nseCodigo;
    private final String svcCodigo;
    private final String ncaCodigo;
    private final String csaCodigo;
    private final String corCodigo;
    private final List<String> adeCodigos;

    public ObtemTotalConsignacaoRegraLimiteQuery(String rseCodigoOperacao, List<String> adeCodigosRenegociacao, TransferObject regra) {
        rseCodigo = rseCodigoOperacao;
        nseCodigo = regra.getAttribute(Columns.NSE_CODIGO) != null ? regra.getAttribute(Columns.NSE_CODIGO).toString() : null;
        svcCodigo = regra.getAttribute(Columns.SVC_CODIGO) != null ? regra.getAttribute(Columns.SVC_CODIGO).toString() : null;
        ncaCodigo = regra.getAttribute(Columns.NCA_CODIGO) != null ? regra.getAttribute(Columns.NCA_CODIGO).toString() : null;
        csaCodigo = regra.getAttribute(Columns.CSA_CODIGO) != null ? regra.getAttribute(Columns.CSA_CODIGO).toString() : null;
        corCodigo = regra.getAttribute(Columns.COR_CODIGO) != null ? regra.getAttribute(Columns.COR_CODIGO).toString() : null;
        adeCodigos = adeCodigosRenegociacao;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select count(*) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" AND svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if (!TextHelper.isNull(ncaCodigo)) {
            corpoBuilder.append(" AND csa.naturezaConsignataria.ncaCodigo ").append(criaClausulaNomeada("ncaCodigo", ncaCodigo));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" AND ade.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        corpoBuilder.append(" AND ((ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS_LIMITE, "','")).append("'))");
        corpoBuilder.append("   OR (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_AGUARD_CONF, "','")).append("')");
        corpoBuilder.append("   AND NOT EXISTS (");
        corpoBuilder.append("       SELECT 1 FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad");
        corpoBuilder.append("       INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem");
        corpoBuilder.append("       WHERE rad.tipoNatureza.tntCodigo IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("','").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
        corpoBuilder.append("         AND adeOrigem.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_AGUARD_LIQ, "','")).append("')");
        corpoBuilder.append("         AND adeOrigem.verbaConvenio.vcoCodigo = vco.vcoCodigo");
        corpoBuilder.append("         AND adeOrigem.registroServidor.rseCodigo = ade.registroServidor.rseCodigo");
        corpoBuilder.append("    )");
        corpoBuilder.append("  )");
        corpoBuilder.append(")");

        if (adeCodigos != null && adeCodigos.size() > 0) {
            List<String> codigos = new ArrayList<>(adeCodigos);
            codigos.add(CodedValues.NOT_EQUAL_KEY);
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", codigos));
        }

        if (ParamSist.paramEquals(CodedValues.TPC_IGNORA_CONTRATOS_A_CONCLUIR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // PRAZO - PAGAS > 1 (ainda tem mais de uma parcela a ser paga) ou se é a última, ainda não existe parcela em processamento
            corpoBuilder.append(" AND (COALESCE(ade.adePrazo, 99999) - COALESCE(ade.adePrdPagas, 0) > 1");
            corpoBuilder.append(" OR NOT EXISTS (");
            corpoBuilder.append("   SELECT 1 FROM ade.parcelaDescontoPeriodoSet prd ");
            corpoBuilder.append("   where prd.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("'");
            corpoBuilder.append(" )");
            corpoBuilder.append(")");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(ncaCodigo)) {
            defineValorClausulaNomeada("ncaCodigo", ncaCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }
        if (adeCodigos != null && adeCodigos.size() > 0) {
            defineValorClausulaNomeada("adeCodigos", adeCodigos, query);
        }

        return query;
    }
}
