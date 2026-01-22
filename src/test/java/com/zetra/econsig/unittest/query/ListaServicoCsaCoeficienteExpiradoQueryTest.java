package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoCsaCoeficienteExpiradoQuery;

public class ListaServicoCsaCoeficienteExpiradoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        boolean count = false;
        String csaCodigo = "267";

        ListaServicoCsaCoeficienteExpiradoQuery query = new ListaServicoCsaCoeficienteExpiradoQuery(count, csaCodigo);

        executarConsulta(query);
    }
}

