package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.arquivo.ListaArquivoServidorQuery;

public class ListaArquivoServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaArquivoServidorQuery query = new ListaArquivoServidorQuery();
        query.serCodigo = "123";
        query.tarCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

