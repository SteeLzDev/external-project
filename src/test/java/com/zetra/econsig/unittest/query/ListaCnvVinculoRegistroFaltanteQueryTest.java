package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.vinculo.ListaCnvVinculoRegistroFaltanteQuery;

public class ListaCnvVinculoRegistroFaltanteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCnvVinculoRegistroFaltanteQuery query = new ListaCnvVinculoRegistroFaltanteQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.vrsCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

