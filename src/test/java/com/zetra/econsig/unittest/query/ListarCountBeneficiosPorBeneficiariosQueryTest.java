package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarCountBeneficiosPorBeneficiariosQuery;

public class ListarCountBeneficiosPorBeneficiariosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarCountBeneficiosPorBeneficiariosQuery query = new ListarCountBeneficiosPorBeneficiariosQuery();
        query.rseCodigo = "123";
        query.scbCodigo = "123";

        executarConsulta(query);
    }
}

