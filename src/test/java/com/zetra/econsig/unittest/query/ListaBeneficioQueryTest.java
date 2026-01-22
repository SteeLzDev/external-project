package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListaBeneficioQuery;

public class ListaBeneficioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaBeneficioQuery query = new ListaBeneficioQuery();
        query.csaCodigo = "267";
        query.benCodigo = null;

        executarConsulta(query);
    }
}

