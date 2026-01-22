package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemContratoSuspensoPrdRejeitadaNaoReativadoQuery</p>
 * <p>Description: Obtem contrato suspenso pela rejeição de parcela não reativado.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemContratoSuspensoPrdRejeitadaNaoReativadoQuery extends HQuery {

    public List<String> adeCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        final String sadSuspenso = CodedValues.SAD_SUSPENSA;

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT ade.adeCodigo");
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", adeCodigos));
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadSuspenso", sadSuspenso));
        corpoBuilder.append(" AND EXISTS");
        corpoBuilder.append(" (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet ocaSuspensaoPrdRejeitada");
        corpoBuilder.append(" WHERE ocaSuspensaoPrdRejeitada.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_SUSPENSAO_CONTRATO_PARCELA_REJEITADA).append("'");
        corpoBuilder.append(" AND NOT EXISTS (select 1 from ade.ocorrenciaAutorizacaoSet ocaReativacaoPrdRejeitada");
        corpoBuilder.append(" where ocaReativacaoPrdRejeitada.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_REATIVACAO_CONTRATO_PARCELA_REJEITADA + "'");
        corpoBuilder.append(" AND ocaReativacaoPrdRejeitada.ocaData > ocaSuspensaoPrdRejeitada.ocaData) ");
        corpoBuilder.append(")");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("adeCodigos", adeCodigos, query);
        defineValorClausulaNomeada("sadSuspenso", sadSuspenso, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }


}
