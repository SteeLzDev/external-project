package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaCargoRegistroServidorQuery;

public class ListaCargoRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCargoRegistroServidorQuery query = new ListaCargoRegistroServidorQuery();
        query.crsCodigo = "123";
        query.crsIdentificador = "123";

        executarConsulta(query);
    }
}

