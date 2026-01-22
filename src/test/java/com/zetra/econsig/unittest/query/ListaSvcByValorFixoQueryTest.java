package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaSvcByValorFixoQuery;

public class ListaSvcByValorFixoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSvcByValorFixoQuery query = new ListaSvcByValorFixoQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

