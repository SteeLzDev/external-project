package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.sdp.plano.ListaNaturezaPlanoQuery;

public class ListaNaturezaPlanoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaNaturezaPlanoQuery query = new ListaNaturezaPlanoQuery();

        executarConsulta(query);
    }
}

