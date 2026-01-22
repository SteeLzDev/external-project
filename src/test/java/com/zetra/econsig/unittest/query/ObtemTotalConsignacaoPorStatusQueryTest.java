package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoPorStatusQuery;

public class ObtemTotalConsignacaoPorStatusQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalConsignacaoPorStatusQuery query = new ObtemTotalConsignacaoPorStatusQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.corCodigo = "EF128080808080808080808080809980";
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

