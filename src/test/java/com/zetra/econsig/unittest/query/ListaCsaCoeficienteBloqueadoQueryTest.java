package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaCoeficienteBloqueadoQuery;

public class ListaCsaCoeficienteBloqueadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsaCoeficienteBloqueadoQuery query = new ListaCsaCoeficienteBloqueadoQuery();

        executarConsulta(query);
    }
}

