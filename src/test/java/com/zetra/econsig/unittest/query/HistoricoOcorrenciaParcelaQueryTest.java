package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.historico.HistoricoOcorrenciaParcelaQuery;

public class HistoricoOcorrenciaParcelaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        HistoricoOcorrenciaParcelaQuery query = new HistoricoOcorrenciaParcelaQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.spdCodigos = java.util.List.of("1", "2");
        query.tocCodigos = java.util.List.of("1", "2");
        query.arquivado = true;
        query.offset = 1;

        executarConsulta(query);
    }
}

