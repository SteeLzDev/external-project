package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.agendamento.ListaOcorrenciaAgendamentoComErroQuery;

public class ListaOcorrenciaAgendamentoComErroQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.util.List<java.lang.String> agdCodigos = java.util.List.of("1", "2");
        java.util.List<java.lang.String> sagCodigos = java.util.List.of("1", "2");
        java.util.List<java.lang.String> tagCodigos = java.util.List.of("1", "2");
        int horasLimite = 1;

        ListaOcorrenciaAgendamentoComErroQuery query = new ListaOcorrenciaAgendamentoComErroQuery(agdCodigos, sagCodigos, tagCodigos, horasLimite);
        query.count = false;

        executarConsulta(query);
    }
}

