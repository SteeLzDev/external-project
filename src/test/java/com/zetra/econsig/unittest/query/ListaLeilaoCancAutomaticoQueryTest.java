package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaLeilaoCancAutomaticoQuery;

public class ListaLeilaoCancAutomaticoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaLeilaoCancAutomaticoQuery query = new ListaLeilaoCancAutomaticoQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

