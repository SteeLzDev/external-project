package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.seguranca.ListaNivelSegurancaQuery;

public class ListaNivelSegurancaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaNivelSegurancaQuery query = new ListaNivelSegurancaQuery();

        executarConsulta(query);
    }
}

