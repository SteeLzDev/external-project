package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaCoeficienteAtivoExpiradoQuery;

public class ListaCsaCoeficienteAtivoExpiradoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.lang.Integer diasParaExpiracao = 1;

        ListaCsaCoeficienteAtivoExpiradoQuery query = new ListaCsaCoeficienteAtivoExpiradoQuery(diasParaExpiracao);

        executarConsulta(query);
    }
}

