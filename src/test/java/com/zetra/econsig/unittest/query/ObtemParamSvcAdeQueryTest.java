package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ObtemParamSvcAdeQuery;

public class ObtemParamSvcAdeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemParamSvcAdeQuery query = new ObtemParamSvcAdeQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.tpsCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

