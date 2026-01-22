package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaRegraRestricaoAcessoQuery;

public class ListaRegraRestricaoAcessoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegraRestricaoAcessoQuery query = new ListaRegraRestricaoAcessoQuery();
        query.csaCodigo = "267";
        query.count = false;
        query.todos = true;

        executarConsulta(query);
    }
}

