package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListaContratosRenegociacaoLiberaMargemQuery extends HQuery {

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT (sum(adeOrigem.adeVlr) - adeDestino.adeVlr) as diferenca, adeDestino.rseCodigo, adeDestino.adeCodigo, adeDestino.adeIncMargem, ");
        corpo.append("csa.csaCodigo, svc.svcCodigo ");
        corpo.append("FROM AutDesconto adeDestino ");
        corpo.append("INNER JOIN adeDestino.ocorrenciaAutorizacaoSet oca ");
        corpo.append("INNER JOIN adeDestino.verbaConvenio vco ");
        corpo.append("INNER JOIN vco.convenio cnv ");
        corpo.append("INNER JOIN cnv.servico svc ");
        corpo.append("INNER JOIN cnv.consignataria csa ");
        corpo.append("INNER JOIN svc.paramSvcConsignanteSet tps WITH tps.tpsCodigo='").append(CodedValues.TPS_PRAZO_DIAS_CANCELAMENTO_RENEGOCIACAO).append("' ");
        corpo.append("LEFT JOIN csa.paramConsignatariaSet tpa WITH tpa.tpaCodigo='").append(CodedValues.TPA_PRAZO_CANCELAMENTO_RENEGOCIACAO_DIAS_UTEIS).append("' ");
        corpo.append("INNER JOIN adeDestino.relacionamentoAutorizacaoByAdeCodigoDestinoSet res ");
        corpo.append("INNER JOIN res.autDescontoByAdeCodigoOrigem adeOrigem ");
        corpo.append("WHERE tntCodigo='").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("' ");
        corpo.append("AND adeDestino.sadCodigo ").append(criaClausulaNomeada("sadCodigoDefAnd", CodedValues.SAD_CODIGOS_DEFERIDAS_OU_ANDAMENTO));
        corpo.append(" AND tps.pseVlr IS NOT NULL and tps.pseVlr !=''  ");
        corpo.append("AND to_numeric(tps.pseVlr) > 0 ");
        corpo.append(" AND oca.tocCodigo ").append(criaClausulaNomeada("tocCodigoRetem", CodedValues.TOC_RETENCAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO));
        corpo.append(" AND NOT EXISTS (SELECT 1 FROM OcorrenciaAutorizacao oca1 WHERE oca1.adeCodigo = adeDestino.adeCodigo ");
        corpo.append(" AND oca1.tocCodigo ").append(criaClausulaNomeada("tocCodigoLibera", CodedValues.TOC_LIBERACAO_MARGEM_APOS_PRAZO_RENEGOCIACAO));
        corpo.append(" ) ");
        corpo.append("AND CASE WHEN tpa.tpaCodigo IS NOT NULL THEN (SELECT COUNT(*) FROM Calendario cal WHERE cal.calDiaUtil = 'S' AND cal.calData BETWEEN res.radData AND data_corrente()) > to_numeric(tps.pseVlr) ");
        corpo.append("       ELSE (to_days(data_corrente()) - to_days(res.radData)) > to_numeric(tps.pseVlr) END  ");
        corpo.append("GROUP BY res.adeCodigoDestino ");
        corpo.append("ORDER BY adeDestino.rseCodigo, res.adeCodigoDestino ");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("tocCodigoRetem", CodedValues.TOC_RETENCAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO, query);
        defineValorClausulaNomeada("tocCodigoLibera", CodedValues.TOC_LIBERACAO_MARGEM_APOS_PRAZO_RENEGOCIACAO, query);
        defineValorClausulaNomeada("sadCodigoDefAnd", CodedValues.SAD_CODIGOS_DEFERIDAS_OU_ANDAMENTO, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                              "DIFERENCA",
                              Columns.RSE_CODIGO,
                              Columns.ADE_CODIGO,
                              Columns.ADE_INC_MARGEM,
                              Columns.CSA_CODIGO,
                              Columns.SVC_CODIGO
        };
    }
}
