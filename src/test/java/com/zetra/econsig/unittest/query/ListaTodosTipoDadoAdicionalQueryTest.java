package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaTodosTipoDadoAdicionalQuery;

public class ListaTodosTipoDadoAdicionalQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTodosTipoDadoAdicionalQuery query = new ListaTodosTipoDadoAdicionalQuery();

        executarConsulta(query);
    }
}

