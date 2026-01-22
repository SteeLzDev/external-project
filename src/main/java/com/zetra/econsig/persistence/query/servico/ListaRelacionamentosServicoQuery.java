package com.zetra.econsig.persistence.query.servico;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRelacionamentosServicoQuery</p>
 * <p>Description: Lista relacionamentos de serviço de acordo com filtros</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRelacionamentosServicoQuery extends HQuery {

    public Object svcCodigoOrigem;
    public Object svcCodigoDestino;
    public Object tntCodigo;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo =
                    "select rel.relSvcCodigo, " +
                    "   rel.servicoBySvcCodigoOrigem.svcCodigo, " +
                    "   rel.servicoBySvcCodigoDestino.svcCodigo, " +
                    "   rel.tipoNatureza.tntCodigo ";
        } else {
            corpo = "select count(*) ";
        }


        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from RelacionamentoServico rel ");
        corpoBuilder.append("where 1 = 1 ");

        if (svcCodigoOrigem != null) {
            corpoBuilder.append(" and rel.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem));
        }

        if (svcCodigoDestino != null) {
            corpoBuilder.append(" and rel.servicoBySvcCodigoDestino.svcCodigo ").append(criaClausulaNomeada("svcCodigoDestino", svcCodigoDestino));
        }

        if (tntCodigo != null) {
            corpoBuilder.append(" and rel.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (tntCodigo != null) {
            defineValorClausulaNomeada("tntCodigo", tntCodigo, query);
        }

        if (svcCodigoOrigem != null) {
            defineValorClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem, query);
        }

        if (svcCodigoDestino != null) {
            defineValorClausulaNomeada("svcCodigoDestino", svcCodigoDestino, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSV_CODIGO,
                Columns.RSV_SVC_CODIGO_ORIGEM,
                Columns.RSV_SVC_CODIGO_DESTINO,
                Columns.RSV_TNT_CODIGO
        };
    }

}
