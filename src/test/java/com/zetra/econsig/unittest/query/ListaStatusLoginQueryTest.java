package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaStatusLoginQuery;

public class ListaStatusLoginQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaStatusLoginQuery query = new ListaStatusLoginQuery();

        executarConsulta(query);
    }
}

