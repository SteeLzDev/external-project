package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoQuery;

public class ObtemConsignacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemConsignacaoQuery query = new ObtemConsignacaoQuery();
        query.adeCodigos = java.util.List.of("1", "2");
        query.arquivado = true;

        executarConsulta(query);
    }
}

