package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemImagemServidorQuery;

public class ObtemImagemServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemImagemServidorQuery query = new ObtemImagemServidorQuery();
        query.cpfServidor = "123";

        executarConsulta(query);
    }
}

