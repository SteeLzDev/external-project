package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamNseRseQuery;

public class ListaParamNseRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamNseRseQuery query = new ListaParamNseRseQuery();
        query.rseCodigo = "123";
        query.nseCodigo = "123";
        query.tpsCodigo = "123";

        executarConsulta(query);
    }
}

