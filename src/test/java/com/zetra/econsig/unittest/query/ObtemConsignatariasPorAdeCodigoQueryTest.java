package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ObtemConsignatariasPorAdeCodigoQuery;

public class ObtemConsignatariasPorAdeCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemConsignatariasPorAdeCodigoQuery query = new ObtemConsignatariasPorAdeCodigoQuery();
        query.adeCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

