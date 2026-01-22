package com.zetra.econsig.persistence.query.servico;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoNaturezaServicoQuery</p>
 * <p>Description: Lista os serviços de uma natureza de serviço dada.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26246 $
 * $Date: 2019-02-14 09:27:49 -0200 (Qui, 14 fev 2019) $
 */
public class ListaServicoNaturezaServicosQuery extends HQuery {

    public List<String> nseCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT svc.svcCodigo, svc.svcIdentificador, svc.svcDescricao " +
                       " FROM Servico svc" +
                       " INNER JOIN svc.naturezaServico nse" +
                       " WHERE 1=1";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (nseCodigos != null && !nseCodigos.isEmpty()) {
            corpoBuilder.append(" and nse.nseCodigo ").append(criaClausulaNomeada("nseCodigos", nseCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (nseCodigos != null && !nseCodigos.isEmpty()) {
            defineValorClausulaNomeada("nseCodigos", nseCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO
        };
    }
}