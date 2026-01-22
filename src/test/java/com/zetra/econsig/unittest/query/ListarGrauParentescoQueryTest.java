package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.beneficiario.ListarGrauParentescoQuery;

public class ListarGrauParentescoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarGrauParentescoQuery query = new ListarGrauParentescoQuery();
        query.grpCodigo = "123";

        executarConsulta(query);
    }
}

