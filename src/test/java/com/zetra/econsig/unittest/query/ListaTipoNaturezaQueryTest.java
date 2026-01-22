package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaTipoNaturezaQuery;

public class ListaTipoNaturezaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoNaturezaQuery query = new ListaTipoNaturezaQuery();

        executarConsulta(query);
    }
}

