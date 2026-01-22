package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaBloqueioNaturezaServicoServidorQuery;

public class ListaBloqueioNaturezaServicoServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaBloqueioNaturezaServicoServidorQuery query = new ListaBloqueioNaturezaServicoServidorQuery();
        query.rseCodigo = "123";
        query.nseCodigo = "123";

        executarConsulta(query);
    }
}

