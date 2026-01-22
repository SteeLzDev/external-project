package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaEnderecosConsignatariaQuery;

public class ListaEnderecosConsignatariaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaEnderecosConsignatariaQuery query = new ListaEnderecosConsignatariaQuery();
        query.csaCodigo = "267";
        query.count = false;

        executarConsulta(query);
    }
}

