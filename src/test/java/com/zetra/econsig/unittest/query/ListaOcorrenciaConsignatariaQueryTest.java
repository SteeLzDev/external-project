package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaOcorrenciaConsignatariaQuery;

public class ListaOcorrenciaConsignatariaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaConsignatariaQuery query = new ListaOcorrenciaConsignatariaQuery();
        query.count = false;
        query.csaCodigo = "267";
        query.occCodigo = "123";
        query.tocCodigo = "123";
        query.tocCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

