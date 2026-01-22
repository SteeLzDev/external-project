package com.zetra.econsig.persistence.query.servico;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoNaturezaSemTransfQuery</p>
 * <p>Description: Lista os serviços de uma natureza que não permite
 * transferência de contratos.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoNaturezaSemTransfQuery extends HQuery {

    public boolean count;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT "
                     + (count ? "count(*)" : "svc.svcCodigo")
                     + " from Servico svc"
                     + " inner join svc.naturezaServico nse"
                     + " where nse.nseTransferirAde = 'N'";

        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO
        };
    }
}
