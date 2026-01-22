package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarNacionalidadeQuery;

public class ListarNacionalidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarNacionalidadeQuery query = new ListarNacionalidadeQuery();

        executarConsulta(query);
    }
}

