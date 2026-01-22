package com.zetra.econsig.persistence.query.agendamento;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaPorPeriodoQuery</p>
 * <p>Description: Listagem de ocorrência de agendamentos por período.</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaAgendamentoPorPeriodoQuery extends HQuery {

    private final String agdCodigo;
    private final List<String> tocCodigos;
    private final Date dataInicio;
    private final Date dataFim;

    public ListaOcorrenciaAgendamentoPorPeriodoQuery(String agdCodigo, List<String> tocCodigos, Date dataInicio, Date dataFim) {
        this.agdCodigo = agdCodigo;
        this.tocCodigos = tocCodigos;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT ");
        corpo.append("oag.oagCodigo ");

        corpo.append("FROM OcorrenciaAgendamento oag ");
        corpo.append("WHERE 1 = 1 ");

        if(!TextHelper.isNull(agdCodigo)){
            corpo.append(" AND oag.agendamento.agdCodigo ").append(criaClausulaNomeada("agdCodigo", agdCodigo));
        }

        if(tocCodigos != null && !tocCodigos.isEmpty()){
            corpo.append(" AND oag.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        }

        if (!TextHelper.isNull(dataInicio)) {
            corpo.append(" AND oag.oagDataInicio >= :dataInicio ");
        }

        if (!TextHelper.isNull(dataFim)) {
            corpo.append(" AND oag.oagDataFim <= :dataFim ");
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if(!TextHelper.isNull(agdCodigo)){
            defineValorClausulaNomeada("agdCodigo", agdCodigo, query);
        }

        if(tocCodigos != null && !tocCodigos.isEmpty()){
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }

        if (!TextHelper.isNull(dataInicio)) {
            defineValorClausulaNomeada("dataInicio", dataInicio, query);
        }

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", dataFim, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OAG_CODIGO
        };
    }
}
