package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.prazo.ListaPrazoConsignatariaQuery;

public class ListaPrazoConsignatariaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPrazoConsignatariaQuery query = new ListaPrazoConsignatariaQuery();

        executarConsulta(query);
    }
}

