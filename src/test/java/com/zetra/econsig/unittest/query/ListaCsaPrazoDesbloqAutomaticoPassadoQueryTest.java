package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaPrazoDesbloqAutomaticoPassadoQuery;

public class ListaCsaPrazoDesbloqAutomaticoPassadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsaPrazoDesbloqAutomaticoPassadoQuery query = new ListaCsaPrazoDesbloqAutomaticoPassadoQuery();

        executarConsulta(query);
    }
}

