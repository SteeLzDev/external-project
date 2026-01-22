package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.arquivo.ListaArquivoRegistroServidorQuery;

public class ListaArquivoRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaArquivoRegistroServidorQuery query = new ListaArquivoRegistroServidorQuery();
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

