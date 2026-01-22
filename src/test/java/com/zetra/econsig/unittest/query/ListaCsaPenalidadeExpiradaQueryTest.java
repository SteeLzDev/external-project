package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaPenalidadeExpiradaQuery;

public class ListaCsaPenalidadeExpiradaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsaPenalidadeExpiradaQuery query = new ListaCsaPenalidadeExpiradaQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

