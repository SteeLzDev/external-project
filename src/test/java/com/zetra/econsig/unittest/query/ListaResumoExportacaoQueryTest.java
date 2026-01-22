package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.movimento.ListaResumoExportacaoQuery;

public class ListaResumoExportacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaResumoExportacaoQuery query = new ListaResumoExportacaoQuery();
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");
        query.exportar = true;

        executarConsulta(query);
    }
}

