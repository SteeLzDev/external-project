package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListCsaTaxaJurosLiberadaQuery;

public class ListCsaTaxaJurosLiberadaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListCsaTaxaJurosLiberadaQuery query = new ListCsaTaxaJurosLiberadaQuery();

        executarConsulta(query);
    }
}

