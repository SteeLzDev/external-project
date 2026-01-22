package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ContaServidorBloqueadoCnvQuery;

public class ContaServidorBloqueadoCnvQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ContaServidorBloqueadoCnvQuery query = new ContaServidorBloqueadoCnvQuery();

        executarConsulta(query);
    }
}

