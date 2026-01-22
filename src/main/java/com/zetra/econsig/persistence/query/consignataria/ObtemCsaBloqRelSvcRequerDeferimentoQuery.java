package com.zetra.econsig.persistence.query.consignataria;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemCsaBloqRelSvcRequerDeferimentoQuery</p>
 * <p>Description: Lista consignatárias no serviço origem em relacionamento
 * que requer deferimento manual.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemCsaBloqRelSvcRequerDeferimentoQuery extends HQuery {

    public List<String> adeCodigos;
    public boolean ativo = true;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select csa.csaCodigo ");
        corpoBuilder.append("from RelacionamentoServico rsv ");

        corpoBuilder.append("inner join rsv.servicoBySvcCodigoOrigem svcOri ");
        corpoBuilder.append("inner join svcOri.convenioSet cnvOri ");
        corpoBuilder.append("inner join cnvOri.verbaConvenioSet vcoOri ");
        corpoBuilder.append("inner join vcoOri.autDescontoSet adeOri ");

        corpoBuilder.append("inner join rsv.servicoBySvcCodigoDestino svcDst ");
        corpoBuilder.append("inner join svcDst.convenioSet cnvDst ");
        corpoBuilder.append("inner join cnvDst.verbaConvenioSet vcoDst ");
        corpoBuilder.append("inner join vcoDst.autDescontoSet adeDst ");

        corpoBuilder.append("inner join cnvOri.consignataria csa ");

        corpoBuilder.append("where rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTRATO_PREEXISTENTE_REQUER_DEFERIMENTO).append("'");
        corpoBuilder.append(" and adeOri.registroServidor.rseCodigo = adeDst.registroServidor.rseCodigo");
        corpoBuilder.append(" and adeOri.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));
        corpoBuilder.append(" and adeDst.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigos));

        if (ativo) {
            // Retorna apenas as consignatárias ativas, já que as bloqueadas não podem ser bloqueadas novamente
            corpoBuilder.append(" and coalesce(csa.csaAtivo, ").append(CodedValues.SRS_ATIVO).append(") = ").append(CodedValues.SRS_ATIVO);
        }

        corpoBuilder.append(" group by csa.csaCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
        defineValorClausulaNomeada("adeCodigo", adeCodigos, query);

        return query;
    }
}
