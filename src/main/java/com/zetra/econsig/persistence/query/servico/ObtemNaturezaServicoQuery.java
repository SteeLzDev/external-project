package com.zetra.econsig.persistence.query.servico;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemNaturezaServicoQuery</p>
 * <p>Description: recupera a natureza de serviço de um serviço dado.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemNaturezaServicoQuery extends HQuery {

    public String svcCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                "nse.nseCodigo, " +
                "nse.nseDescricao " +
                "from Servico svc " +
                "inner join svc.naturezaServico nse " +
                "where svc.svcCodigo " + criaClausulaNomeada("svcCodigo", svcCodigo);

        Query<Object[]> query = instanciarQuery(session, corpo);

        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.NSE_CODIGO,
                Columns.NSE_DESCRICAO
        };
    }

}
