package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ListaComprasParaConclusaoQuery;

public class ListaComprasParaConclusaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaComprasParaConclusaoQuery query = new ListaComprasParaConclusaoQuery();

        executarConsulta(query);
    }
}

