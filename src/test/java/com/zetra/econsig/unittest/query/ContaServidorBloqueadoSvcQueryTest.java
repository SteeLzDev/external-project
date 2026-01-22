package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ContaServidorBloqueadoSvcQuery;

public class ContaServidorBloqueadoSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ContaServidorBloqueadoSvcQuery query = new ContaServidorBloqueadoSvcQuery();

        executarConsulta(query);
    }
}

