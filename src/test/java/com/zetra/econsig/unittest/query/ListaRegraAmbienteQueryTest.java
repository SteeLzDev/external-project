package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaRegraAmbienteQuery;

public class ListaRegraAmbienteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegraAmbienteQuery query = new ListaRegraAmbienteQuery();
        query.reaAtivo = 1;

        executarConsulta(query);
    }
}

