package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficioEscolhidoQuery;

public class ListarBeneficioEscolhidoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBeneficioEscolhidoQuery query = new ListarBeneficioEscolhidoQuery();
        query.benCodigos = java.util.List.of("1", "2");
        query.svcCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

