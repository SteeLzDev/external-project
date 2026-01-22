package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioCseQuery;

public class ObtemUsuarioCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemUsuarioCseQuery query = new ObtemUsuarioCseQuery();

        executarConsulta(query);
    }
}

