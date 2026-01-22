package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficiariosCalculoSubsidioQuery;

public class ListarBeneficiariosCalculoSubsidioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBeneficiariosCalculoSubsidioQuery query = new ListarBeneficiariosCalculoSubsidioQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.serCodigo = "123";
        query.rseCodigo = "123";
        query.tntCodigos = java.util.List.of("1", "2");
        query.scbCodigos = java.util.List.of("1", "2");
        query.ignoraPeriodo = true;

        executarConsulta(query);
    }
}

