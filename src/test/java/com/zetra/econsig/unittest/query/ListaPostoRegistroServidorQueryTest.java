package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaPostoRegistroServidorQuery;

public class ListaPostoRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPostoRegistroServidorQuery query = new ListaPostoRegistroServidorQuery();
        query.count = false;
        query.posCodigo = "123";
        query.posIdentificador = "123";

        executarConsulta(query);
    }
}

