package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaTodosParamTarifCseQuery;

public class ListaTodosParamTarifCseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTodosParamTarifCseQuery query = new ListaTodosParamTarifCseQuery();
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

