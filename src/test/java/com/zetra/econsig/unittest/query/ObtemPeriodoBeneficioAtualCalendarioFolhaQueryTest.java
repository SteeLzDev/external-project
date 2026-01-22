package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ObtemPeriodoBeneficioAtualCalendarioFolhaQuery;

public class ObtemPeriodoBeneficioAtualCalendarioFolhaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemPeriodoBeneficioAtualCalendarioFolhaQuery query = new ObtemPeriodoBeneficioAtualCalendarioFolhaQuery();
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

