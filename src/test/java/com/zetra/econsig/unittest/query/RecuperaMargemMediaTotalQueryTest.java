package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.RecuperaMargemMediaTotalQuery;

public class RecuperaMargemMediaTotalQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RecuperaMargemMediaTotalQuery query = new RecuperaMargemMediaTotalQuery();
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");
        query.recuperaRseExcluido = true;

        executarConsulta(query);
    }
}

