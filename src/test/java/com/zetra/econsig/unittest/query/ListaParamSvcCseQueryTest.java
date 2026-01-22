package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamSvcCseQuery;

public class ListaParamSvcCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamSvcCseQuery query = new ListaParamSvcCseQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.tpsCodigo = "123";
        query.pseVlr = "123";

        executarConsulta(query);
    }
}

