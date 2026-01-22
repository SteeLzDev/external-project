package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTotalContratosPorGrupoSvcQuery</p>
 * <p>Description: Retorna o total de contratos que um servidor possui para um determinado grupo de serviço.
 * Se informado o código da consignatária, então conta apenas os contratos do grupo de serviço para esta
 * consignatária. Utilizado caso exista limite de contratos de grupo de serviço por consignatária.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalContratosPorGrupoSvcQuery extends HQuery {

    public String rseCodigo;
    public String tgsCodigo;
    public String csaCodigo;
    public List<String> adeCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT COUNT(DISTINCT ade.adeCodigo) ");
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" WHERE svc.tipoGrupoSvc.tgsCodigo ").append(criaClausulaNomeada("tgsCodigo", tgsCodigo));
        corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND ((ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS_LIMITE, "','")).append("'))");
        corpoBuilder.append("   OR (ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_AGUARD_CONF, "','")).append("')");
        corpoBuilder.append("   AND NOT EXISTS (SELECT 1 FROM RelacionamentoAutorizacao rad");
        corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem");
        corpoBuilder.append(" INNER JOIN adeOrigem.verbaConvenio vcoOrigem");
        corpoBuilder.append(" INNER JOIN vcoOrigem.convenio cnvOrigem");
        corpoBuilder.append(" INNER JOIN cnvOrigem.servico svcOrigem");
        corpoBuilder.append(" WHERE ade.adeCodigo = rad.adeCodigoDestino");
        corpoBuilder.append(" AND svcOrigem.tipoGrupoSvc.tgsCodigo ").append(criaClausulaNomeada("tgsCodigo", tgsCodigo));
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnvOrigem.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        corpoBuilder.append(" AND rad.tntCodigo IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("','").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
        corpoBuilder.append(" AND adeOrigem.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_AGUARD_LIQ, "','")).append("')");
        corpoBuilder.append(")))");

        if (adeCodigos != null && adeCodigos.size() > 0) {
            List<String> paramAdeList = new ArrayList<>();
            paramAdeList.add(CodedValues.NOT_EQUAL_KEY);
            paramAdeList.addAll(adeCodigos);
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", paramAdeList));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("tgsCodigo", tgsCodigo, query);

        if (adeCodigos != null && adeCodigos.size() > 0) {
            defineValorClausulaNomeada("adeCodigo", adeCodigos, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }
}
