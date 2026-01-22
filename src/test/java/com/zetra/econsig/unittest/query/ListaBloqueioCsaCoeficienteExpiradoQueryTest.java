package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaBloqueioCsaCoeficienteExpiradoQuery;

public class ListaBloqueioCsaCoeficienteExpiradoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String csaCodigo = "267";

        ListaBloqueioCsaCoeficienteExpiradoQuery query = new ListaBloqueioCsaCoeficienteExpiradoQuery(csaCodigo);

        executarConsulta(query);
    }
}

