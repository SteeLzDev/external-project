package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaStatusRegistroServidorQuery;

public class ListaStatusRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaStatusRegistroServidorQuery query = new ListaStatusRegistroServidorQuery();
        query.ignoraStatusExcluidos = true;
        query.ignoraStatusBloqSeguranca = true;

        executarConsulta(query);
    }
}

