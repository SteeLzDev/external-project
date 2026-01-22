package com.zetra.econsig.unittest.query;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemContratoSuspensoPrdRejeitadaNaoReativadoQuery;

public class ObtemContratoSuspensoPrdRejeitadaNaoReativadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        final String adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";

        final ObtemContratoSuspensoPrdRejeitadaNaoReativadoQuery query = new ObtemContratoSuspensoPrdRejeitadaNaoReativadoQuery();
        query.adeCodigos = Arrays.asList(adeCodigo);

        executarConsulta(query);
    }
}

