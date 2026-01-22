package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsasComAdeRenegociaveisQuery;

public class ListaCsasComAdeRenegociaveisQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsasComAdeRenegociaveisQuery query = new ListaCsasComAdeRenegociaveisQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.rseCodigo = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

