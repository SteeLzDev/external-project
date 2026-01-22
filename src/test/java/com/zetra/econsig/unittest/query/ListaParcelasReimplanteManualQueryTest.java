package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListaParcelasReimplanteManualQuery;

public class ListaParcelasReimplanteManualQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParcelasReimplanteManualQuery query = new ListaParcelasReimplanteManualQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.spdCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

