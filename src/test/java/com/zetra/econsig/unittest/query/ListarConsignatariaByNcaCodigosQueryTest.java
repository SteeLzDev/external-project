package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarConsignatariaByNcaCodigosQuery;

public class ListarConsignatariaByNcaCodigosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarConsignatariaByNcaCodigosQuery query = new ListarConsignatariaByNcaCodigosQuery();
        query.ncaCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

