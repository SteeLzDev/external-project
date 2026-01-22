package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.modelotermoaditivo.ListaCodTituloModeloTermoAditivoQuery;

public class ListaCodTituloModeloTermoAditivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final ListaCodTituloModeloTermoAditivoQuery query = new ListaCodTituloModeloTermoAditivoQuery();

        executarConsulta(query);
    }
}

