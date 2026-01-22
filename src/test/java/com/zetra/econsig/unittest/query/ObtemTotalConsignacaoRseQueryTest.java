package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoRseQuery;

public class ObtemTotalConsignacaoRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalConsignacaoRseQuery query = new ObtemTotalConsignacaoRseQuery();
        query.rseCodigo = "123";
        query.adeIncMargem = 1;
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

