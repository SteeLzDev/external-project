package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemContratoPrdRejeitadaReativadoQuery</p>
 * <p>Description: Obtem contrato suspenso pela rejeição de parcela reativado.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemContratoPrdRejeitadaReativadoQuery extends HQuery {

    public List<String> adeCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        final String sadAguarDeferimento = CodedValues.SAD_AGUARD_DEFER;

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT ade.adeCodigo");
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", adeCodigos));
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadAguarDeferimento", sadAguarDeferimento));
        corpoBuilder.append(" AND EXISTS");
        corpoBuilder.append(" (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet ocaReativacaoPrdRejeitada");
        corpoBuilder.append(" WHERE ocaReativacaoPrdRejeitada.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_REATIVACAO_CONTRATO_PARCELA_REJEITADA).append("'");
        corpoBuilder.append(" AND NOT EXISTS (select 1 from ade.ocorrenciaAutorizacaoSet ocaSuspensaoPrdRejeitada");
        corpoBuilder.append(" where ocaSuspensaoPrdRejeitada.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_SUSPENSAO_CONTRATO_PARCELA_REJEITADA + "'");
        corpoBuilder.append(" AND ocaSuspensaoPrdRejeitada.ocaData > ocaReativacaoPrdRejeitada.ocaData) ");
        corpoBuilder.append(")");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("adeCodigos", adeCodigos, query);
        defineValorClausulaNomeada("sadAguarDeferimento", sadAguarDeferimento, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }


}
