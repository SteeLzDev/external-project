package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaContratosRenegociacaoLiberaMargemQuery;

public class ListaContratosRenegociacaoLiberaMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final ListaContratosRenegociacaoLiberaMargemQuery query = new ListaContratosRenegociacaoLiberaMargemQuery();
        executarConsulta(query);
    }
}

