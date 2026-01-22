package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListarCodigoServidoresRetornoQuery;

public class ListarCodigoServidoresRetornoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.util.List<java.lang.Integer> diasParam = java.util.List.of(1, 2);

        ListarCodigoServidoresRetornoQuery query = new ListarCodigoServidoresRetornoQuery(diasParam);

        executarConsulta(query);
    }
}

