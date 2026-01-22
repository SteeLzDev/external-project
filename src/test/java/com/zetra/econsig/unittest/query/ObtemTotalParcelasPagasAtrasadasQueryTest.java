package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasPagasAtrasadasQuery;

public class ObtemTotalParcelasPagasAtrasadasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalParcelasPagasAtrasadasQuery query = new ObtemTotalParcelasPagasAtrasadasQuery();

        executarConsulta(query);
    }
}

