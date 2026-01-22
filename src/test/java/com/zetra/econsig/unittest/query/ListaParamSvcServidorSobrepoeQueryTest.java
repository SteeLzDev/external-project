package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamSvcServidorSobrepoeQuery;

public class ListaParamSvcServidorSobrepoeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamSvcServidorSobrepoeQuery query = new ListaParamSvcServidorSobrepoeQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.rseCodigo = "123";
        query.tpsCodigos = java.util.List.of("1", "2");
        query.psrVlr = "123";

        executarConsulta(query);
    }
}

