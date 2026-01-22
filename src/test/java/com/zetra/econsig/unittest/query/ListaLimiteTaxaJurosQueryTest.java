package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.juros.ListaLimiteTaxaJurosQuery;

public class ListaLimiteTaxaJurosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaLimiteTaxaJurosQuery query = new ListaLimiteTaxaJurosQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.ltjPrazoRef = 1;
        query.notLtjCodigo = "123";
        query.count = false;

        executarConsulta(query);
    }
}

