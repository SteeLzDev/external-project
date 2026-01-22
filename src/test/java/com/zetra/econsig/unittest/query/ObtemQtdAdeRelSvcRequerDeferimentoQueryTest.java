package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemQtdAdeRelSvcRequerDeferimentoQuery;

public class ObtemQtdAdeRelSvcRequerDeferimentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemQtdAdeRelSvcRequerDeferimentoQuery query = new ObtemQtdAdeRelSvcRequerDeferimentoQuery();
        query.svcCodigoDestino = "123";
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

