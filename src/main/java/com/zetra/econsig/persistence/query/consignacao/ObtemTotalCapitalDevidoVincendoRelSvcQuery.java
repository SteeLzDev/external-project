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
 * <p>Title: ObtemTotalCapitalDevidoVincendoRelSvcQuery</p>
 * <p>Description: Retorna o valor total do capital devido vincendo dos contratos abertos .
 * de um registro servidor para um dado relacionamento de serviço.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalCapitalDevidoVincendoRelSvcQuery extends HQuery {

    public String rseCodigo;
    public String svcCodigo;
    public String periodoAtual;
    public List<String> adeCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigosNotIn = new ArrayList<String>();
        // Não retorna contratos já finalizados
        sadCodigosNotIn.addAll(CodedValues.SAD_CODIGOS_INATIVOS);
        // Não retorna contratos em situação de renegociação/compra uma vez que o
        // novo contrato normalmente terá um valor de capital devido maior
        sadCodigosNotIn.addAll(CodedValues.SAD_CODIGOS_AGUARD_LIQ);

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT SUM(ade.adeVlr * max_value(month_diff(:periodo, ade.adeAnoMesFim)+1, 0)) ");
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");

        corpoBuilder.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo NOT IN ('").append(TextHelper.join(sadCodigosNotIn, "','")).append("')");
        corpoBuilder.append(" AND ade.adeAnoMesFim IS NOT NULL");

        corpoBuilder.append(" AND (cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append("   OR EXISTS (SELECT 1 FROM RelacionamentoServico rsv");
        corpoBuilder.append(" WHERE cnv.servico.svcCodigo = rsv.servicoBySvcCodigoDestino.svcCodigo");
        corpoBuilder.append(" AND rsv.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" AND rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_SERVICOS_COM_LIMITE_CAPITAL_DEVIDO).append("')");
        corpoBuilder.append(")");

        if (adeCodigos != null && adeCodigos.size() > 0) {
            List<String> paramAdeList = new ArrayList<String>();
            paramAdeList.add(CodedValues.NOT_EQUAL_KEY);
            paramAdeList.addAll(adeCodigos);
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", paramAdeList));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        defineValorClausulaNomeada("periodo", parseDateString(periodoAtual), query);

        if (adeCodigos != null && adeCodigos.size() > 0) {
            defineValorClausulaNomeada("adeCodigo", adeCodigos, query);
        }

        return query;
    }
}
