package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.periodo.ListaPeriodoBeneficioQuery;

public class ListaPeriodoBeneficioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPeriodoBeneficioQuery query = new ListaPeriodoBeneficioQuery();
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

