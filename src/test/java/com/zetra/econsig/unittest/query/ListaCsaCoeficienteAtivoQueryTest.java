package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaCoeficienteAtivoQuery;

public class ListaCsaCoeficienteAtivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsaCoeficienteAtivoQuery query = new ListaCsaCoeficienteAtivoQuery();

        executarConsulta(query);
    }
}

