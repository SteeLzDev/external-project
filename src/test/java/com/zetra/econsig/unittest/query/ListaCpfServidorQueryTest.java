package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaCpfServidorQuery;

public class ListaCpfServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCpfServidorQuery query = new ListaCpfServidorQuery();

        executarConsulta(query);
    }
}

