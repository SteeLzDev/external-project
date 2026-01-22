package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.dashboardprocessamento.ObtemMenorDataProcessamentoBlocoQuery;

public class ObtemMenorDataProcessamentoBlocoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemMenorDataProcessamentoBlocoQuery query = new ObtemMenorDataProcessamentoBlocoQuery();
        query.sbpCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

