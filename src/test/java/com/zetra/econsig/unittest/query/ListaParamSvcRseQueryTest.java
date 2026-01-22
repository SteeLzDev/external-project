package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamSvcRseQuery;

public class ListaParamSvcRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamSvcRseQuery query = new ListaParamSvcRseQuery();
        query.rseCodigo = "123";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.tpsCodigo = "123";

        executarConsulta(query);
    }
}

