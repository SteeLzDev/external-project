package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaTipoRegistroServidorQuery;

public class ListaTipoRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoRegistroServidorQuery query = new ListaTipoRegistroServidorQuery();

        executarConsulta(query);
    }
}

