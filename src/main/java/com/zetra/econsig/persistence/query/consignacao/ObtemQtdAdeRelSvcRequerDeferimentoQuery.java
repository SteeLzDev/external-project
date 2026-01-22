package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemQtdAdeRelSvcRequerDeferimentoQuery</p>
 * <p>Description: Lista contratos no serviço origem em relacionamento
 * que requer deferimento manual.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemQtdAdeRelSvcRequerDeferimentoQuery extends HQuery {

    public String svcCodigoDestino;
    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select count(ade.adeCodigo) ");
        corpoBuilder.append("from RelacionamentoServico rsv ");
        corpoBuilder.append("inner join rsv.servicoBySvcCodigoOrigem svc ");
        corpoBuilder.append("inner join svc.convenioSet cnv ");
        corpoBuilder.append("inner join cnv.verbaConvenioSet vco ");
        corpoBuilder.append("inner join vco.autDescontoSet ade ");

        corpoBuilder.append("where rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTRATO_PREEXISTENTE_REQUER_DEFERIMENTO).append("'");
        corpoBuilder.append(" and rsv.servicoBySvcCodigoDestino.svcCodigo ").append(criaClausulaNomeada("svcCodigoDestino", svcCodigoDestino));
        corpoBuilder.append(" and ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("svcCodigoDestino", svcCodigoDestino, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("sadCodigo", sadCodigos, query);

        return query;
    }
}
