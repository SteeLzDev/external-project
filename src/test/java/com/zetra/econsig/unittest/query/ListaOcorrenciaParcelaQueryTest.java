package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListaOcorrenciaParcelaQuery;

public class ListaOcorrenciaParcelaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaParcelaQuery query = new ListaOcorrenciaParcelaQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.spdCodigos = java.util.List.of("1", "2");
        query.orderASC = true;

        executarConsulta(query);
    }
}

