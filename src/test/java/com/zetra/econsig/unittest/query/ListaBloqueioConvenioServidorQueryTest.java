package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaBloqueioConvenioServidorQuery;

public class ListaBloqueioConvenioServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaBloqueioConvenioServidorQuery query = new ListaBloqueioConvenioServidorQuery();
        query.rseCodigo = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.csaCodigo = "267";
        query.count = false;

        executarConsulta(query);
    }
}

