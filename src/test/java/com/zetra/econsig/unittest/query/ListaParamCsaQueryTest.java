package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamCsaQuery;

public class ListaParamCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamCsaQuery query = new ListaParamCsaQuery();
        query.csaCodigo = "267";
        query.tpaCodigo = "123";
        query.tpaCseAltera = "123";
        query.tpaCsaAltera = "123";
        query.tpaSupAltera = "123";

        executarConsulta(query);
    }
}

