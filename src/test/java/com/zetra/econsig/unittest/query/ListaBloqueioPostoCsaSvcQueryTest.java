package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.posto.ListaBloqueioPostoCsaSvcQuery;

public class ListaBloqueioPostoCsaSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaBloqueioPostoCsaSvcQuery query = new ListaBloqueioPostoCsaSvcQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

