package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.agendamento.ListaTipoAgendamentoQuery;

public class ListaTipoAgendamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.util.List<java.lang.String> tagCodigos = java.util.List.of("1", "2");

        ListaTipoAgendamentoQuery query = new ListaTipoAgendamentoQuery(tagCodigos);

        executarConsulta(query);
    }
}

