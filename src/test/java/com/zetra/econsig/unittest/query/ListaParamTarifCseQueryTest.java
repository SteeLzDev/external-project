package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamTarifCseQuery;

public class ListaParamTarifCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamTarifCseQuery query = new ListaParamTarifCseQuery();
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

