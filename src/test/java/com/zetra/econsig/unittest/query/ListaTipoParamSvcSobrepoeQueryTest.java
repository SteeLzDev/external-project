package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaTipoParamSvcSobrepoeQuery;

public class ListaTipoParamSvcSobrepoeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoParamSvcSobrepoeQuery query = new ListaTipoParamSvcSobrepoeQuery();

        executarConsulta(query);
    }
}

