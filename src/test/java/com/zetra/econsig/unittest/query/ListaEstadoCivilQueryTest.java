package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaEstadoCivilQuery;

public class ListaEstadoCivilQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaEstadoCivilQuery query = new ListaEstadoCivilQuery();
        query.estCvlCodigo = "123";

        executarConsulta(query);
    }
}

