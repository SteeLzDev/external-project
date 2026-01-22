package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasPagasQuery;

public class ObtemTotalParcelasPagasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalParcelasPagasQuery query = new ObtemTotalParcelasPagasQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";

        executarConsulta(query);
    }
}

