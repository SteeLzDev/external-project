package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoPorCodigoQuery;

public class ObtemTotalValorConsignacaoPorCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalValorConsignacaoPorCodigoQuery query = new ObtemTotalValorConsignacaoPorCodigoQuery(java.util.List.of("1", "2"));

        executarConsulta(query);
    }
}

