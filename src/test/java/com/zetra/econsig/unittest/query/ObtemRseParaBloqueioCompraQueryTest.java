package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ObtemRseParaBloqueioCompraQuery;

public class ObtemRseParaBloqueioCompraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemRseParaBloqueioCompraQuery query = new ObtemRseParaBloqueioCompraQuery();
        query.adeCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

