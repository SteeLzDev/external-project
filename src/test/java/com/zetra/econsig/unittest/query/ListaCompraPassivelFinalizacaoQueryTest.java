package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ListaCompraPassivelFinalizacaoQuery;

public class ListaCompraPassivelFinalizacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCompraPassivelFinalizacaoQuery query = new ListaCompraPassivelFinalizacaoQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.adeCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

