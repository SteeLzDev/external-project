package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.retorno.ListaUltimosPeriodosHistConclusaoRetornoQuery;

public class ListaUltimosPeriodosHistConclusaoRetornoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUltimosPeriodosHistConclusaoRetornoQuery query = new ListaUltimosPeriodosHistConclusaoRetornoQuery();
        query.orgCodigos = java.util.List.of("1", "2");
        query.periodo = "2023-01-01";

        executarConsulta(query);
    }
}

