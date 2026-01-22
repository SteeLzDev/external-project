package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarAnexoBeneficiariosQuery;

public class ListarAnexoBeneficiariosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarAnexoBeneficiariosQuery query = new ListarAnexoBeneficiariosQuery();
        query.bfcCodigo = "123";
        query.count = false;

        executarConsulta(query);
    }
}

