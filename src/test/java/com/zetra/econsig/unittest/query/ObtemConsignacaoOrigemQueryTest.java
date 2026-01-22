package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoOrigemQuery;

public class ObtemConsignacaoOrigemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemConsignacaoOrigemQuery query = new ObtemConsignacaoOrigemQuery();
        query.adeCodigoDestino = "123";

        executarConsulta(query);
    }
}

