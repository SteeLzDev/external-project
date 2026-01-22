package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.penalidade.ListaTipoPenalidadeQuery;

public class ListaTipoPenalidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoPenalidadeQuery query = new ListaTipoPenalidadeQuery();

        executarConsulta(query);
    }
}

