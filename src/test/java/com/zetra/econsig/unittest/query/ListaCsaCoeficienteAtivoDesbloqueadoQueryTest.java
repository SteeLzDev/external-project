package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaCoeficienteAtivoDesbloqueadoQuery;

public class ListaCsaCoeficienteAtivoDesbloqueadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsaCoeficienteAtivoDesbloqueadoQuery query = new ListaCsaCoeficienteAtivoDesbloqueadoQuery();

        executarConsulta(query);
    }
}

