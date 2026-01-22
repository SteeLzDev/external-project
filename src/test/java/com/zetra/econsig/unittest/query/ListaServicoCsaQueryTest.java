package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoCsaQuery;

public class ListaServicoCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoCsaQuery query = new ListaServicoCsaQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

