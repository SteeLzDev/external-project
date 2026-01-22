package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaOcorrenciaRegistroServidorQuery;

public class ListaOcorrenciaRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaRegistroServidorQuery query = new ListaOcorrenciaRegistroServidorQuery();
        query.count = false;
        query.rseCodigo = "123";
        query.tocCodigo = "123";

        executarConsulta(query);
    }
}

