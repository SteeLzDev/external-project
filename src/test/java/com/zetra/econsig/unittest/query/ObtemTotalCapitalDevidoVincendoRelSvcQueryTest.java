package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalCapitalDevidoVincendoRelSvcQuery;

public class ObtemTotalCapitalDevidoVincendoRelSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalCapitalDevidoVincendoRelSvcQuery query = new ObtemTotalCapitalDevidoVincendoRelSvcQuery();
        query.rseCodigo = "123";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.periodoAtual = "2023-01-01";
        query.adeCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

