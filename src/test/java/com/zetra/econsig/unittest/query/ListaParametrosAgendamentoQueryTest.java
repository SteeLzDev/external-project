package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.agendamento.ListaParametrosAgendamentoQuery;

public class ListaParametrosAgendamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String agdCodigo = "123";

        ListaParametrosAgendamentoQuery query = new ListaParametrosAgendamentoQuery(agdCodigo);

        executarConsulta(query);
    }
}

