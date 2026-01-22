package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.rescisao.ListarContratosReterVerbaRescisoriaQuery;

public class ListarContratosReterVerbaRescisoriaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarContratosReterVerbaRescisoriaQuery query = new ListarContratosReterVerbaRescisoriaQuery();
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

