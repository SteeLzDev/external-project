package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaComAdeSerQuery;

public class ListaConsignatariaComAdeSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariaComAdeSerQuery query = new ListaConsignatariaComAdeSerQuery();
        query.count = false;
        query.serCodigo = "123";
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.somenteAtivos = true;
        query.sadAtivosLimite = true;
        query.adeCodigosExcecao = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

