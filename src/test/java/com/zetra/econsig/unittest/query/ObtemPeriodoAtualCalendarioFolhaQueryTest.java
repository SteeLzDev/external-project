package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ObtemPeriodoAtualCalendarioFolhaQuery;

public class ObtemPeriodoAtualCalendarioFolhaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemPeriodoAtualCalendarioFolhaQuery query = new ObtemPeriodoAtualCalendarioFolhaQuery();
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

