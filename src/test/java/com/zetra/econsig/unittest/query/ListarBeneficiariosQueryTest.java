package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarBeneficiariosQuery;

public class ListarBeneficiariosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBeneficiariosQuery query = new ListarBeneficiariosQuery();
        query.rseCodigo = "123";
        query.rseMatricula = "123";
        query.bfcCpf = "123";
        query.cbeNumero = "123";
        query.filtro = "123";
        query.filtro_tipo = "7";
        query.tibCodigo = "123";
        query.count = false;

        executarConsulta(query);
    }
}

