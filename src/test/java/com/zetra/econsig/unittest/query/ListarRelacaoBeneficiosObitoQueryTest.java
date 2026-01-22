package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarRelacaoBeneficiosObitoQuery;

public class ListarRelacaoBeneficiosObitoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarRelacaoBeneficiosObitoQuery query = new ListarRelacaoBeneficiosObitoQuery();
        query.serCodigo = "123";
        query.bfcCodigo = "123";
        query.nseCodigo = "123";
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

