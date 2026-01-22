package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosNaoIncideMargemQuery;

public class ListaExtratoMargemContratosNaoIncideMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        final String rseCodigo = "123";

        final ListaExtratoMargemContratosNaoIncideMargemQuery query = new ListaExtratoMargemContratosNaoIncideMargemQuery(rseCodigo);

        executarConsulta(query);
    }
}

