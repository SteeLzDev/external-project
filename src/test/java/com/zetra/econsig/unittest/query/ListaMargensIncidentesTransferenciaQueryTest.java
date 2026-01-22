package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaMargensIncidentesTransferenciaQuery;

public class ListaMargensIncidentesTransferenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaMargensIncidentesTransferenciaQuery query = new ListaMargensIncidentesTransferenciaQuery();
        query.margens = java.util.List.of((short) 1, (short) 2);
        query.papCodigo = "1";

        executarConsulta(query);
    }
}

