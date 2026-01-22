package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoReimplanteQuery;

public class ObtemConsignacaoReimplanteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemConsignacaoReimplanteQuery query = new ObtemConsignacaoReimplanteQuery();
        query.count = false;
        query.adeNum = java.util.List.of(1l, 2l);

        executarConsulta(query);
    }
}

