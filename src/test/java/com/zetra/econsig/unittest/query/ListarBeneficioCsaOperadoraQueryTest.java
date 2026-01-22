package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarBeneficioCsaOperadoraQuery;

public class ListarBeneficioCsaOperadoraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBeneficioCsaOperadoraQuery query = new ListarBeneficioCsaOperadoraQuery();
        query.ncaCodigo = "1";
        query.filtro = "123";
        query.filtro_tipo = "7";
        query.count = false;

        executarConsulta(query);
    }
}

