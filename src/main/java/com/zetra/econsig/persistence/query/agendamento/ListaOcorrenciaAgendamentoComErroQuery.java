package com.zetra.econsig.persistence.query.agendamento;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAgendamentosQuery</p>
 * <p>Description: Listagem de ocorrência de agendamentos com erro.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaAgendamentoComErroQuery extends HQuery {

    private final List<String> agdCodigos;
    private final List<String> sagCodigos;
    private final List<String> tagCodigos;
    private int horasLimite;
    private final String dataFim;
    public boolean count;

    public ListaOcorrenciaAgendamentoComErroQuery(List<String> agdCodigos, List<String> sagCodigos, List<String> tagCodigos, int horasLimite) {
        this.agdCodigos = agdCodigos;
        this.sagCodigos = sagCodigos;
        this.tagCodigos = tagCodigos;
        this.horasLimite = horasLimite;
        // informa a data limite da execução dos processos agendados
        Date dataLimite = new Date();
        if (this.horasLimite <= 0) {
            this.horasLimite = 30;
        }
        dataLimite = new Date(dataLimite.getTime() - this.horasLimite * 60 * 60 * 1000);
        dataFim = DateHelper.format(dataLimite,"yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("select ");

        if (!count) {
            corpo.append("agd.agdCodigo ");
        } else {
            corpo.append("count(*) as total ");
        }

        corpo.append("from Agendamento agd ");
        corpo.append("where not exists ( ");
        corpo.append("select 1 ");
        corpo.append("from OcorrenciaAgendamento oag ");
        corpo.append("where agd.agdCodigo = oag.agendamento.agdCodigo ");
        corpo.append("and oag.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_PROCESSAMENTO_AGENDAMENTO).append("' ");
        if (!TextHelper.isNull(dataFim)) {
            corpo.append("and oag.oagDataFim > :dataFim ");
        }
        corpo.append(") ");

        if (agdCodigos != null && !agdCodigos.isEmpty()) {
            corpo.append(" and agd.agdCodigo ").append(criaClausulaNomeada("agdCodigos", agdCodigos));
        }

        if (sagCodigos != null && !sagCodigos.isEmpty()) {
            corpo.append(" and agd.statusAgendamento.sagCodigo ").append(criaClausulaNomeada("sagCodigos", sagCodigos));
        }

        if (tagCodigos != null && !tagCodigos.isEmpty()) {
            corpo.append(" and agd.tipoAgendamento.tagCodigo ").append(criaClausulaNomeada("tagCodigos", tagCodigos));
        }

        if (!count) {
            corpo.append(" order by agd.agdDataCadastro desc, agd.agdDataPrevista desc ");
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        if (agdCodigos != null && !agdCodigos.isEmpty()) {
            defineValorClausulaNomeada("agdCodigos", agdCodigos, query);
        }

        if (sagCodigos != null && !sagCodigos.isEmpty()) {
            defineValorClausulaNomeada("sagCodigos", sagCodigos, query);
        }

        if (tagCodigos != null && !tagCodigos.isEmpty()) {
            defineValorClausulaNomeada("tagCodigos", tagCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.AGD_CODIGO
        };
    }
}
