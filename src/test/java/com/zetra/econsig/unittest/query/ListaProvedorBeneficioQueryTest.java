package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.provedor.ListaProvedorBeneficioQuery;

public class ListaProvedorBeneficioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaProvedorBeneficioQuery query = new ListaProvedorBeneficioQuery();
        query.proCodigo = "123";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

