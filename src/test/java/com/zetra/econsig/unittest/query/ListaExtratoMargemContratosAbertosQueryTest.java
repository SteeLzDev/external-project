package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosAbertosQuery;

public class ListaExtratoMargemContratosAbertosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";

        ListaExtratoMargemContratosAbertosQuery query = new ListaExtratoMargemContratosAbertosQuery(rseCodigo);

        executarConsulta(query);
    }
}

