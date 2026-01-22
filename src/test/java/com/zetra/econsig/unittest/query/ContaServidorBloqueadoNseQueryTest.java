package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ContaServidorBloqueadoNseQuery;

public class ContaServidorBloqueadoNseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ContaServidorBloqueadoNseQuery query = new ContaServidorBloqueadoNseQuery();

        executarConsulta(query);
    }
}

