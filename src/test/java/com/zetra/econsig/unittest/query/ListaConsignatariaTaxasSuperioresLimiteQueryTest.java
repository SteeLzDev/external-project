package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaConsignatariaTaxasSuperioresLimiteQuery;

public class ListaConsignatariaTaxasSuperioresLimiteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariaTaxasSuperioresLimiteQuery query = new ListaConsignatariaTaxasSuperioresLimiteQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

