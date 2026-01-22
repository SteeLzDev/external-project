package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoRelacionamentoSvcQuery</p>
 * <p>Description: Lista de servicos com determinado tipo de relacionamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoRelacionamentoSvcQuery extends HQuery {

    public String svcCodigoOrigem;
    public String tntCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = " select " +
                       " servico.svcCodigo, " +
                       " servico.tipoGrupoSvc.tgsCodigo, " +
                       " servico.svcIdentificador, " +
                       " servico.svcDescricao, " +
                       " servico.svcAtivo, " +
                       " servico.svcPrioridade ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Servico servico ");
        corpoBuilder.append(" inner join servico.relacionamentoServicoByDestinoSet relServico ");
        corpoBuilder.append(" with (relServico.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem));
        corpoBuilder.append(" and relServico.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigo)).append(")");
        corpoBuilder.append(" where 1=1 ");
        corpoBuilder.append(" order by servico.svcDescricao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem, query);
        defineValorClausulaNomeada("tntCodigo", tntCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_TGS_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.SVC_ATIVO,
                Columns.SVC_PRIORIDADE
        };
    }
}
