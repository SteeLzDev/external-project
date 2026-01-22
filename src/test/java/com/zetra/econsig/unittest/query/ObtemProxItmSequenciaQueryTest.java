package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.menu.ObtemProxItmSequenciaQuery;

public class ObtemProxItmSequenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemProxItmSequenciaQuery query = new ObtemProxItmSequenciaQuery();

        executarConsulta(query);
    }
}

