package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamCsaRseQuery;

public class ListaParamCsaRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamCsaRseQuery query = new ListaParamCsaRseQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.tpaCodigo = "123";

        executarConsulta(query);
    }
}

