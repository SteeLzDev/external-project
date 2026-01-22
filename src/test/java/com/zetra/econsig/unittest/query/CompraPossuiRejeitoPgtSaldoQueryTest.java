package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.CompraPossuiRejeitoPgtSaldoQuery;

public class CompraPossuiRejeitoPgtSaldoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        CompraPossuiRejeitoPgtSaldoQuery query = new CompraPossuiRejeitoPgtSaldoQuery();
        query.adeCodigoDestino = "123";

        executarConsulta(query);
    }
}

