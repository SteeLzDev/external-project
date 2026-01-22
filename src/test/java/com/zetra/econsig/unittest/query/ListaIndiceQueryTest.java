package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.indice.ListaIndiceQuery;

public class ListaIndiceQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaIndiceQuery query = new ListaIndiceQuery();
        query.count = false;
        query.svcCodigo = "050E8080808080808080808080808280";
        query.csaCodigo = "267";
        query.indCodigo = "123";
        query.indDescricao = "123";

        executarConsulta(query);
    }
}

