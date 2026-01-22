package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.basecalc.ListaTipoBaseCalculoQuery;

public class ListaTipoBaseCalculoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoBaseCalculoQuery query = new ListaTipoBaseCalculoQuery();

        executarConsulta(query);
    }
}

