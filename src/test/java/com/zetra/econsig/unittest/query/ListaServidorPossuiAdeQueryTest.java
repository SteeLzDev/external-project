package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaServidorPossuiAdeQuery;

public class ListaServidorPossuiAdeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServidorPossuiAdeQuery query = new ListaServidorPossuiAdeQuery();
        query.serCpf = "123";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

