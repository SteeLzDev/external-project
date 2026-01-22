package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.cidade.ListaCidadeUfQuery;

public class ListaCidadeUfQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCidadeUfQuery query = new ListaCidadeUfQuery();
        query.ufCod = "123";
        query.termo = "123";

        executarConsulta(query);
    }
}

