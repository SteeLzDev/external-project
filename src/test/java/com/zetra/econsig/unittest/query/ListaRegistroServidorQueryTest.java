package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorQuery;

public class ListaRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegistroServidorQuery query = new ListaRegistroServidorQuery();
        query.count = false;
        query.recuperaRseExcluido = true;
        query.serCodigo = "123";
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");
        query.rseMatriculas = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

