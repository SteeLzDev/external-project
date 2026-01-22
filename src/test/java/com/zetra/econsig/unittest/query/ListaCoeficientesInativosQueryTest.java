package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaCoeficientesInativosQuery;

public class ListaCoeficientesInativosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCoeficientesInativosQuery query = new ListaCoeficientesInativosQuery();

        executarConsulta(query);
    }
}

