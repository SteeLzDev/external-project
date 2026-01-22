package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficiariosForaRegraDependenteQuery;

public class ListarBeneficiariosForaRegraDependenteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBeneficiariosForaRegraDependenteQuery query = new ListarBeneficiariosForaRegraDependenteQuery();
        query.serCodigo = "123";
        query.tntCodigos = java.util.List.of("1", "2");
        query.scbCodigos = java.util.List.of("1", "2");
        query.subsidioConcedido = 0;

        executarConsulta(query);
    }
}

