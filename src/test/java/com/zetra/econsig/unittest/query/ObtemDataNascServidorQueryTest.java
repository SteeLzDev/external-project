package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemDataNascServidorQuery;

public class ObtemDataNascServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ObtemDataNascServidorQuery query = new ObtemDataNascServidorQuery("1631A8967A4249789D4Z8426Z1C5ZB26");
        executarConsulta(query);
    }
}
