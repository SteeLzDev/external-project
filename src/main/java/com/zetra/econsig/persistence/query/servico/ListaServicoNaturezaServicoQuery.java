package com.zetra.econsig.persistence.query.servico;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoNaturezaServicoQuery</p>
 * <p>Description: Lista os serviços de uma natureza de serviço dada.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoNaturezaServicoQuery extends HQuery {

    public String nseCodigo;
    public String svcIdentificador;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                "svc.svcCodigo, " +
                "nse.nseCodigo, " +
                "nse.nseRetemVerba " +
                "from Servico svc " +
                "inner join svc.naturezaServico nse where 1=1";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" and nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append(" and svc.svcIdentificador ").append(criaClausulaNomeada("svcIdentificador", svcIdentificador));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.NSE_CODIGO,
                Columns.NSE_RETEM_VERBA
        };
    }

}
