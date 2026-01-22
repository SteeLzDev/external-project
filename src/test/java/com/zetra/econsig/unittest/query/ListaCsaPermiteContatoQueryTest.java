package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaPermiteContatoQuery;

public class ListaCsaPermiteContatoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsaPermiteContatoQuery query = new ListaCsaPermiteContatoQuery();
        query.csaCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

