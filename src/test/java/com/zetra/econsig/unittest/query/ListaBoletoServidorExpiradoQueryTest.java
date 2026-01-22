package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.boleto.ListaBoletoServidorExpiradoQuery;

public class ListaBoletoServidorExpiradoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaBoletoServidorExpiradoQuery query = new ListaBoletoServidorExpiradoQuery();
        query.diasAposEnvio = 1;

        executarConsulta(query);
    }
}

