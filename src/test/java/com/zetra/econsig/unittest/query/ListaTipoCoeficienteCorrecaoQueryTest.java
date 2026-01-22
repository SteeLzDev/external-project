package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.correcao.ListaTipoCoeficienteCorrecaoQuery;

public class ListaTipoCoeficienteCorrecaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoCoeficienteCorrecaoQuery query = new ListaTipoCoeficienteCorrecaoQuery();

        executarConsulta(query);
    }
}

