package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicosRelacionadosQuery</p>
 * <p>Description: Lista relacionamentos de um servico</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicosRelacionadosQuery extends HQuery {
    public String svcCodigoOrigem;
    public String tntCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select relServico.tipoNatureza.tntCodigo," +
                       "relServico.relSvcCodigo," +
                       "servico.svcCodigo," +
                       "servico.svcIdentificador," +
                       "relServico.servicoBySvcCodigoOrigem.svcCodigo," +
                       "relServico.servicoBySvcCodigoDestino.svcCodigo," +
                       "servico.svcDescricao," +
                       "case when relServico.tipoNatureza.tntCodigo is null then '' " +
                       "else 'SELECTED' end as SELECTED";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Servico servico ");
        corpoBuilder.append(" left outer join servico.relacionamentoServicoByDestinoSet relServico ");
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
                Columns.RSV_TNT_CODIGO,
                Columns.RSV_CODIGO,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.RSV_SVC_CODIGO_ORIGEM,
                Columns.RSV_SVC_CODIGO_DESTINO,
                Columns.SVC_DESCRICAO,
                "SELECTED"
        };
    }
}
