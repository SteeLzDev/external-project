package com.zetra.econsig.persistence.query.contracheque;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaContrachequeQuery</p>
 * <p>Description: HQuery para listagem de contracheque</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaContrachequeQuery extends HQuery {

    public String rseCodigo;
    public Date ccqPeriodo;
    public boolean obtemUltimo;
    public boolean ordemDesc;

    public Date dataInicio;
    public Date dataFim;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ccq.rseCodigo, ccq.ccqPeriodo, ccq.ccqDataCarga, ccq.ccqTexto");
        corpoBuilder.append(" from ContrachequeRegistroSer ccq");
        corpoBuilder.append(" where ccq.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (ccqPeriodo != null) {
            corpoBuilder.append(" and ccq.ccqPeriodo ").append(criaClausulaNomeada("ccqPeriodo", ccqPeriodo));
        }

        if (dataInicio != null && dataFim != null) {
            corpoBuilder.append(" and ccq.ccqPeriodo BETWEEN :dataInicio and :dataFim");
        }

        corpoBuilder.append(" order by ccq.ccqPeriodo " + (ordemDesc || obtemUltimo ? "DESC" : "ASC"));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (ccqPeriodo != null) {
            defineValorClausulaNomeada("ccqPeriodo", ccqPeriodo, query);
        }

        if (dataInicio != null && dataFim != null) {
            defineValorClausulaNomeada("dataInicio", dataInicio, query);
            defineValorClausulaNomeada("dataFim", dataFim, query);
        }

        if (obtemUltimo) {
            query.setMaxResults(1);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CCQ_RSE_CODIGO,
                Columns.CCQ_PERIODO,
                Columns.CCQ_DATA_CARGA,
                Columns.CCQ_TEXTO
        };
    }
}
