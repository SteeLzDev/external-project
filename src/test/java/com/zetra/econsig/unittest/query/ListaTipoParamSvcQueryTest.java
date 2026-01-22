package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaTipoParamSvcQuery;

public class ListaTipoParamSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoParamSvcQuery query = new ListaTipoParamSvcQuery();

        executarConsulta(query);
    }
}

