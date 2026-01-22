package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRelacionamentosQuery</p>
 * <p>Description: Lista relacionamentos de servicos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRelacionamentosQuery extends HQuery {
    public String tntCodigo;
    public String svcCodigoOrigem;
    public String svcCodigoDestino;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select relServico.relSvcCodigo," +
                       "relServico.tipoNatureza.tntCodigo," +
                       "relServico.servicoBySvcCodigoOrigem.svcCodigo," +
                       "relServico.servicoBySvcCodigoDestino.svcCodigo";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from RelacionamentoServico relServico ");
        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(tntCodigo)) {
            corpoBuilder.append(" and relServico.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigo));
        }

        if (!TextHelper.isNull(svcCodigoOrigem)) {
            corpoBuilder.append(" and relServico.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem));
        }

        if (!TextHelper.isNull(svcCodigoDestino)) {
            corpoBuilder.append(" and relServico.servicoBySvcCodigoDestino.svcCodigo ").append(criaClausulaNomeada("svcCodigoDestino", svcCodigoDestino));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(tntCodigo)) {
            defineValorClausulaNomeada("tntCodigo", tntCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigoOrigem)) {
            defineValorClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem, query);
        }
        if (!TextHelper.isNull(svcCodigoDestino)) {
            defineValorClausulaNomeada("svcCodigoDestino", svcCodigoDestino, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSV_CODIGO,
                Columns.RSV_TNT_CODIGO,
                Columns.RSV_SVC_CODIGO_ORIGEM,
                Columns.RSV_SVC_CODIGO_DESTINO
        };
    }
}
