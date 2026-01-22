package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.vinculo.ListaConsignatariasExisteCnvVinculoRegistroServidorQuery;

public class ListaConsignatariasExisteCnvVinculoRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariasExisteCnvVinculoRegistroServidorQuery query = new ListaConsignatariasExisteCnvVinculoRegistroServidorQuery();

        executarConsulta(query);
    }
}

