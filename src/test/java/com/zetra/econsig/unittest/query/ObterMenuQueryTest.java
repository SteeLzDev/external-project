package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.menu.ObterMenuQuery;

public class ObterMenuQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObterMenuQuery query = new ObterMenuQuery();
        query.count = false;
        query.usuCodigo = "123";
        query.papCodigo = "1";
        query.usuCentralizador = "123";

        executarConsulta(query);
    }
}

