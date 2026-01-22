package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ObtemCsaBloqRelSvcRequerDeferimentoQuery;

public class ObtemCsaBloqRelSvcRequerDeferimentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemCsaBloqRelSvcRequerDeferimentoQuery query = new ObtemCsaBloqRelSvcRequerDeferimentoQuery();
        query.adeCodigos = java.util.List.of("1", "2");
        query.ativo = true;

        executarConsulta(query);
    }
}

