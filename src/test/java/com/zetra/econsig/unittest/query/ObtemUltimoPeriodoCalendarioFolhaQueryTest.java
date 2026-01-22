package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ObtemUltimoPeriodoCalendarioFolhaQuery;

public class ObtemUltimoPeriodoCalendarioFolhaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemUltimoPeriodoCalendarioFolhaQuery query = new ObtemUltimoPeriodoCalendarioFolhaQuery();
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

