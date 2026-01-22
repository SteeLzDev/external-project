package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.VerificaAdeProvisionamentoQuery;

public class VerificaAdeProvisionamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        VerificaAdeProvisionamentoQuery query = new VerificaAdeProvisionamentoQuery();
        query.adeCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

