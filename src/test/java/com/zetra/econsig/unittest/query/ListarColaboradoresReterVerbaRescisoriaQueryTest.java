package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.rescisao.ListarColaboradoresReterVerbaRescisoriaQuery;

public class ListarColaboradoresReterVerbaRescisoriaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarColaboradoresReterVerbaRescisoriaQuery query = new ListarColaboradoresReterVerbaRescisoriaQuery();
        query.count = false;
        // query.notSvrCodigos = java.util.List.of("1", "2");
        query.orgCodigo = "751F8080808080808080808080809780";

        executarConsulta(query);
    }
}

