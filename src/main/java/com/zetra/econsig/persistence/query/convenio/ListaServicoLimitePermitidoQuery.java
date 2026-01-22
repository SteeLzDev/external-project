package com.zetra.econsig.persistence.query.convenio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoLimitePermitidoQuery</p>
 * <p>Description: Listagem de Consignações para validação de limite de contratos</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoLimitePermitidoQuery extends HQuery {

    public String rseCodigo;
    public List<String> cnvCodigo;
    public List<String> svcCodigo;
    public List<String> nseCodigo;
    public boolean ignoraConcluir;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "count(ade.adeCodigo) as TOTAL, " +
                       "svc.svcCodigo, " +
                       "cnv.cnvCodigo, " +
                       "nse.nseCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join svc.naturezaServico nse ");
        corpoBuilder.append("inner join cnv.consignataria csa ");

        corpoBuilder.append(" where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (cnvCodigo != null && !cnvCodigo.isEmpty()) {
            corpoBuilder.append(" AND cnv.cnvCodigo ").append(criaClausulaNomeada("cnvCodigo", cnvCodigo));
        } else if (svcCodigo != null && !svcCodigo.isEmpty()) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        } else if (nseCodigo != null && !nseCodigo.isEmpty()) {
            corpoBuilder.append(" AND svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
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

        if(ignoraConcluir) {
            corpoBuilder.append(" AND NOT EXISTS (select 1 from ParcelaDescontoPeriodo prd WHERE ade.adeCodigo = prd.adeCodigo AND (COALESCE(ade.adePrazo,-1) - ade.adePrdPagas) = 1 ");
            corpoBuilder.append("AND prd.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("') ");
        }

        corpoBuilder.append(" GROUP BY ");
        if (cnvCodigo != null && !cnvCodigo.isEmpty()) {
            corpoBuilder.append("  cnv.cnvCodigo ");
        } else if (svcCodigo != null && !svcCodigo.isEmpty()) {
            corpoBuilder.append(" svc.svcCodigo ");
        } else if (nseCodigo != null && !nseCodigo.isEmpty()) {
            corpoBuilder.append(" svc.naturezaServico.nseCodigo ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (cnvCodigo != null && !cnvCodigo.isEmpty()) {
            defineValorClausulaNomeada("cnvCodigo", cnvCodigo, query);
        } else if (svcCodigo != null && !svcCodigo.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        } else if (nseCodigo != null && !nseCodigo.isEmpty()) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "TOTAL",
                Columns.SVC_CODIGO,
                Columns.CNV_CODIGO,
                Columns.NSE_CODIGO
         };
    }
}
