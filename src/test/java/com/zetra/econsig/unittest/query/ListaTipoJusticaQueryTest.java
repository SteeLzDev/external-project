package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.justica.ListaTipoJusticaQuery;

public class ListaTipoJusticaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoJusticaQuery query = new ListaTipoJusticaQuery();

        executarConsulta(query);
    }
}

