package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarRelacaoBeneficiosQuery;

public class ListarRelacaoBeneficiosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarRelacaoBeneficiosQuery query = new ListarRelacaoBeneficiosQuery();
        query.serCodigo = "123";
        query.benCodigo = "123";
        query.reativar = true;
        query.bfcCodigo = "123";
        query.contratosAtivos = true;
        query.nseCodigo = "123";
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

