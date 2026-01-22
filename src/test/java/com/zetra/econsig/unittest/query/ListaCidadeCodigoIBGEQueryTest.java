package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.cidade.ListaCidadeCodigoIBGEQuery;

public class ListaCidadeCodigoIBGEQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCidadeCodigoIBGEQuery query = new ListaCidadeCodigoIBGEQuery();
        query.cidCodigoIbge = "123";

        executarConsulta(query);
    }
}

