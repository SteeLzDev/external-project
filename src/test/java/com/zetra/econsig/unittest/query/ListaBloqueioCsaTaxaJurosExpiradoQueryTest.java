package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaBloqueioCsaTaxaJurosExpiradoQuery;

public class ListaBloqueioCsaTaxaJurosExpiradoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String csaCodigo = "267";

        ListaBloqueioCsaTaxaJurosExpiradoQuery query = new ListaBloqueioCsaTaxaJurosExpiradoQuery(csaCodigo);

        executarConsulta(query);
    }
}

