package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.log.ListaTipoEntidadeLogQuery;

public class ListaTipoEntidadeLogQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoEntidadeLogQuery query = new ListaTipoEntidadeLogQuery();

        executarConsulta(query);
    }
}

