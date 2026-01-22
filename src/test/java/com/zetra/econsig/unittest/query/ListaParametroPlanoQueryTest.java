package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.sdp.plano.ListaParametroPlanoQuery;

public class ListaParametroPlanoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParametroPlanoQuery query = new ListaParametroPlanoQuery();
        query.plaCodigo = "123";
        query.tppCodigo = "123";
        query.count = false;

        executarConsulta(query);
    }
}

