package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaConsultaSalarioServidorQuery;

public class ListaConsultaSalarioServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsultaSalarioServidorQuery query = new ListaConsultaSalarioServidorQuery();
        query.serCpf = "111.111.111-11";

        executarConsulta(query);
    }
}
