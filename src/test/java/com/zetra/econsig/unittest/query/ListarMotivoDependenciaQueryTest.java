package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.beneficiario.ListarMotivoDependenciaQuery;

public class ListarMotivoDependenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarMotivoDependenciaQuery query = new ListarMotivoDependenciaQuery();
        query.mdeCodigo = "123";

        executarConsulta(query);
    }
}

